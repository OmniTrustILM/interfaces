package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.ComplianceSyncApiClient;
import com.czertainly.api.model.connector.compliance.ComplianceGroupsResponseDto;
import com.czertainly.api.model.connector.compliance.ComplianceRequestDto;
import com.czertainly.api.model.connector.compliance.ComplianceResponseDto;
import com.czertainly.api.model.connector.compliance.ComplianceRulesResponseDto;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.net.URI;
import java.util.List;

public class ComplianceApiClient extends BaseApiClient implements ComplianceSyncApiClient {

    private static final String COMPLIANCE_BASE_CONTEXT = "/v1/complianceProvider/{kind}";
    private static final String COMPLIANCE_RULE_GET_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/rules";
    private static final String COMPLIANCE_GROUP_GET_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/groups";
    private static final String COMPLIANCE_GROUP_RULE_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/groups/{uuid}";
    private static final String COMPLIANCE_CONTEXT = COMPLIANCE_BASE_CONTEXT + "/compliance";

    private static final String CERTIFICATE_TYPE_QUERY_HEADER = "certificateType";

    public ComplianceApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }


    @Override
    public List<ComplianceRulesResponseDto> getComplianceRules(ApiClientConnectorInfo connector, String kind, List<String> certificateType) throws ConnectorException {
        URI uri;
        UriBuilder uriBuilder = UriComponentsBuilder.fromUriString(connector.getUrl());
        uriBuilder.path(COMPLIANCE_RULE_GET_CONTEXT.replace("{kind}", kind));

        if(certificateType != null && !certificateType.isEmpty()){
            certificateType.stream()
                    .filter(q -> q != null)
                    .forEach(q -> uriBuilder.queryParam(CERTIFICATE_TYPE_QUERY_HEADER, q));
        }
        uri = uriBuilder.build();
        WebClient.RequestBodySpec request = prepareRequest(HttpMethod.GET, connector, true).uri(uri);

        return processRequest(r -> r
                .retrieve()
                .toEntityList(ComplianceRulesResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }


    @Override
    public List<ComplianceGroupsResponseDto> getComplianceGroups(ApiClientConnectorInfo connector, String kind) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + COMPLIANCE_GROUP_GET_CONTEXT, kind)
                        .retrieve()
                        .toEntityList(ComplianceGroupsResponseDto.class)
                        .block().getBody(),
                request,
                connector);
    }


    @Override
    public List<ComplianceRulesResponseDto> getComplianceGroupRules(ApiClientConnectorInfo connector, String kind, String uuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                        .uri(connector.getUrl() + COMPLIANCE_GROUP_RULE_CONTEXT, kind, uuid)
                        .retrieve()
                        .toEntityList(ComplianceRulesResponseDto.class)
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
