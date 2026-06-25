package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackRequestDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackResponseDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeDefinitionsDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.otilm.api.model.core.connector.ConnectorDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Delegation tests for the MQ-based Attributes v2 client. Verifies each method calls
 * {@link ProxyClient#sendRequest} (or its async variant) with the right path / HTTP method / body /
 * response type and returns the proxy's value.
 *
 * <p>No mocking framework (Mockito etc.) is on this project's test classpath — only JUnit Jupiter and
 * WireMock — so the proxy is a hand-written recording fake.</p>
 *
 * <p><b>Out of scope — AC5 / MQ error parity is a Core concern.</b> The {@link ProxyClient}
 * implementation maps connector errors by raw HTTP status code and never reconstructs a
 * {@code ConnectorProblemException} from the connector's {@code errorCode}, so a connector 404
 * ({@code ATTRIBUTE_DEFINITION_NOT_FOUND}) over MQ loses its error code. That parity fix is tracked as a
 * Core gate (core #1622/#1621), not #726, so this suite does NOT assert error mapping over MQ. Note too that
 * {@code ProxyClient} has no query-string notion — the exploded {@code uuids} string rides in the path
 * (verified below); whether it survives the bus is a separate Core/MQ gate.</p>
 */
class AttributesApiClientMqTest {

    private static final String ATTRIBUTES_PATH = "/v2/attributes";

    private RecordingProxyClient proxyClient;
    private AttributesApiClient client;
    private ConnectorDto connector;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        client = new AttributesApiClient(proxyClient);
        connector = new ConnectorDto();
        connector.setUrl("http://localhost");
    }

    @Test
    void listDefinitions_noUuids_delegatesGetFullRegistry() throws ConnectorException {
        AttributeDefinitionsDto expected = new AttributeDefinitionsDto();
        proxyClient.syncResponse = expected;

        AttributeDefinitionsDto result = client.listDefinitions(connector, null);

        Assertions.assertSame(expected, result);
        Assertions.assertSame(connector, proxyClient.connector);
        Assertions.assertEquals(ATTRIBUTES_PATH, proxyClient.path);
        Assertions.assertEquals("GET", proxyClient.method);
        Assertions.assertNull(proxyClient.body);
        Assertions.assertEquals(AttributeDefinitionsDto.class, proxyClient.responseType);
    }

    @Test
    void listDefinitions_withUuids_delegatesExplodedPath() throws ConnectorException {
        UUID a = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID b = UUID.fromString("22222222-2222-2222-2222-222222222222");
        AttributeDefinitionsDto expected = new AttributeDefinitionsDto();
        proxyClient.syncResponse = expected;

        AttributeDefinitionsDto result = client.listDefinitions(connector, List.of(a, b));

        Assertions.assertSame(expected, result);
        // Exploded form (NOT csv) carried in the path string since the proxy has no query notion.
        Assertions.assertEquals(ATTRIBUTES_PATH + "?uuids=" + a + "&uuids=" + b, proxyClient.path);
        Assertions.assertEquals("GET", proxyClient.method);
        Assertions.assertNull(proxyClient.body);
    }

    @Test
    void getDefinition_delegatesGetByUuidPath() throws ConnectorException {
        UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BaseAttribute expected = new InfoAttributeV3();
        proxyClient.syncResponse = expected;

        BaseAttribute result = client.getDefinition(connector, uuid);

        Assertions.assertSame(expected, result);
        Assertions.assertEquals(ATTRIBUTES_PATH + "/" + uuid, proxyClient.path);
        Assertions.assertEquals("GET", proxyClient.method);
        Assertions.assertNull(proxyClient.body);
        Assertions.assertEquals(BaseAttribute.class, proxyClient.responseType);
    }

    @Test
    void callback_delegatesPostWithBody() throws ConnectorException {
        AttributeCallbackRequestDto request = new AttributeCallbackRequestDto();
        request.setConnectorInterface(ConnectorInterface.AUTHORITY);
        request.setInterfaceVersion("v3");
        request.setAttributeUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        request.setAttributeName("someAttr");
        request.setContextAttributes(List.of());
        request.setCurrentAttributes(List.of());

        AttributeCallbackResponseDto expected = new AttributeCallbackResponseDto();
        proxyClient.syncResponse = expected;

        AttributeCallbackResponseDto result = client.callback(connector, request);

        Assertions.assertSame(expected, result);
        Assertions.assertEquals(ATTRIBUTES_PATH + "/callback", proxyClient.path);
        Assertions.assertEquals("POST", proxyClient.method);
        Assertions.assertSame(request, proxyClient.body);
        Assertions.assertEquals(AttributeCallbackResponseDto.class, proxyClient.responseType);
    }

    @Test
    void getDefinitionAsync_delegatesToAsyncProxy() {
        UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
        BaseAttribute expected = new InfoAttributeV3();
        proxyClient.asyncResponse = CompletableFuture.completedFuture(expected);

        CompletableFuture<BaseAttribute> result = client.getDefinitionAsync(connector, uuid);

        Assertions.assertSame(expected, result.join());
        Assertions.assertEquals(ATTRIBUTES_PATH + "/" + uuid, proxyClient.path);
        Assertions.assertEquals("GET", proxyClient.method);
        Assertions.assertNull(proxyClient.body);
        Assertions.assertEquals(BaseAttribute.class, proxyClient.responseType);
    }

    @Test
    void listDefinitionsAsync_withUuids_delegatesExplodedPath() {
        UUID a = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID b = UUID.fromString("22222222-2222-2222-2222-222222222222");
        AttributeDefinitionsDto expected = new AttributeDefinitionsDto();
        proxyClient.asyncResponse = CompletableFuture.completedFuture(expected);

        CompletableFuture<AttributeDefinitionsDto> result =
                client.listDefinitionsAsync(connector, List.of(a, b));

        Assertions.assertSame(expected, result.join());
        // The exploded-uuids path build is the only async logic that differs from the sync path.
        Assertions.assertEquals(ATTRIBUTES_PATH + "?uuids=" + a + "&uuids=" + b, proxyClient.path);
        Assertions.assertEquals("GET", proxyClient.method);
        Assertions.assertNull(proxyClient.body);
        Assertions.assertEquals(AttributeDefinitionsDto.class, proxyClient.responseType);
    }

    @Test
    void callbackAsync_delegatesPostWithBody() {
        AttributeCallbackRequestDto request = new AttributeCallbackRequestDto();
        request.setConnectorInterface(ConnectorInterface.AUTHORITY);
        request.setInterfaceVersion("v3");
        request.setAttributeUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        request.setAttributeName("someAttr");
        request.setContextAttributes(List.of());
        request.setCurrentAttributes(List.of());

        AttributeCallbackResponseDto expected = new AttributeCallbackResponseDto();
        proxyClient.asyncResponse = CompletableFuture.completedFuture(expected);

        CompletableFuture<AttributeCallbackResponseDto> result = client.callbackAsync(connector, request);

        Assertions.assertSame(expected, result.join());
        Assertions.assertEquals(ATTRIBUTES_PATH + "/callback", proxyClient.path);
        Assertions.assertEquals("POST", proxyClient.method);
        Assertions.assertSame(request, proxyClient.body);
        Assertions.assertEquals(AttributeCallbackResponseDto.class, proxyClient.responseType);
    }

    /**
     * Records the arguments of the single {@code sendRequest}/{@code sendRequestAsync} call under test and
     * returns the preset response. Only the two overloads exercised by the client are implemented; the
     * rest throw to surface any unexpected delegation.
     */
    private static final class RecordingProxyClient implements ProxyClient {
        private ApiClientConnectorInfo connector;
        private String path;
        private String method;
        private Object body;
        private Class<?> responseType;

        private Object syncResponse;
        private CompletableFuture<?> asyncResponse;

        @Override
        @SuppressWarnings("unchecked")
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body, Class<T> responseType) {
            this.connector = connector;
            this.path = path;
            this.method = method;
            this.body = body;
            this.responseType = responseType;
            return (T) syncResponse;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method, Object body, Class<T> responseType) {
            this.connector = connector;
            this.path = path;
            this.method = method;
            this.body = body;
            this.responseType = responseType;
            return (CompletableFuture<T>) asyncResponse;
        }

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body, Class<T> responseType, Duration timeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Map<String, String> pathVariables, Object body, Class<T> responseType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method, Object body, Class<T> responseType, Duration timeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method, Map<String, String> pathVariables, Object body, Class<T> responseType, Duration timeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body, String messageType) {
            throw new UnsupportedOperationException();
        }
    }
}
