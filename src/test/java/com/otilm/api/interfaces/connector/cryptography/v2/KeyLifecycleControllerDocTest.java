package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.cryptography.v2.operations.SignatureAlgorithmAttribute;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the public OpenAPI surface of {@link KeyLifecycleController}. A connector author reads only the generated
 * document.
 */
class KeyLifecycleControllerDocTest {

    private static final String BASE_PATH = "/v2/cryptographyProvider/keyLifecycle";
    private static final Map<String, String> OPERATION_PATHS = Map
            .of("listSignerAttributes", "/signer/attributes", "listCertificateRequestAttributes", "/csr/attributes",
                    "createCertificateRequest", "/csr", "certificateIssued", "/certificate");

    @Test
    void everyOperationIsDocumented() {
        Method[] methods = KeyLifecycleController.class.getDeclaredMethods();
        assertEquals(OPERATION_PATHS.size(), methods.length, "operations on KeyLifecycleController");

        for (Method m : methods) {
            Operation op = m.getAnnotation(Operation.class);
            assertNotNull(op, "missing @Operation on " + m.getName());
            assertFalse(op.summary().isBlank(), "blank @Operation summary on " + m.getName());
            assertFalse(op.description().isBlank(), "blank @Operation description on " + m.getName());
            assertNoJargon(m.getName(), op.summary());
            assertNoJargon(m.getName(), op.description());
            assertLanguageNeutral(m.getName(), op.summary());
            assertLanguageNeutral(m.getName(), op.description());

            ApiResponses responses = m.getAnnotation(ApiResponses.class);
            assertNotNull(responses, "missing @ApiResponses on " + m.getName());
            assertTrue(Arrays.stream(responses.value()).anyMatch(r -> r.responseCode().startsWith("2")),
                    "no 2xx response documented on " + m.getName());
            for (ApiResponse r : responses.value()) {
                assertFalse(r.description().isBlank(), "blank response description on " + m.getName());
                assertNoJargon(m.getName() + " " + r.responseCode() + " response", r.description());
                assertLanguageNeutral(m.getName() + " " + r.responseCode() + " response", r.description());
            }
        }
    }

    @Test
    void operationsHangOffTheKeyLifecyclePath() {
        RequestMapping mapping = KeyLifecycleController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "missing @RequestMapping on KeyLifecycleController");
        assertEquals(List.of(BASE_PATH), List.of(mapping.value()));

        Map<String, String> paths = Arrays
                .stream(KeyLifecycleController.class.getDeclaredMethods())
                .collect(Collectors.toMap(Method::getName, m -> {
                    PostMapping post = m.getAnnotation(PostMapping.class);
                    assertNotNull(post, "missing @PostMapping on " + m.getName());
                    assertEquals(1, post.path().length, "one path on " + m.getName());
                    return post.path()[0];
                }));
        assertEquals(OPERATION_PATHS, paths);
    }

    @Test
    void controllerExtendsCommonV2AuthBase() {
        List<String> supers = Arrays.stream(KeyLifecycleController.class.getInterfaces()).map(Class::getName).toList();
        assertTrue(supers.contains("com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController"),
                "KeyLifecycleController must extend the common.v2 auth base; found " + supers);
    }

    @Test
    void tagDescriptionIsDocumentedAndJargonFree() {
        Tag tag = KeyLifecycleController.class.getAnnotation(Tag.class);
        assertNotNull(tag, "missing @Tag on KeyLifecycleController");
        assertFalse(tag.description().isBlank(), "blank @Tag description");
        assertNoJargon("@Tag", tag.description());
        assertLanguageNeutral("@Tag", tag.description());
    }

    /**
     * The published document renders descriptions as Markdown. A code span marks the identifier a caller matches on.
     */
    @Test
    void everyFailureNamesAKnownErrorCodeInACodeSpanAndDeclaresProblemDetailContent() {
        for (Method m : KeyLifecycleController.class.getDeclaredMethods()) {
            for (ApiResponse r : m.getAnnotation(ApiResponses.class).value()) {
                if (!isFailure(r.responseCode())) {
                    continue;
                }
                String context = "response " + r.responseCode() + " on " + m.getName();
                assertTrue(Arrays.stream(ErrorCode.values()).anyMatch(c -> r.description().contains(c.name())),
                        context + " does not name a known ErrorCode");
                for (ErrorCode code : ErrorCode.values()) {
                    if (r.description().contains(code.name())) {
                        assertTrue(r.description().contains("`" + code.name() + "`"),
                                context + " names " + code.name() + " outside a code span");
                    }
                }
                boolean declaresProblemDetail = Arrays
                        .stream(r.content())
                        .anyMatch(c -> c.mediaType().equals(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                && c.schema().implementation() == ProblemDetailExtended.class);
                assertTrue(declaresProblemDetail, context + " does not declare ProblemDetailExtended content");
            }
        }
    }

    @Test
    void theCertificateRequestListingNamesTheReservedAttribute() {
        String prose = operation("listCertificateRequestAttributes")
                .getAnnotation(Operation.class)
                .description()
                .replaceAll("\\s+", " ");

        assertTrue(prose.contains(SignatureAlgorithmAttribute.NAME),
                "the certificate request listing must name the reserved attribute a connector has to publish");
        assertTrue(prose.contains(SignatureAlgorithmAttribute.ATTRIBUTE_UUID.toString()),
                "the certificate request listing must name the reserved attribute's UUID");
    }

    @Test
    void theCertificateRequestHookDocumentsItsRefusals() {
        assertDocumentsCodes("createCertificateRequest", "409", ErrorCode.SIGNER_MISMATCH, ErrorCode.KEY_CERTIFIED);
        assertDocumentsCodes("createCertificateRequest", "422", ErrorCode.SIGNER_INVALID, ErrorCode.PUBLIC_KEY_MISMATCH,
                ErrorCode.PARAMETER_UNSUPPORTED);
    }

    @Test
    void theCertificateIssuedHookDocumentsItsRefusals() {
        assertDocumentsCodes("certificateIssued", "409", ErrorCode.KEY_UNBOUND);
        assertDocumentsCodes("certificateIssued", "422", ErrorCode.PUBLIC_KEY_MISMATCH);
    }

    @Test
    void bothHooksOfferSynchronousSuccessOnly() {
        for (String hook : List.of("createCertificateRequest", "certificateIssued")) {
            List<String> successCodes = Arrays
                    .stream(operation(hook).getAnnotation(ApiResponses.class).value())
                    .map(ApiResponse::responseCode)
                    .filter(code -> code.startsWith("2"))
                    .toList();
            assertEquals(List.of("200"), successCodes, hook + " must offer 200 as its only success");
        }
    }

    private static void assertDocumentsCodes(String operationName, String responseCode, ErrorCode... codes) {
        ApiResponse response = Arrays
                .stream(operation(operationName).getAnnotation(ApiResponses.class).value())
                .filter(r -> r.responseCode().equals(responseCode))
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError("expected a documented " + responseCode + " on " + operationName));
        for (ErrorCode code : codes) {
            assertTrue(response.description().contains(code.name()), operationName + "'s " + responseCode
                    + " must name " + code.name() + "; was: " + response.description());
        }
    }

    private static Method operation(String name) {
        return Arrays
                .stream(KeyLifecycleController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected a " + name + " operation"));
    }

    private static boolean isFailure(String responseCode) {
        return responseCode.startsWith("4") || responseCode.startsWith("5");
    }
}
