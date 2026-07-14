package com.otilm.api.clients;

import com.otilm.api.exception.*;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.attribute.v2.content.FileAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.SecretAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.core.util.AttributeDefinitionUtils;
import com.otilm.core.util.KeyStoreUtils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
                List<StringAttributeContentV2> usernameContent = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_USERNAME, authAttributes, StringAttributeContentV2.class);
                List<SecretAttributeContentV2> passwordContent = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_PASSWORD, authAttributes, SecretAttributeContentV2.class);

                if (usernameContent == null || usernameContent.isEmpty() || passwordContent == null || passwordContent.isEmpty())
                    throw new IllegalArgumentException("Missing username or password in authentication");

                String usernameValue = usernameContent.get(0).getData();
                String passwordValue = passwordContent.get(0).getData().getSecret();

                request = webClient
                        .method(method)
                        .headers(h -> h.setBasicAuth(usernameValue, passwordValue));
                break;
            case CERTIFICATE:
                SslContext sslContext = createSslContext(authAttributes);
                HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
                request = webClient.mutate().clientConnector(new ReactorClientHttpConnector(httpClient)).build().method(method);
                break;
            case API_KEY:
                List<StringAttributeContentV2> apiKeyHeaderContent = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_API_KEY_HEADER, authAttributes, StringAttributeContentV2.class);
                List<SecretAttributeContentV2> apiKeyContent = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_API_KEY, authAttributes, SecretAttributeContentV2.class);

                if (apiKeyHeaderContent == null || apiKeyHeaderContent.isEmpty() || apiKeyContent == null || apiKeyContent.isEmpty())
                    throw new IllegalArgumentException("Missing API Key or API Key header in authentication");

                String apiKeyHeaderValue = apiKeyHeaderContent.get(0).getData();
                String apiKeyValue = apiKeyContent.get(0).getData().getSecret();

                request = webClient
                        .method(method)
                        .headers(h -> h.set(apiKeyHeaderValue, apiKeyValue));
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
            List<FileAttributeContentV2> keyStoreDataList = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_KEYSTORE, attributes, FileAttributeContentV2.class);
            FileAttributeContentV2 keyStoreData = keyStoreDataList != null && !keyStoreDataList.isEmpty() ? keyStoreDataList.get(0) : null;
            if (keyStoreData != null && !keyStoreData.getData().getContent().isEmpty()) {
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                String keyStorePassword = getStorePassword(attributes, ATTRIBUTE_KEYSTORE_PASSWORD);
                String keyStoreType = getStoreType(attributes, ATTRIBUTE_KEYSTORE_TYPE);
                byte[] keyStoreBytes = Base64.getDecoder().decode(keyStoreData.getData().getContent());

                kmf.init(KeyStoreUtils.bytes2KeyStore(keyStoreBytes, keyStorePassword, keyStoreType), keyStorePassword != null ? keyStorePassword.toCharArray() : null);
                km = kmf.getKeyManagers()[0];
            }

            sslContextBuilder.keyManager(km);

            TrustManager tm;
            List<FileAttributeContentV2> trustStoreDataList = AttributeDefinitionUtils.getAttributeContent(ATTRIBUTE_TRUSTSTORE, attributes, FileAttributeContentV2.class);
            FileAttributeContentV2 trustStoreData = trustStoreDataList != null && !trustStoreDataList.isEmpty() ? trustStoreDataList.get(0) : null;
            if (trustStoreData != null && !trustStoreData.getData().getContent().isEmpty()) {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

                String trustStorePassword = getStorePassword(attributes, ATTRIBUTE_TRUSTSTORE_PASSWORD);
                String trustStoreType = getStoreType(attributes, ATTRIBUTE_TRUSTSTORE_TYPE);
                byte[] trustStoreBytes = Base64.getDecoder().decode(trustStoreData.getData().getContent());

                tmf.init(KeyStoreUtils.bytes2KeyStore(trustStoreBytes, trustStorePassword, trustStoreType));
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

    private static String getStoreType(List<ResponseAttribute> attributes, String name) {
        List<StringAttributeContentV2> keyStoreTypeList = AttributeDefinitionUtils.getAttributeContent(name, attributes, StringAttributeContentV2.class);
        return keyStoreTypeList != null && !keyStoreTypeList.isEmpty() ? keyStoreTypeList.get(0).getData() : null;
    }

    private static String getStorePassword(List<ResponseAttribute> attributes, String attributeName) {
        List<SecretAttributeContentV2> list = AttributeDefinitionUtils.getAttributeContent(attributeName, attributes, SecretAttributeContentV2.class);
        return list != null && !list.isEmpty() ? list.get(0).getData().getSecret() : null;
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

    /**
     * Block on a {@code Mono<ResponseEntity<T>>} and guarantee a non-null response entity.
     * {@code Mono.block()} returns null when the publisher completes empty; dereferencing that
     * would throw an opaque NPE. Surfacing a clear IllegalStateException at the call site makes
     * a misbehaving connector (no response) diagnosable instead of producing a bare NPE.
     */
    protected static <T> ResponseEntity<T> requireResponse(Mono<ResponseEntity<T>> mono, String context) {
        ResponseEntity<T> entity = mono.block();
        if (entity == null) {
            throw new IllegalStateException("No response received from connector for " + context);
        }
        return entity;
    }

    /**
     * Like {@link #requireResponse} but also requires a non-null body — for endpoints whose 2xx
     * response must carry a payload (attribute lists, operation status, identify, CRL, CA certs).
     * An empty body on success is a connector contract violation; fail clearly rather than
     * returning null to the caller (which would NPE later, far from the cause).
     */
    protected static <T> T requireBody(Mono<ResponseEntity<T>> mono, String context) {
        ResponseEntity<T> entity = requireResponse(mono, context);
        if (entity.getBody() == null) {
            throw new IllegalStateException("Connector returned an empty body for " + context);
        }
        return entity.getBody();
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
            } else if (unwrapped instanceof IllegalStateException) {
                // A v2-only connector 404s the v1 discovery probe (empty/no body); surface it as a
                // communication error so connect() falls through to the next adapter version (/v2/info)
                // instead of aborting with a 500.
                throw new ConnectorCommunicationException("Error in connector %s communication. URL: %s".formatted(connector.getName(), connector.getUrl()), unwrapped, connector);
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
