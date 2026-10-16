package com.otilm.api.clients.mq.v2;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusV2;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import com.otilm.api.testsupport.RecordingProxyClient.Invocation;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TokenApiClientMqTest {

    private static final String BASE_PATH = "/v2/cryptographyProvider/tokens";
    private static final String ATTRIBUTES_PATH = BASE_PATH + "/attributes";
    private static final String STATUS_PATH = BASE_PATH + "/status";
    private static final String PROFILE_ATTRIBUTES_PATH = BASE_PATH + "/tokenProfile/attributes";
    private static final String KEY_REQUEST_TYPES_PATH = BASE_PATH + "/keyRequestTypes";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private TokenApiClient client;
    private ConnectorDto connector;
    private RecordingProxyClient proxyClient;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        connector = new ConnectorDto();
        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new TokenApiClient(proxyClient, responseValidator);
    }

    @Test
    void listTokenAttributes_delegatesGetAndReturnsAttributes() throws ConnectorException {
        // given
        BaseAttribute attribute = validMetadataAttribute();
        proxyClient.respondWith(new BaseAttribute[]{attribute});

        // when
        List<BaseAttribute> result = client.listTokenAttributes(connector);

        // then
        assertEquals(List.of(attribute), result);
        assertPlainInvocation(ATTRIBUTES_PATH, "GET", null, BaseAttribute[].class);
    }

    @Test
    void listTokenAttributes_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new BaseAttribute[]{null});

        // when
        Executable call = () -> client.listTokenAttributes(connector);

        // then
        assertValidationFailure(call);
    }

    @Test
    void getTokenStatus_delegatesPostAndReturnsStatus() throws ConnectorException {
        // given
        TokenScopedRequestV2Dto request = tokenScopedRequest();
        TokenStatusResponseV2Dto response = validTokenStatus();
        proxyClient.respondWith(response);

        // when
        TokenStatusResponseV2Dto result = client.getTokenStatus(connector, request);

        // then
        assertSame(response, result);
        assertPlainInvocation(STATUS_PATH, "POST", request, TokenStatusResponseV2Dto.class);
    }

    @Test
    void getTokenStatus_rejectsInvalidResponse() {
        // given
        TokenStatusResponseV2Dto responseWithoutStatus = new TokenStatusResponseV2Dto();
        proxyClient.respondWith(responseWithoutStatus);

        // when
        Executable call = () -> client.getTokenStatus(connector, tokenScopedRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void listTokenProfileAttributes_delegatesPostAndReturnsAttributes() throws ConnectorException {
        // given
        TokenScopedRequestV2Dto request = tokenScopedRequest();
        BaseAttribute attribute = validMetadataAttribute();
        proxyClient.respondWith(new BaseAttribute[]{attribute});

        // when
        List<BaseAttribute> result = client.listTokenProfileAttributes(connector, request);

        // then
        assertEquals(List.of(attribute), result);
        assertPlainInvocation(PROFILE_ATTRIBUTES_PATH, "POST", request, BaseAttribute[].class);
    }

    @Test
    void listTokenProfileAttributes_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new BaseAttribute[]{null});

        // when
        Executable call = () -> client.listTokenProfileAttributes(connector, tokenScopedRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void listSupportedKeyRequestTypes_delegatesPostAndReturnsKeyRequestTypes() throws ConnectorException {
        // given
        TokenProfileScopedRequestV2Dto request = tokenProfileScopedRequest();
        KeyRequestType[] response = {KeyRequestType.SECRET, KeyRequestType.KEY_PAIR};
        proxyClient.respondWith(response);

        // when
        List<KeyRequestType> result = client.listSupportedKeyRequestTypes(connector, request);

        // then
        assertEquals(List.of(response), result);
        assertPlainInvocation(KEY_REQUEST_TYPES_PATH, "POST", request, KeyRequestType[].class);
    }

    @Test
    void listSupportedKeyRequestTypes_rejectsInvalidResponse() {
        // given
        proxyClient.respondWith(new KeyRequestType[]{null});

        // when
        Executable call = () -> client.listSupportedKeyRequestTypes(connector, tokenProfileScopedRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void getTokenStatus_wrapsProxyRuntimeFailure() {
        // given
        RuntimeException proxyFailure = new IllegalStateException("proxy failure");
        proxyClient.failWith(proxyFailure);

        // when
        Executable call = () -> client.getTokenStatus(connector, tokenScopedRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertSame(proxyFailure, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    private void assertPlainInvocation(String path, String method, Object body, Class<?> responseType) {
        Invocation invocation = proxyClient.invocation();
        assertSame(connector, invocation.connector());
        assertEquals(path, invocation.path());
        assertEquals(method, invocation.method());
        if (body == null) {
            assertNull(invocation.body());
        } else {
            assertSame(body, invocation.body());
        }
        assertEquals(responseType, invocation.responseType());
        assertFalse(invocation.entityResponse());
    }

    private void assertValidationFailure(Executable call) {
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    private static TokenStatusResponseV2Dto validTokenStatus() {
        TokenStatusResponseV2Dto response = new TokenStatusResponseV2Dto();
        response.setStatus(TokenStatusV2.CONNECTED);
        return response;
    }

    private static TokenScopedRequestV2Dto tokenScopedRequest() {
        return withValidTokenScope(new TokenScopedRequestV2Dto());
    }

    private static TokenProfileScopedRequestV2Dto tokenProfileScopedRequest() {
        return withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
    }
}
