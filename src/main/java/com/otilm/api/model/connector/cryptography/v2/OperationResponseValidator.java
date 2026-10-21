package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.enums.cryptography.EncryptionAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDestructionStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyMetaMatcher;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyTypeV2;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.UnaddressableKeyV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.CipherDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.DecryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.EncryptDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.EncryptionAlgorithmAttribute;
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
import jakarta.validation.Validator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
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

    public OperationValidationResult validateSynchronousResponseStatus(ResponseEntity<?> response) {
        return validate(() -> requireResponseStatus(response, HttpStatus.OK));
    }

    public OperationValidationResult validateAttributeList(List<BaseAttribute> response) {
        return validateResponseElements(response, "attribute");
    }

    public OperationValidationResult validateSignAttributeList(List<BaseAttribute> response) {
        return validateSignatureAttributeList(response, "Sign");
    }

    public OperationValidationResult validateVerifyAttributeList(List<BaseAttribute> response) {
        return validateSignatureAttributeList(response, "Verify");
    }

    private OperationValidationResult validateSignatureAttributeList(List<BaseAttribute> response, String operation) {
        OperationValidationResult validationResult = validateAttributeList(response);
        if (!validationResult.isValid()) {
            return validationResult;
        }
        return validate(() -> requireSignatureAlgorithmOffered(response, operation));
    }

    private static void requireSignatureAlgorithmOffered(List<BaseAttribute> schema, String operation) {
        String identity = "attribute with name '" + SignatureAlgorithmAttribute.NAME + "' and UUID '"
                + SignatureAlgorithmAttribute.ATTRIBUTE_UUID + "'";
        List<BaseAttribute> declarations = schema
                .stream()
                .filter(attribute -> SignatureAlgorithmAttribute.ATTRIBUTE_UUID
                        .toString()
                        .equalsIgnoreCase(attribute.getUuid())
                        || SignatureAlgorithmAttribute.NAME.equals(attribute.getName()))
                .toList();
        if (declarations.size() != 1 || !(declarations.get(0) instanceof DataAttributeV3 definition)
                || definition.getVersion() != AttributeVersion.V3.getVersion()
                || definition.getSchemaVersion() != AttributeVersion.V3
                || !SignatureAlgorithmAttribute.ATTRIBUTE_UUID.toString().equalsIgnoreCase(definition.getUuid())
                || !SignatureAlgorithmAttribute.NAME.equals(definition.getName())) {
            throw new IllegalArgumentException(operation + " attributes must declare exactly one v3 " + identity);
        }
        DataAttributeProperties properties = definition.getProperties();
        if (properties == null || !properties.isRequired() || properties.isMultiSelect() || !properties.isList()
                || properties.isExtensibleList()) {
            throw new IllegalArgumentException(
                    "The " + identity + " must be a required, single-select, non-extensible list");
        }
        List<? extends AttributeContent> options = definition.getContent();
        if (definition.getContentType() != AttributeContentType.STRING || options == null || options.isEmpty()
                || !options.stream().allMatch(OperationResponseValidator::isSignatureAlgorithmCode)) {
            throw new IllegalArgumentException(
                    "The " + identity + " must offer at least one value, and only signature algorithm codes");
        }
    }

    private static boolean isSignatureAlgorithmCode(AttributeContent option) {
        return option != null && option.getContentType() == AttributeContentType.STRING
                && option.getData() instanceof String code && SignatureAlgorithm.lookupByCode(code).isPresent();
    }

    /** Encrypt/decrypt schemas must publish encryptionAlgorithm as a required closed list of known algorithm codes. */
    public OperationValidationResult validateCipherAttributeList(List<BaseAttribute> response) {
        OperationValidationResult validationResult = validateAttributeList(response);
        if (!validationResult.isValid()) {
            return validationResult;
        }
        return validate(() -> requireEncryptionAlgorithmOffered(response));
    }

    private static void requireEncryptionAlgorithmOffered(List<BaseAttribute> schema) {
        String identity = "attribute with name '" + EncryptionAlgorithmAttribute.NAME + "' and UUID '"
                + EncryptionAlgorithmAttribute.ATTRIBUTE_UUID + "'";
        List<BaseAttribute> declarations = schema
                .stream()
                .filter(attribute -> EncryptionAlgorithmAttribute.ATTRIBUTE_UUID
                        .toString()
                        .equalsIgnoreCase(attribute.getUuid())
                        || EncryptionAlgorithmAttribute.NAME.equals(attribute.getName()))
                .toList();
        if (declarations.size() != 1 || !(declarations.get(0) instanceof DataAttributeV3 definition)
                || definition.getVersion() != AttributeVersion.V3.getVersion()
                || definition.getSchemaVersion() != AttributeVersion.V3
                || !EncryptionAlgorithmAttribute.ATTRIBUTE_UUID.toString().equalsIgnoreCase(definition.getUuid())
                || !EncryptionAlgorithmAttribute.NAME.equals(definition.getName())) {
            throw new IllegalArgumentException("Cipher attributes must declare exactly one v3 " + identity);
        }
        DataAttributeProperties properties = definition.getProperties();
        if (properties == null || !properties.isRequired() || properties.isMultiSelect() || !properties.isList()
                || properties.isExtensibleList()) {
            throw new IllegalArgumentException(
                    "The " + identity + " must be a required, single-select, non-extensible list");
        }
        List<? extends AttributeContent> options = definition.getContent();
        if (definition.getContentType() != AttributeContentType.STRING || options == null || options.isEmpty()
                || !options.stream().allMatch(OperationResponseValidator::isEncryptionAlgorithmCode)) {
            throw new IllegalArgumentException(
                    "The " + identity + " must offer at least one value, and only encryption algorithm codes");
        }
    }

    private static boolean isEncryptionAlgorithmCode(AttributeContent option) {
        return option != null && option.getContentType() == AttributeContentType.STRING
                && option.getData() instanceof String code && EncryptionAlgorithm.lookupByCode(code).isPresent();
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

    public OperationValidationResult validateListKeys(ListKeysRequestV2Dto request, ListKeysResponseV2Dto response) {
        return validate(() -> {
            requireRequest(request, "Key listing");
            validateRequiredBean(response);
            requireWithinLimit(request, response);
            requireRequestedType(request.getKeyRequestType(), response);
            requireNameableHandles(response.getItems());
            requireUnaddressableKeysOnlyWhenUnfiltered(request, response);
            requireNamedByFilter(request, response.getItems());
        });
    }

    private static void requireWithinLimit(ListKeysRequestV2Dto request, ListKeysResponseV2Dto response) {
        Integer limit = request.getLimit();
        if (limit == null) {
            throw new IllegalArgumentException("Key listing limit is required");
        }
        int returned = response.getItems().size() + unaddressableKeysOf(response).size();
        if (returned > limit) {
            throw new IllegalArgumentException(
                    "Connector returned " + returned + " keys; the request allows at most " + limit);
        }
    }

    private static void requireRequestedType(KeyRequestType requested, ListKeysResponseV2Dto response) {
        if (requested == null) {
            throw new IllegalArgumentException("Key listing key request type is required");
        }
        boolean itemsOfType = response.getItems().stream().allMatch(key -> key.getKeyRequestType() == requested);
        boolean unaddressableOfType = unaddressableKeysOf(response)
                .stream()
                .allMatch(key -> unaddressableKeyTypeOf(requested) == keyTypeOf(key.getKeyData()));
        if (!itemsOfType || !unaddressableOfType) {
            throw new IllegalArgumentException("Connector returned a key of another type than the request lists");
        }
    }

    /** A key pair without a unique identifier is reported by its private key. */
    private static KeyTypeV2 unaddressableKeyTypeOf(KeyRequestType requested) {
        return requested == KeyRequestType.SECRET ? KeyTypeV2.SECRET : KeyTypeV2.PRIVATE;
    }

    private static KeyTypeV2 keyTypeOf(KeyDataV2Dto keyData) {
        return keyData == null ? null : keyData.getType();
    }

    private static void requireNameableHandles(List<ListedKeyV2Dto> keys) {
        if (!keys.stream().allMatch(key -> KeyMetaMatcher.isUnambiguous(key.handle()))) {
            throw new IllegalArgumentException("Connector returned a key whose metadata no filter can name");
        }
    }

    private static void requireUnaddressableKeysOnlyWhenUnfiltered(ListKeysRequestV2Dto request,
            ListKeysResponseV2Dto response) {
        if (request.getKeyMetaFilter() != null && !unaddressableKeysOf(response).isEmpty()) {
            throw new IllegalArgumentException("Connector returned unaddressable keys to a filtered listing");
        }
    }

    private static void requireNamedByFilter(ListKeysRequestV2Dto request, List<ListedKeyV2Dto> keys) {
        List<List<MetadataAttribute>> filter = request.getKeyMetaFilter();
        if (filter != null && !keys.stream().allMatch(key -> isNamedBy(filter, key))) {
            throw new IllegalArgumentException("Connector returned a key the filter does not name");
        }
    }

    private static List<UnaddressableKeyV2Dto> unaddressableKeysOf(ListKeysResponseV2Dto response) {
        return Objects.requireNonNullElse(response.getUnaddressableKeys(), List.of());
    }

    private static boolean isNamedBy(List<List<MetadataAttribute>> filter, ListedKeyV2Dto key) {
        return filter
                .stream()
                .filter(Objects::nonNull)
                .anyMatch(handle -> KeyMetaMatcher.matches(handle, key.handle()));
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
