package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pins the token-profile discovery routes against their annotation values because both the controller base path and
 * method path determine their published URLs.
 */
class TokenProfileControllerContractTest {

    private static final String BASE_PATH = "/v1";

    private static final String TOKEN_PROFILE_ATTRIBUTES_PATH = "/tokens/{tokenInstanceUuid}/tokenProfiles/attributes";

    private static final String TOKEN_PROFILE_KEY_USAGES_PATH = "/tokens/{tokenInstanceUuid}/tokenProfiles/keyUsages";

    private static final String TOKEN_PROFILE_KEY_REQUEST_TYPES_PATH = "/tokens/{tokenInstanceUuid}/tokenProfiles/{tokenProfileUuid}/keys/types";

    private static final String TOKEN_INSTANCE_UUID = "tokenInstanceUuid";

    private static final String TOKEN_PROFILE_UUID = "tokenProfileUuid";

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
        assertPathVariables(attributes, TOKEN_INSTANCE_UUID);
    }

    @Test
    void supportedKeyUsagesArePublishedOnTheGetRoute() {
        // given
        Method keyUsages = method("listSupportedTokenProfileKeyUsages");

        // when
        RequestMapping controllerMapping = TokenProfileController.class.getAnnotation(RequestMapping.class);
        GetMapping methodMapping = keyUsages.getAnnotation(GetMapping.class);

        // then
        assertControllerBasePath(controllerMapping);
        assertNotNull(methodMapping, "token profile key-usage discovery must be a GET");
        assertArrayEquals(new String[]{TOKEN_PROFILE_KEY_USAGES_PATH}, methodMapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, methodMapping.produces());
        assertPathVariables(keyUsages, TOKEN_INSTANCE_UUID);
    }

    @Test
    void supportedKeyRequestTypesArePublishedOnTheProfileScopedGetRoute() {
        // given
        Method keyRequestTypes = method("listSupportedKeyRequestTypes");

        // when
        RequestMapping controllerMapping = TokenProfileController.class.getAnnotation(RequestMapping.class);
        GetMapping methodMapping = keyRequestTypes.getAnnotation(GetMapping.class);

        // then
        assertControllerBasePath(controllerMapping);
        assertNotNull(methodMapping, "key request type discovery must be a GET");
        assertArrayEquals(new String[]{TOKEN_PROFILE_KEY_REQUEST_TYPES_PATH}, methodMapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, methodMapping.produces());
        assertPathVariables(keyRequestTypes, TOKEN_INSTANCE_UUID, TOKEN_PROFILE_UUID);
        ParameterizedType returnType = assertInstanceOf(ParameterizedType.class,
                keyRequestTypes.getGenericReturnType());
        assertEquals(List.class, returnType.getRawType());
        assertArrayEquals(new Class<?>[]{KeyRequestType.class}, returnType.getActualTypeArguments());
    }

    private static void assertControllerBasePath(RequestMapping mapping) {
        assertNotNull(mapping, "missing @RequestMapping on TokenProfileController");
        assertArrayEquals(new String[]{BASE_PATH}, mapping.value());
    }

    private static void assertPathVariables(Method method, String... expectedNames) {
        assertEquals(expectedNames.length, method.getParameterCount(),
                "the route must have exactly its path parameters");
        Parameter[] parameters = method.getParameters();
        for (int index = 0; index < expectedNames.length; index++) {
            assertPathVariable(parameters[index], expectedNames[index]);
        }
    }

    private static void assertPathVariable(Parameter parameter, String expectedName) {
        assertEquals(String.class, parameter.getType());
        PathVariable pathVariable = parameter.getAnnotation(PathVariable.class);
        assertNotNull(pathVariable, expectedName + " must be a path variable");

        String boundName = !pathVariable.value().isEmpty()
                ? pathVariable.value()
                : !pathVariable.name().isEmpty() ? pathVariable.name() : parameter.getName();
        assertEquals(expectedName, boundName, "the path variable must match the route placeholder");
    }

    private static Method method(String name) {
        return Arrays
                .stream(TokenProfileController.class.getDeclaredMethods())
                .filter(candidate -> candidate.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("TokenProfileController declares no method named " + name));
    }
}
