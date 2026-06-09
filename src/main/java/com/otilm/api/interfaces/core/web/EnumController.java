package com.otilm.api.interfaces.core.web;

import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.enums.PlatformEnum;
import com.otilm.api.model.core.enums.EnumItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequestMapping("/v1/enums")
@Tag(name = "Enums", description = "Enums API")
public interface EnumController extends AuthProtectedController {

    @Operation(
            summary = "Get platform enums"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Platform settings retrieved")
            })
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    Map<PlatformEnum, Map<String, EnumItemDto>> getPlatformEnums();

}
