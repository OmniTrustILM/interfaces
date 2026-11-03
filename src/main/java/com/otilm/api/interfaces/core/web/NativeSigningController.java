package com.otilm.api.interfaces.core.web;

import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ILM's own signing API for server-side integrations. Every operation authorizes as {@code ResourceAction.SIGN} on
 * {@code Resource.SIGNING_PROFILE}. Operations are synchronous and carry no signature-activation evidence. The
 * ILM_NATIVE gate refusal and input refusals answer 422 with an array of messages; each operation documents its own
 * causes.
 */
@RequestMapping("/v1/signing")
@Tag(name = "Native Signing",
        description = "Native Signing API. "
                + "Synchronous signing operations for server-side integrations, authorized against the Signing Profile "
                + "that is used.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                        examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
        @ApiResponse(responseCode = "502", description = "A connector or the signer failed",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "503",
                description = "A connector or the time source is unavailable; retry after the Retry-After interval",
                headers = @Header(name = "Retry-After", description = "Seconds after which the request may be retried",
                        schema = @Schema(type = "integer")),
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface NativeSigningController extends AuthProtectedController {
}
