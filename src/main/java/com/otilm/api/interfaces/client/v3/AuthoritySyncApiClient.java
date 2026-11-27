package com.otilm.api.interfaces.client.v3;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.authority.AuthorityConnectionResponseDto;
import com.otilm.api.model.connector.v3.authority.CaCertificatesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CaCertificatesResponseDto;
import com.otilm.api.model.connector.v3.authority.CertificateTypesRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlRequestDtoV3;
import com.otilm.api.model.connector.v3.authority.CrlResponseDto;
import com.otilm.api.model.connector.v3.authority.SupportedCertificateTypeDtoV3;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * Sync interface for v3 Authority API client operations. Implemented by both REST and MQ clients.
 *
 * <p>
 * v3 is stateless: no authorityUuid path parameter — authority identity is the authorityAttributes blob carried in each
 * request DTO.
 */
public interface AuthoritySyncApiClient {

    List<BaseAttribute> listAuthorityAttributes(ApiClientConnectorInfo connector) throws ConnectorException;

    /**
     * Validate authority attributes by attempting to reach the upstream CA. On success the connector answers 204 with
     * no body, or 200 with the authority metadata; the returned entity's body is {@code null} when there is none. A
     * body whose metadata is absent, null or empty means no metadata, the same as a 204.
     */
    ResponseEntity<AuthorityConnectionResponseDto> checkAuthorityConnection(ApiClientConnectorInfo connector,
            List<RequestAttribute> attributes) throws ValidationException, ConnectorException;

    List<BaseAttribute> listRaProfileAttributes(ApiClientConnectorInfo connector,
            List<RequestAttribute> authorityAttributes) throws ConnectorException;

    CrlResponseDto getCrl(ApiClientConnectorInfo connector, CrlRequestDtoV3 requestDto) throws ConnectorException;

    CaCertificatesResponseDto getCaCertificates(ApiClientConnectorInfo connector, CaCertificatesRequestDtoV3 requestDto)
            throws ConnectorException;

    /**
     * The certificate types the authority issues, or the types it issues through the RA profile whose attributes the
     * request carries. A connector without the operation fails the call; the caller decides what that means.
     */
    List<SupportedCertificateTypeDtoV3> listCertificateTypes(ApiClientConnectorInfo connector,
            CertificateTypesRequestDtoV3 requestDto) throws ConnectorException;
}
