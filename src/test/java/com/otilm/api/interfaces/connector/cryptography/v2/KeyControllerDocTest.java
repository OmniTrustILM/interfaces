package com.otilm.api.interfaces.connector.cryptography.v2;

import com.otilm.api.model.client.connector.v2.FeatureFlag;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.cryptography.v2.key.KeyExportableAttribute;
import com.otilm.api.testsupport.OpenApiProseAssertions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the public OpenAPI surface of {@link KeyController}: every operation is documented, no Core-internal jargon
 * leaks into the published prose ({@link OpenApiProseAssertions}), and the controller extends the NG auth base. The key
 * import and export operations must also name a real {@link ErrorCode} and declare {@link ProblemDetailExtended} on
 * every failure response.
 */
class KeyControllerDocTest {

    private static final int EXPECTED_OPERATIONS = 16;

    @Test
    void everyOperationIsDocumented() {
        Method[] methods = KeyController.class.getDeclaredMethods();
        assertTrue(methods.length >= EXPECTED_OPERATIONS,
                "expected at least " + EXPECTED_OPERATIONS + " endpoints, found " + methods.length);

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

    /**
     * The reserved create-key attribute is how the exportable intent reaches key creation. A connector author reads
     * only the generated document, so an obligation that lives solely in a helper class for one language is an
     * obligation most connectors never learn about.
     */
    @Test
    void theReservedExportableAttributeIsPublishedWhereAConnectorWillReadIt() {
        Method createAttributes = Arrays
                .stream(KeyController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("listCreateKeyAttributes"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected a listCreateKeyAttributes operation"));

        // A text block keeps its line breaks, which the rendered document reads as spaces.
        String operationProse = createAttributes.getAnnotation(Operation.class).description().replaceAll("\\s+", " ");
        assertTrue(operationProse.contains(KeyExportableAttribute.NAME),
                "the key-creation attribute operation must name the reserved attribute a connector has to publish");
        assertTrue(operationProse.contains("v3 data attribute"),
                "the key-creation attribute operation must name the version of the reserved attribute");

        String flagProse = FeatureFlag.KEY_EXPORT.getDescription();
        assertTrue(flagProse.contains(KeyExportableAttribute.NAME),
                "the key-export flag must name the attribute declaring it obliges a connector to publish");
        assertTrue(flagProse.contains("v3 data attribute"),
                "the key-export flag must name the version of the reserved attribute");
    }

    @Test
    void controllerExtendsCommonV2AuthBase() {
        List<String> supers = Arrays.stream(KeyController.class.getInterfaces()).map(Class::getName).toList();
        assertTrue(supers.contains("com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController"),
                "KeyController must extend the common.v2 auth base, not a legacy one; found " + supers);
    }

    @Test
    void tagDescriptionIsDocumentedAndJargonFree() {
        Tag tag = KeyController.class.getAnnotation(Tag.class);
        assertNotNull(tag, "missing @Tag on KeyController");
        assertFalse(tag.description().isBlank(), "blank @Tag description");
        assertNoJargon("@Tag", tag.description());
        assertLanguageNeutral("@Tag", tag.description());
    }

    @Test
    void everyKeyTransferFailureNamesAKnownErrorCode() {
        for (Method m : keyTransferOperations()) {
            for (ApiResponse r : m.getAnnotation(ApiResponses.class).value()) {
                if (!isFailure(r.responseCode())) {
                    continue;
                }
                assertTrue(Arrays.stream(ErrorCode.values()).anyMatch(c -> r.description().contains(c.name())),
                        "response " + r.responseCode() + " on " + m.getName() + " does not name a known ErrorCode");
            }
        }
    }

    /**
     * The published document renders descriptions as Markdown, so a code named as bare prose reads as a sentence
     * fragment rather than the identifier a caller matches on.
     */
    @Test
    void everyKeyTransferFailureMarksItsErrorCodesAsCode() {
        for (Method m : keyTransferOperations()) {
            for (ApiResponse r : m.getAnnotation(ApiResponses.class).value()) {
                if (!isFailure(r.responseCode())) {
                    continue;
                }
                for (ErrorCode code : ErrorCode.values()) {
                    if (r.description().contains(code.name())) {
                        assertTrue(r.description().contains("`" + code.name() + "`"), "response " + r.responseCode()
                                + " on " + m.getName() + " names " + code.name() + " outside a code span");
                    }
                }
            }
        }
    }

    @Test
    void everyKeyTransferFailureDeclaresProblemDetailContent() {
        for (Method m : keyTransferOperations()) {
            for (ApiResponse r : m.getAnnotation(ApiResponses.class).value()) {
                if (!isFailure(r.responseCode())) {
                    continue;
                }
                boolean declaresProblemDetail = Arrays
                        .stream(r.content())
                        .anyMatch(c -> c.mediaType().equals(MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                                && c.schema().implementation() == ProblemDetailExtended.class);
                assertTrue(declaresProblemDetail, "response " + r.responseCode() + " on " + m.getName()
                        + " does not declare ProblemDetailExtended content");
            }
        }
    }

    @Test
    void importDocumentsIdempotencyConflict() {
        Method importKey = keyTransferOperation("importKey");

        ApiResponse conflict = Arrays
                .stream(importKey.getAnnotation(ApiResponses.class).value())
                .filter(r -> r.responseCode().equals("409"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected a documented 409 on importKey"));

        assertTrue(conflict.description().contains(ErrorCode.RESOURCE_ALREADY_EXISTS.name()),
                "importKey's 409 must name RESOURCE_ALREADY_EXISTS; was: " + conflict.description());
    }

    @Test
    void importResolutionOperationExists() {
        assertTrue(keyTransferOperations().stream().anyMatch(m -> m.getName().equals("getImportKeyResult")),
                "an import outcome must be resolvable after a lost response");
    }

    @Test
    void exportOffersSynchronousSuccessOnly() {
        Method exportKey = keyTransferOperation("exportKey");

        List<String> successCodes = Arrays
                .stream(exportKey.getAnnotation(ApiResponses.class).value())
                .map(ApiResponse::responseCode)
                .filter(code -> code.startsWith("2"))
                .toList();

        assertEquals(List.of("200"), successCodes,
                "export is synchronous only, so 200 must be its only documented success");
    }

    @Test
    void exportDocumentsTheRefusalOfANonExportableKey() {
        Method exportKey = keyTransferOperation("exportKey");

        boolean documentsRefusal = Arrays
                .stream(exportKey.getAnnotation(ApiResponses.class).value())
                .anyMatch(r -> r.description().contains(ErrorCode.KEY_NOT_EXPORTABLE.name()));

        assertTrue(documentsRefusal, "exportKey must document the refusal of a key that is not exportable");
    }

    @Test
    void exportCapabilityDiscoveryOperationsExist() {
        assertNotNull(keyTransferOperation("listExportableKeyTypes"));
        assertNotNull(keyTransferOperation("listExportKeyAttributes"));
    }

    private static Method keyTransferOperation(String name) {
        return keyTransferOperations()
                .stream()
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("expected a " + name + " operation"));
    }

    private static List<Method> keyTransferOperations() {
        List<Method> operations = Arrays.stream(KeyController.class.getDeclaredMethods()).filter(m -> {
            String name = m.getName().toLowerCase(Locale.ROOT);
            return name.contains("import") || name.contains("export");
        }).toList();
        assertFalse(operations.isEmpty(), "expected key-transfer operations on KeyController");
        for (Method m : operations) {
            assertNotNull(m.getAnnotation(ApiResponses.class), "missing @ApiResponses on " + m.getName());
        }
        return operations;
    }

    private static boolean isFailure(String responseCode) {
        return responseCode.startsWith("4") || responseCode.startsWith("5");
    }
}
