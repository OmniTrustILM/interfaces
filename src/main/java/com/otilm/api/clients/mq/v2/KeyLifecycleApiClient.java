package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.KeyLifecycleSyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateIssuedResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CertificateRequestCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.lifecycle.CreateCertificateRequestV2Dto;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * MQ implementation of the connector-facing cryptography v2 key lifecycle API.
 *
 * <p>
 * {@link #createCertificateRequest} and {@link #certificateIssued} accept only HTTP 200, so they require a
 * {@link ProxyClient} that overrides {@link ProxyClient#sendRequestForEntity} and propagates the real status.
 * </p>
 */
@SuppressWarnings("java:S1075")
public class KeyLifecycleApiClient implements KeyLifecycleSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keyLifecycle";
    private static final String SIGNER_ATTRIBUTES_PATH = BASE_PATH + "/signer/attributes";
    private static final String CERTIFICATE_REQUEST_PATH = BASE_PATH + "/csr";
    private static final String CERTIFICATE_REQUEST_ATTRIBUTES_PATH = CERTIFICATE_REQUEST_PATH + "/attributes";
    private static final String CERTIFICATE_ISSUED_PATH = BASE_PATH + "/certificate";
    private static final String POST = "POST";
    private final ProxyClient proxyClient;
    private final OperationResponseValidator responseValidator;

    public KeyLifecycleApiClient(ProxyClient proxyClient, OperationResponseValidator responseValidator) {
        this.proxyClient = proxyClient;
        this.responseValidator = responseValidator;
    }

    private static void requireValid(OperationValidationResult validation, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (!validation.isValid()) {
            IllegalArgumentException cause = validation.getCause();
            throw new ConnectorException(cause.getMessage(), cause, connector);
        }
    }

    @Override
    public List<BaseAttribute> listSignerAttributes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto request) throws ConnectorException {
        List<BaseAttribute> attributes = sendForList(connector, SIGNER_ATTRIBUTES_PATH, request, BaseAttribute[].class);
        requireValid(responseValidator.validateSignerAttributeList(attributes), connector);
        return attributes;
    }

    @Override
    public List<BaseAttribute> listCertificateRequestAttributes(ApiClientConnectorInfo connector,
            KeyScopedRequestV2Dto request) throws ConnectorException {
        List<BaseAttribute> attributes = sendForList(connector, CERTIFICATE_REQUEST_ATTRIBUTES_PATH, request,
                BaseAttribute[].class);
        requireValid(responseValidator.validateCertificateRequestAttributeList(attributes), connector);
        return attributes;
    }

    @Override
    public CertificateRequestCreationResponseV2Dto createCertificateRequest(ApiClientConnectorInfo connector,
            CreateCertificateRequestV2Dto request) throws ConnectorException {
        CertificateRequestCreationResponseV2Dto response = synchronousBody(
                sendEntity(connector, CERTIFICATE_REQUEST_PATH, request, CertificateRequestCreationResponseV2Dto.class),
                connector);
        requireValid(responseValidator.validateCreateCertificateRequest(request, response), connector);
        return response;
    }

    @Override
    public CertificateIssuedResponseV2Dto certificateIssued(ApiClientConnectorInfo connector,
            CertificateIssuedRequestV2Dto request) throws ConnectorException {
        CertificateIssuedResponseV2Dto response = synchronousBody(
                sendEntity(connector, CERTIFICATE_ISSUED_PATH, request, CertificateIssuedResponseV2Dto.class),
                connector);
        requireValid(responseValidator.validateCertificateIssued(response), connector);
        return response;
    }

    private <T> List<T> sendForList(ApiClientConnectorInfo connector, String path, Object body, Class<T[]> responseType)
            throws ConnectorException {
        T[] response = send(connector, path, body, responseType);
        if (response == null) {
            throw new ConnectorException("Connector returned an empty body for " + path, connector);
        }
        return Arrays.asList(response);
    }

    private <T> T send(ApiClientConnectorInfo connector, String path, Object body, Class<T> responseType)
            throws ConnectorException {
        try {
            return proxyClient.sendRequest(connector, path, POST, body, responseType);
        } catch (RuntimeException e) {
            throw new ConnectorException("Key lifecycle request failed for " + path, e, connector);
        }
    }

    private <T> ResponseEntity<T> sendEntity(ApiClientConnectorInfo connector, String path, Object body,
            Class<T> responseType) throws ConnectorException {
        try {
            return proxyClient.sendRequestForEntity(connector, path, POST, body, responseType);
        } catch (RuntimeException e) {
            throw new ConnectorException("Key lifecycle request failed for " + path, e, connector);
        }
    }

    private <T> T synchronousBody(ResponseEntity<T> response, ApiClientConnectorInfo connector)
            throws ConnectorException {
        requireValid(responseValidator.validateSynchronousResponseStatus(response), connector);
        return response.getBody();
    }
}
