package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;

import java.util.List;

/**
 * Sync contract shared by the REST and MQ v2 stateless token clients.
 */
public interface TokenSyncApiClient {

    List<BaseAttribute> listTokenAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    TokenStatusResponseV2Dto getTokenStatus(ApiClientConnectorInfo connector,
                                            TokenScopedRequestV2Dto request) throws ConnectorException;

    List<BaseAttribute> listTokenProfileAttributes(ApiClientConnectorInfo connector,
                                                   TokenScopedRequestV2Dto request) throws ConnectorException;

    List<KeyUsage> listTokenProfileKeyUsages(ApiClientConnectorInfo connector,
                                             TokenScopedRequestV2Dto request) throws ConnectorException;
}
