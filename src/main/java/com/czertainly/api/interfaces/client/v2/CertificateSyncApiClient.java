package com.czertainly.api.interfaces.client.v2;

import com.czertainly.api.exception.ConnectorException;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.BaseAttribute;
import com.czertainly.api.model.connector.v2.*;
import com.czertainly.api.clients.ApiClientConnectorInfo;

import java.util.List;

/**
 * Sync interface for v2 Certificate API client operations.
 * This interface is implemented by both REST and MQ clients.
 */
public interface CertificateSyncApiClient {

    List<BaseAttribute> listIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException;

    Boolean validateIssueCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;

    CertificateDataResponseDto issueCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateSignRequestDto requestDto) throws ConnectorException;

    CertificateDataResponseDto renewCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateRenewRequestDto requestDto) throws ConnectorException;

    List<BaseAttribute> listRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid) throws ConnectorException;

    Boolean validateRevokeCertificateAttributes(ApiClientConnectorInfo connector, String authorityUuid, List<RequestAttribute> attributes) throws ValidationException, ConnectorException;

    void revokeCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertRevocationDto requestDto) throws ConnectorException;

    CertificateIdentificationResponseDto identifyCertificate(ApiClientConnectorInfo connector, String authorityUuid, CertificateIdentificationRequestDto requestDto) throws ValidationException, ConnectorException;
}
