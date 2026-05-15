package com.czertainly.api.clients.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.signing.SignatureFormatterSyncApiClient;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.TrustManager;
import java.util.List;

public class SignatureFormatterApiClient extends BaseApiClient implements SignatureFormatterSyncApiClient {

    private static final String ATTRIBUTES_CONTEXT = "/v1/signatureProvider/formatting/attributes";

    public SignatureFormatterApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> {
                    var entity = r.uri(connector.getUrl() + ATTRIBUTES_CONTEXT)
                            .retrieve()
                            .toEntityList(BaseAttribute.class)
                            .block();
                    return entity != null && entity.getBody() != null ? entity.getBody() : List.of();
                },
                request,
                connector);
    }
}
