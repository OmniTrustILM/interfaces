package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.auth.UpdateUserRequestDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.core.auth.Resource;
import com.czertainly.api.model.core.auth.AuthResourceDto;
import com.czertainly.api.model.core.auth.UserDetailDto;
import com.czertainly.api.model.core.auth.UserProfileDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CertificateException;
import java.util.List;

@RequestMapping("/v1/auth")
@Tag(name = "Authentication Management", description = "Authentication Management API")
public interface AuthController extends AuthProtectedController {

    @Operation(summary = "Profile Authorization")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authenticate a user")})
    @GetMapping(path = "/profile", produces = {"application/json"})
    UserProfileDetailDto profile();

    @Operation(summary = "Update User Profile")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Authenticate a user"), @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PutMapping(path = "/profile", produces = {"application/json"})
    UserDetailDto updateUserProfile(@RequestBody @Valid UpdateUserRequestDto request) throws NotFoundException, CertificateException;

    @Operation(summary = "Get Auth Resources")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Resources retrieved successfully")})
    @GetMapping(path = "/resources", produces = {"application/json"})
    List<AuthResourceDto> getAuthResources();

    @Operation(summary = "Get List of objects for Object Access")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Objects retrieved"), @ApiResponse(responseCode = "404", description = "Resource object not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/resources/{resourceName}/objects", produces = {"application/json"})
    List<NameAndUuidDto> getObjectsForResource(@Parameter(description = "Resource Name") @PathVariable Resource resourceName) throws NotFoundException;
}
