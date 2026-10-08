package com.otilm.api.interfaces.core.client.v2;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.CertificateOperationException;
import com.otilm.api.exception.CertificateRequestException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.certificate.CancelPendingCertificateRequestDto;
import com.otilm.api.model.client.certificate.ManuallyIssueCertificateRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.certificate.CertificateDetailDto;
import com.otilm.api.model.core.v2.AvailableOperationsDto;
import com.otilm.api.model.core.v2.ClientCertificateDataResponseDto;
import com.otilm.api.model.core.v2.ClientCertificateIssueRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRegistrationDto;
import com.otilm.api.model.core.v2.ClientCertificateRekeyRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRenewRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRevocationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Client API for certificate lifecycle operations through an RA profile: issue, renew, rekey, register, revoke,
 * finalize, confirm-revoke, and cancel.
 *
 * <p>
 * The issue, renew, rekey, and revoke operations are asynchronous - the request is validated, persisted, and queued,
 * and the outcome is observed through the certificate's state rather than the HTTP response; register runs inline
 * against the connector; finalize, confirm-revoke, and cancel are the synchronous operator actions that complete or
 * abort a pending operation. See the "Client Operations v2" tag description for the async/state model. For the full
 * certificate state machine, including the registration states, see the certificate lifecycle reference:
 * <a href="https://docs.otilm.com/docs/certificate-key/concept-design/core-components/certificate"> Certificate
 * lifecycle</a>.
 *
 * <p>
 * The state-transition diagram for these operations lives in the "Client Operations v2" tag description, from which it
 * is generated into the API reference.
 */
@RequestMapping("/v2/operations/authorities/{authorityUuid}/raProfiles/{raProfileUuid}")
@Tag(name = "Client Operations v2", description = """
        Certificate lifecycle operations through an RA profile: issue, renew, rekey, register, revoke,
        finalize, confirm-revoke, and cancel.

        The issue, renew, rekey, and revoke operations are asynchronous: the request is validated,
        persisted, and queued; the authority-connector call runs afterwards, and an approval step may
        first move the certificate to
        PENDING_APPROVAL. The HTTP response therefore does not carry the outcome — a 2xx means the request
        was accepted, not that it completed — and the issue/renew/rekey responses never carry the signed
        certificate. Follow an operation by reading the certificate's state, and retrieve the signed
        certificate from the certificate detail once it reaches ISSUED.

        register runs inline against the connector: it reaches REGISTERED synchronously (and may return
        certificate data), or PENDING_REGISTRATION when the connector defers completion.

        A certificate left in PENDING_ISSUE or PENDING_REVOKE is completed either by platform status-polling
        (when the authority advertises it) or by an operator action — issue/finalize, revoke/confirm, or
        cancel. Query availableOperations first to learn whether asynchronous completion and cancellation
        apply to a given authority / RA profile.

        Certificate states driven by this API:

        ```
        Issuance (issueCertificate, renewCertificate, rekeyCertificate,
                  issueExistingCertificate on a REQUESTED cert):
          REQUESTED --> PENDING_ISSUE --> ISSUED       connector completes (synchronously or via polling);
                             |                         manuallyIssueCertificate finalizes it the same way
                             +-- cancel ------------> FAILED
                             +-- connector failure --> FAILED

        Registration (registerCertificate, then issueExistingCertificate on a REGISTERED cert):
          PENDING_REGISTRATION --> REGISTERED --> PENDING_ISSUE --> ISSUED
                   |               (connector 2xx or platform-level; then the issuance flow above)
                   +-- setup failure --> FAILED

        Revocation (revokeCertificate):
          ISSUED --> REVOKED                     authority completes the revocation immediately
          ISSUED --> PENDING_REVOKE --> REVOKED  deferred, then manuallyConfirmRevoke
          PENDING_REVOKE --> ISSUED              cancelPendingCertificateOperation

        Approvals: issue / renew / rekey / revoke may pass through PENDING_APPROVAL first;
                   a rejected issuance ends REJECTED (a pre-registered certificate is restored to REGISTERED),
                   a rejected revocation restores the prior state.
        Terminal (no further transition via this API): REVOKED, REJECTED, FAILED.
        ```
        """)
@ApiResponses(value = {
        @ApiResponse(responseCode = "404", description = "Not Found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "502", description = "Connector Error",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "503", description = "Connector Communication Error",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),})
public interface ClientOperationController extends AuthProtectedController {

    @Operation(summary = "Get issue certificate attributes",
            description = "Return the list of attributes the client must populate when requesting an issuance through this RA profile. The list reflects the certificate authority's current attribute schema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attributes list obtained")})
    @GetMapping(path = "/attributes/issue", produces = {"application/json"})
    List<BaseAttribute> listIssueCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid)
            throws NotFoundException, ConnectorException;

    @Operation(summary = "Validate issue certificate attributes",
            description = "Validate a candidate set of issuance attributes against this RA profile's schema before submitting an issuance request. Returns 422 with a list of error messages when the attributes are not acceptable.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attributes validated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/attributes/issue/validate", consumes = {"application/json"}, produces = {"application/json"})
    void validateIssueCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @RequestBody List<RequestAttribute> attributes)
            throws NotFoundException, ConnectorException, ValidationException;

    @Operation(summary = "Issue an existing certificate (REQUESTED or REGISTERED)", description = """
            Trigger issuance for an existing certificate; behaviour depends on its state:
            - `REQUESTED` (no body): the certificate already carries a CSR (e.g. from an ACME/SCEP/CMP
              protocol layer, or after an approval/compliance cycle); issuance runs against that CSR.
            - `REGISTERED` (body required): a pre-registered certificate is finalized with the operator's
              CSR (sign attributes as the RA profile requires, and the authorization secret only for
              challenge-protected registrations); its registered identity (subject DN, SAN, extensions)
              and connector metadata are preserved.

            Queued and issued asynchronously like `issueCertificate` — track the result through the
            certificate's state.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issuance request accepted"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/certificates/{certificateUuid}/issue", consumes = {"application/json"},
            produces = {"application/json"})
    ClientCertificateDataResponseDto issueExistingCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Issue request body. Required when cert state is REGISTERED (carries the operator's CSR, sign attributes as the RA profile requires, and — for challenge-protected registrations — the authorization secret); must be omitted when cert state is REQUESTED.",
                    required = false) @RequestBody(required = false) @Valid ClientCertificateIssueRequestDto request)
            throws ConnectorException, CertificateException, NoSuchAlgorithmException, AlreadyExistException,
            CertificateRequestException, NotFoundException, AttributeException;

    @Operation(summary = "Issue certificate",
            description = "Submit a new certificate signing request and request issuance through this RA profile. The request is validated, persisted, and queued; issuance runs asynchronously and an approval step may run first. This response returns only the new certificate's UUID — it never carries the signed certificate and does not indicate completion. Track the operation through the certificate's state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Issuance request accepted"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/certificates", consumes = {"application/json"}, produces = {"application/json"})
    ClientCertificateDataResponseDto issueCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @RequestBody ClientCertificateIssueRequestDto request)
            throws NotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            CertificateOperationException, CertificateRequestException;

    @Operation(summary = "Renew certificate",
            description = "Renew a certificate using its existing key pair. The original certificate stays in state `ISSUED`; a new certificate is created, queued, and issued asynchronously. This response returns only the new certificate's UUID — track the result through its state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Renewal request accepted"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/certificates/{certificateUuid}/renew", consumes = {"application/json"},
            produces = {"application/json"})
    ClientCertificateDataResponseDto renewCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
            @RequestBody ClientCertificateRenewRequestDto request)
            throws NotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            CertificateOperationException, CertificateRequestException, ConnectorException, AttributeException;

    @Operation(summary = "Rekey Certificate", description = """
            Request a replacement certificate with a new key pair but the same subject and
            attributes as the original. Provide a new CSR with a new key pair, or select a
            platform-managed key pair; reusing the same key pair, or changing the subject, is
            rejected. Queued and issued asynchronously — track the result through the new
            certificate's state.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rekey request accepted"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")})),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/certificates/{certificateUuid}/rekey", consumes = {"application/json"},
            produces = {"application/json"})
    ClientCertificateDataResponseDto rekeyCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
            @RequestBody ClientCertificateRekeyRequestDto request)
            throws NotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            CertificateOperationException, CertificateRequestException, ConnectorException, AttributeException;

    @Operation(summary = "Get revocation attributes",
            description = "Return the list of attributes the client must populate when revoking a certificate through this RA profile.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attributes obtained")})
    @GetMapping(path = "/attributes/revoke", produces = {"application/json"})
    List<BaseAttribute> listRevokeCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Validate revocation attributes",
            description = "Validate a candidate set of revocation attributes against this RA profile's schema before submitting a revocation request.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attributes validated"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/attributes/revoke/validate", consumes = {"application/json"}, produces = {"application/json"})
    void validateRevokeCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @RequestBody List<RequestAttribute> attributes)
            throws ConnectorException, ValidationException, NotFoundException;

    @Operation(summary = "Get registration attributes",
            description = "Return the list of attributes the client must populate when pre-registering a certificate through this RA profile. The list reflects the certificate authority's register-operation attribute schema.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attributes obtained")})
    @GetMapping(path = "/attributes/register", produces = {"application/json"})
    List<BaseAttribute> listRegisterCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Get renew certificate attributes",
            description = "Return the list of attributes the client must populate when renewing or rekeying a certificate through this RA profile. The list reflects the certificate authority's renew-operation attribute schema; rekey is a renew at the authority, so the same schema applies. An authority that offers no renew schema yields an empty list — this is not an error.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attributes obtained")})
    @GetMapping(path = "/attributes/renew", produces = {"application/json"})
    List<BaseAttribute> listRenewCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Get identify certificate attributes",
            description = "Return the list of attributes the client must populate when identifying a certificate through this RA profile. The list reflects the certificate authority's identify-operation attribute schema. An authority that offers no identify schema yields an empty list — this is not an error.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attributes obtained")})
    @GetMapping(path = "/attributes/identify", produces = {"application/json"})
    List<BaseAttribute> listIdentifyCertificateAttributes(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid)
            throws ConnectorException, NotFoundException;

    @Operation(summary = "Revoke certificate",
            description = "Revoke a certificate currently in state `ISSUED`. The request is accepted and queued; revocation runs through the authority and an approval step may run first. This response is returned before the authority call completes, so it does not indicate completion — the certificate ends in `REVOKED` if the authority completes the revocation immediately, or `PENDING_REVOKE` if completion is deferred and later confirmed. Track the result through its state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "Revoke request accepted and queued — observe the certificate state for the outcome."),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"Cannot perform operation revoke on certificate in state ...\"]")}))})
    @PostMapping(path = "/certificates/{certificateUuid}/revoke", consumes = {"application/json"},
            produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void revokeCertificate(@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
            @RequestBody ClientCertificateRevocationDto request)
            throws ConnectorException, AttributeException, NotFoundException;

    @Operation(summary = "Finalize an asynchronous certificate issuance", description = """
            Finalize a certificate in state `PENDING_ISSUE` by uploading the issued certificate. On
            success it transitions to `ISSUED` and is pushed to any pre-associated locations.

            Validation: the certificate request's public key must match the uploaded certificate
            (mandatory); the subject DN should match (warning only — some CAs canonicalise it); and the
            uploaded certificate must verify against the RA profile's authority (mandatory).

            The body carries a Base64-encoded single certificate, optional certificate-level custom
            attributes, and optional identify-operation attributes for the authority's verification.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificate finalized"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"Certificate is not in PENDING_ISSUE state\",\"Public key mismatch with certificate request\"]")})),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PostMapping(path = "/certificates/{certificateUuid}/issue/finalize", consumes = {"application/json"},
            produces = {"application/json"})
    CertificateDetailDto manuallyIssueCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
            @RequestBody ManuallyIssueCertificateRequestDto request) throws NotFoundException, CertificateException,
            AlreadyExistException, ConnectorException, AttributeException;

    @Operation(summary = "Confirm an asynchronous certificate revocation", description = """
            Confirm that a revoked certificate has been revoked. The certificate
            must be in state `PENDING_REVOKE`. The platform applies the destroy-key flag and
            revoke attributes from the original revoke request, transitions the certificate
            to `REVOKED`, and clears the data carried over from the original request.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Revocation confirmed"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Certificate is not in PENDING_REVOKE state\"]")}))})
    @PostMapping(path = "/certificates/{certificateUuid}/revoke/confirm")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void manuallyConfirmRevoke(@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid) throws NotFoundException;

    @Operation(summary = "Cancel a pending certificate operation", description = """
            State-aware cancel for a certificate in `PENDING_ISSUE` or `PENDING_REVOKE`:

            - `PENDING_ISSUE` → `FAILED`
            - `PENDING_REVOKE` → `ISSUED`

            The optional `reason` is recorded in the certificate event history. If the underlying
            operation can no longer be aborted, the response is `422` and the certificate stays in its
            pending state; it can then be resolved by letting it complete and finalizing/confirming, or
            by retrying the cancel later.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cancellation completed"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = String.class)),
                    examples = {
                            @ExampleObject(
                                    value = "[\"Certificate is not in a pending state\",\"Authority refused to cancel: CA does not support cancellation\"]")}))})
    @PostMapping(path = "/certificates/{certificateUuid}/cancel", consumes = {"application/json"},
            produces = {"application/json"})
    CertificateDetailDto cancelPendingCertificateOperation(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
            @RequestBody(required = false) CancelPendingCertificateRequestDto request) throws NotFoundException;

    @Operation(summary = "Pre-register a certificate", description = """
            Pre-register a certificate to be issued later; the response carries the pre-registered
            certificate's UUID and completion runs through the standard issue flow.

            When the authority's connector supports registration (a v3 connector advertising
            `CERTIFICATE_REGISTRATION`), the registration is made with the upstream CA; otherwise the
            certificate is pre-registered at the platform level with no connector call, which does not imply
            a CA-side end-entity exists. Connector-side completion may be asynchronous and is tracked
            server-side.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration request accepted"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Invalid registration request",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class))))})
    @PostMapping(path = "/certificates/register", consumes = {"application/json"}, produces = {"application/json"})
    ClientCertificateDataResponseDto registerCertificate(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
            @RequestBody @Valid ClientCertificateRegistrationDto request)
            throws NotFoundException, ValidationException, ConnectorException, AttributeException;

    @Operation(summary = "List operations supported by this authority/RA profile", description = """
            Returns per-operation support flags (issue/renew/revoke/register) including whether
            the platform polls for its asynchronous completion and whether each can be cancelled mid-flight. Operators
            use this to drive UI affordances and validate flows before invoking them.
            """)
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Capability advertisement returned")})
    @GetMapping(path = "/availableOperations", produces = {"application/json"})
    AvailableOperationsDto listAvailableOperations(
            @Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
            @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid) throws NotFoundException;

}
