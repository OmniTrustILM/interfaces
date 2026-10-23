package com.otilm.api.model.common.attribute.v3.content.data;

import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResourceObjectContentDataTest {

    @ParameterizedTest
    @ValueSource(strings = {"6f9b2a64-0c1d-4e5f-8a7b-9c0d1e2f3a4b", "6F9B2A64-0C1D-4E5F-8A7B-9C0D1E2F3A4B"})
    void aCanonicalUuidIsValid(String uuid) {
        ResourceSecretContentData data = referenceTo(uuid);

        assertDoesNotThrow(data::validate);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678-1234-4234-8234-1", "1-2-3-4-5", "not-a-uuid"})
    void aUuidThatIsNotCanonicalIsRefused(String uuid) {
        ResourceSecretContentData data = referenceTo(uuid);

        ValidationException refusal = assertThrows(ValidationException.class, data::validate);

        assertEquals("UUID in Resource Object Content Data must be a canonical UUID", refusal.getMessage());
    }

    @Test
    void aMissingUuidIsRefused() {
        ResourceSecretContentData data = referenceTo(null);

        ValidationException refusal = assertThrows(ValidationException.class, data::validate);

        assertEquals("UUID must be provided in Resource Object Content Data", refusal.getMessage());
    }

    private static ResourceSecretContentData referenceTo(String uuid) {
        ResourceSecretContentData data = new ResourceSecretContentData();
        data.setUuid(uuid);
        return data;
    }
}
