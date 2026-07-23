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
import io.netty.channel.ChannelOption;
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
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
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

    private static final int CODEC_MAX_IN_MEMORY = 16 * 1024 * 1024;

    // Pool hygiene, fixed here because it is not deployment-specific. Connector servers typically
    // drop keep-alive connections around 60s, so idle connections are evicted and capped in age to
    // avoid reusing a server-closed connection (PrematureCloseException).
    private static final Duration POOL_MAX_IDLE = Duration.ofSeconds(30);
    private static final Duration POOL_MAX_LIFE = Duration.ofMinutes(5);
    private static final Duration POOL_EVICT_INTERVAL = Duration.ofSeconds(30);
    private static final Duration POOL_DISPOSE_INTERVAL = Duration.ofSeconds(120);
    private static final Duration POOL_DISPOSE_AFTER = Duration.ofSeconds(300);

    // Tuning is applied once (first prepareWebClient wins); the tuned HttpClient is the single source
    // the CERTIFICATE path derives from (via secure()), so per-connector TLS clients inherit the same
    // pool and timeouts. baseHttpClient is built lazily on first use so a deployment that always
    // supplies tuning never builds a throwaway default ConnectionProvider — an orphaned provider is
    // never GC'd (its background eviction task keeps a strong reference to it forever).
    private static volatile ClientTuning appliedTuning;
    private static volatile ConnectionProvider connectionProvider;
    private static volatile HttpClient baseHttpClient;

    // Per-connector CERTIFICATE WebClients, keyed by connector UUID. The stored authMaterialHash
    // invalidates the entry when credentials rotate. Caching the built WebClient (not just the
    // SslContext) keeps the Reactor-Netty pool key stable across requests — the pool key hashes the
    // SslProvider, so a fresh SslContext per request would give CERTIFICATE connectors no connection
    // reuse and an ever-growing pool map.
    private static final Map<String, CachedCertClient> certClientCache = new ConcurrentHashMap<>();

    private record CachedCertClient(String authHash, WebClient webClient) {
    }

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
                request = certificateWebClient(connector).method(method);
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

    /**
     * Resolve the CERTIFICATE-auth WebClient for a connector, reusing the cached instance while the
     * connector's auth material is unchanged. Derives from the shared tuned {@link #baseHttpClient}
     * so the per-connector TLS client inherits the connection pool and timeouts.
     */
    WebClient certificateWebClient(ApiClientConnectorInfo connector) {
        String authHash = authMaterialHash(connector.getAuthAttributes());
        String uuid = connector.getUuid();
        if (uuid == null) {
            // Not cacheable without a stable key (a ConcurrentHashMap rejects null keys anyway); build per request.
            return buildCertificateWebClient(connector);
        }
        // compute() builds at most once per (uuid, authHash): concurrent first-callers for the same
        // connector serialize on the map bin instead of each building a distinct SslContext, which
        // would briefly give one host two Reactor pool keys.
        return certClientCache.compute(uuid, (key, existing) ->
                existing != null && existing.authHash().equals(authHash)
                        ? existing
                        : new CachedCertClient(authHash, buildCertificateWebClient(connector))
        ).webClient();
    }

    private WebClient buildCertificateWebClient(ApiClientConnectorInfo connector) {
        SslContext sslContext = createSslContext(connector.getAuthAttributes());
        HttpClient certHttpClient = baseHttpClient().secure(spec -> spec.sslContext(sslContext));
        return webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(certHttpClient))
                .build();
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

    /**
     * Content hash of the keystore/truststore material used to build the SslContext. Used as the
     * cache-invalidation token for {@link #certClientCache} so rotated credentials rebuild the TLS
     * client. Hashing (rather than keying on the raw material) keeps secrets out of the cache map.
     */
    private static String authMaterialHash(List<ResponseAttribute> attributes) {
        String material = String.join("\n",
                String.valueOf(getStoreType(attributes, ATTRIBUTE_KEYSTORE_TYPE)),
                String.valueOf(storeContent(attributes, ATTRIBUTE_KEYSTORE)),
                String.valueOf(getStorePassword(attributes, ATTRIBUTE_KEYSTORE_PASSWORD)),
                String.valueOf(getStoreType(attributes, ATTRIBUTE_TRUSTSTORE_TYPE)),
                String.valueOf(storeContent(attributes, ATTRIBUTE_TRUSTSTORE)),
                String.valueOf(getStorePassword(attributes, ATTRIBUTE_TRUSTSTORE_PASSWORD)));
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(material.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static String storeContent(List<ResponseAttribute> attributes, String name) {
        List<FileAttributeContentV2> list = AttributeDefinitionUtils.getAttributeContent(name, attributes, FileAttributeContentV2.class);
        return list != null && !list.isEmpty() ? list.get(0).getData().getContent() : null;
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

    /**
     * Build the shared connector WebClient with default tuning. Callers that do not configure tuning
     * (tests, and any consumer without deployment config) get {@link ClientTuning#defaults()}.
     */
    public static WebClient prepareWebClient() {
        return prepareWebClient(ClientTuning.defaults());
    }

    /**
     * Build the shared connector WebClient with the supplied tuning. The first call wins: a later
     * call with different tuning is ignored (with a warning) so the live ConnectionProvider is not
     * orphaned — important under Spring test-context caching, which can invoke the bean factory more
     * than once per JVM.
     */
    public static synchronized WebClient prepareWebClient(ClientTuning tuning) {
        if (appliedTuning == null) {
            appliedTuning = tuning;
            connectionProvider = buildConnectionProvider(tuning);
            baseHttpClient = buildHttpClient(connectionProvider, tuning);
        } else if (!appliedTuning.equals(tuning)) {
            logger.warn("Connector WebClient already tuned; ignoring differing tuning request");
        }
        return buildWebClient(baseHttpClient);
    }

    /** Lazily initialize with defaults if a client is used before any prepareWebClient call. */
    private static synchronized HttpClient baseHttpClient() {
        if (baseHttpClient == null) {
            prepareWebClient();
        }
        return baseHttpClient;
    }

    private static ConnectionProvider buildConnectionProvider(ClientTuning tuning) {
        return ConnectionProvider.builder("connector")
                .maxConnections(tuning.maxConnections())
                .pendingAcquireMaxCount(tuning.maxConnections() * 2)
                .pendingAcquireTimeout(tuning.pendingAcquireTimeout())
                .maxIdleTime(POOL_MAX_IDLE)
                .maxLifeTime(POOL_MAX_LIFE)
                .evictInBackground(POOL_EVICT_INTERVAL)
                .disposeInactivePoolsInBackground(POOL_DISPOSE_INTERVAL, POOL_DISPOSE_AFTER)
                .lifo()
                .build();
    }

    private static HttpClient buildHttpClient(ConnectionProvider provider, ClientTuning tuning) {
        // responseTimeout is reactor-netty's own per-request read guard (added when the request is
        // sent, removed when the response is received), so it fails fast on a connector that never
        // responds without ever firing on an idle pooled connection. A mid-body stall after headers
        // is bounded by the caller's transaction timeout rather than a persistent channel handler.
        return HttpClient.create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) tuning.connectTimeout().toMillis())
                .responseTimeout(tuning.responseTimeout());
    }

    private static WebClient buildWebClient(HttpClient httpClient) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(CODEC_MAX_IN_MEMORY))
                .build();
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(ExchangeFilterFunction.ofResponseProcessor(BaseApiClient::handleHttpExceptions))
                .exchangeStrategies(strategies)
                .build();
    }

    /**
     * Reset the static tuning and CERTIFICATE-client cache to defaults. Test-only seam so cases that
     * apply custom tuning do not leak into subsequent cases in the same JVM.
     */
    static synchronized void resetConnectorClientForTest() {
        if (connectionProvider != null) {
            connectionProvider.dispose();
        }
        connectionProvider = null;
        baseHttpClient = null;
        appliedTuning = null;
        certClientCache.clear();
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
            } else if (unwrapped instanceof IOException
                    || unwrapped instanceof WebClientRequestException
                    || unwrapped instanceof io.netty.handler.timeout.TimeoutException
                    || unwrapped instanceof TimeoutException
                    || isPoolAcquireExhausted(unwrapped)) {
                // Connect, response, and pool-acquire failures land here. Netty's timeout exceptions
                // are not IOExceptions, and the pool "pending acquire limit" exception is a plain
                // RuntimeException, so they are matched explicitly rather than rethrown raw.
                logger.error(String.valueOf(unwrapped.getMessage()));
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

    /**
     * Reactor-Netty's {@code PoolAcquireTimeoutException} extends {@link TimeoutException} (already
     * matched), but {@code PoolAcquirePendingLimitException} — thrown when the pending-acquire queue
     * is saturated — is a plain {@code RuntimeException}. Match it by simple name to avoid a
     * compile-time dependency on Reactor-Netty's shaded internal pool package.
     */
    private static boolean isPoolAcquireExhausted(Throwable t) {
        return "PoolAcquirePendingLimitException".equals(t.getClass().getSimpleName());
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
