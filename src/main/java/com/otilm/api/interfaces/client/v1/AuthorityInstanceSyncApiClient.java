package com.otilm.api.interfaces.client.v1;

import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.authority.AuthorityProviderInstanceDto;
import com.otilm.api.model.connector.authority.AuthorityProviderInstanceRequestDto;
import com.otilm.api.model.connector.authority.CaCertificatesRequestDto;
import com.otilm.api.model.connector.authority.CaCertificatesResponseDto;
import com.otilm.api.model.connector.authority.CertificateRevocationListRequestDto;
import com.otilm.api.model.connector.authority.CertificateRevocationListResponseDto;
import com.otilm.api.clients.ApiClientConnectorInfo;

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
