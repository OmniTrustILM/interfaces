package com.otilm.api.interfaces.connector.v2;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.connector.compliance.v2.*;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequestMapping("/v2/complianceProvider/{kind}")
@Tag(
        name = "Compliance Rules",
        description = "Compliance Provider rules API. " +
                "Used to get the list of rules provided by the connector. " +
                "These rules will be made available for the users to choose from the list." +
                "To check for the compliance of resource objects, the Connector accepts " +
                "content and the list of rule references. Once the values are received, compliance is " +
                "checked based on the rules."
)
public interface ComplianceRuleController extends AuthProtectedConnectorController {
    @GetMapping(
            path = "/rules",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Get list of rules"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Rules retrieved successfully"
                    )
            }
    )
    List<ComplianceRuleResponseDto> getRules(
            @Parameter(description = "Connector kind") @PathVariable String kind,
            @RequestParam(required = false) Resource resource,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String format
    ) throws IOException, NotFoundException;

    @PostMapping(
            path = "/rules",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Get list of rules and groups with rules in one batch"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Rules retrieved successfully"
                    )
            }
    )
    ComplianceRulesBatchResponseDto getRulesBatch(
            @Parameter(description = "Connector kind") @PathVariable String kind,
            @RequestBody @Valid ComplianceRulesBatchRequestDto request
    ) throws IOException, NotFoundException;

    @GetMapping(
            path = "/rules/{ruleUuid}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Get specific rule"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Rule retrieved successfully"
                    )
            }
    )
    ComplianceRuleResponseDto getRule(
            @Parameter(description = "Connector kind") @PathVariable String kind,
            @Parameter(description = "Rule UUID") @PathVariable UUID ruleUuid
    ) throws IOException, NotFoundException;

    @GetMapping(
            path = "/groups",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Get list of groups"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Groups retrieved successfully"
                    )
            }
    )
    List<ComplianceGroupResponseDto> getGroups(
            @Parameter(description = "Connector kind") @PathVariable String kind,
            @RequestParam(required = false) Resource resource
    ) throws IOException, NotFoundException;

    @GetMapping(
            path = "/groups/{groupUuid}",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Get specific group"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Group retrieved successfully"
                    )
            }
    )
    ComplianceGroupResponseDto getGroup(
            @Parameter(description = "Connector kind") @PathVariable String kind,
            @Parameter(description = "Group UUID") @PathVariable UUID groupUuid
    ) throws IOException, NotFoundException;

    @GetMapping(
            path = "/groups/{groupUuid}/rules",
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Operation(
            summary = "Get list of rules for a group"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Rules retrieved successfully"
                    )
            }
    )
    List<ComplianceRuleResponseDto> getGroupRules(@Parameter(description = "Connector kind") @PathVariable String kind,
                                                  @Parameter(description = "Group UUID") @PathVariable UUID groupUuid
    ) throws IOException, NotFoundException;
}
