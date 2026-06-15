package com.otilm.api.interfaces.core.tsp;

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
 * RFC 3161 Timestamp Protocol endpoint — routed by TSP Profile name.
 *
 * <p>This is a binary RFC 3161 protocol endpoint, not a REST resource. Every
 * request is answered with HTTP 200 and a DER-encoded {@code TimeStampResp}
 * ({@code application/timestamp-reply}); both success and failure are conveyed
 * in-band via the response's {@code PKIStatus} (RFC 3161 section 2.4.2):
 * {@code granted}/{@code grantedWithMods} on success, {@code rejection} with a
 * {@code PKIFailureInfo} reason on failure. The endpoint does not surface
 * REST-style HTTP error codes — a malformed or unacceptable request still
 * returns a 200 {@code TimeStampResp} carrying a rejection status.</p>
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc3161">RFC 3161</a>
 */
@RequestMapping("/v1/protocols/tsp/{tspProfileName}")
@Tag(
        name = "TSP — by profile name",
        description = "RFC 3161 Timestamp Protocol endpoint. Routed by TSP Profile name. " +
                "Always returns HTTP 200 with an application/timestamp-reply TimeStampResp; " +
                "success and rejection are encoded in-band via PKIStatus per RFC 3161."
)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "TimeStampResp returned. PKIStatus conveys the outcome: " +
                        "granted/grantedWithMods on success, rejection with PKIFailureInfo on failure.",
                content = @Content(mediaType = "application/timestamp-reply",
                        schema = @Schema(type = "string", format = "binary"))
        )
})
public interface TspController {

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
                    content = @Content(mediaType = "application/timestamp-reply",
                            schema = @Schema(type = "string", format = "binary"))
            )
    })
    @PostMapping(
            consumes = "application/timestamp-query",
            produces = "application/timestamp-reply"
    )
    ResponseEntity<byte[]> timestamp(
            @Parameter(description = "TSP Profile name")
            @PathVariable String tspProfileName,
            @RequestBody @Schema(description = "DER-encoded TimeStampReq", type = "string", format = "binary")
            byte[] request
    );
}
