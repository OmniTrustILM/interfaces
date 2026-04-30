package com.czertainly.api.clients.signing;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.signing.TimestampingConnectorSyncApiClient;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.signatures.formatter.FormatDtbsResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.FormattedResponseDto;
import com.czertainly.api.model.connector.signatures.formatter.TimestampingFormatDtbsRequestDto;
import com.czertainly.api.model.connector.signatures.formatter.TimestampingFormatResponseRequestDto;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.net.ssl.TrustManager;
import java.util.List;

public class TimestampingConnectorApiClient extends BaseApiClient implements TimestampingConnectorSyncApiClient {

    private static final String ATTRIBUTES_CONTEXT = "/v1/signatureProvider/formatting/attributes";
    private static final String DTBS_CONTEXT = "/v1/signatureProvider/formatting/formatDtbs";
    private static final String RESPONSE_CONTEXT = "/v1/signatureProvider/formatting/formatResponse";

    public TimestampingConnectorApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    @Override
    public List<BaseAttribute> listFormatterAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + ATTRIBUTES_CONTEXT)
                .retrieve()
                .toEntityList(BaseAttribute.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public FormatDtbsResponseDto formatDtbs(ApiClientConnectorInfo connector, TimestampingFormatDtbsRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + DTBS_CONTEXT)
                .body(Mono.just(requestDto), TimestampingFormatDtbsRequestDto.class)
                .retrieve()
                .toEntity(FormatDtbsResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }

    @Override
    public FormattedResponseDto formatSigningResponse(ApiClientConnectorInfo connector, TimestampingFormatResponseRequestDto requestDto) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);

        return processRequest(r -> r
                .uri(connector.getUrl() + RESPONSE_CONTEXT)
                .body(Mono.just(requestDto), TimestampingFormatResponseRequestDto.class)
                .retrieve()
                .toEntity(FormattedResponseDto.class)
                .block().getBody(),
                request,
                connector);
    }
}
