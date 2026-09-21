package com.otilm.api.clients.mq.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.interfaces.client.v2.CryptographicOperationsSyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationResponseValidator;
import com.otilm.api.model.connector.cryptography.v2.OperationTrackingRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationValidationResult;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.CipherDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.DecryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.EncryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignOperationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataResponseV2Dto;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;

/**
 * MQ implementation of the connector-facing stateless cryptography v2 operations API.
 */
@SuppressWarnings("java:S1075")
public class CryptographicOperationsApiClient implements CryptographicOperationsSyncApiClient {

    private static final String BASE_PATH = "/v2/cryptographyProvider/operations";
    private static final String ENCRYPT_ATTRIBUTES_PATH = BASE_PATH + "/encrypt/attributes";
    private static final String ENCRYPT_PATH = BASE_PATH + "/encrypt";
    private static final String DECRYPT_ATTRIBUTES_PATH = BASE_PATH + "/decrypt/attributes";
    private static final String DECRYPT_PATH = BASE_PATH + "/decrypt";
    private static final String SIGN_ATTRIBUTES_PATH = BASE_PATH + "/sign/attributes";
    private static final String SIGN_PATH = BASE_PATH + "/sign";
    private static final String SIGN_STATUS_PATH = BASE_PATH + "/sign/status";
    private static final String SIGN_CANCEL_PATH = BASE_PATH + "/sign/cancel";
    private static final String VERIFY_ATTRIBUTES_PATH = BASE_PATH + "/verify/attributes";
    private static final String VERIFY_PATH = BASE_PATH + "/verify";
    private static final String RANDOM_ATTRIBUTES_PATH = BASE_PATH + "/random/attributes";
    private static final String RANDOM_PATH = BASE_PATH + "/random";
    private static final String POST = "POST";
    private final ProxyClient proxyClient;
    private final OperationResponseValidator responseValidator;

    public CryptographicOperationsApiClient(ProxyClient proxyClient, OperationResponseValidator responseValidator) {
        this.proxyClient = proxyClient;
        this.responseValidator = responseValidator;
    }

    @Override
    public List<BaseAttribute> listEncryptAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException {
        List<BaseAttribute> response = sendAttributes(connector, ENCRYPT_ATTRIBUTES_PATH, request,
                "listEncryptAttributes");
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public EncryptDataResponseV2Dto encryptData(ApiClientConnectorInfo connector, CipherDataRequestV2Dto request)
            throws ConnectorException {
        EncryptDataResponseV2Dto response = send(connector, ENCRYPT_PATH, request, EncryptDataResponseV2Dto.class);
        requireValid(responseValidator.validateEncrypt(request, response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listDecryptAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException {
        List<BaseAttribute> response = sendAttributes(connector, DECRYPT_ATTRIBUTES_PATH, request,
                "listDecryptAttributes");
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public DecryptDataResponseV2Dto decryptData(ApiClientConnectorInfo connector, CipherDataRequestV2Dto request)
            throws ConnectorException {
        DecryptDataResponseV2Dto response = send(connector, DECRYPT_PATH, request, DecryptDataResponseV2Dto.class);
        requireValid(responseValidator.validateDecrypt(request, response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listSignAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException {
        List<BaseAttribute> response = sendAttributes(connector, SIGN_ATTRIBUTES_PATH, request, "listSignAttributes");
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<SignDataResponseV2Dto> signData(ApiClientConnectorInfo connector,
            SignDataRequestV2Dto request) throws ConnectorException {
        ResponseEntity<SignDataResponseV2Dto> response = sendEntity(connector, SIGN_PATH, request,
                SignDataResponseV2Dto.class);
        requireValid(responseValidator.validateSign(request, response), connector);
        return response;
    }

    @Override
    public SignOperationStatusResponseV2Dto getSignStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto request) throws ConnectorException {
        SignOperationStatusResponseV2Dto response = send(connector, SIGN_STATUS_PATH, request,
                SignOperationStatusResponseV2Dto.class);
        requireValid(responseValidator.validateSignStatus(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<Void> cancelSign(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto request)
            throws ConnectorException {
        return sendEntity(connector, SIGN_CANCEL_PATH, request, Void.class);
    }

    @Override
    public List<BaseAttribute> listVerifyAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto request)
            throws ConnectorException {
        List<BaseAttribute> response = sendAttributes(connector, VERIFY_ATTRIBUTES_PATH, request,
                "listVerifyAttributes");
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public VerifyDataResponseV2Dto verifyData(ApiClientConnectorInfo connector, VerifyDataRequestV2Dto request)
            throws ConnectorException {
        VerifyDataResponseV2Dto response = send(connector, VERIFY_PATH, request, VerifyDataResponseV2Dto.class);
        requireValid(responseValidator.validateVerify(request, response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listRandomAttributes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto request) throws ConnectorException {
        List<BaseAttribute> response = sendAttributes(connector, RANDOM_ATTRIBUTES_PATH, request,
                "listRandomAttributes");
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public RandomDataResponseV2Dto randomData(ApiClientConnectorInfo connector, RandomDataRequestV2Dto request)
            throws ConnectorException {
        RandomDataResponseV2Dto response = send(connector, RANDOM_PATH, request, RandomDataResponseV2Dto.class);
        requireValid(responseValidator.validateRandom(request, response), connector);
        return response;
    }

    private List<BaseAttribute> sendAttributes(ApiClientConnectorInfo connector, String path, Object request,
            String operation) throws ConnectorException {
        BaseAttribute[] response = send(connector, path, request, BaseAttribute[].class);
        if (response == null) {
            throw new ConnectorException("Connector returned an empty attributes response: " + operation, connector);
        }
        return Arrays.asList(response);
    }

    private <T> T send(ApiClientConnectorInfo connector, String path, Object request, Class<T> responseType)
            throws ConnectorException {
        try {
            return proxyClient.sendRequest(connector, path, POST, request, responseType);
        } catch (RuntimeException e) {
            throw new ConnectorException("Cryptographic operation request failed for " + path, e, connector);
        }
    }

    private <T> ResponseEntity<T> sendEntity(ApiClientConnectorInfo connector, String path, Object request,
            Class<T> responseType) throws ConnectorException {
        try {
            return proxyClient.sendRequestForEntity(connector, path, POST, request, responseType);
        } catch (RuntimeException e) {
            throw new ConnectorException("Cryptographic operation request failed for " + path, e, connector);
        }
    }

    private static void requireValid(OperationValidationResult validation, ApiClientConnectorInfo connector)
            throws ConnectorException {
        if (!validation.isValid()) {
            IllegalArgumentException cause = validation.getCause();
            throw new ConnectorException(cause.getMessage(), cause, connector);
        }
    }
}
