package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the public OpenAPI surface of {@link NativeSigningController}: the route prefix and tag that every native
 * operation inherits, and the class-level not-found response.
 */
class NativeSigningControllerDocTest {

    @Test
    void controllerExtendsTheAuthProtectedBase() {
        List<String> supers = Arrays.stream(NativeSigningController.class.getInterfaces()).map(Class::getName).toList();
        assertTrue(supers.contains("com.otilm.api.interfaces.AuthProtectedController"),
                "NativeSigningController must extend AuthProtectedController; found " + supers);
    }

    @Test
    void operationsShareTheSigningRoutePrefix() {
        RequestMapping mapping = NativeSigningController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "missing @RequestMapping on NativeSigningController");
        assertArrayEquals(new String[]{"/v1/signing"}, mapping.value());
    }

    @Test
    void tagDescriptionIsDocumentedAndJargonFree() {
        Tag tag = NativeSigningController.class.getAnnotation(Tag.class);
        assertNotNull(tag, "missing @Tag on NativeSigningController");
        assertEquals("Native Signing", tag.name());
        assertFalse(tag.description().isBlank(), "blank @Tag description");
        assertNoJargon("@Tag", tag.description());
    }

    @Test
    void classLevelNotFoundDeclaresErrorMessageContent() {
        ApiResponses responses = NativeSigningController.class.getAnnotation(ApiResponses.class);
        assertNotNull(responses, "missing class-level @ApiResponses");
        ApiResponse notFound = Arrays
                .stream(responses.value())
                .filter(r -> r.responseCode().equals("404"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no class-level 404 response"));
        assertTrue(
                Arrays.stream(notFound.content()).anyMatch(c -> c.schema().implementation() == ErrorMessageDto.class),
                "class-level 404 must declare ErrorMessageDto");
    }
}
