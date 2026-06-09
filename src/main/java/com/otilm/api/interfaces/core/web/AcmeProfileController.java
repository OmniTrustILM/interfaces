package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.acme.AcmeProfileEditRequestDto;
import com.otilm.api.model.client.acme.AcmeProfileRequestDto;
import com.otilm.api.model.common.BulkActionMessageDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.core.acme.AcmeProfileDto;
import com.otilm.api.model.core.acme.AcmeProfileListDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/v1/acmeProfiles")
@Tag(name = "ACME Profile Management", description = "ACME Profile Management API")
public interface AcmeProfileController extends AuthProtectedController {

	@Operation(summary = "Get list of ACME Profiles")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profile list retrieved")})
	@GetMapping(produces = {"application/json"})
	List<AcmeProfileListDto> listAcmeProfiles();

	@Operation(summary = "Get details of ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profile details retrieved"), @ApiResponse(responseCode = "404", description = "ACME Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))) })
	@GetMapping(path = "/{uuid}", produces = { "application/json" })
	AcmeProfileDto getAcmeProfile(@Parameter(description = "ACME Profile UUID") @PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Create ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "ACME Profile created"), @ApiResponse(responseCode = "404", description = "RA Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))) })
	@PostMapping(consumes = { "application/json" }, produces = { "application/json" })
	ResponseEntity<UuidDto> createAcmeProfile(@RequestBody @Valid AcmeProfileRequestDto request) throws AlreadyExistException, ValidationException, ConnectorException, AttributeException, NotFoundException;

	@Operation(summary = "Edit ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profile updated"), @ApiResponse(responseCode = "404", description = "ACME Profile or RA profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))) })
	@PutMapping(path="/{uuid}", consumes = { "application/json" }, produces = { "application/json" })
	AcmeProfileDto editAcmeProfile(@Parameter(description = "ACME Profile UUID") @PathVariable String uuid, @RequestBody @Valid AcmeProfileEditRequestDto request) throws ConnectorException, AttributeException, NotFoundException;

	@Operation(summary = "Delete ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "ACME Profile deleted"), @ApiResponse(responseCode = "404", description = "ACME Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))) })
	@DeleteMapping(path="/{uuid}", produces = { "application/json" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteAcmeProfile(@Parameter(description = "ACME Profile UUID") @PathVariable String uuid) throws NotFoundException, ValidationException;

	@Operation(summary = "Enable ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profile enabled"), @ApiResponse(responseCode = "404", description = "ACME Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))) })
	@PatchMapping(path = "/{uuid}/enable", produces = { "application/json" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void enableAcmeProfile(@Parameter(description = "ACME Profile UUID") @PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Disable ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profile disabled"), @ApiResponse(responseCode = "404", description = "ACME Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))) })
	@PatchMapping(path = "/{uuid}/disable", produces = { "application/json" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void disableAcmeProfile(@Parameter(description = "ACME Profile UUID") @PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Enable multiple ACME Profiles")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profiles enabled") })
	@PatchMapping(path = "/enable", consumes = { "application/json" }, produces = { "application/json" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void bulkEnableAcmeProfile(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "ACME Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
			examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);

	@Operation(summary = "Disable multiple ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profiles disabled") })
	@PatchMapping(path = "/disable", consumes = { "application/json" }, produces = { "application/json" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void bulkDisableAcmeProfile(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "ACME Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
			examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);

	@Operation(summary = "Delete multiple ACME Profiles")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profiles deleted") })
	@DeleteMapping(path = "/delete", consumes = { "application/json" }, produces = { "application/json" })
	List<BulkActionMessageDto> bulkDeleteAcmeProfile(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "ACME Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
			examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids);

	@Operation(summary = "Force delete multiple ACME Profiles")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "ACME Profiles forced to delete"),
			@ApiResponse(responseCode = "422", description = "Unprocessible Entity",content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
	@DeleteMapping(path = "/delete/force", produces = {"application/json"})
	List<BulkActionMessageDto> forceDeleteACMEProfiles(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "ACME Profile UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
			examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")})) @RequestBody List<String> uuids) throws NotFoundException, ValidationException;

	@Operation(summary = "Update RA Profile for ACME Profile")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "RA Profile updated"), @ApiResponse(responseCode = "404", description = "RA Profile not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))) })
	@PatchMapping(path = "/{uuid}/raprofile/{raProfileUuid}", produces = { "application/json" })
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void updateRaProfile(@Parameter(description = "ACME Profile UUID") @PathVariable String uuid, @Parameter(description = "RA Profile UUID") @PathVariable String raProfileUuid) throws NotFoundException;
}
