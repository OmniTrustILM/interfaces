package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.*;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.DiscoveryResponseDto;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.client.discovery.DiscoveryCertificateResponseDto;
import com.otilm.api.model.client.discovery.DiscoveryDto;
import com.otilm.api.model.client.discovery.DiscoveryHistoryDetailDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import com.otilm.api.model.core.scheduler.ScheduleDiscoveryDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.cert.CertificateException;
import java.util.List;

@RequestMapping("/v1/discoveries")
@Tag(name = "Discovery Management", description = "Discovery Management API")
@ApiResponses(value = {
				@ApiResponse(
						responseCode = "502",
						description = "Connector Error",
						content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
				),
				@ApiResponse(
						responseCode = "503",
						description = "Connector Communication Error",
						content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))
				)
		})

public interface DiscoveryController extends AuthProtectedController {
	
	@Operation(summary = "List Discovery")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "List of available Discoveries")})
	@PostMapping(path = "/list", produces = {"application/json"})
	DiscoveryResponseDto listDiscoveries(@RequestBody SearchRequestDto request);
	
	@Operation(summary = "Discovery Details")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Discovery details retrieved"), @ApiResponse(responseCode = "404", description = "Discovery not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
	@GetMapping(path = "/{uuid}", produces = {"application/json"})
	DiscoveryHistoryDetailDto getDiscovery(@Parameter(description = "Discovery UUID") @PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Discovery Details")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Discovery details retrieved"), @ApiResponse(responseCode = "404", description = "Discovery not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
	@GetMapping(path = "/{uuid}/certificates", produces = {"application/json"})
	DiscoveryCertificateResponseDto getDiscoveryCertificates(
			@Parameter(description = "Discovery UUID") @PathVariable String uuid,
			@RequestParam(required = false) Boolean newlyDiscovered,
			@RequestParam(required = false, defaultValue = "10") int itemsPerPage,
			@RequestParam(required = false, defaultValue = "0") int pageNumber
	) throws NotFoundException;
	
	@Operation(summary = "Create Discovery")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Discovery Created", content = @Content(schema = @Schema(implementation = UuidDto.class))),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")})),
			@ApiResponse(responseCode = "404", description = "Connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
	@PostMapping(consumes = {"application/json"}, produces = {"application/json"})
	ResponseEntity<?> createDiscovery(@RequestBody DiscoveryDto request)
            throws AlreadyExistException, NotFoundException, CertificateException, InterruptedException, ConnectorException, AttributeException;
	
	@Operation(summary = "Delete Discovery")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Discovery deleted"), @ApiResponse(responseCode = "404", description = "Discovery not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
	@DeleteMapping(path = "/{uuid}", produces = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteDiscovery(@Parameter(description = "Discovery UUID") @PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Delete Multiple Discoveries")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Discoveries deleted"), @ApiResponse(responseCode = "404", description = "Discovery not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
	@DeleteMapping(produces = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void bulkDeleteDiscovery(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Discovery UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
			examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
										@RequestBody List<String> discoveryUuids) throws NotFoundException;

	@Operation(operationId = "getDiscoverySearchableFields", summary = "Get Discovery searchable fields information")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Discovery searchable field information retrieved")})
	@GetMapping(path = "/search", produces = {"application/json"})
	List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

	@Operation(summary = "Schedule Discovery")
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Discovery Scheduled", content = @Content(schema = @Schema(implementation = UuidDto.class))),
			@ApiResponse(responseCode = "422", description = "Unprocessable Entity", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
					examples={@ExampleObject(value="[\"Error Message 1\",\"Error Message 2\"]")})),
			@ApiResponse(responseCode = "404", description = "Connector not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
	@PostMapping(path = "/schedule", consumes = {"application/json"}, produces = {"application/json"})
	ResponseEntity<?> scheduleDiscovery(@RequestBody ScheduleDiscoveryDto scheduleDiscoveryDto)
            throws AlreadyExistException, CertificateException, InterruptedException, ConnectorException, SchedulerException, AttributeException, NotFoundException;

}
