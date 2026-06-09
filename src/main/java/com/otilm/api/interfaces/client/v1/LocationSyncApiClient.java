package com.otilm.api.interfaces.client.v1;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.entity.GenerateCsrRequestDto;
import com.otilm.api.model.connector.entity.GenerateCsrResponseDto;
import com.otilm.api.model.connector.entity.LocationDetailRequestDto;
import com.otilm.api.model.connector.entity.LocationDetailResponseDto;
import com.otilm.api.model.connector.entity.PushCertificateRequestDto;
import com.otilm.api.model.connector.entity.PushCertificateResponseDto;
import com.otilm.api.model.connector.entity.RemoveCertificateRequestDto;
import com.otilm.api.model.connector.entity.RemoveCertificateResponseDto;
import com.otilm.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Synchronous API client interface for Location operations.
 * Implementations include REST-based and MQ-based clients.
 */
public interface LocationSyncApiClient {

    LocationDetailResponseDto getLocationDetail(ApiClientConnectorInfo connector, String entityUuid, LocationDetailRequestDto requestDto) throws ConnectorException;

    PushCertificateResponseDto pushCertificateToLocation(ApiClientConnectorInfo connector, String entityUuid, PushCertificateRequestDto requestDto) throws ConnectorException;

    List<BaseAttribute> listPushCertificateAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException;

    void validatePushCertificateAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> pushAttributes) throws ConnectorException;

    RemoveCertificateResponseDto removeCertificateFromLocation(ApiClientConnectorInfo connector, String entityUuid, RemoveCertificateRequestDto requestDto) throws ConnectorException;

    GenerateCsrResponseDto generateCsrLocation(ApiClientConnectorInfo connector, String entityUuid, GenerateCsrRequestDto requestDto) throws ConnectorException;

    List<BaseAttribute> listGenerateCsrAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException;

    void validateGenerateCsrAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> pushAttributes) throws ConnectorException;
}
