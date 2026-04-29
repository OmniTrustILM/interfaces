package com.otilm.api.interfaces.connector;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.interfaces.AuthProtectedConnectorController;
import com.otilm.api.model.connector.compliance.ComplianceGroupsResponseDto;
import com.otilm.api.model.connector.compliance.ComplianceRulesResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@RequestMapping("/v1/complianceProvider/{kind}")
@Tag(
        name = "Compliance Rules",
        description = "Compliance Provider rules API. " +
                "Used to get the list of rules provided by the connector. " +
                "These rules will be made available for the users to choose from the list." +
                "To check for the compliance of a certificate, the Connector accepts " +
                "certificate content and the list of rule references. Once the values are received, compliance is " +
                "checked based on the rules."
)
public interface ComplianceRulesController extends AuthProtectedConnectorController {
    @GetMapping(
            path = "/rules",
            produces = {"application/json"}
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
    List<ComplianceRulesResponseDto> getRules(@Parameter(description = "Connector kind") @PathVariable String kind)
            throws IOException, NotFoundException;

    @GetMapping(
            path = "/groups",
            produces = {"application/json"}
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
    List<ComplianceGroupsResponseDto> getGroups(@Parameter(description = "Connector kind") @PathVariable String kind)
            throws IOException, NotFoundException;

    @GetMapping(
            path = "/groups/{uuid}",
            produces = {"application/json"}
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
    List<ComplianceRulesResponseDto> getGroupRules(@Parameter(description = "Connector kind") @PathVariable String kind,
                                                   @Parameter(description = "Group kind") @PathVariable String uuid)
            throws IOException, NotFoundException;
}