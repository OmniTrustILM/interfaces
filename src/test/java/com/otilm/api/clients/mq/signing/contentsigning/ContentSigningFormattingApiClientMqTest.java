package com.otilm.api.clients.mq.signing.contentsigning;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.clients.signing.contentsigning.ContentSigningFormattingPaths;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingOperation;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedSignatureValueRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedTimestampRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationScopedRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationStatusResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.TimestampImprintResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.ContentSigningDeclarationDto;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.SignatureFamilyDeclarationDto;
import com.otilm.api.model.connector.signatures.contentsigning.declaration.SignatureLevelDeclarationDto;
import com.otilm.api.model.core.connector.ConnectorDto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Delegation tests: each method must reach {@link ProxyClient} with the right connector, path, method, body and
 * response type. The proxy is a hand-written fake because no mocking framework is on this project's test classpath.
 */
class ContentSigningFormattingApiClientMqTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private RecordingProxyClient proxyClient;
    private ConnectorDto connector;
    private ContentSigningFormattingApiClient client;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        connector = new ConnectorDto();
        connector.setUrl("http://localhost");
        client = new ContentSigningFormattingApiClient(proxyClient, VALIDATOR);
    }

    @Test
    void getDeclaration_delegatesGetToTheDeclarationRoute() throws ConnectorException {
        ContentSigningDeclarationDto declaration = declarationUpTo(SignatureLevel.SIGNED);
        proxyClient.syncResponse = declaration;

        ContentSigningDeclarationDto result = client.getDeclaration(connector);

        Assertions.assertSame(declaration, result);
        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/declaration", "GET", null,
                        ContentSigningDeclarationDto.class);
    }

    @Test
    void anAbsentDeclarationIsRejected() {
        proxyClient.syncResponse = null;

        Assertions.assertThrows(ConnectorException.class, () -> client.getDeclaration(connector));
    }

    @Test
    void aDeclarationBreakingItsRulesIsRejected() {
        ContentSigningDeclarationDto fromTimestamped = declarationUpTo(SignatureLevel.TIMESTAMPED);
        fromTimestamped.getFamilies().getFirst().getLevels().removeFirst();
        proxyClient.syncResponse = fromTimestamped;

        ConnectorException fault = Assertions
                .assertThrows(ConnectorException.class, () -> client.getDeclaration(connector));

        Assertions
                .assertEquals(
                        "Connector returned an invalid content-signing declaration: families[0].levels: must run "
                                + "up from SIGNED without a gap, but TIMESTAMPED comes where SIGNED belongs",
                        fault.getMessage());
    }

    @Test
    void listFormattingAttributes_delegatesGetToTheOperationsAttributeRoute() throws ConnectorException {
        BaseAttribute attribute = new InfoAttributeV3();
        proxyClient.syncResponse = new BaseAttribute[]{attribute};

        List<BaseAttribute> result = client
                .listFormattingAttributes(connector, ContentSigningFormattingOperation.EXTEND_TO_LEVEL);

        Assertions.assertEquals(List.of(attribute), result);
        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/extendToLevel/attributes", "GET", null,
                        BaseAttribute[].class);
    }

    @Test
    void listFormattingAttributes_returnsAMutableList() throws ConnectorException {
        proxyClient.syncResponse = new BaseAttribute[0];

        List<BaseAttribute> result = client
                .listFormattingAttributes(connector, ContentSigningFormattingOperation.COMPUTE_DTBS);

        Assertions.assertDoesNotThrow(() -> result.add(new InfoAttributeV3()));
    }

    @Test
    void computeDtbs_delegatesPost() throws ConnectorException {
        ComputeDtbsRequestDto request = new ComputeDtbsRequestDto();
        ComputeDtbsResponseDto expected = new ComputeDtbsResponseDto();
        proxyClient.syncResponse = expected;

        ComputeDtbsResponseDto result = client.computeDtbs(connector, request);

        Assertions.assertSame(expected, result);
        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/computeDtbs", "POST", request,
                        ComputeDtbsResponseDto.class);
    }

    @Test
    void embedSignatureValue_delegatesPost() throws ConnectorException {
        EmbedSignatureValueRequestDto request = new EmbedSignatureValueRequestDto();
        SignedDocumentResponseDto expected = new SignedDocumentResponseDto();
        proxyClient.syncResponse = expected;

        SignedDocumentResponseDto result = client.embedSignatureValue(connector, request);

        Assertions.assertSame(expected, result);
        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/embedSignatureValue", "POST", request,
                        SignedDocumentResponseDto.class);
    }

    @Test
    void computeSignatureTimestampImprint_delegatesPost() throws ConnectorException {
        SignedDocumentRequestDto request = new SignedDocumentRequestDto();
        TimestampImprintResponseDto expected = new TimestampImprintResponseDto();
        proxyClient.syncResponse = expected;

        TimestampImprintResponseDto result = client.computeSignatureTimestampImprint(connector, request);

        Assertions.assertSame(expected, result);
        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/computeSignatureTimestampImprint",
                        "POST", request, TimestampImprintResponseDto.class);
    }

    /** Identical signature to the signature-timestamp imprint, so the route is the only thing separating them. */
    @Test
    void computeArchiveTimestampImprint_delegatesPostToItsOwnRoute() throws ConnectorException {
        SignedDocumentRequestDto request = new SignedDocumentRequestDto();
        proxyClient.syncResponse = new TimestampImprintResponseDto();

        client.computeArchiveTimestampImprint(connector, request);

        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/computeArchiveTimestampImprint", "POST",
                        request, TimestampImprintResponseDto.class);
    }

    @Test
    void embedSignatureTimestamp_delegatesPost() throws ConnectorException {
        EmbedTimestampRequestDto request = new EmbedTimestampRequestDto();
        proxyClient.syncResponse = new SignedDocumentResponseDto();

        client.embedSignatureTimestamp(connector, request);

        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/embedSignatureTimestamp", "POST",
                        request, SignedDocumentResponseDto.class);
    }

    /**
     * Shares its signature with {@code embedSignatureTimestamp}; only the route separates TIMESTAMPED from ARCHIVAL.
     */
    @Test
    void embedArchiveTimestamp_delegatesPostToItsOwnRoute() throws ConnectorException {
        EmbedTimestampRequestDto request = new EmbedTimestampRequestDto();
        proxyClient.syncResponse = new SignedDocumentResponseDto();

        client.embedArchiveTimestamp(connector, request);

        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/embedArchiveTimestamp", "POST", request,
                        SignedDocumentResponseDto.class);
    }

    @Test
    void extendToLevel_delegatesPost() throws ConnectorException {
        ExtendToLevelRequestDto request = new ExtendToLevelRequestDto();
        proxyClient.syncResponse = new ExtendToLevelResponseDto();

        client.extendToLevel(connector, request);

        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/extendToLevel", "POST", request,
                        ExtendToLevelResponseDto.class);
    }

    /** The body guarantee is the same on both transports, or a null body reaches Core from one of them only. */
    @Test
    void extendToLevel_rejectsAnEmptyBody() {
        proxyClient.syncResponse = null;

        Assertions
                .assertThrows(ConnectorException.class,
                        () -> client.extendToLevel(connector, new ExtendToLevelRequestDto()));
    }

    /** The MQ transport must draw the same 200/202 distinction as REST, or the two clients disagree on the wire. */
    @Test
    void extendToLevel_keepsAnAcceptedStatusDistinctFromAResult() throws ConnectorException {
        proxyClient.syncResponse = new ExtendToLevelResponseDto();
        proxyClient.syncStatus = HttpStatus.ACCEPTED;

        ResponseEntity<ExtendToLevelResponseDto> result = client
                .extendToLevel(connector, new ExtendToLevelRequestDto());

        Assertions.assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
    }

    @Test
    void getExtendToLevelStatus_delegatesToTheStatusCompanion() throws ConnectorException {
        ExtendOperationScopedRequestDto request = new ExtendOperationScopedRequestDto();
        proxyClient.syncResponse = new ExtendOperationStatusResponseDto();

        client.getExtendToLevelStatus(connector, request);

        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/extendToLevel/status", "POST", request,
                        ExtendOperationStatusResponseDto.class);
    }

    /** Cancellation answers 204, so requiring a body here would turn a successful abort into a failure. */
    @Test
    void cancelExtendToLevel_delegatesAndToleratesAnAbsentBody() throws ConnectorException {
        ExtendOperationScopedRequestDto request = new ExtendOperationScopedRequestDto();
        proxyClient.syncResponse = null;

        ResponseEntity<Void> response = client.cancelExtendToLevel(connector, request);

        Assertions
                .assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
                        "a proxy that reports 200 must still surface cancellation's contract status");
        proxyClient
                .assertCalled(connector, ContentSigningFormattingPaths.BASE + "/extendToLevel/cancel", "POST", request,
                        Void.class);
    }

    /** An absent body is a non-conformant response, not an empty result, and must not reach the caller as null. */
    @Test
    void anAbsentBodyIsRejectedNamingTheOperation() {
        proxyClient.syncResponse = null;

        ConnectorException failure = Assertions
                .assertThrows(ConnectorException.class,
                        () -> client.computeDtbs(connector, new ComputeDtbsRequestDto()));

        Assertions.assertTrue(failure.getMessage().contains("computeDtbs"), failure.getMessage());
    }

    @Test
    void anAbsentAttributeBodyIsRejected() {
        proxyClient.syncResponse = null;

        Assertions
                .assertThrows(ConnectorException.class, () -> client
                        .listFormattingAttributes(connector, ContentSigningFormattingOperation.COMPUTE_DTBS));
    }

    @Test
    void theProxyIsRequired() {
        Assertions
                .assertThrows(NullPointerException.class, () -> new ContentSigningFormattingApiClient(null, VALIDATOR));
    }

    private static ContentSigningDeclarationDto declarationUpTo(SignatureLevel highest) {
        SignatureFamilyDeclarationDto pades = new SignatureFamilyDeclarationDto();
        pades.setCode(SignatureFamily.PADES);
        pades.setLabel("PAdES");
        pades
                .setLevels(Arrays
                        .stream(SignatureLevel.values())
                        .filter(level -> level.compareTo(highest) <= 0)
                        .map(level -> new SignatureLevelDeclarationDto(level, "PAdES " + level.name()))
                        .collect(Collectors.toCollection(ArrayList::new)));
        pades.setTransferModes(Set.of(DocumentTransferMode.INLINE));
        return new ContentSigningDeclarationDto(new ArrayList<>(List.of(pades)));
    }

    /** Named {@code seenConnector}: reusing the test's own field name would make {@code assertCalled} tautological. */
    private static final class RecordingProxyClient extends UnsupportedProxyClient {
        private ApiClientConnectorInfo seenConnector;
        private String path;
        private String method;
        private Object body;
        private Class<?> responseType;

        private Object syncResponse;
        private HttpStatus syncStatus = HttpStatus.OK;

        @Override
        @SuppressWarnings("unchecked")
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body,
                Class<T> responseType) {
            this.seenConnector = connector;
            this.path = path;
            this.method = method;
            this.body = body;
            this.responseType = responseType;
            return (T) syncResponse;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> ResponseEntity<T> sendRequestForEntity(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType) {
            this.seenConnector = connector;
            this.path = path;
            this.method = method;
            this.body = body;
            this.responseType = responseType;
            return ResponseEntity.status(syncStatus).body((T) syncResponse);
        }

        private void assertCalled(ApiClientConnectorInfo expectedConnector, String expectedPath, String expectedMethod,
                Object expectedBody, Class<?> expectedResponseType) {
            Assertions
                    .assertSame(expectedConnector, seenConnector, "the caller's connector must be forwarded unchanged");
            Assertions.assertEquals(expectedPath, path);
            Assertions.assertEquals(expectedMethod, method);
            Assertions.assertSame(expectedBody, body);
            Assertions.assertEquals(expectedResponseType, responseType);
        }
    }

    /**
     * Every {@link ProxyClient} member this client must not touch; a {@code Duration} overload means a bespoke timeout.
     */
    private abstract static class UnsupportedProxyClient implements ProxyClient {

        private static final String BESPOKE_TIMEOUT = "the content-signing formatting MQ client takes the proxy's default timeout";

        // sendRequest(…) without a Duration is left abstract: it is the one overload this client may
        // call, so the concrete fake supplies it.

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body,
                Class<T> responseType, Duration timeout) {
            throw new AssertionError(BESPOKE_TIMEOUT + " - a Duration overload was called for " + path);
        }

        @Override
        public <T> ResponseEntity<T> sendRequestForEntity(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType, Duration timeout) {
            throw new AssertionError(BESPOKE_TIMEOUT + " - a Duration overload was called for " + path);
        }

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method,
                Map<String, String> pathVariables, Object body, Class<T> responseType) {
            throw new AssertionError("routes are built by ContentSigningFormattingPaths, not by proxy path variables");
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType, Duration timeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
                Map<String, String> pathVariables, Object body, Class<T> responseType, Duration timeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body,
                String messageType) {
            throw new UnsupportedOperationException();
        }
    }
}
