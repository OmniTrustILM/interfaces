package com.czertainly.api.interfaces.core.web;

import com.czertainly.api.exception.NotFoundException;
import com.czertainly.api.interfaces.AuthProtectedController;
import com.czertainly.api.model.client.certificate.SearchRequestDto;
import com.czertainly.api.model.common.BulkActionMessageDto;
import com.czertainly.api.model.common.ErrorMessageDto;
import com.czertainly.api.model.common.PaginationResponseDto;
import com.czertainly.api.model.core.search.SearchFieldDataByGroupDto;
import com.czertainly.api.model.core.signing.signingrecord.SigningRecordDto;
import com.czertainly.api.model.core.signing.signingrecord.SigningRecordListDto;
import com.czertainly.api.model.core.signing.signingrecord.SigningRecordValidationResultDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/v1/signingRecords")
@Tag(
        name = "Signing Record Management",
        description = "Signing Record Management API. " +
                "Signing Records are produced as a result of digital signing cryptographic operations. They cannot be created or updated directly via this API."
)
@ApiResponses(value = {@ApiResponse(responseCode = "404", description = "Not Found", content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
public interface SigningRecordController extends AuthProtectedController {

    @Operation(operationId = "listSigningRecordSearchableFields", summary = "List search filters for Signing Records")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "List of search filters retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();

    @Operation(operationId = "listSigningRecords", summary = "List of Signing Records")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signing Records retrieved")})
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE}, consumes = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<SigningRecordListDto> listSigningRecords(@RequestBody SearchRequestDto request);

    @Operation(operationId = "getSigningRecord", summary = "Details of a Signing Record")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Signing Record details retrieved")})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    SigningRecordDto getSigningRecord(@Parameter(description = "Signing Record UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(
            operationId = "validateSigningRecord",
            summary = "Validate a Signing Record",
            description = "Triggers a full validation of the Signing Record: cryptographic integrity, " +
                    "certificate chain, and revocation status of all involved certificates at the time of signing. " +
                    "Returns the highest ETSI conformance level that was successfully verified, along with " +
                    "any warnings or errors encountered."
    )
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Validation completed (check 'valid' field for the outcome)")})
    @PostMapping(path = "/{uuid}/validate", produces = {MediaType.APPLICATION_JSON_VALUE})
    SigningRecordValidationResultDto validateSigningRecord(
            @Parameter(description = "Signing Record UUID") @PathVariable UUID uuid
    ) throws NotFoundException;

    // :TODO: Deletion endpoints: need to clarify first the retention/archival policy. Perhaps archive first and delete after period of time?
    // :TODO: Do we need legal hold?
}
