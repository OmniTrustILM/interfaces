package com.otilm.api.interfaces.client.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateRequestCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CreateCertificateRequestV2Dto;
import java.util.List;

/**
 * Sync contract shared by REST and MQ clients for the cryptography v2 key lifecycle hooks.
 */
public interface KeyLifecycleSyncApiClient {

    List<BaseAttribute> listSignerAttributes(ApiClientConnectorInfo connector, TokenProfileScopedRequestV2Dto request)
            throws ConnectorException;

    List<BaseAttribute> listCertificateRequestAttributes(ApiClientConnectorInfo connector,
            KeyScopedRequestV2Dto request) throws ConnectorException;

    CertificateRequestCreationResponseV2Dto createCertificateRequest(ApiClientConnectorInfo connector,
            CreateCertificateRequestV2Dto request) throws ConnectorException;

    CertificateIssuedResponseV2Dto certificateIssued(ApiClientConnectorInfo connector,
            CertificateIssuedRequestV2Dto request) throws ConnectorException;
}
