package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.core.oid.*;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    CustomOidEntryDetailResponseDto getCustomOidEntry(@Parameter(description = "OID identifier") @PathVariable String oid) throws NotFoundException;

    @Operation(summary = "Edit an existing custom OID entry")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom OID entry updated")})
    @PutMapping(path = "/{oid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CustomOidEntryDetailResponseDto editCustomOidEntry(
            @Parameter(description = "OID identifier") @PathVariable String oid,
            @Valid @RequestBody CustomOidEntryUpdateRequestDto updateDto) throws NotFoundException;

    @Operation(summary = "Delete a custom OID entry by OID")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom OID entry deleted")})
    @DeleteMapping("/{oid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteCustomOidEntry(@Parameter(description = "OID identifier") @PathVariable String oid) throws NotFoundException;

    @Operation(summary = "Bulk delete customOID entries")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Custom OID entries deleted")})
    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void bulkDeleteCustomOidEntry(@RequestBody List<String> oids);

    @Operation(summary = "List custom OID entries with filtering and pagination")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Custom OID entries retrieved")})
    @PostMapping(path = "/list", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    CustomOidEntryListResponseDto listCustomOidEntries(@RequestBody SearchRequestDto searchRequestDto);

    @Operation(operationId = "getCustomOidEntrySearchableFields", summary = "Get searchable filter fields for custom OID entries")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Searchable fields retrieved")})
    @GetMapping(path = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    List<SearchFieldDataByGroupDto> getSearchableInformation();
}
