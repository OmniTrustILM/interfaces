package com.otilm.api.interfaces.core.web;

import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.SearchFilterRequestDto;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.core.audit.AuditLogResponseDto;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/auditLogs")
@Tag(name = "Audit Log", description = "Audit Log API")
public interface AuditLogController extends AuthProtectedController {

    @Operation(summary = "List Audit logs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of audit logs"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Content",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(produces = {"application/json"})
    AuditLogResponseDto listAuditLogs(@Valid @RequestBody SearchRequestDto requestDto);

    @Operation(summary = "Export Audit logs")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Export of audit logs")})
    @PostMapping(path = "/export", produces = {"application/json"})
    ResponseEntity<Resource> exportAuditLogs(@RequestBody List<SearchFilterRequestDto> filters);

    @Operation(summary = "Purge Audit logs")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Audit logs purged")})
    @PostMapping(path = "/purge", produces = {"application/json"})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void purgeAuditLogs(@RequestBody List<SearchFilterRequestDto> filters);

    @Operation(operationId = "getAuditLogSearchableFields", summary = "Get Audit logs searchable fields information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Audit logs searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {"application/json"})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();
}
