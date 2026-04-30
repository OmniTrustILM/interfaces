package com.czertainly.api.clients.mq;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.interfaces.client.v1.AuthorityInstanceSyncApiClient;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.authority.AuthorityProviderInstanceDto;
import com.czertainly.api.model.connector.authority.AuthorityProviderInstanceRequestDto;
import com.czertainly.api.model.connector.authority.CaCertificatesRequestDto;
import com.czertainly.api.model.connector.authority.CaCertificatesResponseDto;
import com.czertainly.api.model.connector.authority.CertificateRevocationListRequestDto;
import com.czertainly.api.model.connector.authority.CertificateRevocationListResponseDto;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Message queue based Authority Instance API client for connectors.
 * Uses ProxyClient to send authority instance requests via message queue
 * instead of direct REST calls.
 */
public class AuthorityInstanceApiClient implements AuthorityInstanceSyncApiClient {

    private static final String BASE_PATH = "/v1/authorityProvider/authorities";
    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";
    private static final String HTTP_METHOD_DELETE = "DELETE";

    private final ProxyClient proxyClient;

    public AuthorityInstanceApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<AuthorityProviderInstanceDto> listAuthorityInstances(ApiClientConnectorInfo connector) throws ConnectorException {
        AuthorityProviderInstanceDto[] result = proxyClient.sendRequest(
                connector,
                BASE_PATH,
                HTTP_METHOD_GET,
                null,
                AuthorityProviderInstanceDto[].class
        );
        return Arrays.asList(result);
    }

    @Override
    public AuthorityProviderInstanceDto getAuthorityInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid;
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                AuthorityProviderInstanceDto.class
        );
    }

    @Override
    public AuthorityProviderInstanceDto createAuthorityInstance(ApiClientConnectorInfo connector, AuthorityProviderInstanceRequestDto requestDto) throws ConnectorException {
        return proxyClient.sendRequest(
                connector,
                BASE_PATH,
                HTTP_METHOD_POST,
                requestDto,
                AuthorityProviderInstanceDto.class
        );
    }

    @Override
    public AuthorityProviderInstanceDto updateAuthorityInstance(ApiClientConnectorInfo connector, String uuid, AuthorityProviderInstanceRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid;
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                AuthorityProviderInstanceDto.class
        );
    }

    @Override
    public void removeAuthorityInstance(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid;
        proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_DELETE,
                null,
                Void.class
        );
    }

    @Override
    public List<BaseAttribute> listRAProfileAttributes(ApiClientConnectorInfo connector, String uuid) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/raProfile/attributes";
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
    public Boolean validateRAProfileAttributes(ApiClientConnectorInfo connector, String uuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/raProfile/attributes/validate";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                attributes,
                Boolean.class
        );
    }

    @Override
    public CertificateRevocationListResponseDto getCrl(ApiClientConnectorInfo connector, String uuid, CertificateRevocationListRequestDto requestDto) throws ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/crl";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                CertificateRevocationListResponseDto.class
        );
    }

    @Override
    public CaCertificatesResponseDto getCaCertificates(ApiClientConnectorInfo connector, String uuid, CaCertificatesRequestDto requestDto) throws ValidationException, ConnectorException {
        String path = BASE_PATH + "/" + uuid + "/caCertificates";
        return proxyClient.sendRequest(
                connector,
                path,
                HTTP_METHOD_POST,
                requestDto,
                CaCertificatesResponseDto.class
        );
    }

    // Async variants for key methods
    public CompletableFuture<List<AuthorityProviderInstanceDto>> listAuthorityInstancesAsync(ApiClientConnectorInfo connector) {
        return proxyClient.sendRequestAsync(
                connector,
                BASE_PATH,
                HTTP_METHOD_GET,
                null,
                AuthorityProviderInstanceDto[].class
        ).thenApply(Arrays::asList);
    }

    public CompletableFuture<AuthorityProviderInstanceDto> getAuthorityInstanceAsync(ApiClientConnectorInfo connector, String uuid) {
        String path = BASE_PATH + "/" + uuid;
        return proxyClient.sendRequestAsync(
                connector,
                path,
                HTTP_METHOD_GET,
                null,
                AuthorityProviderInstanceDto.class
        );
    }
}
