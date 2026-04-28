package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.entity.EntityInstanceDto;
import com.czertainly.api.model.connector.entity.EntityInstanceRequestDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Synchronous API client interface for Entity Provider Instance operations.
 * Implementations include REST-based and MQ-based clients.
 */
public interface EntityInstanceSyncApiClient {

    List<EntityInstanceDto> listEntityInstances(ApiClientConnectorInfo connector) throws ConnectorException;

    EntityInstanceDto getEntityInstance(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException;

    EntityInstanceDto createEntityInstance(ApiClientConnectorInfo connector, EntityInstanceRequestDto requestDto) throws ConnectorException;

    EntityInstanceDto updateEntityInstance(ApiClientConnectorInfo connector, String entityUuid, EntityInstanceRequestDto requestDto) throws ConnectorException;

    void removeEntityInstance(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException;

    List<BaseAttribute> listLocationAttributes(ApiClientConnectorInfo connector, String entityUuid) throws ConnectorException;

    void validateLocationAttributes(ApiClientConnectorInfo connector, String entityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;
}
