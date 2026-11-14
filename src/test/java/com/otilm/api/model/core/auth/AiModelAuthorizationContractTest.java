package com.otilm.api.model.core.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.core.model.auth.ResourceAction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiModelAuthorizationContractTest {
    @Test
    void modelsHaveIndependentObjectAccessWithoutSecretOrOwnerTraits() throws Exception {
        var resource = assertDoesNotThrow(() -> Resource.findByCode("aiModels"));
        assertEquals("AI_MODEL", resource.name());
        assertTrue(resource.hasObjectAccess());
        assertFalse(resource.hasCustomAttributes());
        assertFalse(resource.hasGroups());
        assertFalse(resource.hasOwner());
        assertEquals("\"aiModels\"", new ObjectMapper().writeValueAsString(resource));
    }

    @Test
    void inferenceRequiresExplicitWriteClassPermission() throws Exception {
        var action = assertDoesNotThrow(() -> ResourceAction.findByCode("use"));
        assertEquals(ResourceAction.AccessType.WRITE, action.getAccessType());
        assertFalse(action.isGrantableToReadOnlyRole());
        assertNotEquals(ResourceAction.DETAIL, action);
        assertNotEquals(ResourceAction.CONNECT, action);
        assertEquals("\"use\"", new ObjectMapper().writeValueAsString(action));
    }

    @Test
    void destinationApprovalIsSeparateFromConnectionEditingAndChecks() throws Exception {
        var action = assertDoesNotThrow(() -> ResourceAction.findByCode("approveDestination"));
        assertEquals(ResourceAction.AccessType.WRITE, action.getAccessType());
        assertFalse(action.isGrantableToReadOnlyRole());
        assertNotEquals(ResourceAction.APPROVE, action);
        assertNotEquals(ResourceAction.UPDATE, action);
        assertNotEquals(ResourceAction.CONNECT, action);
        assertEquals("\"approveDestination\"", new ObjectMapper().writeValueAsString(action));
    }
}
