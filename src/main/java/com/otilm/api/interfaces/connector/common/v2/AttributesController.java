package com.otilm.api.interfaces.connector.common.v2;

import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackRequestDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackResponseDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeDefinitionsDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

/**
 * Attributes v2 API — the common NG provider interface implemented once per NG connector, alongside
 * /v2/info and /v2/health. Exposes the attribute-definition registry and the standard callback surface.
 * Endpoints are interface-agnostic and keyed by connector-global attribute UUID.
 *
 * <p><b>"v2" here is the common-interface / NG generation version</b> (this controller sits with
 * {@code InfoController}/{@code HealthController}/{@code MetricsController} in {@code connector.common.v2}),
 * <b>not</b> attribute schema v2. The attribute payloads it exchanges use the independent attribute
 * schema axis (v2/v3). See {@code AttributeDefinitionsDto} for the full axis explanation.
 *
 * <p><b>Registry conformance.</b> One attribute UUID maps to exactly one definition across all
 * attribute types, for a given connector build. Definitions carry static default content only —
 * dynamic content is produced by the callback. A GROUP definition is returned as the group itself,
 * never its children.
 *
 * <p><b>Connector startup self-validation.</b> At startup the connector validates its own registry —
 * UUIDs are unique across attribute types, and every attribute named by another attribute's trigger
 * declaration is itself dispatchable — and fails fast if the registry is inconsistent.
 */
@RequestMapping("/v2/attributes")
@Tag(
        name = "Connector Attributes v2",
        description = "Attribute-definition registry and stateless callback surface for NG connectors."
)
public interface AttributesController extends AuthProtectedConnectorController {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "List attribute definitions",
            description = "Returns the connector's attribute-definition registry. With the optional uuids "
                    + "parameter, returns only the matching definitions (found ones); otherwise returns the full registry."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attribute definitions retrieved successfully")
    })
    AttributeDefinitionsDto listDefinitions(
            @Parameter(
                    description = "Connector-global attribute UUIDs to look up. Repeat the parameter per UUID.",
                    explode = Explode.TRUE
            )
            @RequestParam(name = "uuids", required = false) List<UUID> uuids);

    @GetMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Get an attribute definition",
            description = "Returns a single attribute definition by its connector-global UUID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attribute definition retrieved successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Attribute definition not found for the given UUID",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )
    })
    BaseAttribute getDefinition(@PathVariable UUID uuid);

    @PostMapping(
            value = "/callback",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Execute an attribute callback",
            description = "Resolves dynamic attribute content for the attribute identified in the request, "
                    + "using the supplied scope and current attribute values."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Callback executed successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Attribute definition referenced by the callback was not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Callback request failed validation at the connector",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetailExtended.class)
                    )
            )
    })
    AttributeCallbackResponseDto callback(@Valid @RequestBody AttributeCallbackRequestDto request);
}
