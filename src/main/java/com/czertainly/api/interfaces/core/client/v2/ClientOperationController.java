package com.czertainly.api.interfaces.core.client.v2;

import com.czertainly.api.exception.*;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.client.certificate.CancelPendingCertificateRequestDto;
import com.czertainly.api.model.client.certificate.UploadCertificateRequestDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.core.certificate.CertificateDetailDto;
import com.czertainly.api.model.core.v2.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

@RequestMapping("/v2/operations/authorities/{authorityUuid}/raProfiles/{raProfileUuid}")
@Tag(name = "Client Operations v2", description = "Client Operations v2 API")
@ApiResponses(
		value = {
				@ApiResponse(
						responseCode = "404",
						description = "Not Found",
						content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
				),
				@ApiResponse(
						responseCode = "502",
						description = "Connector Error",
						content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
				),
				@ApiResponse(
						responseCode = "503",
						description = "Connector Communication Error",
						content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
				),
		})
public interface ClientOperationController extends AuthProtectedController {

	@Operation(
			summary = "Get issue certificate attributes",
			description = "Return the list of attributes the client must populate when requesting an issuance through this RA profile. The list reflects the certificate authority's current attribute schema."
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes list obtained"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}))})
	@GetMapping(path = "/attributes/issue", produces = {"application/json"})
	List<BaseAttribute> listIssueCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid) throws NotFoundException, ConnectorException;

	@Operation(
			summary = "Validate issue certificate attributes",
			description = "Validate a candidate set of issuance attributes against this RA profile's schema before submitting an issuance request. Returns 422 with a list of error messages when the attributes are not acceptable."
	)
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attributes validated"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/attributes/issue/validate", consumes = {"application/json"}, produces = {"application/json"})
	void validateIssueCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@RequestBody List<RequestAttribute>attributes) throws NotFoundException, ConnectorException, ValidationException;

	@Operation(
			summary = "Issue an existing certificate request",
			description = "Trigger issuance for a certificate already in state `REQUESTED`. Used after the certificate request has been created (typically via a protocol such as ACME, SCEP, or CMP) and any approval and compliance flows have completed."
	)
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate issued"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/certificates/{certificateUuid}/issue", produces = {"application/json"})
	ClientCertificateDataResponseDto issueRequestedCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid) throws ConnectorException, CertificateException, NoSuchAlgorithmException, AlreadyExistException, CertificateRequestException, NotFoundException;

	@Operation(
			summary = "Issue certificate",
			description = "Submit a new certificate signing request and trigger issuance through this RA profile. If the issuance is asynchronous, the certificate is returned in state `PENDING_ISSUE` and must be finalized once it has been issued."
	)
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate issued"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/certificates", consumes = {"application/json"}, produces = {"application/json"})
	ClientCertificateDataResponseDto issueCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@RequestBody ClientCertificateSignRequestDto request) throws NotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException, CertificateOperationException, CertificateRequestException;

	@Operation(
			summary = "Renew certificate",
			description = "Renew an existing certificate using the same key pair. The original certificate stays in state `ISSUED`; the renewed certificate is returned and, if its issuance is asynchronous, ends in state `PENDING_ISSUE` until it is finalized."
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Certificate renewed"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/certificates/{certificateUuid}/renew", consumes = {"application/json"}, produces = {"application/json"})
	ClientCertificateDataResponseDto renewCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
			@RequestBody ClientCertificateRenewRequestDto request) throws NotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException, CertificateOperationException, CertificateRequestException;

	@Operation(
			summary = "Rekey Certificate",
			description = """
					The rekey operation is used to request a new certificate with a new key pair.
					The new certificate will be issued with the same subject and attributes as the original certificate,
					but with a new public key. Therefore, new certificate signing request (CSR) with new key pair needs
					to be provided, or new key pair managed by the platform needs to be selected. When the same key pair
					is used, or the subject is changed, the rekey operation will be rejected.
					"""
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Certificate regenerated"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/certificates/{certificateUuid}/rekey", consumes = {"application/json"}, produces = {"application/json"})
	ClientCertificateDataResponseDto rekeyCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
			@RequestBody ClientCertificateRekeyRequestDto request) throws NotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException, CertificateOperationException, CertificateRequestException;

	@Operation(
			summary = "Get revocation attributes",
			description = "Return the list of attributes the client must populate when revoking a certificate through this RA profile."
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes obtained") })
	@GetMapping(path = "/attributes/revoke", produces = {"application/json"})
	List<BaseAttribute> listRevokeCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid) throws ConnectorException, NotFoundException;

	@Operation(
			summary = "Validate revocation attributes",
			description = "Validate a candidate set of revocation attributes against this RA profile's schema before submitting a revocation request."
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes validated")})
	@PostMapping(path = "/attributes/revoke/validate", consumes = {"application/json"}, produces = {"application/json"})
	void validateRevokeCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@RequestBody List<RequestAttribute>attributes) throws ConnectorException, ValidationException, NotFoundException;

	@Operation(
			summary = "Revoke certificate",
			description = "Revoke a certificate currently in state `ISSUED`. If the revocation is asynchronous, the certificate moves to state `PENDING_REVOKE` and must be confirmed once the revocation has been performed."
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Certificate revoked")})
	@PostMapping(path = "/certificates/{certificateUuid}/revoke", consumes = {"application/json"}, produces = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void revokeCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
			@RequestBody ClientCertificateRevocationDto request) throws ConnectorException, AttributeException, NotFoundException;

	@Operation(
			summary = "Finalize an asynchronous certificate issuance",
			description = """
					Upload an issued certificate to finalize a certificate currently in
					state `PENDING_ISSUE`. On success, the certificate transitions to `ISSUED`
					and is pushed to any pre-associated locations.

					Validation:
					- the certificate request's public key must match the uploaded certificate's
					  public key (mandatory);
					- the certificate request's subject DN should match the uploaded certificate's
					  subject DN (warning on mismatch — some CAs canonicalise the DN);
					- the uploaded certificate must be verifiable against the certificate
					  authority associated with the RA profile (mandatory).

					Request body carries a Base64-encoded single certificate and an optional list
					of certificate-level custom attributes.
					"""
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Certificate finalized"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Certificate is not in PENDING_ISSUE state\",\"Public key mismatch with certificate request\"]")}))})
	@PostMapping(path = "/certificates/{certificateUuid}/issue/finalize", consumes = {"application/json"}, produces = {"application/json"})
	CertificateDetailDto manuallyIssueCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
			@RequestBody UploadCertificateRequestDto request)
			throws NotFoundException, CertificateException, AlreadyExistException, ConnectorException, AttributeException;

	@Operation(
			summary = "Confirm an asynchronous certificate revocation",
			description = """
					Confirm that a revoked certificate has been revoked. The certificate
					must be in state `PENDING_REVOKE`. The platform applies the destroy-key flag and
					revoke attributes from the original revoke request, transitions the certificate
					to `REVOKED`, and clears the data carried over from the original request.
					"""
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Revocation confirmed"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Certificate is not in PENDING_REVOKE state\"]")}))})
	@PostMapping(path = "/certificates/{certificateUuid}/revoke/confirm")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void manuallyConfirmRevoke(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid)
			throws NotFoundException;

	@Operation(
			summary = "Cancel a pending certificate operation",
			description = """
					State-aware cancel for a certificate currently in state `PENDING_ISSUE` or
					`PENDING_REVOKE`. Transitions:

					- `PENDING_ISSUE` → `FAILED`
					- `PENDING_REVOKE` → `ISSUED`

					The optional `reason` from the request body is recorded in the certificate
					event history.

					If the underlying operation cannot be aborted (for example, it has
					progressed beyond a point where cancellation is possible), the response is
					`422 Unprocessable Entity` with the reason, and the certificate **stays in
					its pending state**. In that case the operation can still be resolved by
					waiting for it to complete and then finalizing or confirming
					the certificate, or by retrying the cancel later if circumstances change.
					"""
	)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Cancellation completed"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Certificate is not in a pending state\",\"Authority refused to cancel: CA does not support cancellation\"]")}))})
	@PostMapping(path = "/certificates/{certificateUuid}/cancel", consumes = {"application/json"}, produces = {"application/json"})
	CertificateDetailDto cancelPendingCertificateOperation(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
			@RequestBody(required = false) CancelPendingCertificateRequestDto request)
			throws NotFoundException;

}
