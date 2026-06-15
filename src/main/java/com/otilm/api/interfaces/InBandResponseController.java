package com.otilm.api.interfaces;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.web.bind.annotation.RestController;

/**
 * Base controller for unauthenticated binary protocol endpoints that convey
 * every outcome in-band rather than via REST-style HTTP error codes.
 *
 * <p>Unlike {@link NoAuthController}, this base deliberately declares no
 * {@code @ApiResponses}: protocols such as RFC 3161 TSP and RFC 4210 CMP answer
 * both success and failure with the same HTTP 200 response, encoding the result
 * inside the protocol payload (e.g. {@code PKIStatus} / a CMP error
 * {@code PKIBody}). Declaring generic 400/404/500 responses here would
 * misrepresent that contract, so each endpoint documents its own responses
 * instead.</p>
 *
 * @see NoAuthController
 */
@OpenAPIDefinition(
        servers = {
                @Server(
                        url = "https://demo.otilm.com/api",
                        description = "Platform Demo server"
                )
        }
)
@RestController
@SecurityRequirements
public interface InBandResponseController {
}
