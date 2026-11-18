package com.otilm.api.interfaces.core.web;

import com.otilm.api.interfaces.NoAuthController;
import com.otilm.api.model.core.auth.ProtectedResourceMetadataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.servers.Server;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the path of the metadata document: applications and the gateway route find it by this exact URL, and Core
 * permits it without authentication only because it is a {@link NoAuthController}.
 */
class ProtectedResourceMetadataControllerContractTest {

    @Test
    void theDocumentIsAPublicGetAtTheWellKnownPath() throws Exception {
        assertTrue(NoAuthController.class.isAssignableFrom(ProtectedResourceMetadataController.class));
        assertArrayEquals(new String[]{"/.well-known/oauth-protected-resource/api"},
                ProtectedResourceMetadataController.class.getAnnotation(RequestMapping.class).value());

        Method read = ProtectedResourceMetadataController.class.getMethod("getProtectedResourceMetadata");
        GetMapping mapping = read.getAnnotation(GetMapping.class);
        assertNotNull(mapping);
        assertEquals(0, mapping.path().length);
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.produces());
        assertEquals(ProtectedResourceMetadataDto.class, read.getReturnType());
    }

    @Test
    void theOperationIsDocumentedAtTheOriginRatherThanUnderTheApiBase() throws Exception {
        Method read = ProtectedResourceMetadataController.class.getMethod("getProtectedResourceMetadata");
        Server[] servers = read.getAnnotation(Operation.class).servers();

        assertEquals(1, servers.length);
        assertEquals("https://demo.otilm.com", servers[0].url());
    }
}
