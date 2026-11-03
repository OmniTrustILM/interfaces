package com.otilm.api.interfaces.core.web;

import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ILM's own signing API for server-side integrations. Every operation authorizes as {@code ResourceAction.SIGN} on
 * {@code Resource.SIGNING_PROFILE}. Operations are synchronous and carry no signature-activation evidence.
 */
@RequestMapping("/v1/signing")
@Tag(name = "Native Signing",
        description = "Native Signing API. "
                + "Synchronous signing operations for server-side integrations, authorized against the Signing Profile "
                + "that is used.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface NativeSigningController extends AuthProtectedController {
}
