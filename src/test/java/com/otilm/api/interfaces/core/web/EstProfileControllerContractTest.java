package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.est.EstProfileEditRequestDto;
import com.otilm.api.model.client.est.EstProfileRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstProfileControllerContractTest {

    @Test
    void baseMappingIsTheManagementPath() {
        assertArrayEquals(new String[]{"/v1/estProfiles"},
                EstProfileController.class.getAnnotation(RequestMapping.class).value());
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "listEstProfiles, GET, ''",
            "getEstProfile, GET, /{uuid}",
            "createEstProfile, POST, ''",
            "updateEstProfile, PUT, /{uuid}",
            "deleteEstProfile, DELETE, /{uuid}",
            "bulkDeleteEstProfiles, DELETE, /delete",
            "forceDeleteEstProfiles, DELETE, /delete/force",
            "enableEstProfile, PATCH, /{uuid}/enable",
            "bulkEnableEstProfiles, PATCH, /enable",
            "disableEstProfile, PATCH, /{uuid}/disable",
            "bulkDisableEstProfiles, PATCH, /disable",
            "updateEstProfileRaProfile, PATCH, /{uuid}/raProfiles/{raProfileUuid}"})
    void operationIsMappedAsDocumented(String operationId, RequestMethod method, String path) {
        RequestMapping mapping = AnnotatedElementUtils
                .findMergedAnnotation(methodByOperationId(operationId), RequestMapping.class);
        assertNotNull(mapping, operationId + " must be mapped");
        assertArrayEquals(new RequestMethod[]{method}, mapping.method());
        assertArrayEquals(path.isEmpty() ? new String[0] : new String[]{path}, mapping.path());
    }

    @Test
    void everyOperationHasAUniqueOperationId() {
        List<String> ids = Arrays
                .stream(EstProfileController.class.getDeclaredMethods())
                .map(m -> m.getAnnotation(Operation.class).operationId())
                .toList();
        assertEquals(12, ids.size());
        assertEquals(ids.size(), ids.stream().distinct().count());
    }

    @Test
    void createAnswersCreatedAndValidatesTheBody() {
        Method create = methodByOperationId("createEstProfile");
        assertEquals(HttpStatus.CREATED, create.getAnnotation(ResponseStatus.class).value());
        assertValidatedBody(create, EstProfileRequestDto.class);
    }

    @Test
    void updateValidatesTheBodyWithoutAName() {
        assertValidatedBody(methodByOperationId("updateEstProfile"), EstProfileEditRequestDto.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "bulkDeleteEstProfiles",
            "forceDeleteEstProfiles",
            "bulkEnableEstProfiles",
            "bulkDisableEstProfiles"})
    void bulkOperationsTakeUuidLists(String operationId) {
        Parameter body = bodyParameter(methodByOperationId(operationId));
        ParameterizedType type = (ParameterizedType) body.getParameterizedType();
        assertEquals(List.class, type.getRawType());
        assertEquals(UUID.class, type.getActualTypeArguments()[0]);
    }

    private static void assertValidatedBody(Method method, Class<?> bodyType) {
        Parameter body = bodyParameter(method);
        assertEquals(bodyType, body.getType());
        assertTrue(body.isAnnotationPresent(Valid.class), method.getName() + " must validate its body");
    }

    private static Parameter bodyParameter(Method method) {
        return Arrays
                .stream(method.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .orElseThrow(() -> new AssertionError(method.getName() + " has no request body"));
    }

    private static Method methodByOperationId(String operationId) {
        return Arrays
                .stream(EstProfileController.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Operation.class)
                        && m.getAnnotation(Operation.class).operationId().equals(operationId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No operation " + operationId));
    }
}
