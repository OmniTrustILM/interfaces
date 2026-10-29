package com.otilm.api.clients.mq.v2;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateRequestCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CreateCertificateRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignatureAlgorithmAttribute;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import com.otilm.api.testsupport.RecordingProxyClient.Invocation;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.CERTIFICATE_REQUEST_ALGORITHM;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.certificationRequestInfo;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.pkcs10;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.signerIdDefinition;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validBoundCertificateRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateIssuedRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateIssuedResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.KeyLifecycleDtoFixtures.validCertificateRequestResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyLifecycleApiClientMqTest {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keyLifecycle";
    private static final String SIGNER_ATTRIBUTES_PATH = BASE_PATH + "/signer/attributes";
    private static final String CERTIFICATE_REQUEST_PATH = BASE_PATH + "/csr";
    private static final String CERTIFICATE_REQUEST_ATTRIBUTES_PATH = CERTIFICATE_REQUEST_PATH + "/attributes";
    private static final String CERTIFICATE_ISSUED_PATH = BASE_PATH + "/certificate";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private KeyLifecycleApiClient client;
    private ConnectorDto connector;
    private RecordingProxyClient proxyClient;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        connector = new ConnectorDto();
        OperationResponseValidator responseValidator = new OperationResponseValidator(VALIDATORS.validator());
        client = new KeyLifecycleApiClient(proxyClient, responseValidator);
    }

    @Test
    void listSignerAttributes_delegatesPostAndReturnsTheDefinitions() throws ConnectorException {
        // given
        TokenProfileScopedRequestV2Dto request = tokenProfileScopedRequest();
        BaseAttribute definition = signerIdDefinition();
        proxyClient.respondWith(new BaseAttribute[]{definition});

        // when
        List<BaseAttribute> result = client.listSignerAttributes(connector, request);

        // then
        assertEquals(List.of(definition), result);
        assertPostInvocation(SIGNER_ATTRIBUTES_PATH, request, BaseAttribute[].class, false);
    }

    @Test
    void listSignerAttributes_rejectsAnEmptyListing() {
        // given
        proxyClient.respondWith(new BaseAttribute[0]);

        // when
        Executable call = () -> client.listSignerAttributes(connector, tokenProfileScopedRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void listSignerAttributes_rejectsAnEmptyBody() {
        // given
        proxyClient.respondWith(null);

        // when
        Executable call = () -> client.listSignerAttributes(connector, tokenProfileScopedRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertEquals("Connector returned an empty body for " + SIGNER_ATTRIBUTES_PATH, exception.getMessage());
        assertSame(connector, exception.getConnector());
    }

    @Test
    void listSignerAttributes_wrapsProxyRuntimeFailure() {
        // given
        RuntimeException proxyFailure = new IllegalStateException("proxy failure");
        proxyClient.failWith(proxyFailure);

        // when
        Executable call = () -> client.listSignerAttributes(connector, tokenProfileScopedRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertSame(proxyFailure, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    @Test
    void listCertificateRequestAttributes_delegatesPostAndReturnsTheAlgorithmListing() throws ConnectorException {
        // given
        KeyScopedRequestV2Dto request = keyScopedRequest();
        BaseAttribute definition = SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        proxyClient.respondWith(new BaseAttribute[]{definition});

        // when
        List<BaseAttribute> result = client.listCertificateRequestAttributes(connector, request);

        // then
        assertEquals(List.of(definition), result);
        assertPostInvocation(CERTIFICATE_REQUEST_ATTRIBUTES_PATH, request, BaseAttribute[].class, false);
    }

    @Test
    void listCertificateRequestAttributes_rejectsAListingWithoutTheSignatureAlgorithm() {
        // given
        proxyClient.respondWith(new BaseAttribute[]{signerIdDefinition()});

        // when
        Executable call = () -> client.listCertificateRequestAttributes(connector, keyScopedRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void createCertificateRequest_delegatesPostAndReturnsThePkcs10() throws ConnectorException {
        // given
        CreateCertificateRequestV2Dto request = validBoundCertificateRequest();
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        proxyClient.respondWithEntity(ResponseEntity.ok(response));

        // when
        CertificateRequestCreationResponseV2Dto result = client.createCertificateRequest(connector, request);

        // then
        assertSame(response, result);
        assertPostInvocation(CERTIFICATE_REQUEST_PATH, request, CertificateRequestCreationResponseV2Dto.class, true);
    }

    @Test
    void createCertificateRequest_rejectsAPkcs10ForAnotherSubject() {
        // given
        CertificateRequestCreationResponseV2Dto response = validCertificateRequestResponse();
        response.setPkcs10(pkcs10(certificationRequestInfo("CN=John Roe"), CERTIFICATE_REQUEST_ALGORITHM));
        proxyClient.respondWithEntity(ResponseEntity.ok(response));

        // when
        Executable call = () -> client.createCertificateRequest(connector, validBoundCertificateRequest());

        // then
        ConnectorException exception = assertValidationFailure(call);
        assertTrue(exception.getMessage().contains("another subject"), exception.getMessage());
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"CREATED", "ACCEPTED", "PARTIAL_CONTENT"})
    void createCertificateRequest_rejectsUnexpectedSuccessStatusWithValidBody(HttpStatus status) {
        // given
        proxyClient.respondWithEntity(ResponseEntity.status(status).body(validCertificateRequestResponse()));

        // when
        Executable call = () -> client.createCertificateRequest(connector, validBoundCertificateRequest());

        // then
        assertUnexpectedSuccessStatus(status, call);
    }

    @Test
    void certificateIssued_delegatesPostAndReturnsTheEvents() throws ConnectorException {
        // given
        CertificateIssuedRequestV2Dto request = validCertificateIssuedRequest();
        CertificateIssuedResponseV2Dto response = validCertificateIssuedResponse();
        proxyClient.respondWithEntity(ResponseEntity.ok(response));

        // when
        CertificateIssuedResponseV2Dto result = client.certificateIssued(connector, request);

        // then
        assertSame(response, result);
        assertPostInvocation(CERTIFICATE_ISSUED_PATH, request, CertificateIssuedResponseV2Dto.class, true);
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"CREATED", "ACCEPTED", "PARTIAL_CONTENT"})
    void certificateIssued_rejectsUnexpectedSuccessStatusWithValidBody(HttpStatus status) {
        // given
        proxyClient.respondWithEntity(ResponseEntity.status(status).body(validCertificateIssuedResponse()));

        // when
        Executable call = () -> client.certificateIssued(connector, validCertificateIssuedRequest());

        // then
        assertUnexpectedSuccessStatus(status, call);
    }

    @Test
    void certificateIssued_rejectsAnEmptyBody() {
        // given
        proxyClient.respondWithEntity(ResponseEntity.ok().build());

        // when
        Executable call = () -> client.certificateIssued(connector, validCertificateIssuedRequest());

        // then
        assertValidationFailure(call);
    }

    @Test
    void certificateIssued_wrapsProxyRuntimeFailure() {
        // given
        RuntimeException proxyFailure = new IllegalStateException("proxy failure");
        proxyClient.failWith(proxyFailure);

        // when
        Executable call = () -> client.certificateIssued(connector, validCertificateIssuedRequest());

        // then
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertSame(proxyFailure, exception.getCause());
        assertSame(connector, exception.getConnector());
    }

    private void assertPostInvocation(String path, Object body, Class<?> responseType, boolean entityResponse) {
        Invocation invocation = proxyClient.invocation();
        assertSame(connector, invocation.connector());
        assertEquals(path, invocation.path());
        assertEquals("POST", invocation.method());
        assertSame(body, invocation.body());
        assertEquals(responseType, invocation.responseType());
        assertEquals(entityResponse, invocation.entityResponse());
    }

    private ConnectorException assertValidationFailure(Executable call) {
        ConnectorException exception = assertThrows(ConnectorException.class, call);
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
        assertSame(connector, exception.getConnector());
        return exception;
    }

    private void assertUnexpectedSuccessStatus(HttpStatus status, Executable call) {
        ConnectorException exception = assertValidationFailure(call);
        assertEquals("Connector returned HTTP " + status.value() + "; expected HTTP 200", exception.getMessage());
    }

    private static TokenProfileScopedRequestV2Dto tokenProfileScopedRequest() {
        return withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
    }

    private static KeyScopedRequestV2Dto keyScopedRequest() {
        KeyScopedRequestV2Dto request = withValidTokenProfileScope(new KeyScopedRequestV2Dto());
        request.setKeyMeta(validMetadata());
        return request;
    }
}
