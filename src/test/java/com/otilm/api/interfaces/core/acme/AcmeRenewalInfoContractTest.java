package com.otilm.api.interfaces.core.acme;

import com.otilm.api.model.core.acme.RenewalInfo;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcmeRenewalInfoContractTest {

    static Stream<Object[]> controllers() {
        return Stream
                .of(new Object[]{AcmeController.class, "acmeProfileName", "/v1/protocols/acme/{acmeProfileName}"},
                        new Object[]{
                                AcmeRaProfileController.class,
                                "raProfileName",
                                "/v1/protocols/acme/raProfile/{raProfileName}"});
    }

    private static Method operation(Class<?> controller) {
        return Arrays
                .stream(controller.getMethods())
                .filter(m -> m.getName().equals("getRenewalInfo"))
                .findFirst()
                .orElseThrow();
    }

    @ParameterizedTest
    @MethodSource("controllers")
    void isServedAsAGetUnderRenewalInfo(Class<?> controller, String profileVariable, String basePath) {
        Method operation = operation(controller);
        GetMapping mapping = operation.getAnnotation(GetMapping.class);

        assertArrayEquals(new String[]{"/renewal-info/{certId}"}, mapping.path());
        assertEquals(basePath, AnnotatedElementUtils.findMergedAnnotation(controller, RequestMapping.class).value()[0]);
    }

    @ParameterizedTest
    @MethodSource("controllers")
    void takesTheProfileAndCertIdPathVariables(Class<?> controller, String profileVariable, String basePath) {
        Method operation = operation(controller);

        assertEquals(2, operation.getParameterCount());
        assertEquals(profileVariable, operation.getParameters()[0].getName());
        assertEquals("certId", operation.getParameters()[1].getName());
        assertEquals(String.class, operation.getParameterTypes()[0]);
        assertEquals(String.class, operation.getParameterTypes()[1]);
        for (var parameter : operation.getParameters()) {
            assertTrue(parameter.isAnnotationPresent(PathVariable.class));
        }
    }

    @ParameterizedTest
    @MethodSource("controllers")
    void returnsRenewalInfoAndDocumentsTheStatuses(Class<?> controller, String profileVariable, String basePath) {
        Method operation = operation(controller);
        ParameterizedType returnType = (ParameterizedType) operation.getGenericReturnType();

        assertEquals(ResponseEntity.class, returnType.getRawType());
        assertEquals(RenewalInfo.class, returnType.getActualTypeArguments()[0]);

        ApiResponses responses = operation.getAnnotation(ApiResponses.class);
        assertArrayEquals(new String[]{"200", "400", "404"},
                Arrays.stream(responses.value()).map(ApiResponse::responseCode).sorted().toArray(String[]::new));
    }
}
