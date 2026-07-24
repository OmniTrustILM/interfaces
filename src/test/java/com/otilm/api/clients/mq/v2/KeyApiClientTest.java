package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.*;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.otilm.api.model.connector.cryptography.v2.MetadataTestUtils.stringMetadata;
import static org.junit.jupiter.api.Assertions.*;

class KeyApiClientTest {
    @Test
    void mapsProtocolTypesToExactConnectorPaths() throws Exception {
        RecordingProxy proxy = new RecordingProxy();
        ConnectorDto connector = new ConnectorDto();
        KeyApiClient client = new KeyApiClient(proxy);
        TokenProfileScopedRequestV2Dto request = new TokenProfileScopedRequestV2Dto();
        request.setKeyUsages(Set.of(KeyUsage.SIGN));

        proxy.response = new KeyRequestType[]{KeyRequestType.SECRET, KeyRequestType.KEY_PAIR};
        assertEquals(2, client.listSupportedKeyTypes(connector, request).size());
        proxy.assertCall("/v2/cryptographyProvider/keys/types", request, KeyRequestType[].class);

        proxy.response = new BaseAttribute[0];
        assertTrue(client.listCreateKeyAttributes(connector, KeyRequestType.SECRET, request).isEmpty());
        proxy.assertCall("/v2/cryptographyProvider/keys/secret/attributes", request, BaseAttribute[].class);

        assertTrue(client.listCreateKeyAttributes(connector, KeyRequestType.KEY_PAIR, request).isEmpty());
        proxy.assertCall("/v2/cryptographyProvider/keys/pair/attributes", request, BaseAttribute[].class);
    }

    @Test
    void preservesSynchronousAndAsynchronousCreationResponses() throws Exception {
        RecordingProxy proxy = new RecordingProxy();
        KeyApiClient client = new KeyApiClient(proxy);
        SecretKeyDataResponseV2Dto completed = new SecretKeyDataResponseV2Dto();
        SecretKeyDataV2Dto keyData = new SecretKeyDataV2Dto();
        keyData.setAlgorithm(KeyAlgorithm.AES);
        keyData.setLength(256);
        completed.setKeyData(keyData);
        completed.setKeyMeta(List.of(stringMetadata("keyId", "secret-1")));
        proxy.entityResponse = ResponseEntity.ok(completed);
        CreateKeyRequestV2Dto request = synchronousCreateRequest();
        request.setKeyCreationId("6c87b73a-5659-4d17-b625-abc72cd94150");

        assertSame(completed, client.createSecretKey(new ConnectorDto(), request).getBody());
        proxy.assertCall("/v2/cryptographyProvider/keys/secret", request, SecretKeyDataResponseV2Dto.class);
        assertEquals("6c87b73a-5659-4d17-b625-abc72cd94150",
                ((CreateKeyRequestV2Dto) proxy.body).getKeyCreationId());

        SecretKeyDataResponseV2Dto pending = new SecretKeyDataResponseV2Dto();
        pending.setOperationMeta(java.util.List.of(stringMetadata("operationId", "123")));
        proxy.entityResponse = ResponseEntity.accepted().body(pending);
        CreateKeyRequestV2Dto asyncRequest = new CreateKeyRequestV2Dto();
        asyncRequest.setExecutionMode(OperationExecutionMode.ASYNCHRONOUS);
        assertEquals(202, client.createSecretKey(new ConnectorDto(), asyncRequest).getStatusCode().value());
    }

    @Test
    void returnsRoleSpecificCompletedKeyPair() throws Exception {
        RecordingProxy proxy = new RecordingProxy();
        KeyApiClient client = new KeyApiClient(proxy);
        KeyPairDataResponseV2Dto completed = completedKeyPair();
        proxy.entityResponse = ResponseEntity.ok(completed);
        CreateKeyRequestV2Dto request = synchronousCreateRequest();

        assertSame(completed, client.createKeyPair(new ConnectorDto(), request).getBody());
        proxy.assertCall("/v2/cryptographyProvider/keys/pair", request, KeyPairDataResponseV2Dto.class);
        assertEquals(KeyAlgorithm.RSA, completed.getPrivateKeyData().getKeyData().getAlgorithm());
        assertEquals(KeyAlgorithm.RSA, completed.getPublicKeyData().getKeyData().getAlgorithm());
    }

    @Test
    void rejectsMalformedCompletedKeyData() {
        RecordingProxy proxy = new RecordingProxy();
        SecretKeyDataResponseV2Dto malformed = new SecretKeyDataResponseV2Dto();
        malformed.setKeyData(new SecretKeyDataV2Dto());
        proxy.entityResponse = ResponseEntity.ok(malformed);

        ConnectorException error = assertThrows(ConnectorException.class,
                () -> new KeyApiClient(proxy).createSecretKey(new ConnectorDto(), synchronousCreateRequest()));
        assertTrue(error.getMessage().contains("invalid completed key data"));
    }

    @Test
    void rejectsNullSupportedTypeEntries() {
        RecordingProxy proxy = new RecordingProxy();
        proxy.response = new KeyRequestType[]{KeyRequestType.SECRET, null};

        assertThrows(ConnectorException.class, () -> new KeyApiClient(proxy)
                .listSupportedKeyTypes(new ConnectorDto(), new TokenProfileScopedRequestV2Dto()));
    }

    private static KeyPairDataResponseV2Dto completedKeyPair() {
        PrivateKeyDataV2Dto privateKey = new PrivateKeyDataV2Dto();
        privateKey.setAlgorithm(KeyAlgorithm.RSA);
        privateKey.setLength(2048);
        PrivateKeyDataResponseV2Dto privateResponse = new PrivateKeyDataResponseV2Dto();
        privateResponse.setKeyData(privateKey);
        privateResponse.setKeyMeta(List.of(stringMetadata("keyId", "private-1")));

        PublicKeyDataV2Dto publicKey = new PublicKeyDataV2Dto();
        publicKey.setAlgorithm(KeyAlgorithm.RSA);
        publicKey.setLength(2048);
        PublicKeyDataResponseV2Dto publicResponse = new PublicKeyDataResponseV2Dto();
        publicResponse.setKeyData(publicKey);
        publicResponse.setKeyMeta(List.of(stringMetadata("keyId", "public-1")));

        KeyPairDataResponseV2Dto pair = new KeyPairDataResponseV2Dto();
        pair.setPrivateKeyData(privateResponse);
        pair.setPublicKeyData(publicResponse);
        pair.setKeyPairMeta(List.of(stringMetadata("pairId", "pair-1")));
        return pair;
    }

    private static CreateKeyRequestV2Dto synchronousCreateRequest() {
        CreateKeyRequestV2Dto request = new CreateKeyRequestV2Dto();
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        return request;
    }

    private static final class RecordingProxy implements ProxyClient {
        private String path;
        private String method;
        private Object body;
        private Class<?> responseType;
        private Object response;
        private ResponseEntity<?> entityResponse;

        @Override
        @SuppressWarnings("unchecked")
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method,
                                 Object body, Class<T> responseType) {
            record(path, method, body, responseType);
            return (T) response;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> ResponseEntity<T> sendRequestForEntity(ApiClientConnectorInfo connector, String path,
                                                          String method, Object body, Class<T> responseType) {
            record(path, method, body, responseType);
            return (ResponseEntity<T>) entityResponse;
        }

        private void record(String path, String method, Object body, Class<?> responseType) {
            this.path = path;
            this.method = method;
            this.body = body;
            this.responseType = responseType;
        }

        void assertCall(String expectedPath, Object expectedBody, Class<?> expectedType) {
            assertEquals(expectedPath, path);
            assertEquals("POST", method);
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
