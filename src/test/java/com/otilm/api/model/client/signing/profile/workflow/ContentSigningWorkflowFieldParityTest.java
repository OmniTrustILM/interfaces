package com.otilm.api.model.client.signing.profile.workflow;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContentSigningWorkflowFieldParityTest {

    @Test
    void theAllowListIsDeclaredAsTheSameCollectionOnBothSurfaces() {
        assertGenericTypeMatches("allowedRequestParameterGroups");
    }

    @Test
    void theTypedDefaultsAreDeclaredAsTheSameUnionOnBothSurfaces() {
        assertGenericTypeMatches("defaultSignatureParameters");
    }

    private static void assertGenericTypeMatches(String fieldName) {
        Type request = declaredField(ContentSigningWorkflowRequestDto.class, fieldName).getGenericType();
        Type response = declaredField(ContentSigningWorkflowDto.class, fieldName).getGenericType();

        assertEquals(request.getTypeName(), response.getTypeName(),
                "declared type mismatch on paired field '" + fieldName + "'");
    }

    private static Field declaredField(Class<?> type, String fieldName) {
        try {
            return type.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new AssertionError(type.getSimpleName() + " does not declare '" + fieldName + "'", e);
        }
    }
}
