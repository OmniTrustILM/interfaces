package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.core.raprofile.RaProfileDto;
import com.otilm.api.model.core.raprofile.RaProfileRenewalSettingsUpdateDto;
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

class RaProfileRenewalContractTest {

    private static Method renewalOperation() {
        return Arrays
                .stream(RAProfileManagementController.class.getMethods())
                .filter(m -> m.getName().equals("updateRaProfileRenewalSettings"))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void isPatchedUnderTheRaProfileRenewalPath() {
        RequestMapping mapping = AnnotatedElementUtils.findMergedAnnotation(renewalOperation(), RequestMapping.class);

        assertArrayEquals(new RequestMethod[]{RequestMethod.PATCH}, mapping.method());
        assertArrayEquals(new String[]{"/authorities/{authorityUuid}/raProfiles/{raProfileUuid}/renewal"},
                mapping.path());
    }

    @Test
    void takesAValidatedBodyAndReturnsTheRaProfile() {
        Method operation = renewalOperation();
        Parameter body = Arrays
                .stream(operation.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .orElseThrow();

        assertEquals(RaProfileDto.class, operation.getReturnType());
        assertEquals(RaProfileRenewalSettingsUpdateDto.class, body.getType());
        assertTrue(body.isAnnotationPresent(Valid.class));
    }
}
