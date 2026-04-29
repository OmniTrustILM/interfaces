package com.otilm.api;

import com.otilm.api.clients.AttributeApiClient;
import com.otilm.api.clients.BaseApiClient;
import com.otilm.api.exception.*;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.model.core.connector.FunctionGroupCode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;

class ApiClientTest {

    private AttributeApiClient attributeApiClient;

    private WireMockServer mockServer;

    @BeforeEach
    public void setUp() {
        attributeApiClient = new AttributeApiClient(BaseApiClient.prepareWebClient(), null);

        mockServer = new WireMockServer(3665);
        mockServer.start();

        WireMock.configureFor("localhost", mockServer.port());
    }

    @AfterEach
    public void tearDown() {
        mockServer.stop();
    }

    @Test
    void testGetAttributes_unknownHost() {
        ConnectorDto connector = new ConnectorDto();
        connector.setUrl("http://wrong-host:1234");
        connector.setStatus(ConnectorStatus.CONNECTED);

        Throwable cause = Assertions.assertThrows(ConnectorCommunicationException.class, () ->
            // tested method
            attributeApiClient.listAttributeDefinitions(
                    connector,
                    FunctionGroupCode.CREDENTIAL_PROVIDER,
                    "certificate")
        );

        WebClientRequestException e = (WebClientRequestException) cause.getCause();
        Assertions.assertEquals(WebClientRequestException.class, cause.getCause().getClass());
        Assertions.assertEquals(UnknownHostException.class, e.getCause().getClass());
    }

    @Test
    void testGetAttributes_connectionRefused() {
        ConnectorDto connector = new ConnectorDto();
        connector.setUrl("http://localhost:1234");
        connector.setStatus(ConnectorStatus.CONNECTED);

        Throwable cause = Assertions.assertThrows(ConnectorCommunicationException.class, () ->
                // tested method
                attributeApiClient.listAttributeDefinitions(
                        connector,
                        FunctionGroupCode.CREDENTIAL_PROVIDER,
                        "certificate")
        );

        WebClientRequestException e = (WebClientRequestException) cause.getCause();
        Assertions.assertEquals(WebClientRequestException.class, cause.getCause().getClass());
        Assertions.assertEquals(ConnectException.class, e.getCause().getCause().getClass());
    }

    @Test
    void testGetAttributes_clientNotFoundError() {
        String bodyString = new NotFoundException("Attribute", 1).getMessage();

        mockServer.stubFor(WireMock.get("/v1/credentialProvider/certificate/attributes")
                .willReturn(WireMock
                        .aResponse()
                        .withStatus(404)
                        .withBody(bodyString)));

        ConnectorDto connector = new ConnectorDto();
        connector.setUrl("http://localhost:3665");
        connector.setStatus(ConnectorStatus.CONNECTED);

        ConnectorEntityNotFoundException cause = Assertions.assertThrows(ConnectorEntityNotFoundException.class, () ->
                // tested method
                attributeApiClient.listAttributeDefinitions(
                        connector,
                        FunctionGroupCode.CREDENTIAL_PROVIDER,
                        "certificate")
        );

        Assertions.assertEquals(bodyString, cause.getMessage());
        Assertions.assertEquals(connector, cause.getConnector());
    }

    @Test
    void testGetAttributes_clientError() {
        String bodyString = "Bad client request";

        mockServer.stubFor(WireMock.get("/v1/credentialProvider/certificate/attributes")
                .willReturn(WireMock
                        .aResponse()
                        .withStatus(400)
                        .withBody(bodyString)));

        ConnectorDto connector = new ConnectorDto();
        connector.setUrl("http://localhost:3665");
        connector.setStatus(ConnectorStatus.CONNECTED);

        ConnectorClientException cause = Assertions.assertThrows(ConnectorClientException.class, () ->
                // tested method
                attributeApiClient.listAttributeDefinitions(
                        connector,
                        FunctionGroupCode.CREDENTIAL_PROVIDER,
                        "certificate")
        );

        Assertions.assertEquals(bodyString, cause.getMessage());
        Assertions.assertEquals(connector, cause.getConnector());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, cause.getHttpStatus());
    }

    @Test
    void testGetAttributes_serverError() {
        String bodyString = "Internal server error";

        mockServer.stubFor(WireMock.get("/v1/credentialProvider/certificate/attributes")
                .willReturn(WireMock
                        .aResponse()
                        .withStatus(500)
                        .withBody(bodyString)));

        ConnectorDto connector = new ConnectorDto();
        connector.setUrl("http://localhost:3665");
        connector.setStatus(ConnectorStatus.CONNECTED);

        ConnectorServerException cause = Assertions.assertThrows(ConnectorServerException.class, () ->
                // tested method
                attributeApiClient.listAttributeDefinitions(
                        connector,
                        FunctionGroupCode.CREDENTIAL_PROVIDER,
                        "certificate")
        );

        Assertions.assertEquals(bodyString, cause.getMessage());
        Assertions.assertEquals(connector, cause.getConnector());
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, cause.getHttpStatus());
    }


    @Test
    void testGetAttributes_waitingForApproval() {

        ConnectorDto connector = new ConnectorDto();
        connector.setUrl("http://localhost:3665");
        connector.setStatus(ConnectorStatus.WAITING_FOR_APPROVAL);

        Assertions.assertThrows(ValidationException.class, () -> attributeApiClient.validateAttributes(
                connector,
                FunctionGroupCode.CREDENTIAL_PROVIDER,
                new ArrayList<>(),
                "certificate"));
    }
}
