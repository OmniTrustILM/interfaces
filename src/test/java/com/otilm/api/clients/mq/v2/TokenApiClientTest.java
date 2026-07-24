package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class TokenApiClientTest {
    @Test
    void usesExactV3PathsAndMethods() throws Exception {
        RecordingProxy proxy = new RecordingProxy();
        ConnectorDto connector = new ConnectorDto();
        TokenApiClient client = new TokenApiClient(proxy);

        proxy.response = new BaseAttribute[0];
        assertTrue(client.listTokenAttributes(connector).isEmpty());
        proxy.assertCall(connector, "/v2/cryptographyProvider/tokens/attributes", "GET", null, BaseAttribute[].class);

        TokenScopedRequestV2Dto status = new TokenScopedRequestV2Dto();
        TokenStatusResponseV2Dto statusResponse = new TokenStatusResponseV2Dto();
        proxy.response = statusResponse;
        assertSame(statusResponse, client.getTokenStatus(connector, status));
        proxy.assertCall(connector, "/v2/cryptographyProvider/tokens/status", "POST", status, TokenStatusResponseV2Dto.class);

        TokenScopedRequestV2Dto profile = new TokenScopedRequestV2Dto();
        proxy.response = new BaseAttribute[0];
        assertTrue(client.listTokenProfileAttributes(connector, profile).isEmpty());
        proxy.assertCall(connector, "/v2/cryptographyProvider/tokens/tokenProfile/attributes", "POST", profile, BaseAttribute[].class);

        proxy.response = new KeyUsage[]{KeyUsage.SIGN};
        assertEquals(List.of(KeyUsage.SIGN), client.listTokenProfileKeyUsages(connector, profile));
        proxy.assertCall(connector, "/v2/cryptographyProvider/tokens/tokenProfile/keyUsages", "POST", profile, KeyUsage[].class);
    }

    @Test
    void normalizesMqFailureWithoutRetainingSecretBearingCause() {
        RecordingProxy proxy = new RecordingProxy();
        proxy.failure = new IllegalStateException("expanded-pin-1234");
        TokenApiClient client = new TokenApiClient(proxy);

        Exception error = assertThrows(Exception.class,
                () -> client.getTokenStatus(new ConnectorDto(), new TokenScopedRequestV2Dto()));

        assertFalse(error.getMessage().contains("expanded-pin-1234"));
        assertNull(error.getCause());
    }

    private static final class RecordingProxy implements ProxyClient {
        private ApiClientConnectorInfo connector;
        private String path;
        private String method;
        private Object body;
        private Class<?> responseType;
        private Object response;
        private RuntimeException failure;

        @Override
        @SuppressWarnings("unchecked")
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method,
                                 Object body, Class<T> responseType) {
            this.connector = connector;
            this.path = path;
            this.method = method;
            this.body = body;
            this.responseType = responseType;
            if (failure != null)
                throw failure;
            return (T) response;
        }

        void assertCall(ApiClientConnectorInfo expectedConnector, String expectedPath, String expectedMethod,
                        Object expectedBody, Class<?> expectedType) {
            assertSame(expectedConnector, connector);
            assertEquals(expectedPath, path);
            assertEquals(expectedMethod, method);
            assertSame(expectedBody, body);
            assertEquals(expectedType, responseType);
        }

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo c, String p, String m, Object b, Class<T> t, Duration d) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo c, String p, String m, Map<String, String> v, Object b, Class<T> t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo c, String p, String m, Object b, Class<T> t) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo c, String p, String m, Object b, Class<T> t, Duration d) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo c, String p, String m, Map<String, String> v, Object b, Class<T> t, Duration d) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo c, String p, String m, Object b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo c, String p, String m, Object b, String type) {
            throw new UnsupportedOperationException();
        }
    }
}
