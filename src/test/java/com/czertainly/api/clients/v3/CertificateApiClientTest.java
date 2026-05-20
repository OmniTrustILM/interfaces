package com.czertainly.api.clients.v3;

import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ConnectorProblemException;
import com.czertainly.api.model.common.error.ErrorCode;
import com.czertainly.api.model.connector.v3.certificate.CertificateDataResponseDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateOperationCancelRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateSignRequestDto;
import com.czertainly.api.model.core.connector.ConnectorDto;
import com.czertainly.api.model.core.connector.ConnectorStatus;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

class CertificateApiClientTest {

    private static final int PORT = 3669;

    private CertificateApiClient client;
    private ConnectorDto connector;
    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        client = new CertificateApiClient(BaseApiClient.prepareWebClient(), null);

        mockServer = new WireMockServer(PORT);
        mockServer.start();
        WireMock.configureFor("localhost", mockServer.port());

        connector = new ConnectorDto();
        connector.setUrl("http://localhost:" + PORT);
        connector.setStatus(ConnectorStatus.CONNECTED);
    }

    @AfterEach
    void tearDown() {
        mockServer.stop();
    }

    @Test
    void issue_sync200ReturnsCertificateData() throws ConnectorException {
        mockServer.stubFor(WireMock.post("/v3/authorityProvider/certificates/issue")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"certificateData\":\"MIIBkjCCATs...\",\"certificateType\":\"X.509\"}")));

        CertificateSignRequestDto req = new CertificateSignRequestDto();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());
        req.setRequest("MIICij...");

        ResponseEntity<CertificateDataResponseDto> response = client.issue(connector, req);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals("MIIBkjCCATs...", response.getBody().getCertificateData());
    }

    @Test
    void issue_async202ReturnsMetaOnly() throws ConnectorException {
        mockServer.stubFor(WireMock.post("/v3/authorityProvider/certificates/issue")
                .willReturn(WireMock.aResponse()
                        .withStatus(202)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"certificateData\":null,\"meta\":[]}")));

        CertificateSignRequestDto req = new CertificateSignRequestDto();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());
        req.setRequest("MIICij...");

        ResponseEntity<CertificateDataResponseDto> response = client.issue(connector, req);

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNull(response.getBody().getCertificateData());
    }

    @Test
    void cancelIssue_204OnSuccess() throws ConnectorException {
        mockServer.stubFor(WireMock.post("/v3/authorityProvider/certificates/issue/cancel")
                .willReturn(WireMock.aResponse().withStatus(204)));

        CertificateOperationCancelRequestDto req = new CertificateOperationCancelRequestDto();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());
        req.setMeta(List.of());

        ResponseEntity<Void> response = client.cancelIssue(connector, req);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void issueProblemJsonMapsToConnectorProblemException() {
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/connector/authority/CSR_MALFORMED",
                  "title": "CSR malformed",
                  "status": 422,
                  "detail": "CSR ASN.1 parse error",
                  "errorCode": "CSR_MALFORMED",
                  "timestamp": "2026-05-20T10:00:00Z",
                  "correlationId": "test-corr-2",
                  "retryable": false
                }
                """;

        mockServer.stubFor(WireMock.post("/v3/authorityProvider/certificates/issue")
                .willReturn(WireMock.aResponse()
                        .withStatus(422)
                        .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody(problemJson)));

        CertificateSignRequestDto req = new CertificateSignRequestDto();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());
        req.setRequest("bogus");

        ConnectorProblemException ex = Assertions.assertThrows(
                ConnectorProblemException.class,
                () -> client.issue(connector, req));

        Assertions.assertEquals(ErrorCode.CSR_MALFORMED, ex.getProblemDetail().getErrorCode());
        Assertions.assertFalse(ex.getProblemDetail().isRetryable());
        Assertions.assertEquals(connector, ex.getConnector());
    }
}
