package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.certificate.BulkOperationResponse;
import com.otilm.api.model.core.certificate.BulkEarlyRenewalRequestDto;
import com.otilm.api.model.core.certificate.EarlyRenewalRequestDto;
import com.otilm.api.model.core.raprofile.RaProfileEarlyRenewalResponseDto;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EarlyRenewalContractTest {

    private static Method operation(Class<?> controller, String name) {
        return Arrays.stream(controller.getMethods()).filter(m -> m.getName().equals(name)).findFirst().orElseThrow();
    }

    private static void assertPostedTo(Method operation, String path) {
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(operation, RequestMapping.class);
        assertArrayEquals(new RequestMethod[]{RequestMethod.POST}, mapping.method());
        assertArrayEquals(new String[]{path}, mapping.path());
    }

    private static void assertValidatedBody(Method operation, Class<?> bodyType) {
        Parameter body = Arrays
                .stream(operation.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .orElseThrow();
        assertEquals(bodyType, body.getType());
        assertTrue(body.isAnnotationPresent(Valid.class));
    }

    @Test
    void raProfileEarlyRenewalIsPostedAndReturnsTheAffectedCount() {
        Method operation = operation(RAProfileManagementController.class, "requestRaProfileEarlyRenewal");

        assertPostedTo(operation, "/authorities/{authorityUuid}/raProfiles/{raProfileUuid}/earlyRenewal");
        assertValidatedBody(operation, EarlyRenewalRequestDto.class);
        assertEquals(RaProfileEarlyRenewalResponseDto.class, operation.getReturnType());
    }

    @Test
    void bulkEarlyRenewalIsPostedAndReturnsTheBulkResponse() {
        Method operation = operation(CertificateController.class, "bulkRequestEarlyRenewal");

        assertPostedTo(operation, "/earlyRenewal");
        assertValidatedBody(operation, BulkEarlyRenewalRequestDto.class);
        assertEquals(BulkOperationResponse.class, operation.getReturnType());
    }
}
