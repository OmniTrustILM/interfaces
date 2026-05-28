package com.czertainly.api.interfaces.client.v3;

import com.czertainly.api.clients.ApiClientConnectorInfo;
import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v3.certificate.CertificateAttributeListRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateDataResponseDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateIdentificationRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateIdentificationResponseDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateOperationCancelRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateOperationStatusRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateOperationStatusResponseDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateRegistrationRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateRenewRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateRevocationRequestDto;
import com.czertainly.api.model.connector.v3.certificate.CertificateSignRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Sync interface for v3 Certificate API client operations.
 * Implemented by both REST and MQ clients.
 *
 * <p>Return-type convention (see design §4.5):
 * <ul>
 *   <li>{@code ResponseEntity<CertificateDataResponseDto>} for issue/renew/revoke/register —
 *       caller distinguishes sync {@code 200 OK} from async {@code 202 Accepted}.</li>
 *   <li>{@code ResponseEntity<Void>} for cancel endpoints — {@code 204} success vs {@code 404}/{@code 422} failure modes
 *       carried via {@code ConnectorProblemException} on the failure path.</li>
 *   <li>plain DTO for non-lifecycle ops (identify, attribute lists, status queries).</li>
 * </ul>
 */
public interface CertificateSyncApiClient {

    // ---- Issue ----

    List<BaseAttribute> listIssueAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDto requestDto) throws ConnectorException;

    ResponseEntity<CertificateDataResponseDto> issue(ApiClientConnectorInfo connector, CertificateSignRequestDto requestDto) throws ConnectorException;

    CertificateOperationStatusResponseDto getIssueStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException;

    ResponseEntity<Void> cancelIssue(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException;

    // ---- Renew (status/cancel via /issue/*) ----

    ResponseEntity<CertificateDataResponseDto> renew(ApiClientConnectorInfo connector, CertificateRenewRequestDto requestDto) throws ConnectorException;

    // ---- Revoke ----

    List<BaseAttribute> listRevokeAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDto requestDto) throws ConnectorException;

    ResponseEntity<CertificateDataResponseDto> revoke(ApiClientConnectorInfo connector, CertificateRevocationRequestDto requestDto) throws ConnectorException;

    CertificateOperationStatusResponseDto getRevokeStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException;

    ResponseEntity<Void> cancelRevoke(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException;

    // ---- Register ----

    List<BaseAttribute> listRegisterAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDto requestDto) throws ConnectorException;

    ResponseEntity<CertificateDataResponseDto> register(ApiClientConnectorInfo connector, CertificateRegistrationRequestDto requestDto) throws ConnectorException;

    CertificateOperationStatusResponseDto getRegisterStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDto requestDto) throws ConnectorException;

    ResponseEntity<Void> cancelRegister(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDto requestDto) throws ConnectorException;

    // ---- Identify ----

    CertificateIdentificationResponseDto identify(ApiClientConnectorInfo connector, CertificateIdentificationRequestDto requestDto) throws ConnectorException;
}
