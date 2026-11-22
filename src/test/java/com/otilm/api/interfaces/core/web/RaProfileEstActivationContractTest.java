package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.raprofile.ActivateEstForRaProfileRequestDto;
import com.otilm.api.model.client.raprofile.RaProfileEstDetailResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaProfileEstActivationContractTest {

    private static final String EST_PATH = "/authorities/{authorityUuid}/raProfiles/{raProfileUuid}/protocols/est";

    @ParameterizedTest(name = "{0}")
    @CsvSource({
            "getEstForRaProfile, GET, ''",
            "activateEstForRaProfile, PATCH, /activate/{estProfileUuid}",
            "deactivateEstForRaProfile, PATCH, /deactivate"})
    void operationIsMappedUnderTheRaProfileProtocolPath(String operationId, RequestMethod method, String suffix) {
        RequestMapping mapping = AnnotatedElementUtils
                .findMergedAnnotation(methodByOperationId(operationId), RequestMapping.class);
        assertArrayEquals(new RequestMethod[]{method}, mapping.method());
        assertArrayEquals(new String[]{EST_PATH + suffix}, mapping.path());
    }

    @Test
    void activationTakesValidatedIssueAttributesAndReturnsTheDetail() {
        Method activate = methodByOperationId("activateEstForRaProfile");
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(activate, RequestMapping.class);
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes());
        assertEquals(RaProfileEstDetailResponseDto.class, activate.getReturnType());
        Parameter body = Arrays
                .stream(activate.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .orElseThrow();
        assertEquals(ActivateEstForRaProfileRequestDto.class, body.getType());
        assertTrue(body.isAnnotationPresent(Valid.class));
    }

    @Test
    void detailIsReturnedForGet() {
        assertEquals(RaProfileEstDetailResponseDto.class, methodByOperationId("getEstForRaProfile").getReturnType());
    }

    @Test
    void deactivationAnswersNoContent() {
        Method deactivate = methodByOperationId("deactivateEstForRaProfile");
        assertEquals(HttpStatus.NO_CONTENT, deactivate.getAnnotation(ResponseStatus.class).value());
        assertEquals(void.class, deactivate.getReturnType());
    }

    private static Method methodByOperationId(String operationId) {
        return Arrays
                .stream(RAProfileManagementController.class.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Operation.class)
                        && m.getAnnotation(Operation.class).operationId().equals(operationId))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No operation " + operationId));
    }
}
