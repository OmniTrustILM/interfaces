package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedController;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDetailDto;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDto;
import com.otilm.api.model.core.search.ConfigurableColumnsDocs;
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
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Read-only inventory of cryptographic assets aggregated and deduplicated across every stored CBOM document. Assets
 * enter the inventory only through the document sync, so the surface is deliberately list and detail only — there is no
 * write operation to expose. Listing and the searchable-fields sibling are gated by {@code ResourceAction.LIST} and the
 * detail by {@code ResourceAction.DETAIL} on {@code Resource.CRYPTO_ASSET} — the action set proposed on interfaces#874
 * (the resource itself is ratified); the inventory dashboard on the statistics API deliberately shares the same LIST
 * action.
 *
 * <p>
 * Error responses deliberately use the legacy {@link ErrorMessageDto} model to match the other core web controllers;
 * the platform-wide move to problem-detail responses replaces them together, not one controller at a time.
 */
@RequestMapping("/v1/cryptoAssets")
@Tag(name = "Cryptographic Asset Inventory", description = "Cryptographic Asset Inventory API")
public interface CryptographicAssetController extends AuthProtectedController {

    @Operation(summary = "List cryptographic assets", description = """
            Returns one page of the deduplicated cross-CBOM asset inventory, narrowed by the supplied \
            filters. When no sort is supplied, rows are ordered by name ascending, then UUID ascending, a \
            deterministic default within a deployment. The name ordered on, whether by default or by a \
            `CBOM_ASSET_NAME` sort, is the name the listing serves: the producers' name, else the recorded \
            OID, so an asset known only by its OID sorts among the named ones. Page numbering is positional, \
            so a sync landing between requests can shift rows across page boundaries.

            """ + ConfigurableColumnsDocs.SORT_AND_COLUMNS + ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cryptographic assets"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)),
                            examples = {@ExampleObject(value = "[\"Error Message 1\",\"Error Message 2\"]")}))})
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    PaginationResponseDto<CryptographicAssetDto> listCryptographicAssets(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    schema = @Schema(implementation = SearchRequestDto.class),
                    examples = {
                            @ExampleObject(name = "With ordering and columns",
                                    value = """
                                            {
                                              "pageNumber": 1,
                                              "itemsPerPage": 10,
                                              "filters": [],
                                              "sort": {"fieldSource": "property", "fieldIdentifier": "CBOM_ASSET_PQC_VERDICT", "direction": "asc"},
                                              "columns": [
                                                {"fieldSource": "property", "fieldIdentifier": "CBOM_ASSET_NAME"},
                                                {"fieldSource": "property", "fieldIdentifier": "CBOM_ASSET_TYPE"},
                                                {"fieldSource": "property", "fieldIdentifier": "CBOM_ASSET_PQC_VERDICT"},
                                                {"fieldSource": "property", "fieldIdentifier": "CBOM_ASSET_SOURCE_COUNT"}
                                              ]
                                            }""")})) @Valid @RequestBody SearchRequestDto request);

    @Operation(summary = "Cryptographic asset detail",
            description = "Returns the asset with its verdict provenance, its normalized properties, the elected "
                    + "representative payload beside the source CBOM documents that reference it — per-source "
                    + "payloads and occurrence evidence included — and the object identifiers producers recorded "
                    + "for it, refuted ones included.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cryptographic asset details retrieved"),
            @ApiResponse(responseCode = "404", description = "Cryptographic asset not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))})
    @GetMapping(path = "/{uuid}", produces = {MediaType.APPLICATION_JSON_VALUE})
    CryptographicAssetDetailDto getCryptographicAsset(
            @Parameter(description = "Cryptographic asset UUID") @PathVariable UUID uuid) throws NotFoundException;

    @Operation(operationId = "getCryptographicAssetSearchableFields",
            summary = "Get cryptographic asset searchable fields information", description = """
                    Returns the fields the list operation accepts in its filters, grouped by field source. Only the \
                    fields listed here are filterable; the platform's internal deduplication keys are never offered as \
                    fields.

                    """ + ConfigurableColumnsDocs.CATALOGUE_FLAGS)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Cryptographic asset searchable field information retrieved")})
    @GetMapping(path = "/search", produces = {MediaType.APPLICATION_JSON_VALUE})
    List<SearchFieldDataByGroupDto> getSearchableFieldInformation();
}
