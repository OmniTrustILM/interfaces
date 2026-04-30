package com.czertainly.api.interfaces.client.v2;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.connector.compliance.v2.*;
import com.czertainly.api.model.core.auth.Resource;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;
import java.util.UUID;

/**
 * Synchronous API client interface for v2 Compliance Provider operations.
 * This interface provides an abstraction layer allowing both REST and MQ implementations.
 */
public interface ComplianceSyncApiClient {

    /**
     * Get compliance rules for the given kind with optional filtering.
     *
     * @param connector Connector to use
     * @param kind Kind of compliance provider
     * @param resource Resource to filter by (optional)
     * @param type Resource type to filter by (optional)
     * @param format Resource format to filter by (optional)
     * @return List of compliance rules
     * @throws ConnectorException If there is an error communicating with the connector
     */
    List<ComplianceRuleResponseDto> getComplianceRules(ApiClientConnectorInfo connector, String kind, Resource resource, String type, String format) throws ConnectorException;

    /**
     * Get a specific compliance rule by UUID.
     *
     * @param connector Connector to use
     * @param kind Kind of compliance provider
     * @param ruleUuid UUID of the rule
     * @return Compliance rule details
     * @throws ConnectorException If there is an error communicating with the connector
     */
    ComplianceRuleResponseDto getComplianceRule(ApiClientConnectorInfo connector, String kind, UUID ruleUuid) throws ConnectorException;

    /**
     * Get compliance rules in batch.
     *
     * @param connector Connector to use
     * @param kind Kind of compliance provider
     * @param requestDto Batch request containing rule and group UUIDs
     * @return Batch response with rules and groups
     * @throws ConnectorException If there is an error communicating with the connector
     */
    ComplianceRulesBatchResponseDto getComplianceRulesBatch(ApiClientConnectorInfo connector, String kind, ComplianceRulesBatchRequestDto requestDto) throws ConnectorException;

    /**
     * Get compliance groups for the given kind with optional filtering.
     *
     * @param connector Connector to use
     * @param kind Kind of compliance provider
     * @param resource Resource to filter by (optional)
     * @return List of compliance groups
     * @throws ConnectorException If there is an error communicating with the connector
     */
    List<ComplianceGroupResponseDto> getComplianceGroups(ApiClientConnectorInfo connector, String kind, Resource resource) throws ConnectorException;

    /**
     * Get a specific compliance group by UUID.
     *
     * @param connector Connector to use
     * @param kind Kind of compliance provider
     * @param groupUuid UUID of the group
     * @return Compliance group details
     * @throws ConnectorException If there is an error communicating with the connector
     */
    ComplianceGroupResponseDto getComplianceGroup(ApiClientConnectorInfo connector, String kind, UUID groupUuid) throws ConnectorException;

    /**
     * Get rules within a compliance group.
     *
     * @param connector Connector to use
     * @param kind Kind of compliance provider
     * @param groupUuid UUID of the group
     * @return List of rules in the group
     * @throws ConnectorException If there is an error communicating with the connector
     */
    List<ComplianceRuleResponseDto> getComplianceGroupRules(ApiClientConnectorInfo connector, String kind, UUID groupUuid) throws ConnectorException;

    /**
     * Check compliance of a resource against defined rules.
     *
     * @param connector Connector to use for compliance check
     * @param kind Kind of compliance provider
     * @param requestDto Compliance check request containing resource data and rules to check
     * @return Compliance response with rule results
     * @throws ConnectorException If there is an error communicating with the connector
     */
    ComplianceResponseDto checkCompliance(ApiClientConnectorInfo connector, String kind, ComplianceRequestDto requestDto) throws ConnectorException;
}
