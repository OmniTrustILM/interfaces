package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.client.signing.nativesigning.SignDocumentRequestDto;
import com.otilm.api.model.client.signing.nativesigning.SignRawRequestDto;
import com.otilm.api.model.common.ErrorMessageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the public OpenAPI surface of {@link NativeSigningController}: the route prefix and tag that every native
 * operation inherits, and the class-level error responses.
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
    void classLevelErrorsDeclareErrorMessageContent() {
        ApiResponses responses = NativeSigningController.class.getAnnotation(ApiResponses.class);
        assertNotNull(responses, "missing class-level @ApiResponses");
        assertEquals(List.of("502", "503"), Arrays.stream(responses.value()).map(ApiResponse::responseCode).toList());
        for (ApiResponse response : responses.value()) {
            assertTrue(
                    Arrays
                            .stream(response.content())
                            .anyMatch(c -> c.schema().implementation() == ErrorMessageDto.class),
                    "class-level " + response.responseCode() + " must declare ErrorMessageDto");
        }
    }

    @Test
    void unprocessableAnswersAnArrayOfMessages() throws NoSuchMethodException {
        for (Method operation : List.of(signDocumentMethod(), signRawMethod())) {
            assertEquals(String.class, unprocessable(operation).content()[0].array().schema().implementation(),
                    operation.getName() + ": Core renders a refusal as an array of messages");
        }
    }

    @Test
    void signDocumentIsPostedToTheSigningProfilesDocumentsPath() throws NoSuchMethodException {
        Method signDocument = signDocumentMethod();
        PostMapping mapping = signDocument.getAnnotation(PostMapping.class);
        assertNotNull(mapping, "signDocument must be a POST");
        assertArrayEquals(new String[]{"/signingProfiles/{signingProfileUuid}/documents/sign"}, mapping.path());
        assertEquals("signDocument", signDocument.getAnnotation(Operation.class).operationId());
    }

    @Test
    void signDocumentStatesThatTheProfileFixesTheLevel() throws NoSuchMethodException {
        String description = signDocumentMethod().getAnnotation(Operation.class).description();
        assertTrue(description.contains("at the Signing Profile's signature level"), description);
        assertTrue(description.contains("succeeds or fails as a whole"), description);
        assertNoJargon("signDocument", description);
    }

    @Test
    void signRawIsPostedToTheSigningProfilesRawPath() throws NoSuchMethodException {
        Method signRaw = signRawMethod();
        PostMapping mapping = signRaw.getAnnotation(PostMapping.class);
        assertNotNull(mapping, "signRaw must be a POST");
        assertArrayEquals(new String[]{"/signingProfiles/{signingProfileUuid}/raw/sign"}, mapping.path());
        assertEquals("signRaw", signRaw.getAnnotation(Operation.class).operationId());
    }

    @Test
    void signRawStatesItsBatchContract() throws NoSuchMethodException {
        String description = signRawMethod().getAnnotation(Operation.class).description();
        for (String promise : List
                .of("in request order", "succeeds or fails as a whole", "writes no signing record",
                        "A retry is a new signing", "names the item by its identifier", "synchronous",
                        "no signature activation data")) {
            assertTrue(description.contains(promise), "signRaw does not state '" + promise + "': " + description);
        }
        assertNoJargon("signRaw", description);
        assertLanguageNeutral("signRaw", description);
    }

    /** Integrators tell a mistake of theirs from a platform fault only if every refusal is listed. */
    @Test
    void signRawDescribesItsRefusals() throws NoSuchMethodException {
        String description = unprocessable(signRawMethod()).description();
        for (String refusal : List
                .of("ILM Native API inactive", "not a raw-signing profile", "`signatureInputMode` is not `dataDigest`",
                        "`maxItemsPerRequest`", "`digestAlgorithm` differs", "digest size", "repeats",
                        "`certificateFingerprint`")) {
            assertTrue(description.contains(refusal), "signRaw's 422 omits '" + refusal + "': " + description);
        }
        assertNoJargon("signRaw 422", description);
        assertLanguageNeutral("signRaw 422", description);
    }

    @Test
    void serviceUnavailableDeclaresTheRetryAfterHeader() {
        ApiResponse unavailable = Arrays
                .stream(NativeSigningController.class.getAnnotation(ApiResponses.class).value())
                .filter(r -> r.responseCode().equals("503"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no class-level 503 response"));
        assertTrue(Arrays.stream(unavailable.headers()).anyMatch(h -> h.name().equals("Retry-After")),
                "class-level 503 must declare the Retry-After header");
    }

    private static Method signDocumentMethod() throws NoSuchMethodException {
        return NativeSigningController.class.getMethod("signDocument", UUID.class, SignDocumentRequestDto.class);
    }

    private static Method signRawMethod() throws NoSuchMethodException {
        return NativeSigningController.class.getMethod("signRaw", UUID.class, SignRawRequestDto.class);
    }

    private static ApiResponse unprocessable(Method operation) {
        return Arrays
                .stream(operation.getAnnotation(ApiResponses.class).value())
                .filter(r -> r.responseCode().equals("422"))
                .findFirst()
                .orElseThrow(() -> new AssertionError(operation.getName() + " declares no 422 response"));
    }
}
