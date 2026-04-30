package com.czertainly.api.interfaces.client.v1;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.authority.AuthorityProviderInstanceDto;
import com.czertainly.api.model.connector.authority.AuthorityProviderInstanceRequestDto;
import com.czertainly.api.model.connector.authority.CaCertificatesRequestDto;
import com.czertainly.api.model.connector.authority.CaCertificatesResponseDto;
import com.czertainly.api.model.connector.authority.CertificateRevocationListRequestDto;
import com.czertainly.api.model.connector.authority.CertificateRevocationListResponseDto;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Synchronous API client interface for Authority Provider Instance operations.
 * Implementations include REST-based and MQ-based clients.
 */
public interface AuthorityInstanceSyncApiClient {

    List<AuthorityProviderInstanceDto> listAuthorityInstances(ApiClientConnectorInfo connector) throws ConnectorException;

    AuthorityProviderInstanceDto getAuthorityInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;

    AuthorityProviderInstanceDto createAuthorityInstance(ApiClientConnectorInfo connector, AuthorityProviderInstanceRequestDto requestDto) throws ConnectorException;

    AuthorityProviderInstanceDto updateAuthorityInstance(ApiClientConnectorInfo connector, String uuid, AuthorityProviderInstanceRequestDto requestDto) throws ConnectorException;

    void removeAuthorityInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;

    List<BaseAttribute> listRAProfileAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException;

    Boolean validateRAProfileAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;

    CertificateRevocationListResponseDto getCrl(ApiClientConnectorInfo connector, String uuid, CertificateRevocationListRequestDto requestDto) throws ConnectorException;

    CaCertificatesResponseDto getCaCertificates(ApiClientConnectorInfo connector, String uuid, CaCertificatesRequestDto requestDto) throws ValidationException, ConnectorException;
}
