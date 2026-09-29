package com.otilm.api.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.otilm.api.exception.ConnectorClientException;
import com.otilm.api.exception.ConnectorCommunicationException;
import com.otilm.api.exception.ConnectorEntityNotFoundException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.exception.PlatformException;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
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
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.codec.EncodingException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

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

    // Pool hygiene (not deployment-specific): evict idle and age-cap connections so a server-closed
    // keep-alive is not reused (PrematureCloseException).
    private static final Duration POOL_MAX_IDLE = Duration.ofSeconds(30);
    private static final Duration POOL_MAX_LIFE = Duration.ofMinutes(5);
    private static final Duration POOL_EVICT_INTERVAL = Duration.ofSeconds(30);
    private static final Duration POOL_DISPOSE_INTERVAL = Duration.ofSeconds(120);
    private static final Duration POOL_DISPOSE_AFTER = Duration.ofSeconds(300);

    // Bounds findInCauseChain so a cyclic cause chain cannot spin forever inside an exception handler.
    private static final int MAX_CAUSE_CHAIN_DEPTH = 32;

    // Applied once (first prepareWebClient wins). The tuned HttpClient is the single source the
    // CERTIFICATE path derives from, built lazily so a tuned deployment never leaves an orphaned
    // default ConnectionProvider (which its background task would pin forever).
    private static volatile ClientTuning appliedTuning;
    private static volatile ConnectionProvider connectionProvider;
    private static volatile HttpClient baseHttpClient;

    // Per-connector CERTIFICATE WebClients keyed by UUID; authMaterialHash invalidates on credential
    // rotation. Caching the built WebClient (not just the SslContext) keeps the Reactor pool key
    // stable — a fresh SslContext per request means a new SslProvider, hence no connection reuse.
    // Process-wide: assumes all clients share one base WebClient + defaultTrustManagers (true in core).
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

    public WebClient.RequestBodyUriSpec prepareRequest(HttpMethod method, ApiClientConnectorInfo connector,
            boolean validateConnectorStatus) {
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
                List<StringAttributeContentV2> usernameContent = AttributeDefinitionUtils
                        .getAttributeContent(ATTRIBUTE_USERNAME, authAttributes, StringAttributeContentV2.class);
                List<SecretAttributeContentV2> passwordContent = AttributeDefinitionUtils
                        .getAttributeContent(ATTRIBUTE_PASSWORD, authAttributes, SecretAttributeContentV2.class);

                if (usernameContent == null || usernameContent.isEmpty() || passwordContent == null
                        || passwordContent.isEmpty()) {
                    throw new IllegalArgumentException("Missing username or password in authentication");
                }

                String usernameValue = usernameContent.get(0).getData();
                String passwordValue = passwordContent.get(0).getData().getSecret();

                request = webClient.method(method).headers(h -> h.setBasicAuth(usernameValue, passwordValue));
                break;
            case CERTIFICATE:
                request = certificateWebClient(connector).method(method);
                break;
            case API_KEY:
                List<StringAttributeContentV2> apiKeyHeaderContent = AttributeDefinitionUtils
                        .getAttributeContent(ATTRIBUTE_API_KEY_HEADER, authAttributes, StringAttributeContentV2.class);
                List<SecretAttributeContentV2> apiKeyContent = AttributeDefinitionUtils
                        .getAttributeContent(ATTRIBUTE_API_KEY, authAttributes, SecretAttributeContentV2.class);

                if (apiKeyHeaderContent == null || apiKeyHeaderContent.isEmpty() || apiKeyContent == null
                        || apiKeyContent.isEmpty()) {
                    throw new IllegalArgumentException("Missing API Key or API Key header in authentication");
                }

                String apiKeyHeaderValue = apiKeyHeaderContent.get(0).getData();
                String apiKeyValue = apiKeyContent.get(0).getData().getSecret();

                request = webClient.method(method).headers(h -> h.set(apiKeyHeaderValue, apiKeyValue));
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
            throw new ValidationException(
                    ValidationError.create("Connector has invalid status: " + connectorStatus.getLabel()));
        }
    }

    /**
     * CERTIFICATE-auth WebClient for a connector, cached while its auth material is unchanged and derived from the
     * shared tuned {@link #baseHttpClient} so it inherits the pool and timeouts.
     */
    WebClient certificateWebClient(ApiClientConnectorInfo connector) {
        // Resolve the base client (may take the class lock) before compute(), so the map-bin lock
        // never nests the class lock — the reverse of resetConnectorClientForTest, a deadlock risk.
        HttpClient httpClient = baseHttpClient();
        String uuid = connector.getUuid();
        if (uuid == null) {
            // Not cacheable without a stable key; build per request.
            return buildCertificateWebClient(connector, httpClient);
        }
        String authHash = authMaterialHash(connector.getAuthAttributes());
        // compute() builds once per (uuid, authHash); concurrent first-callers serialize on the bin
        // rather than each building a distinct SslContext (two pool keys for one host).
        return certClientCache
                .compute(uuid,
                        (key, existing) -> existing != null && existing.authHash().equals(authHash)
                                ? existing
                                : new CachedCertClient(authHash, buildCertificateWebClient(connector, httpClient)))
                .webClient();
    }

    private WebClient buildCertificateWebClient(ApiClientConnectorInfo connector, HttpClient httpClient) {
        SslContext sslContext = createSslContext(connector.getAuthAttributes());
        HttpClient certHttpClient = httpClient.secure(spec -> spec.sslContext(sslContext));
        return webClient.mutate().clientConnector(new ReactorClientHttpConnector(certHttpClient)).build();
    }

    /**
     * Drop the cached CERTIFICATE WebClient for a connector. Core calls this on connector delete or auth change so the
     * process-wide cache does not retain a stale client and its key material.
     */
    public static void evictCertificateClient(String connectorUuid) {
        if (connectorUuid != null) {
            certClientCache.remove(connectorUuid);
        }
    }

    private SslContext createSslContext(List<ResponseAttribute> attributes) {
        try {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();

            KeyManager km = null;
            List<FileAttributeContentV2> keyStoreDataList = AttributeDefinitionUtils
                    .getAttributeContent(ATTRIBUTE_KEYSTORE, attributes, FileAttributeContentV2.class);
            FileAttributeContentV2 keyStoreData = keyStoreDataList != null && !keyStoreDataList.isEmpty()
                    ? keyStoreDataList.get(0)
                    : null;
            if (keyStoreData != null && !keyStoreData.getData().getContent().isEmpty()) {
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());

                String keyStorePassword = getStorePassword(attributes, ATTRIBUTE_KEYSTORE_PASSWORD);
                String keyStoreType = getStoreType(attributes, ATTRIBUTE_KEYSTORE_TYPE);
                byte[] keyStoreBytes = Base64.getDecoder().decode(keyStoreData.getData().getContent());

                kmf
                        .init(KeyStoreUtils.bytes2KeyStore(keyStoreBytes, keyStorePassword, keyStoreType),
                                keyStorePassword != null ? keyStorePassword.toCharArray() : null);
                km = kmf.getKeyManagers()[0];
            }

            sslContextBuilder.keyManager(km);

            TrustManager tm;
            List<FileAttributeContentV2> trustStoreDataList = AttributeDefinitionUtils
                    .getAttributeContent(ATTRIBUTE_TRUSTSTORE, attributes, FileAttributeContentV2.class);
            FileAttributeContentV2 trustStoreData = trustStoreDataList != null && !trustStoreDataList.isEmpty()
                    ? trustStoreDataList.get(0)
                    : null;
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
     * Content hash of the keystore/truststore material — the cache-invalidation token for {@link #certClientCache}.
     * Hashing keeps secrets out of the map keys.
     */
    private static String authMaterialHash(List<ResponseAttribute> attributes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Length-prefix each field fed straight to the digest (no concatenated keystore copy),
            // so absent/adjacent values cannot alias.
            updateField(digest, getStoreType(attributes, ATTRIBUTE_KEYSTORE_TYPE));
            updateField(digest, storeContent(attributes, ATTRIBUTE_KEYSTORE));
            updateField(digest, getStorePassword(attributes, ATTRIBUTE_KEYSTORE_PASSWORD));
            updateField(digest, getStoreType(attributes, ATTRIBUTE_TRUSTSTORE_TYPE));
            updateField(digest, storeContent(attributes, ATTRIBUTE_TRUSTSTORE));
            updateField(digest, getStorePassword(attributes, ATTRIBUTE_TRUSTSTORE_PASSWORD));
            return Base64.getEncoder().encodeToString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private static void updateField(MessageDigest digest, String value) {
        if (value == null) {
            digest.update((byte) 0);
            return;
        }
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        digest.update((byte) 1);
        digest.update((byte) (bytes.length >>> 24));
        digest.update((byte) (bytes.length >>> 16));
        digest.update((byte) (bytes.length >>> 8));
        digest.update((byte) bytes.length);
        digest.update(bytes);
    }

    private static String storeContent(List<ResponseAttribute> attributes, String name) {
        List<FileAttributeContentV2> list = AttributeDefinitionUtils
                .getAttributeContent(name, attributes, FileAttributeContentV2.class);
        if (list == null || list.isEmpty()) {
            return null;
        }
        var data = list.get(0).getData();
        return data != null ? data.getContent() : null;
    }

    private static String getStoreType(List<ResponseAttribute> attributes, String name) {
        List<StringAttributeContentV2> keyStoreTypeList = AttributeDefinitionUtils
                .getAttributeContent(name, attributes, StringAttributeContentV2.class);
        return keyStoreTypeList != null && !keyStoreTypeList.isEmpty() ? keyStoreTypeList.get(0).getData() : null;
    }

    private static String getStorePassword(List<ResponseAttribute> attributes, String attributeName) {
        List<SecretAttributeContentV2> list = AttributeDefinitionUtils
                .getAttributeContent(attributeName, attributes, SecretAttributeContentV2.class);
        return list != null && !list.isEmpty() ? list.get(0).getData().getSecret() : null;
    }

    private static final ParameterizedTypeReference<List<String>> ERROR_LIST_TYPE_REF = new ParameterizedTypeReference<>() {
    };

    /**
     * Build the shared connector WebClient with default tuning. Callers that do not configure tuning (tests, and any
     * consumer without deployment config) get {@link ClientTuning#defaults()}.
     */
    public static WebClient prepareWebClient() {
        return prepareWebClient(ClientTuning.defaults());
    }

    /**
     * Build the shared WebClient with the given tuning. First call wins; a later call with different tuning is ignored
     * (warned) so the live ConnectionProvider is not orphaned — matters under Spring test-context caching.
     */
    public static synchronized WebClient prepareWebClient(ClientTuning tuning) {
        Objects.requireNonNull(tuning, "tuning must not be null");
        if (appliedTuning == null) {
            appliedTuning = tuning;
            connectionProvider = buildConnectionProvider(tuning);
            baseHttpClient = buildHttpClient(connectionProvider, tuning);
        } else if (!appliedTuning.equals(tuning)) {
            logger
                    .warn("Connector WebClient already tuned with {}; ignoring differing request {}", appliedTuning,
                            tuning);
        }
        // appliedTuning, not the argument: a warned-and-ignored caller must still receive a client
        // whose knobs match the single live baseHttpClient.
        return buildWebClient(baseHttpClient, appliedTuning);
    }

    /** Lazily initialize with defaults if a client is used before any prepareWebClient call. */
    private static synchronized HttpClient baseHttpClient() {
        if (baseHttpClient == null) {
            prepareWebClient();
        }
        return baseHttpClient;
    }

    private static ConnectionProvider buildConnectionProvider(ClientTuning tuning) {
        return ConnectionProvider
                .builder("connector")
                .maxConnections(tuning.maxConnections())
                .pendingAcquireMaxCount(Math.multiplyExact(tuning.maxConnections(), 2))
                .pendingAcquireTimeout(tuning.pendingAcquireTimeout())
                .maxIdleTime(POOL_MAX_IDLE)
                .maxLifeTime(POOL_MAX_LIFE)
                .evictInBackground(POOL_EVICT_INTERVAL)
                .disposeInactivePoolsInBackground(POOL_DISPOSE_INTERVAL, POOL_DISPOSE_AFTER)
                .lifo()
                .build();
    }

    private static HttpClient buildHttpClient(ConnectionProvider provider, ClientTuning tuning) {
        // responseTimeout is reactor-netty's per-request read guard (idle-safe), failing fast on a
        // non-responding connector; a mid-body stall is bounded by the caller's transaction timeout.
        return HttpClient
                .create(provider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.toIntExact(tuning.connectTimeout().toMillis()))
                .responseTimeout(tuning.responseTimeout());
    }

    static ExchangeStrategies connectorExchangeStrategies(final ClientTuning tuning) {
        return ExchangeStrategies.builder().codecs(codecs -> {
            codecs.defaultCodecs().maxInMemorySize(tuning.maxInMemorySize());
            ApiClientCodecs.configureJsonCodecs(codecs);
        }).build();
    }

    private static WebClient buildWebClient(HttpClient httpClient, ClientTuning tuning) {
        return WebClient
                .builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(ExchangeFilterFunction.ofResponseProcessor(BaseApiClient::handleHttpExceptions))
                .exchangeStrategies(connectorExchangeStrategies(tuning))
                .build();
    }

    /**
     * Reset the static tuning and CERTIFICATE-client cache to defaults. Test-only seam so cases that apply custom
     * tuning do not leak into subsequent cases in the same JVM.
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
     * Block on a {@code Mono<ResponseEntity<T>>} and guarantee a non-null response entity. {@code Mono.block()} returns
     * null when the publisher completes empty; dereferencing that would throw an opaque NPE. Surfacing a clear
     * IllegalStateException at the call site makes a misbehaving connector (no response) diagnosable instead of
     * producing a bare NPE.
     *
     * <p>
     * The {@link IllegalStateException} is unchecked, so it escapes methods declaring {@code throws ConnectorException}
     * and is not attributed to a connector. A caller that must distinguish a bodiless response has to catch it
     * alongside {@code ConnectorException}, and take the connector's identity from the call site. (The MQ discovery
     * client's equivalent guard throws a checked, connector-attributed {@code ConnectorException} instead.)
     */
    protected static <T> ResponseEntity<T> requireResponse(Mono<ResponseEntity<T>> mono, String context) {
        ResponseEntity<T> entity = mono.block();
        if (entity == null) {
            throw new IllegalStateException("No response received from connector for " + context);
        }
        return entity;
    }

    /**
     * Like {@link #requireResponse} but also requires a non-null body — for endpoints whose 2xx response must carry a
     * payload (attribute lists, operation status, identify, CRL, CA certs). An empty body on success is a connector
     * contract violation; fail clearly rather than returning null to the caller (which would NPE later, far from the
     * cause).
     */
    protected static <T> T requireBody(Mono<ResponseEntity<T>> mono, String context) {
        ResponseEntity<T> entity = requireResponse(mono, context);
        if (entity.getBody() == null) {
            throw new IllegalStateException("Connector returned an empty body for " + context);
        }
        return entity.getBody();
    }

    public static <T, R> R processRequest(Function<T, R> func, T request, ApiClientConnectorInfo connector)
            throws ConnectorException {
        try {
            return func.apply(request);
        } catch (Exception e) {
            Throwable unwrapped = Exceptions.unwrap(e);
            if (unwrapped instanceof ConnectorProblemException pde) {
                pde.setConnector(connector);
                throw pde;
            } else if (isJsonTypeResolutionFailure(unwrapped)) {
                // Must precede the IOException branch: JsonProcessingException extends IOException, so a
                // decode failure would otherwise be reported as a transport failure it is not.
                //
                // 502, not 422: the connector answered and its answer does not conform. Core serves 422
                // for a caller's own invalid input, so 422 here would blame the user and invert the
                // retry signal.
                //
                // The log carries the failure type, never the exception's message: Jackson's message
                // quotes fragments of the response body, which for discovery can include key material.
                logger
                        .error("Connector {} response failed type resolution at {}: {}", connector.getName(),
                                safeLocation(connector), unwrapped.getClass().getName());
                ConnectorServerException typeFailure = new ConnectorServerException(
                        "Connector %s returned a response that could not be parsed against the expected type"
                                .formatted(connector.getName()),
                        unwrapped, HttpStatus.BAD_GATEWAY);
                typeFailure.setConnector(connector);
                throw typeFailure;
            } else if (unwrapped instanceof IOException || unwrapped instanceof WebClientRequestException
                    || unwrapped instanceof io.netty.handler.timeout.TimeoutException
                    || unwrapped instanceof TimeoutException || isPoolAcquireExhausted(unwrapped)) {
                // Connect, response, and pool-acquire failures. Netty timeouts aren't IOExceptions and
                // the pool pending-limit is a plain RuntimeException, so match them explicitly. Log
                // type+message; the full cause rides on the exception thrown below.
                logger
                        .error("Connector {} communication failure at {}: {}", connector.getName(),
                                safeLocation(connector), unwrapped.getClass().getName());
                throw new ConnectorCommunicationException(
                        "Error in connector %s communication".formatted(connector.getName()), unwrapped, connector);
            } else if (isOversizedResponse(unwrapped)) {
                // The codec's maxInMemorySize (ClientTuning) was exceeded while decoding the body — a
                // connector fault, not a communication failure. Unmapped it would escape as
                // WebClientResponseException or a bare DataBufferLimitException, which callers declaring
                // `throws ConnectorException` cannot catch and which carries no connector attribution.
                logger
                        .error("Connector {} response exceeded the configured read limit at {}: {}",
                                connector.getName(), safeLocation(connector), unwrapped.getClass().getName());
                ConnectorServerException cse = new ConnectorServerException(
                        "Connector %s response exceeded the configured read limit".formatted(connector.getName()),
                        unwrapped, upstreamStatus(unwrapped));
                cse.setConnector(connector);
                throw cse;
            } else if (unwrapped instanceof ConnectorException ce) {
                ce.setConnector(connector);
                throw ce;
            } else if (unwrapped instanceof PlatformException) {
                // Expected business error (e.g. connector 422); caller handles it. Skip the noisy
                // Reactor-enhanced stacktrace. The log names the failure type only: the message is the
                // connector's own text, which can echo a secret the request carried.
                logger.debug("Connector {} request rejected: {}", connector.getName(), unwrapped.getClass().getName());
                // Throw the unwrapped exception so callers catch the domain type (e.g. ValidationException)
                // rather than a Reactor wrapper; fall back to the original when it is not re-throwable.
                if (unwrapped instanceof RuntimeException re) {
                    throw re;
                }
                throw e;
            } else {
                logger.error(unwrapped.getMessage(), unwrapped);
                throw e;
            }
        }
    }

    /**
     * True when a {@link DataBufferLimitException} appears anywhere in {@code t}'s cause chain — a codec
     * {@code maxInMemorySize} breach, whatever wrapped it.
     *
     * <p>
     * The whole chain is walked, not one or two levels. {@code WebClient.retrieve()} wraps a body-decode failure into a
     * {@link WebClientResponseException}, and Spring's {@code Jackson2JsonDecoder} raises a {@code DecodingException}
     * around a decode failure, so a breach can sit several levels deep; anything missed escapes to
     * {@code processRequest}'s catch-all and surfaces as a Spring-internal type.
     */
    private static boolean isOversizedResponse(Throwable t) {
        return findInCauseChain(t, DataBufferLimitException.class) != null;
    }

    /**
     * {@link HttpStatus#valueOf} throws for a code with no enum constant, and 499 and 430 are both valid HTTP codes
     * without one. Anywhere a connector's status reaches us it is arbitrary, so it is resolved with a fallback rather
     * than trusted to be an enum member.
     */
    private static HttpStatus resolveOr(int statusCode, HttpStatus fallback) {
        HttpStatus resolved = HttpStatus.resolve(statusCode);
        return resolved == null ? fallback : resolved;
    }

    /**
     * Makes the transport's status win over the {@code status} member inside the problem document.
     *
     * <p>
     * RFC 9457 says the member SHOULD match the HTTP status, which means a connector can disagree — and callers act on
     * it: the discovery clients treat 404-plus-not-tracked as an already-terminal cancellation. A connector answering
     * 422 with a body claiming {@code "status": 404} could otherwise have a refused cancel read as a completed one, so
     * the value a caller reads has to come from the response rather than from the body describing it.
     */
    private static ProblemDetailExtended withTransportStatus(ProblemDetailExtended problemDetail, int statusCode) {
        if (problemDetail.getStatus() != statusCode) {
            logger
                    .warn("Connector problem document declares status {} on a {} response; using the response status",
                            problemDetail.getStatus(), statusCode);
            problemDetail.setStatus(statusCode);
        }
        return problemDetail;
    }

    /**
     * The connector's location with anything secret stripped: scheme, host, port and path only, never user-info and
     * never the query string. Internal topology is fine in a server-side log and is what an operator needs to place a
     * failure; a credential is not, and a log outlives the request that wrote it. Nothing stops a configured connector
     * URL carrying {@code user:password@} or a token in a query parameter, so this never trusts the raw value.
     *
     * <p>
     * Package-private so it can be tested directly — the log line itself is not assertable, because slf4j-simple binds
     * its stream at initialization.
     */
    static String safeLocation(ApiClientConnectorInfo connector) {
        String raw = connector.getUrl();
        if (raw == null) {
            return "<no url>";
        }
        try {
            URI uri = URI.create(raw);
            if (uri.getHost() == null) {
                return "<unparseable url>";
            }
            StringBuilder location = new StringBuilder();
            if (uri.getScheme() != null) {
                location.append(uri.getScheme()).append("://");
            }
            location.append(uri.getHost());
            if (uri.getPort() != -1) {
                location.append(':').append(uri.getPort());
            }
            if (uri.getPath() != null) {
                location.append(uri.getPath());
            }
            return location.toString();
        } catch (IllegalArgumentException e) {
            return "<unparseable url>";
        }
    }

    /**
     * True when a Jackson {@link JsonProcessingException} appears anywhere in {@code t}'s cause chain — covering
     * {@code MismatchedInputException}, {@code InvalidTypeIdException}, {@code ValueInstantiationException} and
     * {@code InvalidFormatException}, whatever wrapped them.
     *
     * <p>
     * The whole chain is walked because the wrapping is not a single layer: {@code WebClient.retrieve()} can surface a
     * {@code WebClientResponseException} around the {@code DecodingException} that Spring's {@code Jackson2JsonDecoder}
     * raises around the Jackson failure. Missing that shape is worse than a misclassification — it falls through to
     * {@code processRequest}'s catch-all, which logs the exception's message, and a Jackson message quotes fragments of
     * the connector's response body.
     */
    private static boolean isJsonTypeResolutionFailure(Throwable t) {
        // An EncodingException means Jackson failed while writing OUR request body, so no connector
        // response exists and the fault is ours — reporting it as a connector 502 would point an
        // operator at the wrong system. Only decode failures are the connector's.
        if (findInCauseChain(t, EncodingException.class) != null) {
            return false;
        }
        return findInCauseChain(t, JsonProcessingException.class) != null;
    }

    /**
     * The status the connector actually answered with, for a failure raised while decoding its response. It must not be
     * invented: Core's {@code ExceptionHandlingAdvice} renders it verbatim as an "Original response code ..." suffix on
     * the operator-facing error, and a read-limit breach is typically hit on a {@code 200}, so a synthesized
     * {@code 413 PAYLOAD_TOO_LARGE} would assert a status that never existed and point operators at a request-size
     * problem rather than at this client's own read cap.
     *
     * <p>
     * The real status comes from the {@link WebClientResponseException} that {@code WebClient.retrieve()} wraps a
     * body-decode failure into. {@link HttpStatus#BAD_GATEWAY} is the fallback when no such wrapper is in the chain (a
     * bare codec failure carries no status) or when the connector answered a code {@link HttpStatus} cannot represent.
     */
    private static HttpStatus upstreamStatus(Throwable t) {
        WebClientResponseException responseException = findInCauseChain(t, WebClientResponseException.class);
        HttpStatus resolved = responseException == null
                ? null
                : HttpStatus.resolve(responseException.getStatusCode().value());
        return resolved != null ? resolved : HttpStatus.BAD_GATEWAY;
    }

    /**
     * The first throwable of the given type in {@code t}'s cause chain, {@code t} itself included, or {@code null} when
     * there is none.
     *
     * <p>
     * {@link #MAX_CAUSE_CHAIN_DEPTH} caps a cycle of any length; the self-cause check short-circuits the common
     * one-node case.
     */
    private static <E extends Throwable> E findInCauseChain(Throwable t, Class<E> type) {
        Throwable current = t;
        for (int depth = 0; current != null && depth < MAX_CAUSE_CHAIN_DEPTH; depth++) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            if (current == current.getCause()) {
                return null;
            }
            current = current.getCause();
        }
        return null;
    }

    /**
     * The pool pending-limit exception is a plain RuntimeException (unlike PoolAcquireTimeoutException, a
     * {@link TimeoutException} matched above). Match by name to avoid depending on Reactor-Netty's shaded internal pool
     * type.
     */
    @SuppressWarnings("java:S1872") // intentional name match — instanceof would couple to the shaded type
    private static boolean isPoolAcquireExhausted(Throwable t) {
        return "PoolAcquirePendingLimitException".equals(t.getClass().getSimpleName());
    }

    private static Mono<ClientResponse> handleHttpExceptions(ClientResponse clientResponse) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return Mono.just(clientResponse);
        }

        // Check if response is RFC 9457 problem+json format
        String contentType = clientResponse
                .headers()
                .contentType()
                .map(mediaType -> mediaType.toString().toLowerCase())
                .orElse("");

        if (contentType.contains(MediaType.APPLICATION_PROBLEM_JSON_VALUE)) {
            return handleProblemDetailResponse(clientResponse);
        }
        if (contentType.contains(MediaType.TEXT_HTML_VALUE)) {
            // defaultIfEmpty for the same reason as the legacy branches below: bodyToMono completes
            // empty for a zero-length body, so flatMap never runs and the failure escapes unmapped.
            return clientResponse
                    .bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono
                            .error(new ConnectorCommunicationException(
                                    "Received response with unexpected content type '%s'.".formatted(contentType),
                                    null)));
        }

        // Legacy error handling
        return handleLegacyErrorResponse(clientResponse);
    }

    /**
     * Map a non-2xx response with no {@code application/problem+json} body onto the connector exception its status
     * means.
     *
     * <p>
     * Each branch reads the body as a {@code String} for the exception message and needs {@code defaultIfEmpty} to do
     * it: {@code bodyToMono(String.class)} completes <em>empty</em> for a zero-length body, and an empty source never
     * runs {@code flatMap}, so without the default the filter completes empty and no exception is raised — the status
     * then vanishes into an unmapped {@link IllegalStateException}. Bodiless error statuses are ordinary: a Go
     * connector's {@code w.WriteHeader(404)} sends no body, and discovery's {@code cancel} is declared bodiless even on
     * success.
     */
    private static Mono<ClientResponse> handleLegacyErrorResponse(ClientResponse clientResponse) {
        if (clientResponse.statusCode().isSameCodeAs(HttpStatus.UNPROCESSABLE_ENTITY)) {
            return clientResponse
                    .bodyToMono(ERROR_LIST_TYPE_REF)
                    .defaultIfEmpty(List.of("Connector returned 422 with an empty body"))
                    .flatMap(body -> Mono
                            .error(new ValidationException(body.stream().map(ValidationError::create).toList())));
        }
        if (HttpStatus.NOT_FOUND.equals(clientResponse.statusCode())) {
            return clientResponse
                    .bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono.error(new ConnectorEntityNotFoundException(body)));
        }
        if (clientResponse.statusCode().is4xxClientError()) {
            return clientResponse
                    .bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono
                            .error(new ConnectorClientException(body,
                                    resolveOr(clientResponse.statusCode().value(), HttpStatus.BAD_REQUEST))));
        }
        if (clientResponse.statusCode().is5xxServerError()) {
            return clientResponse
                    .bodyToMono(String.class)
                    .defaultIfEmpty("")
                    .flatMap(body -> Mono
                            .error(new ConnectorServerException(body,
                                    resolveOr(clientResponse.statusCode().value(), HttpStatus.BAD_GATEWAY))));
        }
        return Mono.just(clientResponse);
    }

    private static Mono<ClientResponse> handleProblemDetailResponse(ClientResponse clientResponse) {
        // The status code is captured as an int and only resolved to an HttpStatus on the empty-body
        // path. HttpStatus.valueOf throws for a code with no enum constant — 499 and 430 both do — so
        // resolving it eagerly here would reject a perfectly good problem document purely because of the
        // status it arrived with.
        int statusCode = clientResponse.statusCode().value();
        return clientResponse
                .bodyToMono(ProblemDetailExtended.class)
                .<ClientResponse>flatMap(problemDetail -> Mono
                        .error(new ConnectorProblemException(withTransportStatus(problemDetail, statusCode))))
                .switchIfEmpty(Mono.error(() -> emptyProblemBodyFailure(statusCode)));
    }

    /**
     * A response labelled {@code application/problem+json} whose body is empty cannot become a
     * {@link ConnectorProblemException} — there is no {@code ErrorCode} to carry — so it maps by status alone, as
     * {@link #handleLegacyErrorResponse} does. Without this it completed empty and the status vanished into an unmapped
     * {@link IllegalStateException}.
     *
     * <p>
     * Derived from the status rather than by re-reading the body: a {@link ClientResponse} body can be consumed only
     * once, so delegating to the legacy handler here would fail on the second read.
     */
    private static ConnectorException emptyProblemBodyFailure(int statusCode) {
        String label = String.valueOf(statusCode);
        HttpStatus status = HttpStatus.resolve(statusCode);
        String message = "Connector returned %s with an empty application/problem+json body".formatted(label);
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return new ConnectorEntityNotFoundException(message);
        }
        // A code outside the enum still has a recognisable class, so classify on the hundreds digit
        // rather than losing the distinction between a client and a server fault.
        if (statusCode >= 400 && statusCode < 500) {
            return new ConnectorClientException(message, status == null ? HttpStatus.BAD_REQUEST : status);
        }
        return new ConnectorServerException(message, status == null ? HttpStatus.BAD_GATEWAY : status);
    }
}
