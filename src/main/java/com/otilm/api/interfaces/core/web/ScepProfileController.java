package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.scep.ScepProfileEditRequestDto;
import com.otilm.api.model.client.scep.ScepProfileRequestDto;
import com.otilm.api.model.common.BulkActionMessageDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.certificate.CertificateDto;
import com.otilm.api.model.core.scep.ScepProfileDetailDto;
import com.otilm.api.model.core.scep.ScepProfileDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/scepProfiles")
@Tag(name = "SCEP Profile Management", description = "SCEP Profile Management API")
public interface ScepProfileController extends AuthProtectedController {

	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------
	// List and Detail
	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------

	@Operation(
			summary = "Get list of SCEP Profiles"
	)
	@ApiResponses(
			value = { @ApiResponse(responseCode = "200", description = "SCEP Profile list retrieved")}
	)
	@GetMapping(
			produces = {"application/json"}
	)
	List<ScepProfileDto> listScepProfiles();


	@Operation(
			summary = "Get details of SCEP Profile"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "SCEP Profile details retrieved"),
					@ApiResponse(responseCode = "404", description = "SCEP Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
			}
	)
	@GetMapping(
			path = "/{uuid}",
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	ScepProfileDetailDto getScepProfile(
			@Parameter(description = "SCEP Profile UUID") @PathVariable String uuid
	) throws NotFoundException;

	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------
	// Create and Update
	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------

	@Operation(
			summary = "Create SCEP Profile"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "201", description = "SCEP Profile created"),
					@ApiResponse(responseCode = "404", description = "SCEP Profile, Certificate or RA Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
			}
	)
	@PostMapping(
			consumes = { "application/json" },
			produces = { "application/json" }
	)
	ResponseEntity<ScepProfileDetailDto> createScepProfile(
			@RequestBody @Valid ScepProfileRequestDto request
	) throws AlreadyExistException, ValidationException, ConnectorException, AttributeException, NotFoundException;


	@Operation(
			summary = "Edit SCEP Profile"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "SCEP Profile updated"),
					@ApiResponse(responseCode = "404", description = "SCEP Profile, Certificate or RA Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
			}
	)
	@PutMapping(
			path="/{uuid}",
			consumes = { "application/json" },
			produces = { "application/json" }
	)
	ScepProfileDetailDto editScepProfile(
			@Parameter(description = "SCEP Profile UUID") @PathVariable String uuid,
			@RequestBody @Valid ScepProfileEditRequestDto request
	) throws ConnectorException, AttributeException, NotFoundException;

	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------
	// Delete
	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------

	@Operation(
			summary = "Delete SCEP Profile"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "204", description = "SCEP Profile deleted"),
					@ApiResponse(responseCode = "404", description = "SCEP Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
			}
	)
	@DeleteMapping(
			path="/{uuid}",
			produces = { "application/json" }
	)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteScepProfile(
			@Parameter(description = "SCEP Profile UUID") @PathVariable String uuid
	) throws NotFoundException, ValidationException;


	@Operation(
			summary = "Delete multiple SCEP Profiles"
	)
	@ApiResponses(
			value = { @ApiResponse(responseCode = "200", description = "SCEP Profiles deleted") }
	)
	@DeleteMapping(
			path = "/delete",
			consumes = { "application/json" },
			produces = { "application/json" }
	)
	List<BulkActionMessageDto> bulkDeleteScepProfile(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "SCEP Profile UUIDs",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
							examples={
									@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")
							})) @RequestBody List<String> uuids
	);

	@Operation(
			summary = "Force delete multiple SCEP Profiles"
	)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "SCEP Profiles forced to delete"),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
							examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))
	})
	@DeleteMapping(
			path = "/delete/force",
			produces = {"application/json"}
	)
	List<BulkActionMessageDto> forceDeleteScepProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "SCEP Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
			examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids
	);


	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------
	// Enable
	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------

	@Operation(
			summary = "Enable SCEP Profile"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "204", description = "SCEP Profile enabled"),
					@ApiResponse(responseCode = "404", description = "SCEP Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
			}
	)
	@PatchMapping(
			path = "/{uuid}/enable",
			produces = { "application/json" }
	)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void enableScepProfile(
			@Parameter(description = "SCEP Profile UUID") @PathVariable String uuid
	) throws NotFoundException;


	@Operation(
			summary = "Enable multiple SCEP Profiles"
	)
	@ApiResponses(
			value = { @ApiResponse(responseCode = "204", description = "SCEP Profiles enabled") }
	)
	@PatchMapping(
			path = "/enable",
			consumes = { "application/json" },
			produces = { "application/json" }
	)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void bulkEnableScepProfile(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "SCEP Profile UUIDs",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
							examples={
									@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")
							})) @RequestBody List<String> uuids);

	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------
	// Disable
	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------
	@Operation(
			summary = "Disable SCEP Profile"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "204", description = "SCEP Profile disabled"),
					@ApiResponse(responseCode = "404", description = "SCEP Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
			}
	)
	@PatchMapping(
			path = "/{uuid}/disable",
			produces = { "application/json" }
	)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void disableScepProfile(
			@Parameter(description = "SCEP Profile UUID") @PathVariable String uuid
	) throws NotFoundException;


	@Operation(
			summary = "Disable multiple SCEP Profile"
	)
	@ApiResponses(
			value = { @ApiResponse(responseCode = "204", description = "SCEP Profiles disabled") }
	)
	@PatchMapping(
			path = "/disable",
			consumes = { "application/json" },
			produces = { "application/json" }
	)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void bulkDisableScepProfile(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(
					description = "SCEP Profile UUIDs",
					content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={
							@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")
					})) @RequestBody List<String> uuids);


	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------
	// RA profile Assignment
	// -----------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------

	@Operation(
			summary = "Update RA Profile for SCEP Profile"
	)
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200", description = "RA Profile updated"),
					@ApiResponse(responseCode = "404", description = "SCEP Profile or RA Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
			}
	)
	@PatchMapping(
			path = "/{uuid}/raProfiles/{raProfileUuid}",
			produces = { "application/json" }
	)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void updateRaProfile(
			@Parameter(description = "SCEP Profile UUID") @PathVariable String uuid,
			@Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid
	) throws NotFoundException;

	@Operation(
			summary = "Get list of certificates eligible for CA certificate of SCEP requests"
	)
	@ApiResponses(
			value = { @ApiResponse(responseCode = "200", description = "List of CA certificates retrieved") }
	)
	@GetMapping(
			path = "/caCertificates",
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	List<CertificateDto> listScepCaCertificates(@Parameter(description = "flag to return certificates that are eligible for Intune integration") @RequestParam boolean intuneEnabled);
}
