package com.otilm.api.interfaces.connector.common.v2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the public OpenAPI surface of {@link AttributesController}: every operation is documented,
 * no internal design jargon leaks into the published prose, and the controller extends the NG
 * (common.v2) auth base rather than the legacy one.
 */
class AttributesControllerDocTest {

    private static final List<String> BANNED_JARGON = List.of(
            "rung", "dispatch ladder", "expander", "scope chain", "fail closed", "fail-closed",
            "footgun", "s-1", "dependson", "ladder", "envelope assembly");

    @Test
    void everyOperationIsDocumented() {
        Method[] methods = AttributesController.class.getDeclaredMethods();
        assertTrue(methods.length >= 3, "expected at least 3 endpoints, found " + methods.length);

        for (Method m : methods) {
            Operation op = m.getAnnotation(Operation.class);
            assertNotNull(op, "missing @Operation on " + m.getName());
            assertFalse(op.summary().isBlank(), "blank @Operation summary on " + m.getName());
            assertFalse(op.description().isBlank(), "blank @Operation description on " + m.getName());

            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            boolean hasSuccess = Arrays.stream(responses.value())
                    .anyMatch(r -> r.responseCode().startsWith("2"));
            assertTrue(hasSuccess, "no 2xx response documented on " + m.getName());
            for (ApiResponse r : responses.value()) {
                assertFalse(r.description().isBlank(), "blank response description on " + m.getName());
            }

            assertNoJargon(m.getName(), op.summary());
            assertNoJargon(m.getName(), op.description());
        }
    }

    @Test
    void controllerExtendsCommonV2AuthBase() {
        List<String> supers = Arrays.stream(AttributesController.class.getInterfaces())
                .map(Class::getName).toList();
        assertTrue(supers.contains(
                        "com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController"),
                "AttributesController must extend the common.v2 auth base, not the legacy one; found " + supers);
    }

    private static void assertNoJargon(String method, String text) {
        String lower = text.toLowerCase(Locale.ROOT);
        for (String banned : BANNED_JARGON) {
            assertFalse(lower.contains(banned),
                    "internal jargon \"" + banned + "\" leaked into OpenAPI prose on " + method);
        }
    }
}
