package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.EndEntitySyncApiClient;
import com.czertainly.api.model.core.authority.AddEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EditEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EndEntityDto;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class EndEntityApiClient extends BaseApiClient implements EndEntitySyncApiClient {

    private static final String END_ENTITY_BASE_CONTEXT = "/v1/authorityProvider/authorities/{uuid}/endEntityProfiles/{endEntityProfileName}/endEntities";
    private static final String END_ENTITY_IDENTIFIED_CONTEXT = END_ENTITY_BASE_CONTEXT + "/{endEntityName}";
    private static final String END_ENTITY_RESET_PASSWORD_CONTEXT = END_ENTITY_IDENTIFIED_CONTEXT + "/resetPassword";

    public EndEntityApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<EndEntityDto> listEntities(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_BASE_CONTEXT, authorityUuid, endEntityProfileName)
                .retrieve()
                .toEntityList(EndEntityDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public EndEntityDto getEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_IDENTIFIED_CONTEXT, authorityUuid, endEntityProfileName, endEntityName)
                .retrieve()
                .toEntity(EndEntityDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void createEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, AddEndEntityRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_BASE_CONTEXT, authorityUuid, endEntityProfileName)
                .body(Mono.just(requestDto), AddEndEntityRequestDto.class)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void updateEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName, EditEndEntityRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_IDENTIFIED_CONTEXT, authorityUuid, endEntityProfileName, endEntityName)
                .body(Mono.just(requestDto), EditEndEntityRequestDto.class)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void revokeAndDeleteEndEntity(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_IDENTIFIED_CONTEXT, authorityUuid, endEntityProfileName, endEntityName)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public void resetPassword(ApiClientConnectorInfo connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.PUT, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_RESET_PASSWORD_CONTEXT, authorityUuid, endEntityProfileName, endEntityName)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }
}
