package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.core.settings.authentication.OAuth2ProviderSettingsResponseDto;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pins the optimistic concurrency contract of a single OAuth2 provider's read and update: the read carries an
 * {@code ETag}, and the update takes an optional {@code If-Match} answered with 412 on a mismatch.
 */
class SettingControllerOAuth2ProviderContractTest {

    @Test
    void returnsResponseEntity_whenReadingAProvider() throws Exception {
        // given
        Method read = SettingController.class.getMethod("getOAuth2ProviderSettings", String.class);

        // when
        ParameterizedType returnType = (ParameterizedType) read.getGenericReturnType();

        // then
        assertEquals(ResponseEntity.class, returnType.getRawType());
        assertArrayEquals(new Object[]{OAuth2ProviderSettingsResponseDto.class}, returnType.getActualTypeArguments());
    }

    @Test
    void documentsETag_whenReadingAProvider() throws Exception {
        // given
        Method read = SettingController.class.getMethod("getOAuth2ProviderSettings", String.class);

        // when
        ApiResponse ok = documentedResponse(read, "200");

        // then
        assertArrayEquals(new String[]{HttpHeaders.ETAG},
                Arrays.stream(ok.headers()).map(Header::name).toArray(String[]::new));
    }

    @Test
    void acceptsOptionalIfMatch_whenUpdatingAProvider() {
        // given
        Method update = updateMethod();

        // when
        RequestHeader ifMatch = Arrays
                .stream(update.getParameters())
                .map(parameter -> parameter.getAnnotation(RequestHeader.class))
                .filter(header -> header != null && HttpHeaders.IF_MATCH.equals(header.name()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("update declares no If-Match header"));

        // then
        assertFalse(ifMatch.required(), "an update without If-Match must keep working for older clients");
    }

    @Test
    void documentsPreconditionFailed_whenUpdatingAProvider() {
        // given
        Method update = updateMethod();

        // when
        ApiResponse preconditionFailed = documentedResponse(update, "412");

        // then
        assertNotNull(preconditionFailed.content()[0].schema().implementation());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Method updateMethod() {
        return Arrays
                .stream(SettingController.class.getDeclaredMethods())
                .filter(method -> method.getName().equals("updateOAuth2ProviderSettings"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("SettingController declares no updateOAuth2ProviderSettings"));
    }

    private static ApiResponse documentedResponse(Method method, String responseCode) {
        return Arrays
                .stream(method.getAnnotation(ApiResponses.class).value())
                .filter(response -> response.responseCode().equals(responseCode))
                .findFirst()
                .orElseThrow(() -> new AssertionError(method.getName() + " documents no " + responseCode));
    }
}
