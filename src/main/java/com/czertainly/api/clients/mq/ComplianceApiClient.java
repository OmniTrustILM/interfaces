package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.ComplianceSyncApiClient;
import com.czertainly.api.model.connector.compliance.ComplianceGroupsResponseDto;
import com.czertainly.api.model.connector.compliance.ComplianceRequestDto;
import com.czertainly.api.model.connector.compliance.ComplianceResponseDto;
import com.czertainly.api.model.connector.compliance.ComplianceRulesResponseDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * MQ-based implementation of v1 Compliance API client.
 */
public class ComplianceApiClient implements ComplianceSyncApiClient {

    private static final String BASE_PATH = "/v1/complianceProvider";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public ComplianceApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<ComplianceRulesResponseDto> getComplianceRules(ApiClientConnectorInfo connector, String kind, List<String> certificateType) throws ConnectorException {
        StringBuilder pathBuilder = new StringBuilder(BASE_PATH).append("/").append(kind).append("/rules");
        if (certificateType != null && !certificateType.isEmpty()) {
            String queryParams = certificateType.stream()
                    .filter(q -> q != null)
                    .map(q -> "certificateType=" + URLEncoder.encode(q, StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
            if (!queryParams.isEmpty()) {
                pathBuilder.append("?").append(queryParams);
            }
        }
        ComplianceRulesResponseDto[] result = proxyClient.sendRequest(connector, pathBuilder.toString(), HTTP_METHOD_GET, null, ComplianceRulesResponseDto[].class);
        return Arrays.asList(result);
    }

    @Override
    public List<ComplianceGroupsResponseDto> getComplianceGroups(ApiClientConnectorInfo connector, String kind) throws ConnectorException {
        String path = BASE_PATH + "/" + kind + "/groups";
        ComplianceGroupsResponseDto[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, ComplianceGroupsResponseDto[].class);
        return Arrays.asList(result);
    }

    @Override
    public List<ComplianceRulesResponseDto> getComplianceGroupRules(ApiClientConnectorInfo connector, String kind, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + kind + "/groups/" + uuid;
        ComplianceRulesResponseDto[] result = proxyClient.sendRequest(connector, path, HTTP_METHOD_GET, null, ComplianceRulesResponseDto[].class);
        return Arrays.asList(result);
    }

    @Override
    public ComplianceResponseDto checkCompliance(ApiClientConnectorInfo connector, String kind, ComplianceRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + kind + "/compliance";
        return proxyClient.sendRequest(connector, path, HTTP_METHOD_POST, requestDto, ComplianceResponseDto.class);
    }

    // Async variant
    public CompletableFuture<ComplianceResponseDto> checkComplianceAsync(ApiClientConnectorInfo connector, String kind, ComplianceRequestDto requestDto) {
        String path = BASE_PATH + "/" + kind + "/compliance";
        return proxyClient.sendRequestAsync(connector, path, HTTP_METHOD_POST, requestDto, ComplianceResponseDto.class);
    }
}
