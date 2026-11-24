package com.otilm.api.clients.v3;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorEntityNotFoundException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.connector.v3.authority.CaCertificatesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.otilm.api.model.connector.v3.authority.CertificateTypesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlResponseDto;
import com.otilm.api.model.connector.v3.authority.SupportedCertificateTypeDtoV3;
import com.otilm.api.model.core.certificate.CertificateType;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class AuthorityApiClientTest {

    private static final String CERTIFICATE_TYPES_PATH = "/v3/authorityProvider/authorities/certificateTypes";

    private AuthorityApiClient client;
    private ConnectorDto connector;
    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        client = new AuthorityApiClient(BaseApiClient.prepareWebClient(), null);

        mockServer = new WireMockServer(options().dynamicPort());
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());

        connector = new ConnectorDto();
        connector.setUrl("http://localhost:" + mockServer.port());
        connector.setStatus(ConnectorStatus.CONNECTED);
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void checkAuthorityConnection_204OnSuccess() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/authorities")
                        .willReturn(WireMock.aResponse().withStatus(204)));

        ResponseEntity<Void> response = client.checkAuthorityConnection(connector, List.of());

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getCrl_returnsCrlBody() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/authorities/crl")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"crl\":\"MIIBkjCCATs...\"}")));

        CrlRequestDtoV3 req = new CrlRequestDtoV3();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());
        req.setDelta(false);

        CrlResponseDto response = client.getCrl(connector, req);
        Assertions.assertEquals("MIIBkjCCATs...", response.getCrl());
    }

    @Test
    void getCaCertificates_returnsChain() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/authorities/caCertificates")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(
                                        "{\"certificates\":[{\"certificateData\":\"issuing-base64\",\"certificateType\":\"X.509\"},{\"certificateData\":\"root-base64\",\"certificateType\":\"X.509\"}]}")));

        CaCertificatesRequestDtoV3 req = new CaCertificatesRequestDtoV3();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());

        CaCertificatesResponseDto response = client.getCaCertificates(connector, req);
        Assertions.assertEquals(2, response.getCertificates().size());
        Assertions.assertEquals("issuing-base64", response.getCertificates().get(0).getCertificateData());
    }

    @Test
    void listCertificateTypes_returnsTheTypesOfTheRaProfile() throws ConnectorException {
        // given
        mockServer
                .stubFor(WireMock
                        .post(CERTIFICATE_TYPES_PATH)
                        .withRequestBody(
                                WireMock.equalToJson("{\"authorityAttributes\":[],\"raProfileAttributes\":[]}"))
                        .willReturn(typesResponse("[{\"certificateType\":\"X.509\"},{\"certificateType\":\"SSH\"}]")));
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        request.setRaProfileAttributes(List.of());

        // when
        List<SupportedCertificateTypeDtoV3> types = client.listCertificateTypes(connector, request);

        // then
        Assertions
                .assertEquals(List
                        .of(SupportedCertificateTypeDtoV3.of(CertificateType.X509),
                                SupportedCertificateTypeDtoV3.of(CertificateType.SSH)),
                        types);
    }

    @Test
    void listCertificateTypes_leavesOutTheRaProfileAttributesWhenTheAuthorityIsAskedFor() throws ConnectorException {
        // given
        mockServer
                .stubFor(WireMock
                        .post(CERTIFICATE_TYPES_PATH)
                        .withRequestBody(WireMock.equalToJson("{\"authorityAttributes\":[]}", false, false))
                        .willReturn(typesResponse("[]")));

        // when
        List<SupportedCertificateTypeDtoV3> types = client.listCertificateTypes(connector, authorityRequest());

        // then
        Assertions.assertEquals(List.of(), types);
    }

    @Test
    void listCertificateTypes_returnsNoTypesWhenTheConnectorAnswersWithoutABody() throws ConnectorException {
        // given
        mockServer
                .stubFor(WireMock
                        .post(CERTIFICATE_TYPES_PATH)
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

        // when
        List<SupportedCertificateTypeDtoV3> types = client.listCertificateTypes(connector, authorityRequest());

        // then
        Assertions.assertEquals(List.of(), types);
    }

    @Test
    void listCertificateTypes_failsWithNotFoundWhenTheConnectorDoesNotServeTheOperation() {
        // given
        mockServer.stubFor(WireMock.post(CERTIFICATE_TYPES_PATH).willReturn(WireMock.aResponse().withStatus(404)));
        CertificateTypesRequestDtoV3 request = authorityRequest();

        // when / then
        Assertions
                .assertThrows(ConnectorEntityNotFoundException.class,
                        () -> client.listCertificateTypes(connector, request));
    }

    @Test
    void listCertificateTypes_failsWithNotImplementedWhenTheConnectorAnswers501() {
        // given
        mockServer.stubFor(WireMock.post(CERTIFICATE_TYPES_PATH).willReturn(WireMock.aResponse().withStatus(501)));
        CertificateTypesRequestDtoV3 request = authorityRequest();

        // when
        ConnectorServerException thrown = Assertions
                .assertThrows(ConnectorServerException.class, () -> client.listCertificateTypes(connector, request));

        // then
        Assertions.assertEquals(HttpStatus.NOT_IMPLEMENTED, thrown.getHttpStatus());
    }

    @Test
    void listCertificateTypes_carriesTheErrorCodeWhenTheConnectorDeclinesTheOperation() {
        // given
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/connector/authority/OPERATION_NOT_SUPPORTED",
                  "title": "Operation not supported",
                  "status": 501,
                  "errorCode": "OPERATION_NOT_SUPPORTED",
                  "timestamp": "2026-05-20T10:00:00Z",
                  "correlationId": "test-corr-1",
                  "retryable": false
                }
                """;
        mockServer
                .stubFor(WireMock
                        .post(CERTIFICATE_TYPES_PATH)
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(501)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(problemJson)));
        CertificateTypesRequestDtoV3 request = authorityRequest();

        // when
        ConnectorProblemException thrown = Assertions
                .assertThrows(ConnectorProblemException.class, () -> client.listCertificateTypes(connector, request));

        // then
        Assertions.assertEquals(ErrorCode.OPERATION_NOT_SUPPORTED, thrown.getProblemDetail().getErrorCode());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("refusedListings")
    void listCertificateTypes_failsWhenTheListingCannotBeUsed(String listing, String body, String refusal) {
        // given
        mockServer.stubFor(WireMock.post(CERTIFICATE_TYPES_PATH).willReturn(typesResponse(body)));
        CertificateTypesRequestDtoV3 request = authorityRequest();

        // when
        ConnectorException thrown = Assertions
                .assertThrows(ConnectorException.class, () -> client.listCertificateTypes(connector, request));

        // then
        Assertions.assertEquals(refusal, thrown.getMessage());
    }

    @Test
    void listCertificateTypes_leavesOutATypeItDoesNotKnow() throws ConnectorException {
        // given
        var unknownCode = "a-type-of-a-later-version";
        mockServer
                .stubFor(WireMock
                        .post(CERTIFICATE_TYPES_PATH)
                        .willReturn(typesResponse(
                                "[{\"certificateType\":\"X.509\"},{\"certificateType\":\"" + unknownCode + "\"}]")));

        // when
        List<SupportedCertificateTypeDtoV3> types = client.listCertificateTypes(connector, authorityRequest());

        // then
        Assertions.assertEquals(List.of(SupportedCertificateTypeDtoV3.of(CertificateType.X509)), types);
    }

    private static Stream<Arguments> refusedListings() {
        var withoutAType = "Connector listed a supported certificate type without a type";
        return Stream
                .of(Arguments
                        .of("a type listed twice", "[{\"certificateType\":\"X.509\"},{\"certificateType\":\"X.509\"}]",
                                "Connector listed the certificate type X.509 more than once"),
                        Arguments.of("a null entry", "[null]", withoutAType),
                        Arguments.of("an entry without a type", "[{}]", withoutAType),
                        Arguments
                                .of("only types it does not know",
                                        "[{\"certificateType\":\"a-type-of-a-later-version\"}]",
                                        "Connector listed only certificate types this version does not know"));
    }

    private static CertificateTypesRequestDtoV3 authorityRequest() {
        CertificateTypesRequestDtoV3 request = new CertificateTypesRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        return request;
    }

    private static ResponseDefinitionBuilder typesResponse(String body) {
        return WireMock
                .aResponse()
                .withStatus(200)
                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .withBody(body);
    }

    @Test
    void problemJsonResponseMapsToConnectorProblemException() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/connector/authority/REGISTRATION_NOT_FOUND",
                  "title": "Pre-registration reference not tracked by upstream CA",
                  "status": 422,
                  "detail": "ref ABC unknown",
                  "errorCode": "REGISTRATION_NOT_FOUND",
                  "timestamp": "2026-05-20T10:00:00Z",
                  "correlationId": "test-corr-1",
                  "retryable": false
                }
                """;

        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/authorities/crl")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(422)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(problemJson)));

        CrlRequestDtoV3 req = new CrlRequestDtoV3();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());

        ConnectorProblemException ex = Assertions
                .assertThrows(ConnectorProblemException.class, () -> client.getCrl(connector, req));

        Assertions.assertEquals(ErrorCode.REGISTRATION_NOT_FOUND, ex.getProblemDetail().getErrorCode());
        Assertions.assertEquals(connector, ex.getConnector());
    }
}
