package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.connector.compliance.ComplianceGroupsResponseDto;
import com.czertainly.api.model.connector.compliance.ComplianceRequestDto;
import com.czertainly.api.model.connector.compliance.ComplianceResponseDto;
import com.czertainly.api.model.connector.compliance.ComplianceRulesResponseDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Sync interface for v1 Compliance API client operations.
 * This interface is implemented by both REST and MQ clients.
 */
public interface ComplianceSyncApiClient {

    List<ComplianceRulesResponseDto> getComplianceRules(ApiClientConnectorInfo connector, String kind, List<String> certificateType) throws ConnectorException;

    List<ComplianceGroupsResponseDto> getComplianceGroups(ApiClientConnectorInfo connector, String kind) throws ConnectorException;

    List<ComplianceRulesResponseDto> getComplianceGroupRules(ApiClientConnectorInfo connector, String kind, String uuid) throws ConnectorException;

    ComplianceResponseDto checkCompliance(ApiClientConnectorInfo connector, String kind, ComplianceRequestDto requestDto) throws ConnectorException;
}
