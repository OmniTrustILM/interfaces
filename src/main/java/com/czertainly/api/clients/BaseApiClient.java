package com.czertainly.api.clients;

import com.czertainly.api.exception.*;
import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.attribute.common.AttributeContent;
import com.czertainly.api.model.common.attribute.v2.content.FileAttributeContentV2;
import com.czertainly.api.model.common.error.ProblemDetailExtended;
import com.czertainly.api.model.core.connector.ConnectorStatus;
import com.czertainly.core.util.AttributeDefinitionUtils;
import com.czertainly.core.util.KeyStoreUtils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.*;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

public abstract class BaseApiClient {
    private static final Logger logger = LoggerFactory.getLogger(BaseApiClient.class);

    // Basic auth attribute names
    public static final String ATTRIBUTE_USERNAME = "username";
    public static final String ATTRIBUTE_PASSWORD = "password";

    // Certificate attribute names
    public static final String ATTRIBUTE_KEYSTORE_TYPE = "keyStoreType";
    public static final String ATTRIBUTE_KEYSTORE = "keyStore";
    public static final String ATTRIBUTE_KEYSTORE_PASSWORD = "keyStorePassword";
    public static final String ATTRIBUTE_TRUSTSTORE_TYPE = "trustStoreType";
    public static final String ATTRIBUTE_TRUSTSTORE = "trustStore";
    public static final String ATTRIBUTE_TRUSTSTORE_PASSWORD = "trustStorePassword";

    // API key attribute names
    public static final String ATTRIBUTE_API_KEY_HEADER = "apiKeyHeader";
    public static final String ATTRIBUTE_API_KEY = "apiKey";

    protected WebClient webClient;

    protected TrustManager[] defaultTrustManagers;

    protected BaseApiClient() {

    }

    protected BaseApiClient(WebClient webClient, TrustManager[] defaultTrustManagers) {
        this.webClient = webClient;
        this.defaultTrustManagers = defaultTrustManagers;
    }

    public WebClient.RequestBodyUriSpec prepareRequest(HttpMethod method, ApiClientConnectorInfo connector, boolean validateConnectorStatus) {
        if (validateConnectorStatus) {
            validateConnectorStatus(connector.getStatus());
        }
        WebClient.RequestBodySpec request;

        // for backward compatibility
        if (connector.getAuthType() == null) {
            request = webClient.method(method);
            return (WebClient.RequestBodyUriSpec) request;
        }

        List<ResponseAttribute> authAttributes = connector.getAuthAttributes();

        switch (connector.getAuthType()) {
            case NONE:
                request = webClient.method(method);
                break;
            case BASIC:
                AttributeContent username = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_USERNAME, authAttributes, false);
                AttributeContent password = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_PASSWORD, authAttributes, false);

                if (username == null || password == null)
                    throw new IllegalArgumentException("Missing username or password in authentication");

                request = webClient
                        .method(method)
                        .headers(h -> h.setBasicAuth(username.getData(), password.getData()));
                break;
            case CERTIFICATE:
                SslContext sslContext = createSslContext(authAttributes);
                HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
                webClient.mutate().clientConnector(new ReactorClientHttpConnector(httpClient)).build();

                request = webClient.method(method);
                break;
            case API_KEY:
                AttributeContent apiKeyHeader = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_API_KEY_HEADER, authAttributes, false);
                AttributeContent apiKey = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_API_KEY, authAttributes, false);

                if (apiKeyHeader == null || apiKey == null)
                    throw new IllegalArgumentException("Missing API Key or API Key header in authentication");

                request = webClient
                        .method(method)
                        .headers(h -> h.set(apiKeyHeader.getData(), apiKey.getData()));
                break;
            case JWT:
                throw new UnsupportedOperationException("JWT is unimplemented");
            default:
                throw new IllegalArgumentException("Unknown auth type " + connector.getAuthType());
        }

        return (WebClient.RequestBodyUriSpec) request;
    }

    public void validateConnectorStatus(ConnectorStatus connectorStatus) throws ValidationException {
        if (connectorStatus == ConnectorStatus.WAITING_FOR_APPROVAL) {
            throw new ValidationException(ValidationError.create("Connector has invalid status: " + connectorStatus.getLabel()));
        }
    }

    private SslContext createSslContext(List<ResponseAttribute> attributes) {
        try {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

            KeyManager km = null;
            FileAttributeContentV2 keyStoreData = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_KEYSTORE, attributes, false);
            if (keyStoreData != null && !keyStoreData.getData().getContent().isEmpty()) {
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); //"SunX509"

                AttributeContent keyStoreType = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_KEYSTORE_TYPE, attributes, false);
                AttributeContent keyStorePassword = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_KEYSTORE_PASSWORD, attributes, false);
                byte[] keyStoreBytes = Base64.getDecoder().decode(keyStoreData.getData().getContent());

                kmf.init(KeyStoreUtils.bytes2KeyStore(keyStoreBytes, keyStorePassword.getData(), keyStoreType.getData()), ((String) keyStorePassword.getData()).toCharArray());
                km = kmf.getKeyManagers()[0];
            }

            sslContextBuilder.keyManager(km);

            TrustManager tm;
            FileAttributeContentV2 trustStoreData = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_TRUSTSTORE, attributes, false);
            if (trustStoreData != null && !trustStoreData.getData().getContent().isEmpty()) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()); //"SunX509"

                AttributeContent trustStoreType = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_TRUSTSTORE_TYPE, attributes, false);
                AttributeContent trustStorePassword = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_TRUSTSTORE_PASSWORD, attributes, false);
                byte[] trustStoreBytes = Base64.getDecoder().decode(trustStoreData.getData().getContent());

                tmf.init(KeyStoreUtils.bytes2KeyStore(trustStoreBytes, trustStorePassword.getData(), trustStoreType.getData()));
                tm = tmf.getTrustManagers()[0];
            } else {
                // set default trustManager
                tm = defaultTrustManagers[0];
            }

            sslContextBuilder.trustManager(tm);

            return sslContextBuilder.protocols("TLSv1.2").build();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize SslContext.", e);
        }
    }

    private static final ParameterizedTypeReference<List<String>> ERROR_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    public static WebClient prepareWebClient() {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        return WebClient.builder()
                .filter(ExchangeFilterFunction.ofResponseProcessor(BaseApiClient::handleHttpExceptions))
                .exchangeStrategies(strategies)
                .build();
    }

    public static <T, R> R processRequest(Function<T, R> func, T request, ApiClientConnectorInfo connector) throws ConnectorException {
        try {
            return func.apply(request);
        } catch (Exception e) {
            Throwable unwrapped = Exceptions.unwrap(e);
            if (unwrapped instanceof ConnectorProblemException pde) {
                pde.setConnector(connector);
                throw pde;
            } else if (unwrapped instanceof IOException || unwrapped instanceof WebClientRequestException) {
                logger.error(unwrapped.getMessage());
                throw new ConnectorCommunicationException("Error in connector %s communication. URL: %s".formatted(connector.getName(), connector.getUrl()), unwrapped, connector);
            } else if (unwrapped instanceof ConnectorException ce) {
                ce.setConnector(connector);
                throw ce;
            } else {
                logger.error(unwrapped.getMessage(), unwrapped);
                throw e;
            }
        }
    }

    private static Mono<ClientResponse> handleHttpExceptions(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return Mono.just(clientResponse);
        }

        // Check if response is RFC 9457 problem+json format
        String contentType = clientResponse.headers().contentType()
                .map(mediaType -> mediaType.toString().toLowerCase())
                .orElse("");

        if (contentType.contains(MediaType.APPLICATION_PROBLEM_JSON_VALUE)) {
            return handleProblemDetailResponse(clientResponse);
        }
        if (contentType.contains(MediaType.TEXT_HTML_VALUE)) {
            // Attempt to parse legacy error format
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ConnectorCommunicationException("Received response with unexpected content type '%s'.".formatted(contentType), null)));
        }

        // Legacy error handling
        return handleLegacyErrorResponse(clientResponse);
    }

    private static Mono<ClientResponse> handleLegacyErrorResponse(ClientResponse clientResponse) {
        if (HttpStatus.UNPROCESSABLE_ENTITY.equals(clientResponse.statusCode())) {
            return clientResponse.bodyToMono(ERROR_LIST_TYPE_REF).flatMap(body ->
                    Mono.error(new ValidationException(body.stream()
                                    .map(ValidationError::create)
                                    .toList()
                            )
                    )
            );
        }
        if (HttpStatus.NOT_FOUND.equals(clientResponse.statusCode())) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ConnectorEntityNotFoundException(body)));
        }
        if (clientResponse.statusCode().is4xxClientError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ConnectorClientException(body, HttpStatus.valueOf(clientResponse.statusCode().value()))));
        }
        if (clientResponse.statusCode().is5xxServerError()) {
            return clientResponse.bodyToMono(String.class)
                    .flatMap(body -> Mono.error(new ConnectorServerException(body, HttpStatus.valueOf(clientResponse.statusCode().value()))));
        }
        return Mono.just(clientResponse);
    }

    private static Mono<ClientResponse> handleProblemDetailResponse(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(ProblemDetailExtended.class)
                .flatMap(problemDetail -> Mono.error(new ConnectorProblemException(problemDetail)));
    }
}
