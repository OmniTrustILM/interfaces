package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.trustedcertificate.TrustedCertificateDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds the release 2.20.0 trusted certificate API in place while it is deprecated, so clients written against it keep
 * the same routes, request and item.
 */
class TrustedCertificateControllerContractTest {

    private static Method method(String name) {
        return Arrays
                .stream(TrustedCertificateController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TrustedCertificateController declares no " + name));
    }

    private static void assertRoute(String name, Class<? extends Annotation> mappingType, String[] path,
            Class<?> returnType) {
        Method method = method(name);
        Annotation mapping = method.getAnnotation(mappingType);
        assertNotNull(mapping, name + " must carry " + mappingType.getSimpleName());
        String[] actualPath = switch (mapping) {
            case GetMapping m -> m.path();
            case PostMapping m -> m.path();
            case DeleteMapping m -> m.path();
            default -> throw new AssertionError("Unexpected mapping " + mapping);
        };
        assertArrayEquals(path, actualPath, name + " path");
        assertEquals(returnType, method.getReturnType(), name + " return type");
    }

    @Test
    void keepsTheFourRoutesOfRelease2200() {
        assertArrayEquals(new String[]{"/v1/trustedCertificates"},
                TrustedCertificateController.class.getAnnotation(RequestMapping.class).value());
        assertEquals(4, TrustedCertificateController.class.getDeclaredMethods().length);
        assertRoute("listTrustedCertificates", GetMapping.class, new String[]{}, List.class);
        assertRoute("getTrustedCertificate", GetMapping.class, new String[]{"/{uuid}"}, TrustedCertificateDto.class);
        assertRoute("createTrustedCertificate", PostMapping.class, new String[]{}, ResponseEntity.class);
        assertRoute("deleteTrustedCertificate", DeleteMapping.class, new String[]{"/{uuid}"}, void.class);
        assertArrayEquals(new Class<?>[]{TrustedCertificateRequestDto.class},
                method("createTrustedCertificate").getParameterTypes());
    }

    @Test
    void everyRouteIsDeprecatedForRemovalAfterRelease2210() {
        for (Method method : TrustedCertificateController.class.getDeclaredMethods()) {
            assertTrue(method.getAnnotation(Operation.class).deprecated(), method.getName());
            Deprecated deprecated = method.getAnnotation(Deprecated.class);
            assertNotNull(deprecated, method.getName());
            assertTrue(deprecated.forRemoval(), method.getName());
            assertEquals("2.21.0", deprecated.since(), method.getName());
        }
    }

    @Test
    void documentsTheAnswersCoreAddsFromRelease2210() {
        assertTrue(statuses(TrustedCertificateController.class.getAnnotation(ApiResponses.class)).contains("503"));
        Method create = method("createTrustedCertificate");
        assertTrue(statuses(create.getAnnotation(ApiResponses.class)).contains("409"));
        assertTrue(create.getAnnotation(Operation.class).description().contains("permission to create certificates"));
    }

    @Test
    void theItemKeepsTheFieldsOfRelease2200() {
        Set<String> fields = Arrays
                .stream(TrustedCertificateDto.class.getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(Field::getName)
                .collect(Collectors.toSet());
        assertEquals(Set
                .of("uuid", "certificateContent", "issuer", "san", "serialNumber", "subject", "thumbprint", "notBefore",
                        "notAfter"),
                fields);
    }

    private static Set<String> statuses(ApiResponses responses) {
        return Arrays.stream(responses.value()).map(ApiResponse::responseCode).collect(Collectors.toSet());
    }
}
