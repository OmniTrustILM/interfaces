package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.key.KeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyPairDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Validates that an async-capable connector response honors the caller-selected execution mode.
 */
public final class OperationResponseValidator {

    private OperationResponseValidator() {
    }

    public static void validateCreate(OperationExecutionMode mode, ResponseEntity<?> response) {
        requireResponse(mode, response, 200);
        if (mode == OperationExecutionMode.ASYNCHRONOUS) {
            requireTrackingMeta(operationMetadata(response.getBody()));
        }
    }

    public static void validateSign(OperationExecutionMode mode, ResponseEntity<SignDataResponseV2Dto> response) {
        requireResponse(mode, response, 200);
        SignDataResponseV2Dto body = requireBody(response);
        if (mode == OperationExecutionMode.SYNCHRONOUS && body.getSignatures() == null) {
            throw new IllegalArgumentException("Synchronous signing response must contain signatures");
        }
        if (mode == OperationExecutionMode.ASYNCHRONOUS) {
            requireTrackingMeta(body.getSignOperationMeta());
        }
    }

    public static void validateDestroy(OperationExecutionMode mode, ResponseEntity<KeyDataResponseV2Dto> response) {
        requireResponse(mode, response, 204);
        if (mode == OperationExecutionMode.ASYNCHRONOUS) {
            requireTrackingMeta(requireBody(response).getOperationMeta());
        }
    }

    private static void requireResponse(OperationExecutionMode mode, ResponseEntity<?> response,
                                        int synchronousStatus) {
        if (mode == null) {
            throw new IllegalArgumentException("Execution mode is required");
        }
        if (response == null) {
            throw new IllegalArgumentException("Connector returned no response");
        }
        int expected = mode == OperationExecutionMode.SYNCHRONOUS ? synchronousStatus : 202;
        if (response.getStatusCode().value() != expected) {
            throw new IllegalArgumentException("Connector returned HTTP " + response.getStatusCode().value()
                    + " for " + mode.getCode() + " execution; expected HTTP " + expected);
        }
    }

    private static <T> T requireBody(ResponseEntity<T> response) {
        if (response.getBody() == null) {
            throw new IllegalArgumentException("Connector response body is required");
        }
        return response.getBody();
    }

    private static List<MetadataAttributeV2> operationMetadata(Object body) {
        if (body instanceof SecretKeyDataResponseV2Dto secret)
            return secret.getOperationMeta();
        if (body instanceof KeyPairDataResponseV2Dto pair)
            return pair.getOperationMeta();
        throw new IllegalArgumentException("Unsupported key creation response");
    }

    private static void requireTrackingMeta(List<MetadataAttributeV2> metadata) {
        if (!isUsableMetadata(metadata)) {
            throw new IllegalArgumentException("Asynchronous response must contain operation tracking metadata");
        }
    }

    public static boolean isUsableMetadata(List<MetadataAttributeV2> metadata) {
        return metadata != null && !metadata.isEmpty() && metadata.stream().allMatch(entry -> entry != null
                && entry.getName() != null && !entry.getName().isBlank()
                && entry.getContentType() != null
                && entry.getProperties() != null
                && entry.getContent() != null && !entry.getContent().isEmpty()
                && entry.getContent().stream().allMatch(java.util.Objects::nonNull));
    }
}
