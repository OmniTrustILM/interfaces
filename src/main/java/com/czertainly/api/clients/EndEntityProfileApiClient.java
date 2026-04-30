package com.czertainly.api.clients;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.EndEntityProfileSyncApiClient;
import com.czertainly.api.model.common.NameAndIdDto;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;
import java.util.List;

public class EndEntityProfileApiClient extends BaseApiClient implements EndEntityProfileSyncApiClient {

    private static final String END_ENTITY_PROFILE_BASE_CONTEXT = "/v1/authorityProvider/authorities/{uuid}/endEntityProfiles";
    private static final String END_ENTITY_PROFILE_IDENTIFIED_CONTEXT = END_ENTITY_PROFILE_BASE_CONTEXT + "/{endEntityProfileId}";
    private static final String END_ENTITY_PROFILE_CERT_PROFILE_CONTEXT = END_ENTITY_PROFILE_IDENTIFIED_CONTEXT + "/certificateprofiles";
    private static final String END_ENTITY_PROFILE_CAS_IN_PROFILE_CONTEXT = END_ENTITY_PROFILE_IDENTIFIED_CONTEXT + "/cas";

    public EndEntityProfileApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<NameAndIdDto> listEndEntityProfiles(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_PROFILE_BASE_CONTEXT, authorityUuid)
                .retrieve()
                .toEntityList(NameAndIdDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<NameAndIdDto> listCertificateProfiles(ApiClientConnectorInfo connector, String authorityUuid, int endEntityProfileId) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_PROFILE_CERT_PROFILE_CONTEXT, authorityUuid, endEntityProfileId)
                .retrieve()
                .toEntityList(NameAndIdDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public List<NameAndIdDto> listCAsInProfile(ApiClientConnectorInfo connector, String authorityUuid, int endEntityProfileId) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + END_ENTITY_PROFILE_CAS_IN_PROFILE_CONTEXT, authorityUuid, endEntityProfileId)
                .retrieve()
                .toEntityList(NameAndIdDto.class)
                .block().getBody(),
                request,
                connector);
    }
}
