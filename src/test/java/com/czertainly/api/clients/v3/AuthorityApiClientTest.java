package com.czertainly.api.clients.v3;

import com.czertainly.api.clients.BaseApiClient;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ConnectorProblemException;
import com.czertainly.api.model.common.error.ErrorCode;
import com.czertainly.api.model.connector.v3.authority.CaCertificatesRequestDto;
import com.czertainly.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.czertainly.api.model.connector.v3.authority.CrlRequestDto;
import com.czertainly.api.model.connector.v3.authority.CrlResponseDto;
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

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class AuthorityApiClientTest {

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
        mockServer.stubFor(WireMock.post("/v3/authorityProvider/authorities")
                .willReturn(WireMock.aResponse().withStatus(204)));

        ResponseEntity<Void> response = client.checkAuthorityConnection(connector, List.of());

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getCrl_returnsCrlBody() throws ConnectorException {
        mockServer.stubFor(WireMock.post("/v3/authorityProvider/authorities/crl")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"crl\":\"MIIBkjCCATs...\"}")));

        CrlRequestDto req = new CrlRequestDto();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());
        req.setDelta(false);

        CrlResponseDto response = client.getCrl(connector, req);
        Assertions.assertEquals("MIIBkjCCATs...", response.getCrl());
    }

    @Test
    void getCaCertificates_returnsChain() throws ConnectorException {
        mockServer.stubFor(WireMock.post("/v3/authorityProvider/authorities/caCertificates")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"certificates\":[{\"certificateData\":\"issuing-base64\",\"certificateType\":\"X.509\"},{\"certificateData\":\"root-base64\",\"certificateType\":\"X.509\"}]}")));

        CaCertificatesRequestDto req = new CaCertificatesRequestDto();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());

        CaCertificatesResponseDto response = client.getCaCertificates(connector, req);
        Assertions.assertEquals(2, response.getCertificates().size());
        Assertions.assertEquals("issuing-base64", response.getCertificates().get(0).getCertificateData());
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

        mockServer.stubFor(WireMock.post("/v3/authorityProvider/authorities/crl")
                .willReturn(WireMock.aResponse()
                        .withStatus(422)
                        .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody(problemJson)));

        CrlRequestDto req = new CrlRequestDto();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());

        ConnectorProblemException ex = Assertions.assertThrows(
                ConnectorProblemException.class,
                () -> client.getCrl(connector, req));

        Assertions.assertEquals(ErrorCode.REGISTRATION_NOT_FOUND, ex.getProblemDetail().getErrorCode());
        Assertions.assertEquals(connector, ex.getConnector());
    }
}
