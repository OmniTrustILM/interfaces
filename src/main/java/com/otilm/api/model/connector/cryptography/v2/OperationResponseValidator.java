package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.DataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDestructionStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.CipherDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.DecryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.EncryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignOperationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignatureAlgorithmAttribute;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenStatusResponseV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;

/**
 * Validates that an async-capable connector response honors the caller-selected execution mode.
 */
public final class OperationResponseValidator extends ResponseChecks {

    private final KeyTransferResponseValidator keyTransfer;

    public OperationResponseValidator(Validator validator) {
        super(validator);
        this.keyTransfer = new KeyTransferResponseValidator(validator());
    }

    /**
     * Validation of the operations that move key material into and out of a technology. Those responses are checked
     * against the platform's record of the key as well as against their own field rules, which is a concern of its own.
     *
     * @return the validator for the key import and key export operations
     */
    public KeyTransferResponseValidator keyTransfer() {
        return keyTransfer;
    }

    private static void requireMatchingIdentifiers(List<? extends IdentifiedDataV2Dto> requestItems,
            List<? extends IdentifiedDataV2Dto> responseItems, String errorMessage) {
        if (requestItems == null) {
            throw new IllegalArgumentException("Request data is required");
        }
        if (responseItems == null) {
            throw new IllegalArgumentException("Response data is required");
        }
        requireNonNullItems(requestItems, "Request data");
        requireNonNullItems(responseItems, "Response data");
        Set<String> requestIdentifiers = requestItems
                .stream()
                .map(IdentifiedDataV2Dto::getIdentifier)
                .collect(Collectors.toSet());
        List<String> responseIdentifiers = responseItems.stream().map(IdentifiedDataV2Dto::getIdentifier).toList();
        Set<String> uniqueResponseIdentifiers = new HashSet<>(responseIdentifiers);
        if (uniqueResponseIdentifiers.size() != responseIdentifiers.size()
                || !uniqueResponseIdentifiers.equals(requestIdentifiers)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static void requireNonNullItems(List<? extends IdentifiedDataV2Dto> items, String itemSource) {
        for (IdentifiedDataV2Dto item : items) {
            if (item == null) {
                throw new IllegalArgumentException(itemSource + " must not contain null items");
            }
        }
    }

    public OperationValidationResult validateTokenStatus(TokenStatusResponseV2Dto response) {
        return validateBeanConstraints(response);
    }

    public OperationValidationResult validateAttributeList(List<BaseAttribute> response) {
        return validateResponseElements(response, "attribute");
    }

    /** A caller names the signature algorithm from the selection before it signs, so the schema must offer it. */
    public OperationValidationResult validateSignAttributeList(List<BaseAttribute> response) {
        OperationValidationResult elements = validateAttributeList(response);
        if (!elements.isValid()) {
            return elements;
        }
        return validate(() -> requireSignatureAlgorithmOffered(response));
    }

    private static void requireSignatureAlgorithmOffered(List<BaseAttribute> schema) {
        DataAttribute definition = schema
                .stream()
                .filter(attribute -> SignatureAlgorithmAttribute.NAME.equals(attribute.getName()))
                .filter(DataAttribute.class::isInstance)
                .map(DataAttribute.class::cast)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sign attributes must declare the " + SignatureAlgorithmAttribute.NAME + " data attribute"));
        DataAttributeProperties properties = definition.getProperties();
        if (properties == null || !properties.isRequired() || properties.isMultiSelect()) {
            throw new IllegalArgumentException(
                    SignatureAlgorithmAttribute.NAME + " must be a required single-select attribute");
        }
        List<? extends AttributeContent> options = definition.getContent();
        if (definition.getContentType() != AttributeContentType.STRING || options == null || options.isEmpty()
                || !options.stream().allMatch(OperationResponseValidator::isSignatureAlgorithmCode)) {
            throw new IllegalArgumentException(SignatureAlgorithmAttribute.NAME
                    + " must offer at least one value, and only signature algorithm codes");
        }
    }

    private static boolean isSignatureAlgorithmCode(AttributeContent option) {
        return option != null && option.getData() instanceof String code
                && SignatureAlgorithm.lookupByCode(code).isPresent();
    }

    public OperationValidationResult validateKeyUsageList(List<KeyUsage> response) {
        return validateResponseElements(response, "key usage");
    }

    public OperationValidationResult validateSupportedKeyRequestTypes(List<KeyRequestType> response) {
        return validateResponseElements(response, "key request type");
    }

    public OperationValidationResult validateCreateKey(CreateKeyRequestV2Dto request,
            ResponseEntity<? extends KeyCreationResponseV2Dto> response) {
        return validate(() -> {
            requireRequest(request, "Key creation");
            validateKeyCreationOutcome(request.getKeyRequestType(), request.getExecutionMode(), response);
        });
    }

    public OperationValidationResult validateCreateKeyStatus(KeyCreationStatusResponseV2Dto response) {
        return validateBeanConstraints(response);
    }

    public OperationValidationResult validateDestroy(OperationExecutionMode mode,
            ResponseEntity<KeyOperationResponseV2Dto> response) {
        return validateOperationResponse(mode, response);
    }

    private OperationValidationResult validateOperationResponse(OperationExecutionMode mode,
            ResponseEntity<?> response) {
        return validate(() -> validateOperationResponseConstraints(mode, response));
    }

    public OperationValidationResult validateDestroyKeyStatus(KeyDestructionStatusResponseV2Dto response) {
        return validateBeanConstraints(response);
    }

    public OperationValidationResult validateEncrypt(CipherDataRequestV2Dto request,
            EncryptDataResponseV2Dto response) {
        return validate(() -> {
            requireRequest(request, "Encryption");
            validateRequiredBean(response);
            requireMatchingIdentifiers(request.getCipherData(), response.getEncryptedData(),
                    "Encryption response identifiers must match request identifiers");
        });
    }

    public OperationValidationResult validateDecrypt(CipherDataRequestV2Dto request,
            DecryptDataResponseV2Dto response) {
        return validate(() -> {
            requireRequest(request, "Decryption");
            validateRequiredBean(response);
            requireMatchingIdentifiers(request.getCipherData(), response.getDecryptedData(),
                    "Decryption response identifiers must match request identifiers");
        });
    }

    public OperationValidationResult validateVerify(VerifyDataRequestV2Dto request, VerifyDataResponseV2Dto response) {
        return validate(() -> {
            requireRequest(request, "Verification");
            validateRequiredBean(response);
            requireMatchingIdentifiers(request.getData(), response.getVerifications(),
                    "Verification response identifiers must match request identifiers");
        });
    }

    public OperationValidationResult validateRandom(RandomDataRequestV2Dto request, RandomDataResponseV2Dto response) {
        return validate(() -> {
            requireRequest(request, "Random-data");
            validateRequiredBean(response);
            if (response.getData().length != request.getLength()) {
                throw new IllegalArgumentException("Connector returned " + response.getData().length
                        + " random bytes; expected " + request.getLength());
            }
        });
    }

    public OperationValidationResult validateSignStatus(SignOperationStatusResponseV2Dto response) {
        return validateBeanConstraints(response);
    }

    public OperationValidationResult validateSign(SignDataRequestV2Dto request,
            ResponseEntity<SignDataResponseV2Dto> response) {
        return validate(() -> {
            requireRequest(request, "Signing");
            validateOperationResponseConstraints(request.getExecutionMode(), response);
            if (request.getExecutionMode() == OperationExecutionMode.SYNCHRONOUS) {
                requireMatchingIdentifiers(request.getData(), requireBody(response).getSignatures(),
                        "Synchronous signing response identifiers must match request identifiers");
            }
        });
    }
}
