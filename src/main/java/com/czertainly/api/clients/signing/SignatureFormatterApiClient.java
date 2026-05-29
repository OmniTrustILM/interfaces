package com.czertainly.api.clients.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.signing.SignatureFormatterSyncApiClient;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.FormatResponseRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.FormattedResponseDto;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class SignatureFormatterApiClient extends BaseApiClient implements SignatureFormatterSyncApiClient {

    private static final String ATTRIBUTES_CONTEXT = "/v1/signatureProvider/formatting/attributes";
    private static final String DTBS_CONTEXT = "/v1/signatureProvider/formatting/formatDtbs";
    private static final String RESPONSE_CONTEXT = "/v1/signatureProvider/formatting/formatResponse";

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

    @Override
    public FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, FormatDtbsRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + DTBS_CONTEXT)
                .body(Mono.just(requestDto), FormatDtbsRequestDto.class)
                .retrieve()
                .toEntity(FormatDtbsResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, FormatResponseRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + RESPONSE_CONTEXT)
                .body(Mono.just(requestDto), FormatResponseRequestDto.class)
                .retrieve()
                .toEntity(FormattedResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }
}
