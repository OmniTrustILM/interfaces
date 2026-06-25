package com.otilm.api.clients.v2;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackRequestDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeCallbackResponseDto;
import com.otilm.api.model.client.connector.v2.attribute.AttributeDefinitionsDto;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

class AttributesApiClientTest {

    private AttributesApiClient client;
    private ConnectorDto connector;
    private WireMockServer mockServer;

    @BeforeEach
    void setUp() {
        client = new AttributesApiClient(BaseApiClient.prepareWebClient(), null);

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

    /**
     * Full-registry list round-trips a NON-EMPTY, mixed attribute-schema v2/v3 definition list, proving
     * the polymorphic {@code BaseAttribute} (de)serialization survives over the wire and the concrete
     * subtypes (v2 DATA + v3 INFO) are reconstructed by version/type discriminator.
     */
    @Test
    void listDefinitions_returnsDto() throws ConnectorException {
        String json = """
                {
                  "connectorVersion": "1.2.3",
                  "definitions": [
                    {
                      "uuid": "11111111-1111-1111-1111-111111111111",
                      "name": "v2DataAttr",
                      "type": "data",
                      "contentType": "string",
                      "version": 2
                    },
                    {
                      "uuid": "22222222-2222-2222-2222-222222222222",
                      "name": "v3InfoAttr",
                      "type": "info",
                      "contentType": "string",
                      "version": 3
                    }
                  ]
                }
                """;
        mockServer.stubFor(WireMock.get("/v2/attributes")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(json)));

        AttributeDefinitionsDto dto = client.listDefinitions(connector, null);

        Assertions.assertEquals("1.2.3", dto.getConnectorVersion());
        Assertions.assertEquals(2, dto.getDefinitions().size());

        BaseAttribute first = dto.getDefinitions().get(0);
        BaseAttribute second = dto.getDefinitions().get(1);

        Assertions.assertInstanceOf(DataAttributeV2.class, first);
        Assertions.assertEquals(2, first.getVersion());
        Assertions.assertEquals(AttributeType.DATA, first.getType());

        Assertions.assertInstanceOf(InfoAttributeV3.class, second);
        Assertions.assertEquals(3, second.getVersion());
        Assertions.assertEquals(AttributeType.INFO, second.getType());
    }

    /**
     * With a non-empty uuid list the outgoing request MUST be the exploded form
     * {@code ?uuids=a&uuids=b} (matching the controller's {@code Explode.TRUE}), NOT a single CSV value.
     */
    @Test
    void listDefinitions_emitsExplodedUuidsQuery() throws ConnectorException {
        UUID a = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID b = UUID.fromString("22222222-2222-2222-2222-222222222222");

        mockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/v2/attributes"))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"connectorVersion\":\"1.0\",\"definitions\":[]}")));

        client.listDefinitions(connector, List.of(a, b));

        // Each uuid is its own repeated query parameter; a CSV value "a,b" would NOT match these.
        mockServer.verify(WireMock.getRequestedFor(WireMock.urlPathEqualTo("/v2/attributes"))
                .withQueryParam("uuids", WireMock.equalTo(a.toString()))
                .withQueryParam("uuids", WireMock.equalTo(b.toString())));

        // And explicitly reject the CSV form.
        mockServer.verify(0, WireMock.getRequestedFor(WireMock.urlPathEqualTo("/v2/attributes"))
                .withQueryParam("uuids", WireMock.equalTo(a + "," + b)));
    }

    @Test
    void listDefinitions_successWithEmptyBody_failsClearly() {
        mockServer.stubFor(WireMock.get("/v2/attributes")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

        IllegalStateException ex = Assertions.assertThrows(
                IllegalStateException.class,
                () -> client.listDefinitions(connector, null));

        Assertions.assertTrue(ex.getMessage().contains("Attributes v2 list definitions"));
    }

    @Test
    void getDefinition_returnsBaseAttribute() throws ConnectorException {
        UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String json = """
                {
                  "uuid": "11111111-1111-1111-1111-111111111111",
                  "name": "v3InfoAttr",
                  "type": "info",
                  "contentType": "string",
                  "version": 3
                }
                """;
        mockServer.stubFor(WireMock.get("/v2/attributes/" + uuid)
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(json)));

        BaseAttribute attr = client.getDefinition(connector, uuid);

        Assertions.assertInstanceOf(InfoAttributeV3.class, attr);
        Assertions.assertEquals(3, attr.getVersion());
        Assertions.assertEquals(AttributeType.INFO, attr.getType());
    }

    @Test
    void getDefinition_successWithEmptyBody_failsClearly() {
        UUID uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
        mockServer.stubFor(WireMock.get("/v2/attributes/" + uuid)
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

        IllegalStateException ex = Assertions.assertThrows(
                IllegalStateException.class,
                () -> client.getDefinition(connector, uuid));

        Assertions.assertTrue(ex.getMessage().contains("Attributes v2 get definition"));
    }

    /**
     * A connector 404 carrying an RFC 9457 {@code application/problem+json} body with
     * {@code errorCode=ATTRIBUTE_DEFINITION_NOT_FOUND} surfaces as {@link ConnectorProblemException}
     * (via {@code BaseApiClient.handleHttpExceptions}), preserving the error code — the in-repo half of AC5.
     */
    @Test
    void getDefinition_unknownUuid_mapsProblem() {
        UUID uuid = UUID.fromString("99999999-9999-9999-9999-999999999999");
        String problemJson = """
                {
                  "type": "https://docs.otilm.com/problems/connector/ATTRIBUTE_DEFINITION_NOT_FOUND",
                  "title": "Attribute definition not found",
                  "status": 404,
                  "detail": "No definition for 99999999-9999-9999-9999-999999999999",
                  "errorCode": "ATTRIBUTE_DEFINITION_NOT_FOUND",
                  "timestamp": "2026-06-24T10:00:00Z",
                  "correlationId": "test-corr-404",
                  "retryable": false
                }
                """;
        mockServer.stubFor(WireMock.get("/v2/attributes/" + uuid)
                .willReturn(WireMock.aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                        .withBody(problemJson)));

        ConnectorProblemException ex = Assertions.assertThrows(
                ConnectorProblemException.class,
                () -> client.getDefinition(connector, uuid));

        Assertions.assertEquals(ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND, ex.getProblemDetail().getErrorCode());
        Assertions.assertEquals(404, ex.getProblemDetail().getStatus());
        Assertions.assertEquals(connector, ex.getConnector());
    }

    /**
     * POST body round-trips and the response {@code content} arm (attribute schema v3) deserializes.
     */
    @Test
    void callback_contentArm_returnsResponse() throws ConnectorException {
        String responseJson = """
                {
                  "content": [
                    { "reference": "opt-1", "contentType": "string", "data": "value-1" }
                  ],
                  "totalItems": 1
                }
                """;
        mockServer.stubFor(WireMock.post("/v2/attributes/callback")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseJson)));

        AttributeCallbackRequestDto request = new AttributeCallbackRequestDto();
        request.setConnectorInterface(ConnectorInterface.AUTHORITY);
        request.setInterfaceVersion("v3");
        request.setAttributeUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        request.setAttributeName("someAttr");
        request.setContextAttributes(List.of());
        request.setCurrentAttributes(List.of());

        AttributeCallbackResponseDto response = client.callback(connector, request);

        Assertions.assertNotNull(response.getContent());
        Assertions.assertEquals(1, response.getContent().size());
        Assertions.assertEquals(1L, response.getTotalItems());
        Assertions.assertNull(response.getAttributes());

        mockServer.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/v2/attributes/callback"))
                .withRequestBody(WireMock.matchingJsonPath("$.attributeName", WireMock.equalTo("someAttr"))));
    }

    @Test
    void callback_successWithEmptyBody_failsClearly() {
        mockServer.stubFor(WireMock.post("/v2/attributes/callback")
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

        AttributeCallbackRequestDto request = new AttributeCallbackRequestDto();
        request.setConnectorInterface(ConnectorInterface.AUTHORITY);
        request.setInterfaceVersion("v3");
        request.setAttributeUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        request.setAttributeName("someAttr");
        request.setContextAttributes(List.of());
        request.setCurrentAttributes(List.of());

        IllegalStateException ex = Assertions.assertThrows(
                IllegalStateException.class,
                () -> client.callback(connector, request));

        Assertions.assertTrue(ex.getMessage().contains("Attributes v2 callback"));
    }
}
