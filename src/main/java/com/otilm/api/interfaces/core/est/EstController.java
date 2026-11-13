package com.otilm.api.interfaces.core.est;

import com.otilm.api.interfaces.InBandResponseController;
import com.otilm.api.model.core.est.EstMediaTypes;
import com.otilm.api.model.core.est.EstPaths;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(EstPaths.BASE)
@Tag(name = "EST operations", description = "Interfaces used by EST clients (RFC 7030). Every operation is "
        + "available without a label and with one label, which names an EST Profile or an RA Profile with EST "
        + "activated. Request and response bodies are base64-encoded DER (RFC 4648, section 4) and error bodies are "
        + "plain text. Clients authenticate as the authentication policy of the selected EST Profile requires.")
public interface EstController extends InBandResponseController {

    @Operation(operationId = "estCaCertificates", summary = "Distribute CA certificates",
            externalDocs = @ExternalDocumentation(description = "RFC 7030, section 4.1",
                    url = "https://www.rfc-editor.org/rfc/rfc7030#section-4.1"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CA certificate chain",
                    content = @Content(mediaType = EstMediaTypes.PKCS7_MIME,
                            schema = @Schema(type = "string", description = "Base64-encoded certs-only CMS"))),
            @ApiResponse(responseCode = "404", description = "No EST service for the label (Not found.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "405",
                    description = "The operation does not accept the HTTP method (The method is not allowed for this operation.)",
                    headers = @Header(name = "Allow", description = "The method the operation accepts",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "The request could not be processed.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "503",
                    description = "CA certificates are not available (The service is temporarily unavailable. Try again later.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))})
    @GetMapping(path = {"/" + EstPaths.CACERTS, "/{label}/" + EstPaths.CACERTS})
    ResponseEntity<byte[]> getCaCertificates(
            @Parameter(description = "EST Profile or RA Profile name") @PathVariable(required = false) String label);

    @Operation(operationId = "estCsrAttributes", summary = "Get CSR attributes",
            externalDocs = @ExternalDocumentation(description = "RFC 7030, section 4.5, updated by RFC 9908",
                    url = "https://www.rfc-editor.org/rfc/rfc9908"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attributes the CA expects in certification requests",
                    content = @Content(mediaType = EstMediaTypes.CSR_ATTRS,
                            schema = @Schema(type = "string", description = "Base64-encoded DER CsrAttrs"))),
            @ApiResponse(responseCode = "204", description = "No attributes are requested"),
            @ApiResponse(responseCode = "404", description = "No EST service for the label (Not found.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "405",
                    description = "The operation does not accept the HTTP method (The method is not allowed for this operation.)",
                    headers = @Header(name = "Allow", description = "The method the operation accepts",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "The request could not be processed.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "503",
                    description = "CSR attributes are not available (The service is temporarily unavailable. Try again later.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))})
    @GetMapping(path = {"/" + EstPaths.CSRATTRS, "/{label}/" + EstPaths.CSRATTRS})
    ResponseEntity<byte[]> getCsrAttributes(
            @Parameter(description = "EST Profile or RA Profile name") @PathVariable(required = false) String label);

    @Operation(operationId = "estSimpleEnroll", summary = "Enroll a client",
            externalDocs = @ExternalDocumentation(description = "RFC 7030, section 4.2.1",
                    url = "https://www.rfc-editor.org/rfc/rfc7030#section-4.2.1"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issued certificate",
                    content = @Content(mediaType = EstMediaTypes.PKCS7_CERTS_ONLY,
                            schema = @Schema(type = "string", description = "Base64-encoded certs-only CMS"))),
            @ApiResponse(responseCode = "202",
                    description = "The certificate is not issued yet; repeat the same request later",
                    headers = @Header(name = "Retry-After", description = "Seconds to wait before repeating",
                            schema = @Schema(type = "integer"))),
            @ApiResponse(responseCode = "400",
                    description = "The request is not a base64-encoded DER PKCS#10 certification request, its signature does not verify, or it does not meet the policy of the RA Profile; the last answer adds the reason from the policy",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401",
                    description = "Authentication is required, or under certificateRegistration the request does not match an active certificate registration",
                    headers = @Header(name = "WWW-Authenticate", description = "Basic challenge",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403",
                    description = "The client certificate is not accepted, the request does not match the client certificate, or the certificate request was rejected",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "No EST service for the label (Not found.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "405",
                    description = "The operation does not accept the HTTP method (The method is not allowed for this operation.)",
                    headers = @Header(name = "Allow", description = "The method the operation accepts",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "413", description = "The request is too large.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "415", description = "The content type must be application/pkcs10.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "The request could not be processed.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "503",
                    description = "The service is temporarily unavailable, or the certificate could not be issued (The certificate could not be issued. Try again later.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))})
    @PostMapping(path = {"/" + EstPaths.SIMPLE_ENROLL, "/{label}/" + EstPaths.SIMPLE_ENROLL},
            consumes = EstMediaTypes.PKCS10)
    ResponseEntity<byte[]> simpleEnroll(
            @Parameter(description = "EST Profile or RA Profile name") @PathVariable(required = false) String label,
            @RequestBody @Schema(description = "Base64-encoded DER PKCS#10 certification request",
                    type = "string") byte[] request);

    @Operation(operationId = "estSimpleReenroll", summary = "Renew or rekey a client certificate",
            description = "The client authenticates with the certificate it renews; the same public key renews it, "
                    + "a new public key rekeys it",
            externalDocs = @ExternalDocumentation(description = "RFC 7030, section 4.2.2",
                    url = "https://www.rfc-editor.org/rfc/rfc7030#section-4.2.2"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issued certificate",
                    content = @Content(mediaType = EstMediaTypes.PKCS7_CERTS_ONLY,
                            schema = @Schema(type = "string", description = "Base64-encoded certs-only CMS"))),
            @ApiResponse(responseCode = "202",
                    description = "The certificate is not issued yet; repeat the same request later",
                    headers = @Header(name = "Retry-After", description = "Seconds to wait before repeating",
                            schema = @Schema(type = "integer"))),
            @ApiResponse(responseCode = "400",
                    description = "The request is not a base64-encoded DER PKCS#10 certification request, its signature does not verify, it changes the subject name, or it does not meet the policy of the RA Profile; the last answer adds the reason from the policy",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401",
                    description = "Authentication is required, or under certificateRegistration the request does not match an active certificate registration",
                    headers = @Header(name = "WWW-Authenticate", description = "Basic challenge",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403",
                    description = "The client certificate is not accepted, the request does not match the client certificate, the certificate request was rejected, renewal with the same key or with a new key is not allowed, or the certificate is not yet due for renewal",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "No EST service for the label (Not found.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "409", description = "The certificate already has a pending renewal",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "405",
                    description = "The operation does not accept the HTTP method (The method is not allowed for this operation.)",
                    headers = @Header(name = "Allow", description = "The method the operation accepts",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "413", description = "The request is too large.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "415", description = "The content type must be application/pkcs10.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "The request could not be processed.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "503",
                    description = "The service is temporarily unavailable, or the certificate could not be issued (The certificate could not be issued. Try again later.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))})
    @PostMapping(path = {"/" + EstPaths.SIMPLE_REENROLL, "/{label}/" + EstPaths.SIMPLE_REENROLL},
            consumes = EstMediaTypes.PKCS10)
    ResponseEntity<byte[]> simpleReenroll(
            @Parameter(description = "EST Profile or RA Profile name") @PathVariable(required = false) String label,
            @RequestBody @Schema(description = "Base64-encoded DER PKCS#10 certification request",
                    type = "string") byte[] request);

    @Operation(operationId = "estServerKeyGeneration", summary = "Enroll with a server-generated key",
            description = "The certificate request supplies the subject and requested extensions; its public key and "
                    + "signature are ignored. The private key is returned without additional encryption. The "
                    + "operation never answers 202: it waits for the certificate and answers 503 when it is not "
                    + "issued in time",
            externalDocs = @ExternalDocumentation(description = "RFC 7030, section 4.4",
                    url = "https://www.rfc-editor.org/rfc/rfc7030#section-4.4"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Private key and issued certificate",
                    content = @Content(mediaType = EstMediaTypes.MULTIPART_MIXED,
                            schema = @Schema(type = "string",
                                    description = "Base64-encoded PKCS#8 private key part and certs-only CMS part"))),
            @ApiResponse(responseCode = "400",
                    description = "The request is not a base64-encoded DER PKCS#10 certification request, it does not meet the policy of the RA Profile (the answer adds the reason), or it asks for an encrypted key (Encrypted key delivery is not supported.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "401",
                    description = "Authentication is required, or under certificateRegistration the request does not match an active certificate registration",
                    headers = @Header(name = "WWW-Authenticate", description = "Basic challenge",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "403",
                    description = "The client certificate is not accepted, the request does not match the client certificate, the certificate request was rejected, the EST Profile does not generate keys (Server-side key generation is not enabled.), or the RA Profile asks for an approval (Server-side key generation is not available for this route.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "No EST service for the label (Not found.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "405",
                    description = "The operation does not accept the HTTP method (The method is not allowed for this operation.)",
                    headers = @Header(name = "Allow", description = "The method the operation accepts",
                            schema = @Schema(type = "string")),
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "413", description = "The request is too large.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "415", description = "The content type must be application/pkcs10.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "The request could not be processed.",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "503",
                    description = "The service is temporarily unavailable, the certificate could not be issued (The certificate could not be issued. Try again later.), or it was not issued within the time the client waits (The certificate could not be issued in time.)",
                    content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, schema = @Schema(type = "string")))})
    @PostMapping(path = {"/" + EstPaths.SERVER_KEYGEN, "/{label}/" + EstPaths.SERVER_KEYGEN},
            consumes = EstMediaTypes.PKCS10)
    ResponseEntity<byte[]> serverKeyGeneration(
            @Parameter(description = "EST Profile or RA Profile name") @PathVariable(required = false) String label,
            @RequestBody @Schema(description = "Base64-encoded DER PKCS#10 certification request",
                    type = "string") byte[] request);
}
