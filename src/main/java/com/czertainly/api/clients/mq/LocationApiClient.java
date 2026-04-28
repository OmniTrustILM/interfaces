package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.interfaces.client.v1.LocationSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.entity.GenerateCsrRequestDto;
import com.czertainly.api.model.connector.entity.GenerateCsrResponseDto;
import com.czertainly.api.model.connector.entity.LocationDetailRequestDto;
import com.czertainly.api.model.connector.entity.LocationDetailResponseDto;
import com.czertainly.api.model.connector.entity.PushCertificateRequestDto;
import com.czertainly.api.model.connector.entity.PushCertificateResponseDto;
import com.czertainly.api.model.connector.entity.RemoveCertificateRequestDto;
import com.czertainly.api.model.connector.entity.RemoveCertificateResponseDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Message queue based Location API client for connectors.
 * Uses ProxyClient to send location requests via message queue
 * instead of direct REST calls.
 */
public class LocationApiClient implements LocationSyncApiClient {

    private static final String BASE_PATH = "/v1/entityProvider/entities";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public LocationApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public LocationDetailResponseDto getLocationDetail(ApiClientConnectorInfo connector, String entityUuid, LocationDetailRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                LocationDetailResponseDto.class
        );
    }

    @Override
    public PushCertificateResponseDto pushCertificateToLocation(ApiClientConnectorInfo connector, String entityUuid, PushCertificateRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations/push";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                PushCertificateResponseDto.class
        );
    }

    @Override
    public List<BaseAttribute> listPushCertificateAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations/push/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                BaseAttribute[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public void validatePushCertificateAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> pushAttributes) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations/push/attributes/validate";
        proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                pushAttributes,
                Void.class
        );
    }

    @Override
    public RemoveCertificateResponseDto removeCertificateFromLocation(ApiClientConnectorInfo connector, String entityUuid, RemoveCertificateRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations/remove";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                RemoveCertificateResponseDto.class
        );
    }

    @Override
    public GenerateCsrResponseDto generateCsrLocation(ApiClientConnectorInfo connector, String entityUuid, GenerateCsrRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations/csr";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                GenerateCsrResponseDto.class
        );
    }

    @Override
    public List<BaseAttribute> listGenerateCsrAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations/csr/attributes";
        BaseAttribute[] result = proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                BaseAttribute[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public void validateGenerateCsrAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> pushAttributes) throws ConnectorException {
        String path = BASE_PATH + "/" + entityUuid + "/locations/csr/attributes/validate";
        proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                pushAttributes,
                Void.class
        );
    }

    // Async variants
    public CompletableFuture<LocationDetailResponseDto> getLocationDetailAsync(ApiClientConnectorInfo connector, String entityUuid, LocationDetailRequestDto requestDto) {
        String path = BASE_PATH + "/" + entityUuid + "/locations";
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                LocationDetailResponseDto.class
        );
    }

    public CompletableFuture<PushCertificateResponseDto> pushCertificateToLocationAsync(ApiClientConnectorInfo connector, String entityUuid, PushCertificateRequestDto requestDto) {
        String path = BASE_PATH + "/" + entityUuid + "/locations/push";
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                PushCertificateResponseDto.class
        );
    }
}
