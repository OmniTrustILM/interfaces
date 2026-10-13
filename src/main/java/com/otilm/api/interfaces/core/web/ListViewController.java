package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.listview.ListViewDto;
import com.otilm.api.model.core.listview.ListViewRequestDto;
import com.otilm.api.model.core.listview.ListViewUpdateRequestDto;
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
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/listViews")
@Tag(name = "List View",
        description = "List View API that manages the saved column selections of the logged user. A view belongs to "
                + "one user and one resource; views are never shared between users, so every operation here acts on "
                + "the views of the user the request is authenticated as.")
public interface ListViewController extends AuthProtectedController {

    @Operation(summary = "List views of logged user")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of views")})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    List<ListViewDto> listViews(@Parameter(description = "Return only views of this resource") @RequestParam(
            name = "resource", required = false) Resource resource);

    @Operation(summary = "Create a view for logged user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View created"),
            @ApiResponse(responseCode = "409", description = "View of that name already exists for the resource",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ListViewDto createView(@Valid @RequestBody ListViewRequestDto request) throws AlreadyExistException;

    @Operation(summary = "Edit a view of logged user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "View updated"),
            @ApiResponse(responseCode = "404", description = "View not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409", description = "View of that name already exists for the resource",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PutMapping(path = "/{uuid}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    ListViewDto editView(@Parameter(description = "View UUID") @PathVariable("uuid") String uuid,
            @Valid @RequestBody ListViewUpdateRequestDto request) throws NotFoundException, AlreadyExistException;

    @Operation(summary = "Delete a view of logged user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "View deleted"),
            @ApiResponse(responseCode = "404", description = "View not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping(path = "/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteView(@Parameter(description = "View UUID") @PathVariable("uuid") String uuid) throws NotFoundException;
}
