package com.otilm.api.clients.cryptography.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.BaseApiClient;
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
import java.util.List;
import javax.net.ssl.TrustManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient implementation of the connector-facing stateless cryptography v2 operations API.
 */
@SuppressWarnings("java:S1075") // contract paths, not configurable URIs
public class CryptographicOperationsApiClient extends BaseApiClient implements CryptographicOperationsSyncApiClient {

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
    private final OperationResponseValidator responseValidator;

    public CryptographicOperationsApiClient(WebClient webClient, TrustManager[] defaultTrustManagers,
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
    public List<BaseAttribute> listEncryptAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + ENCRYPT_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listEncryptAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public EncryptDataResponseV2Dto encryptData(ApiClientConnectorInfo connector, CipherDataRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        EncryptDataResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + ENCRYPT_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(EncryptDataResponseV2Dto.class), "encryptData"), request, connector);
        requireValid(responseValidator.validateEncrypt(body, response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listDecryptAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + DECRYPT_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listDecryptAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public DecryptDataResponseV2Dto decryptData(ApiClientConnectorInfo connector, CipherDataRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        DecryptDataResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + DECRYPT_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(DecryptDataResponseV2Dto.class), "decryptData"), request, connector);
        requireValid(responseValidator.validateDecrypt(body, response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listSignAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + SIGN_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listSignAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<SignDataResponseV2Dto> signData(ApiClientConnectorInfo connector, SignDataRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        ResponseEntity<SignDataResponseV2Dto> response = processRequest(r -> requireResponse(
                r.uri(connector.getUrl() + SIGN_PATH).bodyValue(body).retrieve().toEntity(SignDataResponseV2Dto.class),
                "signData"), request, connector);
        requireValid(responseValidator.validateSign(body, response), connector);
        return response;
    }

    @Override
    public SignOperationStatusResponseV2Dto getSignStatus(ApiClientConnectorInfo connector,
            OperationTrackingRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        SignOperationStatusResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + SIGN_STATUS_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(SignOperationStatusResponseV2Dto.class), "getSignStatus"), request, connector);
        requireValid(responseValidator.validateSignStatus(response), connector);
        return response;
    }

    @Override
    public ResponseEntity<Void> cancelSign(ApiClientConnectorInfo connector, OperationTrackingRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        return processRequest(r -> requireResponse(
                r.uri(connector.getUrl() + SIGN_CANCEL_PATH).bodyValue(body).retrieve().toBodilessEntity(),
                "cancelSign"), request, connector);
    }

    @Override
    public List<BaseAttribute> listVerifyAttributes(ApiClientConnectorInfo connector, KeyScopedRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + VERIFY_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listVerifyAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public VerifyDataResponseV2Dto verifyData(ApiClientConnectorInfo connector, VerifyDataRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        VerifyDataResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + VERIFY_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(VerifyDataResponseV2Dto.class), "verifyData"), request, connector);
        requireValid(responseValidator.validateVerify(body, response), connector);
        return response;
    }

    @Override
    public List<BaseAttribute> listRandomAttributes(ApiClientConnectorInfo connector,
            TokenProfileScopedRequestV2Dto body) throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        List<BaseAttribute> response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + RANDOM_ATTRIBUTES_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntityList(BaseAttribute.class), "listRandomAttributes"), request, connector);
        requireValid(responseValidator.validateAttributeList(response), connector);
        return response;
    }

    @Override
    public RandomDataResponseV2Dto randomData(ApiClientConnectorInfo connector, RandomDataRequestV2Dto body)
            throws ConnectorException {
        WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST, connector, true);
        RandomDataResponseV2Dto response = processRequest(r -> requireBody(r
                .uri(connector.getUrl() + RANDOM_PATH)
                .bodyValue(body)
                .retrieve()
                .toEntity(RandomDataResponseV2Dto.class), "randomData"), request, connector);
        requireValid(responseValidator.validateRandom(body, response), connector);
        return response;
    }
}
