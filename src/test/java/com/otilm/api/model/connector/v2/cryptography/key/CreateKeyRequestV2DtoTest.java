package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CreateKeyRequestV2DtoTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void attributeListsAreRequiredButMayBeEmpty() {
        assertEquals(3, VALIDATOR.validate(new TokenProfileScopedRequestV2Dto()).size());
        assertEquals(6, VALIDATOR.validate(new CreateKeyRequestV2Dto()).size());

        TokenProfileScopedRequestV2Dto attributeRequest = new TokenProfileScopedRequestV2Dto();
        attributeRequest.setTokenAttributes(List.of());
        attributeRequest.setTokenProfileAttributes(List.of());
        attributeRequest.setKeyUsages(Set.of(KeyUsage.SIGN));
        assertEquals(0, VALIDATOR.validate(attributeRequest).size());

        CreateKeyRequestV2Dto request = new CreateKeyRequestV2Dto();
        request.setKeyCreationId("6c87b73a-5659-4d17-b625-abc72cd94150");
        request.setTokenAttributes(List.of());
        request.setTokenProfileAttributes(List.of());
        request.setCreateKeyAttributes(List.of());
        request.setKeyUsages(Set.of(KeyUsage.SIGN));
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);

        assertEquals(0, VALIDATOR.validate(request).size());
    }

    @Test
    void keyCreationIdIsRequiredAndLimitedTo256Characters() {
        CreateKeyRequestV2Dto request = validRequest();

        request.setKeyCreationId(null);
        assertTrue(VALIDATOR.validate(request).stream()
                .anyMatch(violation -> violation.getMessage().equals("keyCreationId is required")));

        request.setKeyCreationId(" ");
        assertTrue(VALIDATOR.validate(request).stream()
                .anyMatch(violation -> violation.getMessage().equals("keyCreationId is required")));

        request.setKeyCreationId("a".repeat(257));
        assertTrue(VALIDATOR.validate(request).stream()
                .anyMatch(violation -> violation.getMessage().equals(
                        "keyCreationId must not exceed 256 characters")));

        request.setKeyCreationId("a".repeat(256));
        assertEquals(0, VALIDATOR.validate(request).size());
    }

    @Test
    void keyCreationIdIsNotIncludedInGeneratedLogRepresentation() {
        CreateKeyRequestV2Dto request = validRequest();

        assertFalse(request.toString().contains(request.getKeyCreationId()));
    }

    private static CreateKeyRequestV2Dto validRequest() {
        CreateKeyRequestV2Dto request = new CreateKeyRequestV2Dto();
        request.setTokenAttributes(List.of());
        request.setTokenProfileAttributes(List.of());
        request.setCreateKeyAttributes(List.of());
        request.setKeyCreationId("operation-1");
        request.setKeyUsages(Set.of(KeyUsage.SIGN));
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        return request;
    }
}
