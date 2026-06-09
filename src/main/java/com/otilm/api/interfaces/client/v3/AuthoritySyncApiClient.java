package com.otilm.api.interfaces.client.v3;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.authority.CaCertificatesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.otilm.api.model.connector.v3.authority.CrlRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Sync interface for v3 Authority API client operations.
 * Implemented by both REST and MQ clients.
 *
 * <p>v3 is stateless: no authorityUuid path parameter — authority identity is
 * the authorityAttributes blob carried in each request DTO.
 */
public interface AuthoritySyncApiClient {

    List<BaseAttribute> listAuthorityAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    /**
     * Validate authority attributes by attempting to reach the upstream CA.
     * Returns 204 No Content on success.
     */
    ResponseEntity<Void> checkAuthorityConnection(ApiClientConnectorInfo connector, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;

    List<BaseAttribute> listRaProfileAttributes(ApiClientConnectorInfo connector, List<RequestAttribute> authorityAttributes) throws ConnectorException;

    CrlResponseDto getCrl(ApiClientConnectorInfo connector, CrlRequestDtoV3 requestDto) throws ConnectorException;

    CaCertificatesResponseDto getCaCertificates(ApiClientConnectorInfo connector, CaCertificatesRequestDtoV3 requestDto) throws ConnectorException;
}
