package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.core.certificate.group.GroupDto;
import com.otilm.api.model.core.certificate.group.GroupRequestDto;
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

import java.util.List;

@RequestMapping("/v1/groups")
@Tag(name = "Group management", description = "Group management API")
public interface GroupController extends AuthProtectedController {
	@Operation(summary = "List Groups")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "list of available Group")})
	@GetMapping(produces = {"application/json"})
	List<GroupDto> listGroups();
	
	@Operation(summary = "Group details")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Group details retrieved"),
			@ApiResponse(responseCode = "404", description = "Group not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
	})
	@GetMapping(path = "/{uuid}", produces = {"application/json"})
	GroupDto getGroup(@PathVariable String uuid) throws NotFoundException;
	
	@Operation(summary = "Create Group")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Group created", content = @Content(schema = @Schema(implementation = UuidDto.class))),
			@ApiResponse(responseCode = "404", description = "Attribute definition not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
	})
	@PostMapping(consumes = {"application/json"}, produces = {"application/json"})
	ResponseEntity<?> createGroup(@RequestBody GroupRequestDto request)
            throws AlreadyExistException, NotFoundException, AttributeException;
	
	@Operation(summary = "Edit Group")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Group updated"),
			@ApiResponse(responseCode = "404", description = "Group not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
	})
	@PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {"application/json"})
	GroupDto editGroup(@Parameter(description = "Group UUID") @PathVariable String uuid, @RequestBody GroupRequestDto request)
			throws NotFoundException, AttributeException;
	
	@Operation(summary = "Delete Group")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Group deleted"),
			@ApiResponse(responseCode = "404", description = "Group not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
	})
	@DeleteMapping(path = "/{uuid}", produces = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void deleteGroup(@Parameter(description = "Group UUID") @PathVariable String uuid) throws NotFoundException;

	@Operation(summary = "Delete multiple Groups")
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Groups deleted") })
	@DeleteMapping(produces = {"application/json"})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void bulkDeleteGroup(@io.swagger.v3.oas.annotations.parameters.RequestBody(
			description = "Group UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
			examples={@ExampleObject(value="[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
											   @RequestBody List<String> groupUuids);

}
