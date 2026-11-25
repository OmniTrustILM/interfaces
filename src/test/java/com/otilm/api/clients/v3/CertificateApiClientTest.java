package com.otilm.api.clients.v3;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorClientException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.connector.v3.certificate.CertificateAttributeListRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateDataResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateOperationCancelRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoRequestItemDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewalInfoStatus;
import com.otilm.api.model.connector.v3.certificate.CertificateSignRequestDtoV3;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class CertificateApiClientTest {

    private CertificateApiClient client;
    private ConnectorDto connector;
    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        client = new CertificateApiClient(BaseApiClient.prepareWebClient(), null);

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
    void issue_sync200ReturnsCertificateData() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/issue")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"certificateData\":\"MIIBkjCCATs...\",\"certificateType\":\"X.509\"}")));

        CertificateSignRequestDtoV3 req = new CertificateSignRequestDtoV3();
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
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/issue")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(202)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("{\"certificateData\":null,\"meta\":[]}")));

        CertificateSignRequestDtoV3 req = new CertificateSignRequestDtoV3();
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
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/issue/cancel")
                        .willReturn(WireMock.aResponse().withStatus(204)));

        CertificateOperationCancelRequestDtoV3 req = new CertificateOperationCancelRequestDtoV3();
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

        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/issue")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(422)
                                .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                .withBody(problemJson)));

        CertificateSignRequestDtoV3 req = new CertificateSignRequestDtoV3();
        req.setAuthorityAttributes(List.of());
        req.setRaProfileAttributes(List.of());
        req.setRequest("bogus");

        ConnectorProblemException ex = Assertions
                .assertThrows(ConnectorProblemException.class, () -> client.issue(connector, req));

        Assertions.assertEquals(ErrorCode.CSR_MALFORMED, ex.getProblemDetail().getErrorCode());
        Assertions.assertFalse(ex.getProblemDetail().isRetryable());
        Assertions.assertEquals(connector, ex.getConnector());
    }

    @Test
    void listRequestAttributes_returnsSchema() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/request/attributes")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(schemaJson("commonName"))));

        List<BaseAttribute> attributes = client.listRequestAttributes(connector, attributeListRequest());

        Assertions.assertEquals(1, attributes.size());
        Assertions.assertInstanceOf(DataAttributeV3.class, attributes.get(0));
        Assertions.assertEquals("commonName", ((DataAttributeV3) attributes.get(0)).getName());
    }

    @Test
    void listRenewAttributes_returnsSchema() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/renew/attributes")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(schemaJson("validityOverride"))));

        List<BaseAttribute> attributes = client.listRenewAttributes(connector, attributeListRequest());

        Assertions.assertEquals(1, attributes.size());
        Assertions.assertInstanceOf(DataAttributeV3.class, attributes.get(0));
        Assertions.assertEquals("validityOverride", ((DataAttributeV3) attributes.get(0)).getName());
    }

    @Test
    void listIdentifyAttributes_emptyArrayReturnsEmptyList() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/identify/attributes")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[]")));

        Assertions.assertTrue(client.listIdentifyAttributes(connector, attributeListRequest()).isEmpty());
    }

    @Test
    void renewalInfo_mapsResponseBody() throws ConnectorException {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/renewalInfo")
                        .willReturn(WireMock
                                .aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
                                        {"retryAfter":7200,"results":[
                                          {"reference":"a","status":"available",
                                           "windowStart":"2026-12-01T00:00:00Z","windowEnd":"2026-12-03T00:00:00Z"},
                                          {"reference":"b","status":"notSupported","reason":"not covered"}]}
                                        """)));

        CertificateRenewalInfoResponseDto response = client.renewalInfo(connector, renewalInfoRequest());

        Assertions.assertEquals(7200, response.getRetryAfter());
        Assertions.assertEquals(2, response.getResults().size());
        Assertions.assertEquals(CertificateRenewalInfoStatus.AVAILABLE, response.getResults().get(0).getStatus());
        Assertions.assertNotNull(response.getResults().get(0).getWindowEnd());
        Assertions.assertEquals(CertificateRenewalInfoStatus.NOT_SUPPORTED, response.getResults().get(1).getStatus());
    }

    @Test
    void renewalInfo_tooManyRequestsSurfacesAsClientException() {
        mockServer
                .stubFor(WireMock
                        .post("/v3/authorityProvider/certificates/renewalInfo")
                        .willReturn(WireMock.aResponse().withStatus(429)));

        ConnectorClientException ex = Assertions
                .assertThrows(ConnectorClientException.class,
                        () -> client.renewalInfo(connector, renewalInfoRequest()));

        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, ex.getHttpStatus());
    }

    private static CertificateRenewalInfoRequestDtoV3 renewalInfoRequest() {
        CertificateRenewalInfoRequestItemDtoV3 item = new CertificateRenewalInfoRequestItemDtoV3();
        item.setReference("a");
        item.setCertificate("MIIBkjCCATs...");
        CertificateRenewalInfoRequestDtoV3 request = new CertificateRenewalInfoRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        request.setRaProfileAttributes(List.of());
        request.setCertificates(List.of(item));
        return request;
    }

    private static String schemaJson(String name) {
        return """
                [
                  {
                    "uuid": "11111111-1111-1111-1111-111111111111",
                    "name": "%s",
                    "type": "data",
                    "contentType": "string",
                    "version": 3
                  }
                ]
                """.formatted(name);
    }

    private static CertificateAttributeListRequestDtoV3 attributeListRequest() {
        CertificateAttributeListRequestDtoV3 request = new CertificateAttributeListRequestDtoV3();
        request.setAuthorityAttributes(List.of());
        request.setRaProfileAttributes(List.of());
        return request;
    }
}
