package com.otilm.api.clients.signing.contentsigning;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.model.connector.signatures.contentsigning.common.CertificateChainDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ComputeDtbsResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingOperation;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedSignatureValueRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.EmbedTimestampRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationScopedRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendOperationStatusResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ExtendToLevelResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.FetchedArtifactKind;
import com.otilm.api.model.connector.signatures.contentsigning.common.InlineDocumentTransferDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentRequestDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.SignedDocumentResponseDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.TimestampImprintResponseDto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Routes are literals here and constants in {@link ContentSigningFormattingPathsTest}. Keep it that way: the literals
 * are the independent pin on the constants.
 */
class ContentSigningFormattingApiClientTest {

    private static final String BASE = "/v1/signatureProvider/contentSigning";

    private ContentSigningFormattingApiClient client;
    private ConnectorDto connector;
    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        client = new ContentSigningFormattingApiClient(BaseApiClient.prepareWebClient(), null);

        mockServer = new WireMockServer(options().dynamicPort());
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());

        connector = new ConnectorDto();
        connector.setName("ades-connector-under-test");
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void listFormattingAttributes_readsTheOperationsOwnRoute() throws ConnectorException {
        String json = """
                [
                  { "uuid": "11111111-1111-1111-1111-111111111111", "name": "signaturePacking", "type": "data", "contentType": "string", "version": 2 }
                ]
                """;
        stubGet(BASE + "/computeDtbs/attributes", json);

        List<BaseAttribute> result = client
                .listFormattingAttributes(connector, ContentSigningFormattingOperation.COMPUTE_DTBS);

        assertEquals(1, result.size());
        assertEquals("signaturePacking", result.get(0).getName());
    }

    @Test
    void listFormattingAttributes_bindsTheOperationWireCodeIntoThePath() throws ConnectorException {
        stubGet(BASE + "/embedArchiveTimestamp/attributes", "[]");

        List<BaseAttribute> result = client
                .listFormattingAttributes(connector, ContentSigningFormattingOperation.EMBED_ARCHIVE_TIMESTAMP);

        assertEquals(0, result.size());
    }

    /** The array is wrapped by this client, so the wrapper — not Jackson — decides mutability. */
    @Test
    void listFormattingAttributes_returnsAMutableList() throws ConnectorException {
        stubGet(BASE + "/computeDtbs/attributes", "[]");

        List<BaseAttribute> result = client
                .listFormattingAttributes(connector, ContentSigningFormattingOperation.COMPUTE_DTBS);

        assertDoesNotThrow(() -> result.add(null));
    }

    @Test
    void computeDtbs_postsAndReturnsTheDtbs() throws ConnectorException {
        stubPost(BASE + "/computeDtbs", """
                { "dtbs": "AQID", "formattingContext": "BAUG" }
                """);

        ComputeDtbsResponseDto result = client.computeDtbs(connector, padesRequest());

        assertArrayEquals(new byte[]{1, 2, 3}, result.getDtbs());
        assertArrayEquals(new byte[]{4, 5, 6}, result.getFormattingContext());
    }

    @Test
    void computeDtbs_carriesEachFamilyItIsGiven() throws ConnectorException {
        stubPost(BASE + "/computeDtbs", "{ \"dtbs\": \"AQID\", \"formattingContext\": \"BAUG\" }");

        ComputeDtbsRequestDto request = new ComputeDtbsRequestDto();
        request.setFamily(SignatureFamily.JADES);
        request.setDocument(new InlineDocumentTransferDto(new byte[]{7, 7, 7}));
        request.setSignerCertificateChain(List.of(new byte[]{9}));
        request.setSigningTime(OffsetDateTime.parse("2026-08-11T10:15:30Z"));
        request.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_RSA);
        request.setFormattingAttributes(List.of());

        client.computeDtbs(connector, request);

        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(BASE + "/computeDtbs"))
                        .withRequestBody(WireMock.matchingJsonPath("$.family", WireMock.equalTo("jades"))));
    }

    @Test
    void embedSignatureValue_postsAndReturnsTheSignedDocument() throws ConnectorException {
        stubPost(BASE + "/embedSignatureValue", "{ \"signedDocument\": \"AQID\" }");

        SignedDocumentResponseDto result = client.embedSignatureValue(connector, embedSignatureValueRequest());

        assertArrayEquals(new byte[]{1, 2, 3}, result.getSignedDocument());
    }

    /**
     * The connector cross-checks the embed's algorithm against the one baked into its formattingContext, so a value
     * dropped in transport would surface as drift rather than as a missing field.
     */
    @Test
    void computeDtbsAndEmbedSignatureValue_putTheSameSignatureAlgorithmOnTheWire() throws ConnectorException {
        stubPost(BASE + "/computeDtbs", "{ \"dtbs\": \"AQID\", \"formattingContext\": \"BAUG\" }");
        stubPost(BASE + "/embedSignatureValue", "{ \"signedDocument\": \"AQID\" }");

        client.computeDtbs(connector, padesRequest());
        client.embedSignatureValue(connector, embedSignatureValueRequest());

        for (String operation : List.of("/computeDtbs", "/embedSignatureValue")) {
            mockServer
                    .verify(WireMock
                            .postRequestedFor(WireMock.urlEqualTo(BASE + operation))
                            .withRequestBody(WireMock
                                    .matchingJsonPath("$.signatureAlgorithm",
                                            WireMock.equalTo("SHA256withRSAandMGF1"))));
        }
    }

    @Test
    void computeSignatureTimestampImprint_postsToItsOwnRoute() throws ConnectorException {
        stubPost(BASE + "/computeSignatureTimestampImprint",
                "{ \"imprint\": \"AQID\", \"digestAlgorithm\": \"SHA-256\" }");

        TimestampImprintResponseDto result = client
                .computeSignatureTimestampImprint(connector, new SignedDocumentRequestDto());

        assertArrayEquals(new byte[]{1, 2, 3}, result.getImprint());
        assertEquals(DigestAlgorithm.SHA_256, result.getDigestAlgorithm());
        mockServer.verify(WireMock.postRequestedFor(WireMock.urlEqualTo(BASE + "/computeSignatureTimestampImprint")));
    }

    /** Same types as the signature-timestamp imprint, so a swapped route returns a plausible but wrong imprint. */
    @Test
    void computeArchiveTimestampImprint_postsToItsOwnRoute() throws ConnectorException {
        stubPost(BASE + "/computeArchiveTimestampImprint",
                "{ \"imprint\": \"BAUG\", \"digestAlgorithm\": \"SHA-512\" }");

        TimestampImprintResponseDto result = client
                .computeArchiveTimestampImprint(connector, new SignedDocumentRequestDto());

        assertArrayEquals(new byte[]{4, 5, 6}, result.getImprint());
        assertEquals(DigestAlgorithm.SHA_512, result.getDigestAlgorithm());
        mockServer.verify(WireMock.postRequestedFor(WireMock.urlEqualTo(BASE + "/computeArchiveTimestampImprint")));
    }

    @Test
    void embedSignatureTimestamp_postsToItsOwnRoute() throws ConnectorException {
        stubPost(BASE + "/embedSignatureTimestamp", "{ \"signedDocument\": \"AQID\" }");

        SignedDocumentResponseDto result = client.embedSignatureTimestamp(connector, timestampRequest());

        assertArrayEquals(new byte[]{1, 2, 3}, result.getSignedDocument());
        mockServer.verify(WireMock.postRequestedFor(WireMock.urlEqualTo(BASE + "/embedSignatureTimestamp")));
    }

    /**
     * Shares its signature with {@code embedSignatureTimestamp}; only the route separates TIMESTAMPED from ARCHIVAL.
     */
    @Test
    void embedArchiveTimestamp_postsToItsOwnRoute() throws ConnectorException {
        stubPost(BASE + "/embedArchiveTimestamp", "{ \"signedDocument\": \"BAUG\" }");

        SignedDocumentResponseDto result = client.embedArchiveTimestamp(connector, timestampRequest());

        assertArrayEquals(new byte[]{4, 5, 6}, result.getSignedDocument());
        mockServer.verify(WireMock.postRequestedFor(WireMock.urlEqualTo(BASE + "/embedArchiveTimestamp")));
    }

    @Test
    void extendToLevel_postsTheChainAndTarget() throws ConnectorException {
        stubPost(BASE + "/extendToLevel", "{ \"extendedDocument\": \"AQID\", \"fetchManifest\": [] }");

        ExtendToLevelRequestDto request = validExtendToLevel();
        CertificateChainDto chain = new CertificateChainDto();
        chain.setCertificates(List.of(new byte[]{1}));
        chain.setTrustAnchors(List.of());
        request.setCertificateChain(chain);

        ResponseEntity<ExtendToLevelResponseDto> result = client.extendToLevel(connector, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertArrayEquals(new byte[]{1, 2, 3}, result.getBody().getExtendedDocument());
        // Empty and absent are contractually distinct here — an absent list would read as "the connector
        // did not look" — so the whole body is asserted rather than each field's presence.
        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(BASE + "/extendToLevel"))
                        .withRequestBody(WireMock.equalToJson("""
                                {
                                  "family": "pades",
                                  "formattingAttributes": [],
                                  "signedDocument": "CQ==",
                                  "targetLevel": "long_term",
                                  "executionMode": "synchronous",
                                  "certificateChain": {
                                    "certificates": ["AQ=="],
                                    "trustAnchors": []
                                  }
                                }
                                """)));
    }

    /** The manifest is the account of traffic the platform did not make itself, so it must survive decoding. */
    @Test
    void extendToLevel_readsTheFetchManifest() throws ConnectorException {
        stubPost(BASE + "/extendToLevel", """
                {
                  "extendedDocument": "AQID",
                  "fetchManifest": [
                    {
                      "sourceUrl": "http://crl.example.com/issuing-ca.crl",
                      "kind": "crl",
                      "sha256": "AQ==",
                      "fetchedAt": "2026-08-15T10:15:30Z"
                    }
                  ]
                }
                """);

        ResponseEntity<ExtendToLevelResponseDto> result = client.extendToLevel(connector, validExtendToLevel());

        assertEquals(1, result.getBody().getFetchManifest().size());
        assertEquals(FetchedArtifactKind.CRL, result.getBody().getFetchManifest().get(0).getKind());
    }

    @Test
    void getExtendToLevelStatus_postsToTheStatusCompanion() throws ConnectorException {
        stubPost(BASE + "/extendToLevel/status", "{ \"status\": \"inProgress\" }");

        ExtendOperationStatusResponseDto result = client.getExtendToLevelStatus(connector, scopedRequest());

        assertEquals(OperationStatus.IN_PROGRESS, result.getStatus());
        mockServer
                .verify(WireMock
                        .postRequestedFor(WireMock.urlEqualTo(BASE + "/extendToLevel/status"))
                        .withRequestBody(WireMock.matchingJsonPath("$.operationMeta[0].name"))
                        .withRequestBody(WireMock.matchingJsonPath("$.family")));
    }

    /**
     * A 202 body carries the tracking handle and no document. Collapsing it to a body would hand the caller an apparent
     * synchronous success with nothing in it, so the status has to survive the client.
     */
    @Test
    void extendToLevel_keepsAnAcceptedStatusDistinctFromAResult() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post(BASE + "/extendToLevel")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(202)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{ \"extendOperationMeta\": [] }")));

        ResponseEntity<ExtendToLevelResponseDto> result = client.extendToLevel(connector, validExtendToLevel());

        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
        assertNull(result.getBody().getExtendedDocument());
    }

    @Test
    void cancelExtendToLevel_acceptsABodylessAnswer() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post(WireMock.urlEqualTo(BASE + "/extendToLevel/cancel"))
                        .willReturn(WireMock.aResponse().withStatus(204)));

        ResponseEntity<Void> result = client.cancelExtendToLevel(connector, scopedRequest());

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        mockServer.verify(WireMock.postRequestedFor(WireMock.urlEqualTo(BASE + "/extendToLevel/cancel")));
    }

    private static ExtendToLevelRequestDto validExtendToLevel() {
        ExtendToLevelRequestDto request = new ExtendToLevelRequestDto();
        request.setFamily(SignatureFamily.PADES);
        request.setFormattingAttributes(List.of());
        request.setSignedDocument(new byte[]{9});
        request.setTargetLevel(SignatureLevel.LONG_TERM);
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        CertificateChainDto chain = new CertificateChainDto();
        chain.setCertificates(List.of());
        chain.setTrustAnchors(List.of());
        request.setCertificateChain(chain);
        return request;
    }

    private static ExtendOperationScopedRequestDto scopedRequest() {
        ExtendOperationScopedRequestDto request = new ExtendOperationScopedRequestDto();
        request.setFamily(SignatureFamily.PADES);
        request.setFormattingAttributes(List.of());
        request.setOperationMeta(validMetadata());
        return request;
    }

    /**
     * A 200 carries the extended document and a 202 the tracking handle, so neither may arrive empty — the status is
     * preserved, but not at the cost of handing the caller a null body.
     */
    @Test
    void extendToLevel_rejectsAnEmptyBodyOnEitherSuccessStatus() {
        for (int status : new int[]{200, 202}) {
            mockServer.resetAll();
            mockServer
                    .stubFor(WireMock
                            .post(BASE + "/extendToLevel")
                            .willReturn(WireMock
                                    .aResponse()
                                    .withStatus(status)
                                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

            assertThrows(IllegalStateException.class, () -> client.extendToLevel(connector, validExtendToLevel()),
                    "status " + status);
        }
    }

    /** A 2xx with no body is a connector fault, not an empty result, and must not reach the caller as null. */
    @Test
    void anEmptySuccessBodyIsRejected() {
        mockServer
                .stubFor(WireMock
                        .post(BASE + "/computeDtbs")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

        assertThrows(IllegalStateException.class, () -> client.computeDtbs(connector, padesRequest()));
    }

    private static ComputeDtbsRequestDto padesRequest() {
        ComputeDtbsRequestDto request = new ComputeDtbsRequestDto();
        request.setFamily(SignatureFamily.PADES);
        request.setDocument(new InlineDocumentTransferDto(new byte[]{1, 2, 3}));
        request.setSignerCertificateChain(List.of(new byte[]{9}));
        request.setSigningTime(OffsetDateTime.parse("2026-08-11T10:15:30Z"));
        request.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_RSA_PSS);
        request.setFormattingAttributes(List.of());
        return request;
    }

    private static EmbedSignatureValueRequestDto embedSignatureValueRequest() {
        EmbedSignatureValueRequestDto request = new EmbedSignatureValueRequestDto();
        request.setSignatureValue(new byte[]{1});
        request.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_RSA_PSS);
        request.setFormattingContext(new byte[]{2});
        return request;
    }

    private static EmbedTimestampRequestDto timestampRequest() {
        EmbedTimestampRequestDto request = new EmbedTimestampRequestDto();
        request.setSignedDocument(new byte[]{9});
        request.setTimestampToken(new byte[]{8});
        return request;
    }

    private void stubGet(String path, String json) {
        mockServer
                .stubFor(WireMock
                        .get(path)
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(json)));
    }

    private void stubPost(String path, String json) {
        mockServer
                .stubFor(WireMock
                        .post(path)
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(json)));
    }
}
