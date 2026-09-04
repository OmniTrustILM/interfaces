package com.otilm.api.interfaces.core.web;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pins the token-profile discovery routes against their annotation values. One route moved from a controller whose base
 * path included {@code /tokens}, while the other was added for token-profile key-usage discovery, so both the
 * controller base path and method path participate in their published URLs.
 */
class TokenProfileControllerContractTest {

    private static final String BASE_PATH = "/v1";

    private static final String TOKEN_PROFILE_ATTRIBUTES_PATH = "/tokens/{tokenInstanceUuid}/tokenProfiles/attributes";

    private static final String TOKEN_PROFILE_KEY_USAGES_PATH = "/tokens/{tokenInstanceUuid}/tokenProfile/keyUsages";

    private static final String TOKEN_INSTANCE_UUID = "tokenInstanceUuid";

    @Test
    void tokenProfileAttributesRemainOnTheExistingGetRoute() {
        // given
        Method attributes = method("listTokenProfileAttributes");

        // when
        RequestMapping controllerMapping = TokenProfileController.class.getAnnotation(RequestMapping.class);
        GetMapping methodMapping = attributes.getAnnotation(GetMapping.class);

        // then
        assertControllerBasePath(controllerMapping);
        assertNotNull(methodMapping, "token profile attribute discovery must be a GET");
        assertArrayEquals(new String[]{TOKEN_PROFILE_ATTRIBUTES_PATH}, methodMapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, methodMapping.produces());
        assertTokenInstancePathVariable(attributes);
    }

    @Test
    void supportedKeyUsagesArePublishedOnTheNewPostRoute() {
        // given
        Method keyUsages = method("listSupportedTokenProfileKeyUsages");

        // when
        RequestMapping controllerMapping = TokenProfileController.class.getAnnotation(RequestMapping.class);
        PostMapping methodMapping = keyUsages.getAnnotation(PostMapping.class);

        // then
        assertControllerBasePath(controllerMapping);
        assertNotNull(methodMapping, "token profile key-usage discovery must be a POST");
        assertArrayEquals(new String[]{TOKEN_PROFILE_KEY_USAGES_PATH}, methodMapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, methodMapping.produces());
        assertTokenInstancePathVariable(keyUsages);
    }

    private static void assertControllerBasePath(RequestMapping mapping) {
        assertNotNull(mapping, "missing @RequestMapping on TokenProfileController");
        assertArrayEquals(new String[]{BASE_PATH}, mapping.value());
    }

    private static void assertTokenInstancePathVariable(Method method) {
        assertEquals(1, method.getParameterCount(), "the token instance must be the route's only parameter");
        Parameter parameter = method.getParameters()[0];
        assertEquals(String.class, parameter.getType());
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
        assertNotNull(pathVariable, "tokenInstanceUuid must be a path variable");

        String boundName = !pathVariable.value().isEmpty()
                ? pathVariable.value()
                : !pathVariable.name().isEmpty() ? pathVariable.name() : parameter.getName();
        assertEquals(TOKEN_INSTANCE_UUID, boundName, "the path variable must match the route placeholder");
    }

    private static Method method(String name) {
        return Arrays
                .stream(TokenProfileController.class.getDeclaredMethods())
                .filter(candidate -> candidate.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TokenProfileController declares no method named " + name));
    }
}
