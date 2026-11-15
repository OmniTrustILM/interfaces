package com.otilm.api.interfaces.core.est;

import io.swagger.v3.oas.annotations.Operation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class EstControllerContractTest {

    @Test
    void baseMappingIsTheWellKnownPath() {
        assertArrayEquals(new String[]{"/v1/protocols/.well-known/est"},
                EstController.class.getAnnotation(RequestMapping.class).value());
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "estCaCertificates, GET, cacerts",
            "estCsrAttributes, GET, csrattrs",
            "estSimpleEnroll, POST, simpleenroll",
            "estSimpleReenroll, POST, simplereenroll",
            "estServerKeyGeneration, POST, serverkeygen"})
    void operationMapsUnlabelledAndLabelledPaths(String operationId, RequestMethod method, String operation) {
        Method handler = methodByOperationId(operationId);
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(handler, RequestMapping.class);
        assertArrayEquals(new RequestMethod[]{method}, mapping.method());
        assertArrayEquals(new String[]{"/" + operation, "/{label}/" + operation}, mapping.path());

        Parameter label = Arrays
                .stream(handler.getParameters())
                .filter(p -> p.isAnnotationPresent(PathVariable.class))
                .findFirst()
                .orElseThrow(() -> new AssertionError(operationId + " has no label"));
        assertEquals(String.class, label.getType());
        assertFalse(label.getAnnotation(PathVariable.class).required(), "the label is optional");
    }

    @ParameterizedTest
    @ValueSource(strings = {"estSimpleEnroll", "estSimpleReenroll", "estServerKeyGeneration"})
    void enrollmentOperationsConsumeRawPkcs10Bodies(String operationId) {
        Method handler = methodByOperationId(operationId);
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(handler, RequestMapping.class);
        assertArrayEquals(new String[]{"application/pkcs10"}, mapping.consumes());
        Parameter body = Arrays
                .stream(handler.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .orElseThrow(() -> new AssertionError(operationId + " has no body"));
        assertEquals(byte[].class, body.getType());
    }

    @Test
    void noOperationNegotiatesItsResponseType() {
        for (Method handler : EstController.class.getDeclaredMethods()) {
            RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(handler, RequestMapping.class);
            assertEquals(0, mapping.produces().length, handler.getName() + " must leave Content-Type to Core");
        }
    }

    @Test
    void everyOperationReturnsRawBytes() {
        for (Method handler : EstController.class.getDeclaredMethods()) {
            ParameterizedType type = (ParameterizedType) handler.getGenericReturnType();
            assertEquals(ResponseEntity.class, type.getRawType());
            assertEquals(byte[].class, type.getActualTypeArguments()[0]);
        }
    }

    @Test
    void operationIdsAreUnique() {
        List<String> ids = Arrays
                .stream(EstController.class.getDeclaredMethods())
                .map(m -> m.getAnnotation(Operation.class).operationId())
                .toList();
        assertEquals(5, ids.size());
        assertEquals(ids.size(), ids.stream().distinct().count());
    }

    private static Method methodByOperationId(String operationId) {
        return Arrays
                .stream(EstController.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Operation.class)
                        && m.getAnnotation(Operation.class).operationId().equals(operationId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No operation " + operationId));
    }
}
