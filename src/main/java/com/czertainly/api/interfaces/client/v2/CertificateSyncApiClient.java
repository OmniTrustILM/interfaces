package com.czertainly.api.interfaces.client.v2;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v2.*;
import com.czertainly.api.clients.ApiClientConnectorInfo;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Sync interface for v2 Certificate API client operations.
 * This interface is implemented by both REST and MQ clients.
 */
public interface CertificateSyncApiClient {

    List<BaseAttribute> listIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException;

    Boolean validateIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;

    /**
     * Issue a certificate. Returns a {@link ResponseEntity} so callers can distinguish a
     * synchronous {@code 200 OK} (body carries the issued certificate) from an asynchronous
     * {@code 202 Accepted} (body may carry connector-defined metadata; certificate completion
     * is asynchronous).
     */
    ResponseEntity<CertificateDataResponseDto> issueCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateSignRequestDto requestDto) throws ConnectorException;

    /**
     * Renew a certificate. Same {@code 200}/{@code 202} semantics as {@link #issueCertificate}.
     */
    ResponseEntity<CertificateDataResponseDto> renewCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateRenewRequestDto requestDto) throws ConnectorException;

    List<BaseAttribute> listRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException;

    Boolean validateRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;

    /**
     * Revoke a certificate. Returns a {@link ResponseEntity} so callers can distinguish a
     * synchronous {@code 200 OK}/{@code 204 No Content} from an asynchronous {@code 202
     * Accepted}. The body is empty in the synchronous case; a {@code 202} body MAY carry
     * connector-defined metadata in the standard {@code meta} field.
     */
    ResponseEntity<CertificateDataResponseDto> revokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertRevocationDto requestDto) throws ConnectorException;

    CertificateIdentificationResponseDto identifyCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateIdentificationRequestDto requestDto) throws ValidationException, ConnectorException;

    void cancelIssueCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationCancelRequestDto requestDto) throws ValidationException, ConnectorException;

    void cancelRevokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationCancelRequestDto requestDto) throws ValidationException, ConnectorException;

    CertificateOperationStatusResponseDto getIssueCertificateStatus(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationStatusRequestDto requestDto) throws ConnectorException;

    CertificateOperationStatusResponseDto getRevokeCertificateStatus(ApiClientConnectorInfo connector, String authorityUuid, CertificateOperationStatusRequestDto requestDto) throws ConnectorException;
}
