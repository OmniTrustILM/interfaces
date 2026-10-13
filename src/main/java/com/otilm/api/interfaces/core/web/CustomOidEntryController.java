package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.core.oid.CustomOidEntryDetailResponseDto;
import com.otilm.api.model.core.oid.CustomOidEntryListResponseDto;
import com.otilm.api.model.core.oid.CustomOidEntryRequestDto;
import com.otilm.api.model.core.oid.CustomOidEntryUpdateRequestDto;
import com.otilm.api.model.core.oid.OidCategory;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
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

@RequestMapping("/v1/oids")
@Tag(name = "Custom OID Management", description = "Custom OID Management API")
public interface CustomOidEntryController extends AuthProtectedController {

    @Operation(summary = "Create a new custom OID entry")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom OID entry created")})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CustomOidEntryDetailResponseDto createCustomOidEntry(@Valid @RequestBody CustomOidEntryRequestDto requestDto);

    @Operation(summary = "Get custom OID entry by OID")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom OID entry retrieved")})
    @GetMapping(path = "/{oid}", produces = MediaType.APPLICATION_JSON_VALUE)
    CustomOidEntryDetailResponseDto getCustomOidEntry(
            @Parameter(description = "OID identifier") @PathVariable String oid) throws NotFoundException;

    @Operation(summary = "Edit an existing custom OID entry")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom OID entry updated")})
    @PutMapping(path = "/{oid}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CustomOidEntryDetailResponseDto editCustomOidEntry(
            @Parameter(description = "OID identifier") @PathVariable String oid,
            @Valid @RequestBody CustomOidEntryUpdateRequestDto updateDto) throws NotFoundException;

    @Operation(summary = "Delete a custom OID entry by OID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom OID entry deleted")})
    @DeleteMapping("/{oid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCustomOidEntry(@Parameter(description = "OID identifier") @PathVariable String oid)
            throws NotFoundException;

    @Operation(summary = "Bulk delete customOID entries")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom OID entries deleted")})
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDeleteCustomOidEntry(@RequestBody List<String> oids);

    @Operation(summary = "List custom OID entries with filtering and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Custom OID entries retrieved"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(path = "/list", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    CustomOidEntryListResponseDto listCustomOidEntries(@Valid @RequestBody SearchRequestDto searchRequestDto);

    @Operation(summary = "List built-in system OID entries, optionally filtered by category")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "System OID entries retrieved")})
    @GetMapping(path = "/system", produces = MediaType.APPLICATION_JSON_VALUE)
    List<CustomOidEntryDetailResponseDto> listSystemOidEntries(@Parameter(
            description = "Optional OID category filter") @RequestParam(required = false) OidCategory category);

    @Operation(operationId = "getCustomOidEntrySearchableFields",
            summary = "Get searchable filter fields for custom OID entries")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Searchable fields retrieved")})
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SearchFieldDataByGroupDto> getSearchableInformation();
}
