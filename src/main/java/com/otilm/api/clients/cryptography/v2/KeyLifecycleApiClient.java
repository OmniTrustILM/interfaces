package com.otilm.api.clients.cryptography.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
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
import java.util.List;
import javax.net.ssl.TrustManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

@SuppressWarnings("java:S1075")
public class KeyLifecycleApiClient extends BaseApiClient implements KeyLifecycleSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keyLifecycle";
    private static final String SIGNER_ATTRIBUTES_PATH = BASE_PATH + "/signer/attributes";
    private static final String CERTIFICATE_REQUEST_PATH = BASE_PATH + "/csr";
    private static final String CERTIFICATE_REQUEST_ATTRIBUTES_PATH = CERTIFICATE_REQUEST_PATH + "/attributes";
    private static final String CERTIFICATE_ISSUED_PATH = BASE_PATH + "/certificate";
    private final OperationResponseValidator responseValidator;

    public KeyLifecycleApiClient(WebClient webClient, TrustManager[] defaultTrustManagers,
            OperationResponseValidator responseValidator) {
        super(webClient, defaultTrustManagers);
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
            TokenProfileScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + SIGNER_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listSignerAttributes"), request, connector);
        requireValid(responseValidator.validateSignerAttributeList(response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listCertificateRequestAttributes(ApiClientConnectorInfo connector,
            KeyScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + CERTIFICATE_REQUEST_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listCertificateRequestAttributes"), request, connector);
        requireValid(responseValidator.validateCertificateRequestAttributeList(response), connector);
        return response;
    }

    @Override
    public CertificateRequestCreationResponseV2Dto createCertificateRequest(ApiClientConnectorInfo connector,
            CreateCertificateRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<CertificateRequestCreationResponseV2Dto> entity = processRequest(
                r -> requireResponse(r
                        .uri(connector.getUrl() + CERTIFICATE_REQUEST_PATH)
                        .bodyValue(body)
                        .retrieve()
                        .toEntity(CertificateRequestCreationResponseV2Dto.class), "createCertificateRequest"),
                request, connector);
        CertificateRequestCreationResponseV2Dto response = synchronousBody(entity, connector);
        requireValid(responseValidator.validateCreateCertificateRequest(body, response), connector);
        return response;
    }

    @Override
    public CertificateIssuedResponseV2Dto certificateIssued(ApiClientConnectorInfo connector,
            CertificateIssuedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<CertificateIssuedResponseV2Dto> entity = processRequest(r -> requireResponse(r
                .uri(connector.getUrl() + CERTIFICATE_ISSUED_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(CertificateIssuedResponseV2Dto.class), "certificateIssued"), request, connector);
        CertificateIssuedResponseV2Dto response = synchronousBody(entity, connector);
        requireValid(responseValidator.validateCertificateIssued(response), connector);
        return response;
    }

    private <T> T synchronousBody(ResponseEntity<T> response, ApiClientConnectorInfo connector)
            throws ConnectorException {
        requireValid(responseValidator.validateSynchronousResponseStatus(response), connector);
        return response.getBody();
    }
}
