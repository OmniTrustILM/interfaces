package com.otilm.api.interfaces.client.v3;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.v3.certificate.CertificateAttributeListRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateDataResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateIdentificationRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateIdentificationResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateOperationCancelRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateOperationStatusRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateOperationStatusResponseDto;
import com.otilm.api.model.connector.v3.certificate.CertificateRegistrationRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRenewRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateRevocationRequestDtoV3;
import com.otilm.api.model.connector.v3.certificate.CertificateSignRequestDtoV3;
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

    List<BaseAttribute> listIssueAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDtoV3 requestDto) throws ConnectorException;

    ResponseEntity<CertificateDataResponseDto> issue(ApiClientConnectorInfo connector, CertificateSignRequestDtoV3 requestDto) throws ConnectorException;

    CertificateOperationStatusResponseDto getIssueStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDtoV3 requestDto) throws ConnectorException;

    ResponseEntity<Void> cancelIssue(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDtoV3 requestDto) throws ConnectorException;

    // ---- Renew (status/cancel via /issue/*) ----

    ResponseEntity<CertificateDataResponseDto> renew(ApiClientConnectorInfo connector, CertificateRenewRequestDtoV3 requestDto) throws ConnectorException;

    // ---- Revoke ----

    List<BaseAttribute> listRevokeAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDtoV3 requestDto) throws ConnectorException;

    ResponseEntity<CertificateDataResponseDto> revoke(ApiClientConnectorInfo connector, CertificateRevocationRequestDtoV3 requestDto) throws ConnectorException;

    CertificateOperationStatusResponseDto getRevokeStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDtoV3 requestDto) throws ConnectorException;

    ResponseEntity<Void> cancelRevoke(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDtoV3 requestDto) throws ConnectorException;

    // ---- Register ----

    List<BaseAttribute> listRegisterAttributes(ApiClientConnectorInfo connector, CertificateAttributeListRequestDtoV3 requestDto) throws ConnectorException;

    ResponseEntity<CertificateDataResponseDto> register(ApiClientConnectorInfo connector, CertificateRegistrationRequestDtoV3 requestDto) throws ConnectorException;

    CertificateOperationStatusResponseDto getRegisterStatus(ApiClientConnectorInfo connector, CertificateOperationStatusRequestDtoV3 requestDto) throws ConnectorException;

    ResponseEntity<Void> cancelRegister(ApiClientConnectorInfo connector, CertificateOperationCancelRequestDtoV3 requestDto) throws ConnectorException;

    // ---- Identify ----

    CertificateIdentificationResponseDto identify(ApiClientConnectorInfo connector, CertificateIdentificationRequestDtoV3 requestDto) throws ConnectorException;
}
