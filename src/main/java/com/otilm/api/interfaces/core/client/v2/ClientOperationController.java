package com.otilm.api.interfaces.core.client.v2;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.core.v2.*;

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

	@Operation(summary = "Get issue Certificate Attributes")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes list obtained"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")}))})
	@GetMapping(path = "/attributes/issue", produces = {"application/json"})
	List<BaseAttribute> listIssueCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid) throws NotFoundException, ConnectorException;

	@Operation(summary = "Validate issue Certificate Attributes")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Attributes validated"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/attributes/issue/validate", consumes = {"application/json"}, produces = {"application/json"})
	void validateIssueCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@RequestBody List<RequestAttribute>attributes) throws NotFoundException, ConnectorException, ValidationException;

	@Operation(summary = "Issue existing certificate with status Requested")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate issued"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/certificates/{certificateUuid}/issue", produces = {"application/json"})
	ClientCertificateDataResponseDto issueRequestedCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid) throws ConnectorException, CertificateException, NoSuchAlgorithmException, AlreadyExistException, CertificateRequestException, NotFoundException;

	@Operation(summary = "Issue Certificate")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Certificate issued"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@PostMapping(path = "/certificates", consumes = {"application/json"}, produces = {"application/json"})
	ClientCertificateDataResponseDto issueCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@RequestBody ClientCertificateSignRequestDto request) throws NotFoundException, CertificateException, IOException, NoSuchAlgorithmException, InvalidKeyException, CertificateOperationException, CertificateRequestException;

	@Operation(summary = "Renew Certificate")
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

	@Operation(summary = "Get revocation Attributes")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes obtained") })
	@GetMapping(path = "/attributes/revoke", produces = {"application/json"})
	List<BaseAttribute> listRevokeCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid) throws ConnectorException, NotFoundException;

	@Operation(summary = "Validate revocation Attributes")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Attributes validated")})
	@PostMapping(path = "/attributes/revoke/validate", consumes = {"application/json"}, produces = {"application/json"})
	void validateRevokeCertificateAttributes(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@RequestBody List<RequestAttribute>attributes) throws ConnectorException, ValidationException, NotFoundException;

	@Operation(summary = "Revoke Certificate")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Certificate revoked")})
	@PostMapping(path = "/certificates/{certificateUuid}/revoke", consumes = {"application/json"}, produces = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void revokeCertificate(
			@Parameter(description = "Authority Instance UUID") @PathVariable String authorityUuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid,
			@Parameter(description = "Certificate UUID") @PathVariable String certificateUuid,
			@RequestBody ClientCertificateRevocationDto request) throws ConnectorException, AttributeException, NotFoundException;

}
