package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.AlreadyExistException;
import com.otilm.api.exception.AttributeException;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.attribute.custom.CustomAttributeCreateRequestDto;
import com.otilm.api.model.client.attribute.custom.CustomAttributeDefinitionDetailDto;
import com.otilm.api.model.client.attribute.custom.CustomAttributeDefinitionDto;
import com.otilm.api.model.client.attribute.custom.CustomAttributeUpdateRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.CustomAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.core.auth.Resource;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/attributes/custom")
@Tag(name = "Custom Attributes", description = "Custom Attributes API")
public interface CustomAttributeController extends AuthProtectedController {
    @Operation(summary = "List Custom Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "list of available Custom Attributes")})
    @GetMapping(produces = {"application/json"})
    List<CustomAttributeDefinitionDto> listCustomAttributes(@RequestParam(required = false) AttributeContentType attributeContentType);

    @Operation(summary = "Custom Attribute details")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom Attribute details retrieved"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}", produces = {"application/json"})
    CustomAttributeDefinitionDetailDto getCustomAttribute(@PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Create Custom Attribute")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Custom Attribute created", content = @Content(schema = @Schema(implementation = UuidDto.class)))})
    @PostMapping(consumes = {"application/json"}, produces = {"application/json"})
    ResponseEntity<CustomAttributeDefinitionDetailDto> createCustomAttribute(@RequestBody CustomAttributeCreateRequestDto request)
            throws AlreadyExistException, AttributeException;

    @Operation(summary = "Edit Custom Attribute")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom Attribute updated"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PutMapping(path = "/{uuid}", consumes = {"application/json"}, produces = {"application/json"})
    CustomAttributeDefinitionDetailDto editCustomAttribute(@Parameter(description = "Attribute UUID") @PathVariable String uuid, @RequestBody CustomAttributeUpdateRequestDto request)
            throws NotFoundException, AttributeException;

    @Operation(summary = "Delete Custom Attribute")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom Attribute deleted"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping(path = "/{uuid}", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCustomAttribute(@Parameter(description = "Custom Attribute UUID") @PathVariable String uuid) throws NotFoundException;


    @Operation(summary = "Enable Custom Attribute")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom Attribute enabled"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/enable", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void enableCustomAttribute(@Parameter(description = "Custom Attribute UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Disable Custom Attribute")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom Attribute disabled"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/disable", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void disableCustomAttribute(@Parameter(description = "Custom Attribute UUID") @PathVariable String uuid) throws NotFoundException;

    @Operation(summary = "Delete multiple Custom Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom Attributes deleted")})
    @DeleteMapping(produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDeleteCustomAttributes(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Attribute UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                              @RequestBody List<String> attributeUuids);

    @Operation(summary = "Enable multiple Custom Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom Attributes enabled")})
    @PatchMapping(path = "/enable", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkEnableCustomAttributes(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Attribute UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                              @RequestBody List<String> attributeUuids);

    @Operation(summary = "Disable multiple Custom Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom Attributes disabled")})
    @PatchMapping(path = "/disable", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDisableCustomAttributes(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Attribute UUIDs", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"c2f685d4-6a3e-11ec-90d6-0242ac120003\",\"b9b09548-a97c-4c6a-a06a-e4ee6fc2da98\"]")}))
                               @RequestBody List<String> attributeUuids);

    @Operation(summary = "Associate Custom Attribute to Resource")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom Attribute associated to the resources"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(path = "/{uuid}/resources", produces = {"application/json"}, consumes = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void updateResources(@Parameter(description = "Custom Attribute UUID", schema = @Schema(implementation = UUID.class)) @PathVariable String uuid, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "List of Resources", content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
            examples = {@ExampleObject(value = "[\"raProfiles\",\"authorities\"]")}))
    @RequestBody List<Resource> resources) throws NotFoundException;

    @Operation(summary = "Get Custom Attributes for a resource")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom Attribute retrieved", content = @Content(array = @ArraySchema(schema = @Schema(implementation = CustomAttribute.class))))})
    @GetMapping(path = "/resources/{resource}", produces = {"application/json"})
    List<CustomAttribute> getResourceCustomAttributes(@Parameter(description = "Resource Name", schema = @Schema(implementation = Resource.class)) @PathVariable Resource resource);

    @Operation(summary = "Get available resources for Custom Attributes")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom Attribute retrieved")})
    @GetMapping(path = "/resources", produces = {"application/json"})
    List<Resource> getResources();

    @Operation(summary = "Update Value of a Custom Attribute for a Resource")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom Attribute value updated"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @PatchMapping(
            path = "/resources/{resourceName}/objects/{objectUuid}/{attributeUuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    List<ResponseAttribute> updateAttributeContentForResource(
            @Parameter(description = "Resource Type") @PathVariable Resource resourceName,
            @Parameter(description = "Object UUID") @PathVariable String objectUuid,
            @Parameter(description = "Custom Attribute UUID") @PathVariable String attributeUuid,
            @RequestBody List<AttributeContent> request
            ) throws NotFoundException, AttributeException;
    @Operation(summary = "Delete Value of a Custom Attribute for a Resource")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom Attribute value deleted"), @ApiResponse(responseCode = "404", description = "Custom attribute not found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @DeleteMapping(
            path = "/resources/{resourceName}/objects/{objectUuid}/{attributeUuid}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    List<ResponseAttribute> deleteAttributeContentForResource(
            @Parameter(description = "Resource Type") @PathVariable Resource resourceName,
            @Parameter(description = "Object UUID") @PathVariable String objectUuid,
            @Parameter(description = "Custom Attribute UUID") @PathVariable String attributeUuid
    ) throws NotFoundException, AttributeException;
}
