package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyTypeV2;
import com.otilm.api.model.connector.cryptography.v2.key.PublicKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.TransferableKeyTypeV2Dto;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Validates the responses of the operations that move key material into and out of a technology. Each response is
 * checked against the platform's own record of the key as well as against its field rules.
 */
public final class KeyTransferResponseValidator extends ResponseChecks {

    public KeyTransferResponseValidator(Validator validator) {
        super(validator);
    }

    /**
     * Validates an import response against the key the request asked for.
     *
     * @param request the import request that was sent
     * @param response the connector's response
     * @return the validation outcome
     */
    public OperationValidationResult validateImportKey(ImportKeyRequestV2Dto request,
            ResponseEntity<? extends KeyCreationResponseV2Dto> response) {
        return validate(() -> {
            requireRequest(request, "Key import");
            validateKeyCreationOutcome(request.getKeyRequestType(), request.getExecutionMode(), response);
        });
    }

    /**
     * Validates the status of an asynchronous import against its own field rules.
     *
     * @param response the connector's response
     * @return the validation outcome
     */
    public OperationValidationResult validateImportKeyStatus(KeyCreationStatusResponseV2Dto response) {
        return validateBeanConstraints(response);
    }

    /**
     * Validates the key types a connector declares importable.
     *
     * @param response the connector's response
     * @return the validation outcome
     */
    public OperationValidationResult validateImportableKeyTypes(List<ImportableKeyTypeV2Dto> response) {
        return validateKeyTypeDeclarations(response, "importable key type");
    }

    /**
     * Validates the key types a connector declares exportable.
     *
     * @param response the connector's response
     * @return the validation outcome
     */
    public OperationValidationResult validateExportableKeyTypes(List<ExportableKeyTypeV2Dto> response) {
        return validateKeyTypeDeclarations(response, "exportable key type");
    }

    /**
     * Validates an export response against the request that was sent: HTTP 200 with a body, the durable reference
     * echoed exactly when the request carried one, and a descriptor of the requested key type. Whether the descriptor
     * matches the platform's record is checked with {@link #validateExportedKeyDescriptor}, which needs that record.
     *
     * @param request the export request that was sent
     * @param response the connector's response
     * @return the validation outcome
     */
    public OperationValidationResult validateExportKey(ExportKeyRequestV2Dto request,
            ResponseEntity<ExportKeyResponseV2Dto> response) {
        return validate(() -> {
            requireRequest(request, "Key export");
            KeyRequestType requestedType = request.getKeyRequestType();
            if (requestedType == null) {
                throw new IllegalArgumentException("Key request type is required");
            }
            requireResponseStatus(response, HttpStatus.OK);
            ExportKeyResponseV2Dto body = requireBody(response);
            validateRequiredBean(body);
            requireEchoedKeyReference(request.getKeyReference(), body.getKeyReference());
            requireDescriptorForKeyType(requestedType, body.getKeyData());
        });
    }

    /**
     * Validates that an export describes the key the platform holds a record of: the algorithm must match, a secret
     * key's length must match, and a key pair's public key must be the one on record, byte for byte.
     *
     * @param expected the descriptor of the key the platform holds, built from its own record
     * @param response the connector's export response, already validated with {@link #validateExportKey}
     * @return the validation outcome
     */
    public OperationValidationResult validateExportedKeyDescriptor(KeyDataV2Dto expected,
            ExportKeyResponseV2Dto response) {
        return validate(() -> {
            if (expected == null) {
                throw new IllegalArgumentException("Expected key descriptor is required");
            }
            if (response == null || response.getKeyData() == null) {
                throw new IllegalArgumentException("Connector did not describe the exported key");
            }
            requireMatchingDescriptor(expected, response.getKeyData());
        });
    }

    private static void requireMatchingDescriptor(KeyDataV2Dto expected, KeyDataV2Dto actual) {
        if (expected.getClass() != actual.getClass()) {
            throw new IllegalArgumentException(
                    "Connector described the exported key as " + describe(actual) + "; expected " + describe(expected));
        }
        if (expected.getAlgorithm() != actual.getAlgorithm()) {
            throw new IllegalArgumentException("Connector exported a key with algorithm " + actual.getAlgorithm()
                    + "; expected " + expected.getAlgorithm());
        }
        if (!(expected instanceof PublicKeyDataV2Dto) && !Objects.equals(expected.getLength(), actual.getLength())) {
            throw new IllegalArgumentException(
                    "Connector exported a key of length " + actual.getLength() + "; expected " + expected.getLength());
        }
        if (expected instanceof PublicKeyDataV2Dto expectedPublic && actual instanceof PublicKeyDataV2Dto actualPublic
                && !Arrays.equals(expectedPublic.getPublicKeySpki(), actualPublic.getPublicKeySpki())) {
            throw new IllegalArgumentException(
                    "Connector exported a public key that differs from the platform's record");
        }
    }

    private OperationValidationResult validateKeyTypeDeclarations(List<? extends TransferableKeyTypeV2Dto> response,
            String itemName) {
        OperationValidationResult elements = validateResponseElements(response, itemName);
        if (!elements.isValid()) {
            return elements;
        }
        return validate(() -> requireOneDeclarationPerKeyType(response));
    }

    private static void requireOneDeclarationPerKeyType(List<? extends TransferableKeyTypeV2Dto> declarations) {
        Set<KeyRequestType> seen = new HashSet<>();
        for (TransferableKeyTypeV2Dto declaration : declarations) {
            if (!seen.add(declaration.getKeyRequestType())) {
                throw new IllegalArgumentException(
                        "Connector declared key type " + declaration.getKeyRequestType().getCode() + " more than once");
            }
        }
    }

    private static void requireEchoedKeyReference(String requested, String echoed) {
        if (requested == null) {
            if (echoed != null) {
                throw new IllegalArgumentException("Connector returned a key reference for a key that carries none");
            }
            return;
        }
        if (echoed == null) {
            throw new IllegalArgumentException("Connector did not echo the requested key reference");
        }
        if (!requested.equals(echoed)) {
            throw new IllegalArgumentException("Connector echoed a different key reference than requested");
        }
    }

    /**
     * The descriptor must describe the type that was asked for: the public key of an exported key pair, or the
     * algorithm and length of an exported secret key.
     *
     * <p>
     * The check is on the deserialized type rather than on the {@code type} property, because a property can be
     * restated in a document while the object that was built cannot.
     * </p>
     */
    private static void requireDescriptorForKeyType(KeyRequestType requestedType, KeyDataV2Dto keyData) {
        boolean described = requestedType == KeyRequestType.KEY_PAIR
                ? keyData instanceof PublicKeyDataV2Dto
                : keyData instanceof SecretKeyDataV2Dto;
        if (described) {
            return;
        }
        String subject = requestedType == KeyRequestType.KEY_PAIR ? "key pair" : "secret key";
        String wanted = requestedType == KeyRequestType.KEY_PAIR ? "its public key" : KeyTypeV2.SECRET.getCode();
        throw new IllegalArgumentException(
                "Connector described an exported " + subject + " as " + describe(keyData) + "; expected " + wanted);
    }

    private static String describe(KeyDataV2Dto keyData) {
        if (keyData instanceof PublicKeyDataV2Dto) {
            return KeyTypeV2.PUBLIC.getCode();
        }
        if (keyData instanceof SecretKeyDataV2Dto) {
            return KeyTypeV2.SECRET.getCode();
        }
        return KeyTypeV2.PRIVATE.getCode();
    }
}
