package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.common.attribute.common.callback.AttributeCallback;
import com.czertainly.api.model.common.attribute.common.callback.RequestAttributeCallback;
import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.model.core.connector.FunctionGroupCode;

import java.util.List;

/**
 * Interface for synchronous attribute API operations against connectors.
 * Implementations can use REST (direct HTTP) or MQ (proxy) communication.
 */
public interface AttributeSyncApiClient {

    /**
     * List attribute definitions for a connector.
     *
     * @param connector         Connector configuration
     * @param functionGroupCode Function group code
     * @param kind              Connector kind
     * @return List of base attributes
     * @throws ConnectorException If request fails
     */
    List<BaseAttribute> listAttributeDefinitions(
            ApiClientConnectorInfo connector,
            FunctionGroupCode functionGroupCode,
            String kind) throws ConnectorException;

    /**
     * Validate attributes against a connector.
     *
     * @param connector         Connector configuration
     * @param functionGroupCode Function group code
     * @param attributes        Attributes to validate
     * @param functionGroupType Function group type (kind)
     * @return null on success
     * @throws ValidationException If validation fails
     * @throws ConnectorException  If request fails
     */
    Void validateAttributes(
            ApiClientConnectorInfo connector,
            FunctionGroupCode functionGroupCode,
            List<RequestAttribute> attributes,
            String functionGroupType) throws ValidationException, ConnectorException;

    /**
     * Execute attribute callback.
     *
     * @param connector       Connector configuration
     * @param callback        Callback configuration with method and context
     * @param callbackRequest Request with path variables, query params, and body
     * @return Callback response object
     * @throws ConnectorException If request fails
     */
    Object attributeCallback(
            ApiClientConnectorInfo connector,
            AttributeCallback callback,
            RequestAttributeCallback callbackRequest) throws ConnectorException;
}
