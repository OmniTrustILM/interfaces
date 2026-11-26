package com.otilm.api.interfaces.core.web.v2;

import com.otilm.api.model.client.trustedcertificate.v2.TrustedCertificateBulkResultDto;
import com.otilm.api.model.client.trustedcertificate.v2.TrustedCertificateDto;
import com.otilm.api.model.client.trustedcertificate.v2.TrustedCertificatePlatformDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the trusted certificate routes Core implements and the Administrator UI calls, so a changed verb, path, return
 * type or parameter count fails this build instead of surfacing as a broken page.
 */
class TrustedCertificateControllerContractTest {

    private static final Set<String> BULK_METHODS = Set
            .of("addTrustedCertificates", "updateTrustedCertificates", "removeTrustedCertificates",
                    "applyTrustedCertificates");

    private static Method method(String name) {
        return Arrays
                .stream(TrustedCertificateController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TrustedCertificateController declares no " + name));
    }

    private static void assertRoute(String name, Class<? extends Annotation> mappingType, String[] path,
            Class<?> returnType, int parameterCount) {
        Method method = method(name);
        Annotation mapping = method.getAnnotation(mappingType);
        assertNotNull(mapping, name + " must carry " + mappingType.getSimpleName());
        assertArrayEquals(path, pathOf(mapping), name + " path");
        assertEquals(returnType, method.getReturnType(), name + " return type");
        assertEquals(parameterCount, method.getParameterCount(), name + " parameter count");
    }

    private static String[] pathOf(Annotation mapping) {
        return switch (mapping) {
            case GetMapping m -> m.path();
            case PostMapping m -> m.path();
            case PatchMapping m -> m.path();
            case DeleteMapping m -> m.path();
            default -> throw new AssertionError("Unexpected mapping " + mapping);
        };
    }

    private static Map<String, String> documentedStatuses(String name) {
        return Arrays
                .stream(method(name).getAnnotation(ApiResponses.class).value())
                .collect(Collectors.toMap(ApiResponse::responseCode, ApiResponse::description));
    }

    @Test
    void declaresTheSevenRoutes() {
        assertArrayEquals(new String[]{"/v2/trustedCertificates"},
                TrustedCertificateController.class.getAnnotation(RequestMapping.class).value());
        assertEquals(7, TrustedCertificateController.class.getDeclaredMethods().length);
        assertRoute("listTrustedCertificates", GetMapping.class, new String[]{}, List.class, 0);
        assertRoute("getTrustedCertificate", GetMapping.class, new String[]{"/{uuid}"}, TrustedCertificateDto.class, 1);
        assertRoute("addTrustedCertificates", PostMapping.class, new String[]{}, TrustedCertificateBulkResultDto.class,
                1);
        assertRoute("updateTrustedCertificates", PatchMapping.class, new String[]{},
                TrustedCertificateBulkResultDto.class, 1);
        assertRoute("removeTrustedCertificates", DeleteMapping.class, new String[]{},
                TrustedCertificateBulkResultDto.class, 1);
        assertRoute("applyTrustedCertificates", PostMapping.class, new String[]{"/apply"},
                TrustedCertificateBulkResultDto.class, 1);
        assertRoute("getPlatformTrust", GetMapping.class, new String[]{"/platform"},
                TrustedCertificatePlatformDto.class, 0);
    }

    @Test
    void hasNoSingleCreateOrDelete() {
        assertFalse(Arrays
                .stream(TrustedCertificateController.class.getDeclaredMethods())
                .anyMatch(m -> Set.of("createTrustedCertificate", "deleteTrustedCertificate").contains(m.getName())));
    }

    @Test
    void noRouteInheritsANotFoundResponseFromTheInterface() {
        assertNull(TrustedCertificateController.class.getDeclaredAnnotation(ApiResponses.class));
        assertTrue(documentedStatuses("getTrustedCertificate").containsKey("404"));
        assertFalse(documentedStatuses("getPlatformTrust").containsKey("404"));
    }

    @Test
    void bulkRoutesAnswerOkWithAResultAndUnprocessableContentForAMalformedRequest() {
        for (String name : BULK_METHODS) {
            Map<String, String> statuses = documentedStatuses(name);
            assertEquals(Set.of("200", "422"), statuses.keySet(), name);
            assertEquals("One result per distinct certificate; item failures never change the status code",
                    statuses.get("200"), name);
            assertEquals("Unprocessable Content", statuses.get("422"), name);
        }
    }

    @Test
    void validatedBulkRequestsAreCheckedBeforeTheService() {
        for (String name : BULK_METHODS) {
            var parameter = method(name).getParameters()[0];
            assertNotNull(parameter.getAnnotation(RequestBody.class), name);
            assertNotNull(parameter.getAnnotation(Valid.class), name);
        }
    }
}
