package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.core.authority.AddEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EditEndEntityRequestDto;
import com.czertainly.api.model.core.authority.EndEntityDto;
import com.czertainly.api.model.core.connector.ConnectorApiClientDto;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class EndEntityApiClient extends BaseApiClient {

    private static final String END_ENTITY_BASE_CONTEXT = "/v1/authorityProvider/authorities/{uuid}/endEntityProfiles/{endEntityProfileName}/endEntities";
    private static final String END_ENTITY_IDENTIFIED_CONTEXT = END_ENTITY_BASE_CONTEXT + "/{endEntityName}";
    private static final String END_ENTITY_RESET_PASSWORD_CONTEXT = END_ENTITY_IDENTIFIED_CONTEXT + "/resetPassword";

    public EndEntityApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    public List<EndEntityDto> listEntities(ConnectorApiClientDto connector, String authorityUuid, String endEntityProfileName) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_BASE_CONTEXT, authorityUuid, endEntityProfileName)
                .retrieve()
                .toEntityList(EndEntityDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public EndEntityDto getEndEntity(ConnectorApiClientDto connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_IDENTIFIED_CONTEXT, authorityUuid, endEntityProfileName, endEntityName)
                .retrieve()
                .toEntity(EndEntityDto.class)
                .block().getBody(),
                request,
                connector);
    }

    public void createEndEntity(ConnectorApiClientDto connector, String authorityUuid, String endEntityProfileName, AddEndEntityRequestDto requestDto) throws ConnectorException {
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

    public void updateEndEntity(ConnectorApiClientDto connector, String authorityUuid, String endEntityProfileName, String endEntityName, EditEndEntityRequestDto requestDto) throws ConnectorException {
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

    public void revokeAndDeleteEndEntity(ConnectorApiClientDto connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE, connector, true);

        processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_IDENTIFIED_CONTEXT, authorityUuid, endEntityProfileName, endEntityName)
                .retrieve()
                .toEntity(Void.class)
                .block().getBody(),
                request,
                connector);
    }

    public void resetPassword(ConnectorApiClientDto connector, String authorityUuid, String endEntityProfileName, String endEntityName) throws ConnectorException {
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
