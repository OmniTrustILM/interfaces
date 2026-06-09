package com.otilm.api.interfaces.core.local;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.NoAuthController;
import com.otilm.api.model.client.auth.AddUserRequestDto;
import com.otilm.api.model.core.auth.UserDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

@RequestMapping("/v1/local")
@Tag(name = "Local operations", description = "API only accessible from localhost")
public interface LocalController extends NoAuthController {

    @Operation(summary = "Create Administrator")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Admin created")})
    @PostMapping(path = "/admins", consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<UserDetailDto> addAdmin(
            @RequestBody AddUserRequestDto request)
            throws CertificateException, NotFoundException, NoSuchAlgorithmException, AlreadyExistException, AttributeException;
}
