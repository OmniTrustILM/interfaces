package com.czertainly.api.clients.secret;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.secrets.*;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.TrustManager;
import java.util.List;

public class SecretApiClient extends BaseApiClient {

    private static final String SECRET_BASE_PATH = "/v1/secretProvider/secrets";

    public SecretApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        super(webClient, defaultTrustManagers);
    }

    public List<BaseAttribute> getSecretAttributes(ApiClientConnectorInfo connector, SecretType secretType) throws ConnectorException {
        return processRequest(
                type -> prepareRequest(HttpMethod.GET, connector, true)
                        .uri(connector.getUrl() + SECRET_BASE_PATH + "/" + type.getCode() + "/attributes")
                        .retrieve()
                        .bodyToFlux(BaseAttribute.class)
                        .collectList()
                        .block(),
                secretType,
                connector
        );
    }

    public SecretContentResponseDto getSecretContent(ApiClientConnectorInfo connector, SecretRequestDto request, String version) throws ConnectorException {
        return processRequest(
                req -> {
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder
                            .fromUriString(connector.getUrl() + SECRET_BASE_PATH + "/content");
                    if (version != null) {
                        uriBuilder.queryParam("version", version);
                    }

                    return prepareRequest(HttpMethod.POST, connector, true)
                            .uri(uriBuilder.build().toUri())
                            .bodyValue(req)
                            .retrieve()
                            .bodyToMono(SecretContentResponseDto.class)
                            .block();
                },
                request,
                connector
        );
    }

    public SecretResponseDto createSecret(ApiClientConnectorInfo connector, CreateSecretRequestDto request) throws ConnectorException {
        return processRequest(
                req -> prepareRequest(HttpMethod.POST, connector, true)
                        .uri(connector.getUrl() + SECRET_BASE_PATH)
                        .bodyValue(req)
                        .retrieve()
                        .bodyToMono(SecretResponseDto.class)
                        .block(),
                request,
                connector
        );
    }

    public SecretResponseDto updateSecret(ApiClientConnectorInfo connector, UpdateSecretRequestDto request) throws ConnectorException {
        return processRequest(
                req -> prepareRequest(HttpMethod.PUT, connector, true)
                        .uri(connector.getUrl() + SECRET_BASE_PATH)
                        .bodyValue(req)
                        .retrieve()
                        .bodyToMono(SecretResponseDto.class)
                        .block(),
                request,
                connector
        );
    }

    public void deleteSecret(ApiClientConnectorInfo connector, SecretRequestDto request) throws ConnectorException {
        processRequest(
                req -> prepareRequest(HttpMethod.DELETE, connector, true)
                        .uri(connector.getUrl() + SECRET_BASE_PATH)
                        .bodyValue(req)
                        .retrieve()
                        .toBodilessEntity()
                        .block(),
                request,
                connector
        );
    }

    public List<BaseAttribute> getRotateAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        return processRequest(
                c -> prepareRequest(HttpMethod.GET, c, true)
                        .uri(c.getUrl() + SECRET_BASE_PATH + "/rotate/attributes")
                        .retrieve()
                        .bodyToFlux(BaseAttribute.class)
                        .collectList()
                        .block(),
                connector,
                connector
        );
    }

    public SecretResponseDto rotateSecret(ApiClientConnectorInfo connector, SecretRequestDto request) throws ConnectorException {
        return processRequest(
                req -> prepareRequest(HttpMethod.POST, connector, true)
                        .uri(connector.getUrl() + SECRET_BASE_PATH + "/rotate")
                        .bodyValue(req)
                        .retrieve()
                        .bodyToMono(SecretResponseDto.class)
                        .block(),
                request,
                connector
        );
    }
}
