package com.otilm.api.clients.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.ComplianceSyncApiClient;
import com.otilm.api.model.connector.compliance.v2.*;
import com.otilm.api.model.core.auth.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class ComplianceApiClient extends BaseApiClient implements ComplianceSyncApiClient {

    private static final String COMPLIANCE_BASE_CONTEXT = "/v2/complianceProvider/{kind}";
    private static final String COMPLIANCE_RULES_GET_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/rules";
    private static final String COMPLIANCE_RULE_GET_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/rules/{ruleUuid}";
    private static final String COMPLIANCE_GROUPS_GET_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/groups";
    private static final String COMPLIANCE_GROUP_GET_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/groups/{groupUuid}";
    private static final String COMPLIANCE_GROUP_RULES_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/groups/{groupUuid}/rules";
    private static final String COMPLIANCE_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/compliance";

    private static final String RESOURCE_QUERY_HEADER = "resource";
    private static final String RESOURCE_TYPE_QUERY_HEADER = "type";
    private static final String RESOURCE_FORMAT_QUERY_HEADER = "format";

    public ComplianceApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<ComplianceRuleResponseDto> getComplianceRules(ApiClientConnectorInfo connector, String kind, Resource resource, String type, String format) throws ConnectorException {
        URI uri;
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(connector.getUrl());
        uriBuilder.path(COMPLIANCE_RULES_GET_CONTEXT.replace("{kind}", kind));

        if (resource != null) {
            uriBuilder.queryParam(RESOURCE_QUERY_HEADER, resource.getCode());
        }
        if (type != null) {
            uriBuilder.queryParam(RESOURCE_TYPE_QUERY_HEADER, type);
        }
        if (format != null) {
            uriBuilder.queryParam(RESOURCE_FORMAT_QUERY_HEADER, format);
        }
        uri = uriBuilder.build();
        WebClient.RequestBodySpec request = prepareRequest(HttpMethod.GET, connector, true).uri(uri);

        return processRequest(r -> r
                        .retrieve()
                        .toEntityList(ComplianceRuleResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }

    @Override
    public ComplianceRuleResponseDto getComplianceRule(ApiClientConnectorInfo connector, String kind, UUID ruleUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + COMPLIANCE_RULE_GET_CONTEXT, kind, ruleUuid)
                        .retrieve()
                        .toEntity(ComplianceRuleResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }

    @Override
    public ComplianceRulesBatchResponseDto getComplianceRulesBatch(ApiClientConnectorInfo connector, String kind, ComplianceRulesBatchRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + COMPLIANCE_RULES_GET_CONTEXT, kind)
                        .body(Mono.just(requestDto), ComplianceRulesBatchRequestDto.class)
                        .retrieve()
                        .toEntity(ComplianceRulesBatchResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<ComplianceGroupResponseDto> getComplianceGroups(ApiClientConnectorInfo connector, String kind, Resource resource) throws ConnectorException {
        URI uri;
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(connector.getUrl());
        uriBuilder.path(COMPLIANCE_GROUPS_GET_CONTEXT.replace("{kind}", kind));

        if (resource != null) {
            uriBuilder.queryParam(RESOURCE_QUERY_HEADER, resource.getCode());
        }
        uri = uriBuilder.build();
        WebClient.RequestBodySpec request = prepareRequest(HttpMethod.GET, connector, true).uri(uri);
        return processRequest(r -> r
                        .retrieve()
                        .toEntityList(ComplianceGroupResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }

    @Override
    public ComplianceGroupResponseDto getComplianceGroup(ApiClientConnectorInfo connector, String kind, UUID groupUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + COMPLIANCE_GROUP_GET_CONTEXT, kind, groupUuid)
                        .retrieve()
                        .toEntity(ComplianceGroupResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<ComplianceRuleResponseDto> getComplianceGroupRules(ApiClientConnectorInfo connector, String kind, UUID groupUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + COMPLIANCE_GROUP_RULES_CONTEXT, kind, groupUuid)
                        .retrieve()
                        .toEntityList(ComplianceRuleResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }

    @Override
    public ComplianceResponseDto checkCompliance(ApiClientConnectorInfo connector, String kind, ComplianceRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + COMPLIANCE_CONTEXT, kind)
                        .body(Mono.just(requestDto), ComplianceRequestDto.class)
                        .retrieve()
                        .toEntity(ComplianceResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }
}
