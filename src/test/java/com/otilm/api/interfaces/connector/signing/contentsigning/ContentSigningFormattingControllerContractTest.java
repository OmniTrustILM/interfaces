package com.otilm.api.interfaces.connector.signing.contentsigning;

import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingAttributesDto;
import com.otilm.api.model.connector.signatures.contentsigning.common.ContentSigningFormattingOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the wire surface of {@link ContentSigningFormattingController} against hard-coded expectations, so a typo in a
 * route or a silently changed verb fails the build rather than shipping to four connector implementations.
 */
class ContentSigningFormattingControllerContractTest {

    private static final String BASE_PATH = "/v1/signatureProvider/contentSigning";

    /** The seven ladder operations, each of which owns an attribute companion and an enum code. */
    private static final Map<String, String> EXPECTED_OPERATION_PATHS = Map
            .ofEntries(Map.entry("computeDtbs", "/computeDtbs"),
                    Map.entry("embedSignatureValue", "/embedSignatureValue"),
                    Map.entry("computeSignatureTimestampImprint", "/computeSignatureTimestampImprint"),
                    Map.entry("embedSignatureTimestamp", "/embedSignatureTimestamp"),
                    Map.entry("computeArchiveTimestampImprint", "/computeArchiveTimestampImprint"),
                    Map.entry("embedArchiveTimestamp", "/embedArchiveTimestamp"),
                    Map.entry("extendToLevel", "/extendToLevel"));

    /** The envelope companions of the one operation that carries a lifecycle. They are not ladder operations. */
    private static final Map<String, String> EXPECTED_COMPANION_PATHS = Map
            .of("getExtendToLevelStatus", "/extendToLevel/status", "cancelExtendToLevel", "/extendToLevel/cancel");

    private static final Map<String, String> EXPECTED_POST_PATHS = allPostPaths();

    private static final Map<String, String> EXPECTED_GET_PATHS = Map
            .of("listFormattingAttributes", "/{operation}/attributes", "getContentSigningDeclaration", "/declaration");

    /** Cancellation answers 204 with no body, so it is the one route that produces nothing. */
    private static final Set<String> BODYLESS_RESPONSES = Set.of("cancelExtendToLevel");

    /**
     * The documented success of every operation that returns its result directly. Only the extension has a lifecycle,
     * so only it may document a 202, and only cancellation answers 204.
     */
    private static final Map<String, String> EXPECTED_SUCCESS_CODES = expectedSuccessCodes();

    private static Map<String, String> allPostPaths() {
        Map<String, String> paths = new HashMap<>(EXPECTED_OPERATION_PATHS);
        paths.putAll(EXPECTED_COMPANION_PATHS);
        return Map.copyOf(paths);
    }

    private static Map<String, String> expectedSuccessCodes() {
        Map<String, String> codes = new HashMap<>();
        EXPECTED_OPERATION_PATHS.keySet().forEach(name -> codes.put(name, "200"));
        EXPECTED_GET_PATHS.keySet().forEach(name -> codes.put(name, "200"));
        codes.put("getExtendToLevelStatus", "200");
        codes.put("cancelExtendToLevel", "204");
        return Map.copyOf(codes);
    }

    /** The operation description is where a connector author learns who chooses the algorithm, so it is pinned. */
    @Test
    void theOperationsSayWhoChoosesTheSignatureAlgorithm() {
        String computeDtbs = operationDescription("computeDtbs");
        assertTrue(computeDtbs.contains("The platform also supplies signatureAlgorithm"),
                "computeDtbs does not say that the platform supplies the algorithm");
        assertTrue(computeDtbs.contains("MUST NOT substitute one of its own"),
                "computeDtbs does not forbid the connector substituting an algorithm of its own");

        String embed = operationDescription("embedSignatureValue");
        assertTrue(embed.contains("the same value the platform gave computeDtbs"),
                "embedSignatureValue does not say the algorithm is the value computeDtbs was given");
        assertTrue(embed.contains("MUST refuse a request whose algorithm disagrees"),
                "embedSignatureValue does not require refusing compute-to-embed drift");
    }

    /** Drift is its own error code, so an operator can alert on it apart from a caller's bad field. */
    @Test
    void embedSignatureValueDocumentsContextMismatchAmongItsUnprocessableCodes() {
        String description = unprocessableDescription("embedSignatureValue");

        assertTrue(description.contains("`CONTEXT_MISMATCH`"),
                "embedSignatureValue does not document CONTEXT_MISMATCH as a 422 code");
        assertTrue(description.contains("formattingContext"),
                "the CONTEXT_MISMATCH entry does not say what the request contradicts");
    }

    /** A connector author learns from this list which code answers a stamp the document cannot take. */
    @Test
    void computeDtbsDocumentsTheStampRefusalsAmongItsUnprocessableCodes() {
        String description = unprocessableDescription("computeDtbs");

        assertTrue(description.contains("`SIGNATURE_PLACEMENT_REJECTED`"),
                "computeDtbs does not document SIGNATURE_PLACEMENT_REJECTED as a 422 code");
        assertTrue(description.contains("`DOCUMENT_SIGNING_FORBIDDEN`"),
                "computeDtbs does not document DOCUMENT_SIGNING_FORBIDDEN as a 422 code");
    }

    /** A certification request on a signed document is the document's refusal, not a parameter's. */
    @Test
    void computeDtbsForbidsCertifyingAnAlreadySignedDocument() {
        String description = unprocessableDescription("computeDtbs");
        String forbidden = description.substring(description.indexOf("`DOCUMENT_SIGNING_FORBIDDEN`"));

        assertTrue(forbidden.substring(0, forbidden.indexOf("\n")).contains("certif"),
                "the DOCUMENT_SIGNING_FORBIDDEN entry does not cover certifying an already signed document");
    }

    /** The split decides which values a signing request may set. A bare array would lose it. */
    @Test
    void theListingReturnsProfileAndRequestAttributesApart() {
        assertEquals(ContentSigningFormattingAttributesDto.class, method("listFormattingAttributes").getReturnType());
    }

    /** The platform refuses a listing that breaks these rules. A connector author finds them only here. */
    @Test
    void theListingStatesTheRulesThePlatformChecks() {
        String description = operationDescription("listFormattingAttributes");

        assertTrue(description.contains("`properties.group` names its permission group"),
                "the listing does not say how a request attribute names its group");
        assertTrue(
                description
                        .contains("Every request attribute must be a data attribute whose name appears only in "
                                + "`requestAttributes` across the operations a Signing Profile reaches"),
                "the listing does not confine a request attribute to data attributes and to requestAttributes");
        assertTrue(description.contains("no group may share a request attribute's name"),
                "the listing does not keep group names apart from request-attribute names");
    }

    /** A connector author learns from this list which code refuses request attributes that fail to merge. */
    @Test
    void computeDtbsDocumentsTheAttributeRefusalAmongItsUnprocessableCodes() {
        assertTrue(unprocessableDescription("computeDtbs").contains("`ATTRIBUTES_REJECTED`"),
                "computeDtbs does not document ATTRIBUTES_REJECTED as a 422 code");
    }

    /** A request-side fault and a merge fault share one code, so a connector author learns both from this entry. */
    @Test
    void computeDtbsRefusesAnUndeclaredRequestAttributeAsAnAttributeRefusal() {
        assertTrue(unprocessableEntry("computeDtbs", "ATTRIBUTES_REJECTED").contains("undeclared"),
                "the ATTRIBUTES_REJECTED entry does not cover a request attribute the listing does not declare");
    }

    /** A stamp the connector cannot draw names no single attribute to fix. */
    @Test
    void computeDtbsRefusesAVisibleSignatureItCannotDrawAsUnsupported() {
        assertTrue(unprocessableEntry("computeDtbs", "PARAMETER_UNSUPPORTED").contains("visible signature"),
                "the PARAMETER_UNSUPPORTED entry does not cover a visible signature the connector cannot draw");
    }

    /** Document permissions bind a timestamp revision as they bind a signature. */
    @Test
    void theSignatureTimestampPairDocumentsTheDocumentsRefusal() {
        for (String methodName : List.of("computeSignatureTimestampImprint", "embedSignatureTimestamp")) {
            assertTrue(unprocessableDescription(methodName).contains("`DOCUMENT_SIGNING_FORBIDDEN`"),
                    methodName + " does not document DOCUMENT_SIGNING_FORBIDDEN as a 422 code");
        }
    }

    /** A connector without ARCHIVAL answers the archive pair as it answers extendToLevel without LONG_TERM. */
    @Test
    void theArchiveTimestampPairDocumentsTheUndeclaredLevel() {
        for (String methodName : List.of("computeArchiveTimestampImprint", "embedArchiveTimestamp")) {
            assertTrue(unprocessableEntry(methodName, "PARAMETER_UNSUPPORTED").contains("ARCHIVAL"),
                    methodName + " does not refuse a family the connector declares no ARCHIVAL level for");
        }
    }

    /** The body reads a malformed family code as a string, so bean validation refuses it as a field rule. */
    @Test
    void everyOperationRefusesAMalformedFamilyAsAFieldRule() {
        for (String methodName : EXPECTED_POST_PATHS.keySet()) {
            assertFalse(responseDescription(methodName, "400").contains("family"),
                    methodName + " documents a malformed family as an unreadable body");
            assertTrue(unprocessableDescription(methodName).contains("malformed family"),
                    methodName + " does not document a malformed family as a field rule");
        }
    }

    private static String unprocessableEntry(String methodName, String errorCode) {
        String description = unprocessableDescription(methodName);
        int start = description.indexOf("`" + errorCode + "`");
        assertTrue(start >= 0, methodName + " does not document " + errorCode + " as a 422 code");
        int end = description.indexOf("\n", start);
        return end < 0 ? description.substring(start) : description.substring(start, end);
    }

    private static String unprocessableDescription(String methodName) {
        return responseDescription(methodName, "422");
    }

    private static String responseDescription(String methodName, String responseCode) {
        return Arrays
                .stream(method(methodName).getAnnotation(ApiResponses.class).value())
                .filter(response -> responseCode.equals(response.responseCode()))
                .map(ApiResponse::description)
                .findFirst()
                .orElseThrow(() -> new AssertionError(methodName + " documents no " + responseCode + " at all"));
    }

    private static String operationDescription(String methodName) {
        Operation operation = method(methodName).getAnnotation(Operation.class);
        assertNotNull(operation, methodName + " carries no @Operation");
        return operation.description();
    }

    @Test
    void basePathIsPinned() {
        RequestMapping mapping = ContentSigningFormattingController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "missing @RequestMapping on ContentSigningFormattingController");
        assertArrayEquals(new String[]{BASE_PATH}, mapping.value());
    }

    @Test
    void everyOperationIsMappedToItsExpectedPath() {
        for (Map.Entry<String, String> expected : EXPECTED_POST_PATHS.entrySet()) {
            PostMapping mapping = method(expected.getKey()).getAnnotation(PostMapping.class);
            assertNotNull(mapping, "missing @PostMapping on " + expected.getKey());
            assertArrayEquals(new String[]{expected.getValue()}, mapping.path(), expected.getKey());
        }

        for (Map.Entry<String, String> expected : EXPECTED_GET_PATHS.entrySet()) {
            GetMapping mapping = method(expected.getKey()).getAnnotation(GetMapping.class);
            assertNotNull(mapping, "missing @GetMapping on " + expected.getKey());
            assertArrayEquals(new String[]{expected.getValue()}, mapping.path(), expected.getKey());
        }
    }

    @Test
    void everyOperationSpeaksJson() {
        for (String name : EXPECTED_POST_PATHS.keySet()) {
            PostMapping mapping = method(name).getAnnotation(PostMapping.class);
            assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes(), name + " consumes");
            String[] expectedProduces = BODYLESS_RESPONSES.contains(name)
                    ? new String[]{}
                    : new String[]{MediaType.APPLICATION_JSON_VALUE};
            assertArrayEquals(expectedProduces, mapping.produces(), name + " produces");
        }

        for (String name : EXPECTED_GET_PATHS.keySet()) {
            GetMapping mapping = method(name).getAnnotation(GetMapping.class);
            assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.produces(), name + " produces");
        }
    }

    /**
     * Every operation returns its result directly, so every documented success is a plain 200 with a body — except
     * cancellation, which has nothing to return. A 202 anywhere but the extension would mean an operation had quietly
     * acquired a lifecycle.
     */
    @Test
    void everyOperationAnswersSynchronously() {
        for (Method m : ContentSigningFormattingController.class.getDeclaredMethods()) {
            List<String> documented = Arrays
                    .stream(m.getAnnotation(ApiResponses.class).value())
                    .map(ApiResponse::responseCode)
                    .filter(code -> code.startsWith("2"))
                    .toList();
            assertTrue(!documented.isEmpty(), "no 2xx documented on " + m.getName());
            assertEquals(EXPECTED_SUCCESS_CODES.get(m.getName()), documented.get(0),
                    m.getName() + " documents an unexpected primary success code");

            ResponseStatus status = m.getAnnotation(ResponseStatus.class);
            if (status != null) {
                assertEquals(HttpStatus.valueOf(Integer.parseInt(EXPECTED_SUCCESS_CODES.get(m.getName()))),
                        status.value(),
                        m.getName() + " declares a @ResponseStatus that contradicts its documented success code");
            }
        }
    }

    /** The extension is the only operation allowed to promise a later answer, and only ever as a second option. */
    @Test
    void onlyTheExtensionDocumentsAnAsynchronousAccept() {
        for (Method m : ContentSigningFormattingController.class.getDeclaredMethods()) {
            boolean documents202 = Arrays
                    .stream(m.getAnnotation(ApiResponses.class).value())
                    .anyMatch(response -> "202".equals(response.responseCode()));
            assertEquals("extendToLevel".equals(m.getName()), documents202,
                    m.getName() + " disagrees with the contract on whether it may answer 202");
        }
    }

    @Test
    void theContractDeclaresExactlyTheExpectedOperations() {
        assertEquals(EXPECTED_POST_PATHS.size() + EXPECTED_GET_PATHS.size(),
                ContentSigningFormattingController.class.getDeclaredMethods().length,
                "ContentSigningFormattingController declares operations this test does not pin");
    }

    /** A POST route that drifted from its code would leave the attribute route configuring a different operation. */
    @Test
    void everyOperationPostsToItsOwnEnumCode() {
        Set<String> mapped = Set.copyOf(EXPECTED_OPERATION_PATHS.values());
        Set<String> fromEnum = Arrays
                .stream(ContentSigningFormattingOperation.values())
                .map(op -> "/" + op.getCode())
                .collect(Collectors.toSet());

        assertEquals(fromEnum, mapped,
                "ContentSigningFormattingOperation codes and the controller's POST routes disagree");
    }

    /** One body shape serves every family code. */
    @Test
    void everyOperationTakesAConcreteRequest() {
        for (String name : EXPECTED_POST_PATHS.keySet()) {
            Class<?> body = method(name).getParameterTypes()[0];
            assertFalse(Modifier.isAbstract(body.getModifiers()),
                    name + " takes " + body.getSimpleName() + ", which is abstract");
        }
    }

    private static Method method(String name) {
        return Arrays
                .stream(ContentSigningFormattingController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("no method named " + name));
    }

    @Test
    void everyDeclaredMethodIsPinnedByName() {
        for (Method m : ContentSigningFormattingController.class.getDeclaredMethods()) {
            assertTrue(EXPECTED_POST_PATHS.containsKey(m.getName()) || EXPECTED_GET_PATHS.containsKey(m.getName()),
                    "unpinned method " + m.getName());
        }
    }
}
