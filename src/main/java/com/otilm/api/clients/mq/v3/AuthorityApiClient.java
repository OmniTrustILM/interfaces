package com.otilm.api.clients.mq.v3;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.interfaces.client.v3.AuthoritySyncApiClient;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.authority.CaCertificatesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.otilm.api.model.connector.v3.authority.CrlRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

/**
 * MQ-based implementation of v3 Authority API client.
 * Mirror of {@link com.otilm.api.clients.v3.AuthorityApiClient}.
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class AuthorityApiClient implements AuthoritySyncApiClient {

    private static final String BASE_PATH = "/v3/authorityProvider/authorities";
    private static final String PATH_ATTRIBUTES = BASE_PATH + "/attributes";
    private static final String PATH_RA_PROFILE_ATTRIBUTES = BASE_PATH + "/raProfile/attributes";
    private static final String PATH_CRL = BASE_PATH + "/crl";
    private static final String PATH_CA_CERTIFICATES = BASE_PATH + "/caCertificates";

    private static final String HTTP_METHOD_GET = "GET";
    private static final String HTTP_METHOD_POST = "POST";

    private final ProxyClient proxyClient;

    public AuthorityApiClient(ProxyClient proxyClient) {
        this.proxyClient = proxyClient;
    }

    @Override
    public List<BaseAttribute> listAuthorityAttributes(ApiClientConnectorInfo connector) throws ConnectorException {
        BaseAttribute[] result = proxyClient.sendRequest(connector, PATH_ATTRIBUTES, HTTP_METHOD_GET, null, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public ResponseEntity<Void> checkAuthorityConnection(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ValidationException, ConnectorException {
        return proxyClient.sendRequestForEntity(connector, BASE_PATH, HTTP_METHOD_POST, attributes, Void.class);
    }

    @Override
    public List<BaseAttribute> listRaProfileAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> authorityAttributes) throws ConnectorException {
        BaseAttribute[] result = proxyClient.sendRequest(connector, PATH_RA_PROFILE_ATTRIBUTES, HTTP_METHOD_POST, authorityAttributes, BaseAttribute[].class);
        return Arrays.asList(result);
    }

    @Override
    public CrlResponseDto getCrl(ApiClientConnectorInfo connector, CrlRequestDtoV3 requestDto) throws ConnectorException {
        return proxyClient.sendRequest(connector, PATH_CRL, HTTP_METHOD_POST, requestDto, CrlResponseDto.class);
    }

    @Override
    public CaCertificatesResponseDto getCaCertificates(ApiClientConnectorInfo connector, CaCertificatesRequestDtoV3 requestDto) throws ConnectorException {
        return proxyClient.sendRequest(connector, PATH_CA_CERTIFICATES, HTTP_METHOD_POST, requestDto, CaCertificatesResponseDto.class);
    }
}
