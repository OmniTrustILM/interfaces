package com.czertainly.api.interfaces.core.tsp;

import com.czertainly.api.interfaces.NoAuthController;
import com.czertainly.api.interfaces.core.tsp.error.TspException;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * RFC 3161 Timestamp Protocol endpoint — routed by Signing Profile name.
 * The identified Signing Profile must have TSP activated.
 *
 * <p>Spring MVC note: the static segment "signingProfiles" here takes precedence
 * over the path variable in {@link TspController}, so the two mappings never conflict.</p>
 *
 * <p>SpringDoc note: the class-level {@code @ApiResponses} below override the
 * 400/500 entries inherited from {@link NoAuthController}. SpringDoc merges
 * annotations from parent interfaces; if the same response code appears in both,
 * the subtype's declaration wins for that code.</p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc3161">RFC 3161</a>
 */
@RequestMapping("/v1/protocols/tsp/signingProfiles/{signingProfileName}")
@Tag(
        name = "TSP — by signing profile name",
        description = "RFC 3161 Timestamp Protocol endpoint. Routed by Signing Profile name. " +
                "The Signing Profile must have TSP activated. All responses — including errors — " +
                "are returned as application/timestamp-response per RFC 3161 section 3."
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "TimeStampResp returned (check PKIStatus inside the response for success or rejection)",
                content = @Content(mediaType = "application/timestamp-response",
                        schema = @Schema(type = "string", format = "binary"))
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Request could not be processed; rejection returned as TimeStampResp",
                content = @Content(mediaType = "application/timestamp-response",
                        schema = @Schema(type = "string", format = "binary"))
        ),
        @ApiResponse(
                responseCode = "500",
                description = "Internal error; rejection returned as TimeStampResp with systemFailure",
                content = @Content(mediaType = "application/timestamp-response",
                        schema = @Schema(type = "string", format = "binary"))
        )
})
public interface TspSigningProfileController extends NoAuthController {

    @Operation(
            summary = "Request a timestamp token",
            description = "Accepts a DER-encoded TimeStampReq and returns a DER-encoded TimeStampResp. " +
                    "Error conditions are encoded inside the response as PKIStatus rejection with PKIFailureInfo.",
            externalDocs = @ExternalDocumentation(
                    description = "RFC 3161 — Internet X.509 PKI Time-Stamp Protocol",
                    url = "https://www.rfc-editor.org/rfc/rfc3161"
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "TimeStampResp returned",
                    content = @Content(mediaType = "application/timestamp-response",
                            schema = @Schema(type = "string", format = "binary"))
            )
    })
    @PostMapping(
            value = "/sign",
            consumes = "application/timestamp-query",
            produces = "application/timestamp-response"
    )
    ResponseEntity<byte[]> timestamp(
            @Parameter(description = "Signing Profile name (must have TSP activated)")
            @PathVariable String signingProfileName,
            @RequestBody @Schema(description = "DER-encoded TimeStampReq", type = "string", format = "binary")
            byte[] request
    ) throws TspException;
}
