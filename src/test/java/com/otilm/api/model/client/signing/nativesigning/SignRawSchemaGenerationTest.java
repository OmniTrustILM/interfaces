package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.client.signing.nativesigning.validation.ItemIdentifierFormat;
import com.otilm.api.model.core.settings.SigningSettingsDto;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Generates the OpenAPI schemas of {@code signRaw} in the 3.1 mode the published specification uses, and asserts what a
 * generated client keys off.
 */
class SignRawSchemaGenerationTest {

    private static final Map<String, Schema> REQUEST_SCHEMAS = openApi31Schemas(SignRawRequestDto.class);
    private static final Map<String, Schema> RESPONSE_SCHEMAS = openApi31Schemas(SignRawResponseDto.class);
    private static final List<String> SCHEMA_NAMES = List
            .of("SignRawRequest", "SignRawItem", "SignRawResult", "SignRawResponse");

    @Test
    void theRequestPublishesOnlyItsOwnFields() {
        Schema<?> request = schema(REQUEST_SCHEMAS, "SignRawRequest");

        assertEquals(Set.of("signatureInputMode", "digestAlgorithm", "items", "certificateFingerprint"),
                request.getProperties().keySet());
        assertEquals(List.of("digestAlgorithm", "items", "signatureInputMode"), sorted(request.getRequired()));
    }

    @Test
    void anItemRequiresItsIdentifierAndData() {
        Schema<?> item = schema(REQUEST_SCHEMAS, "SignRawItem");

        assertEquals(Set.of("identifier", "data"), item.getProperties().keySet());
        assertEquals(List.of("data", "identifier"), sorted(item.getRequired()));
    }

    /** A {@code $ref} keeps the mode one shared component with the cryptography v2 contract. */
    @Test
    void theInputModeIsTheSharedComponent() {
        assertEquals("#/components/schemas/SignatureInputMode",
                property(REQUEST_SCHEMAS, "SignRawRequest", "signatureInputMode").get$ref());
    }

    /** The cap is a platform setting, so a static bound in the schema would contradict an operator who raised it. */
    @Test
    void theItemsListNamesTheConfigurableCapInsteadOfAStaticOne() {
        Schema<?> items = property(REQUEST_SCHEMAS, "SignRawRequest", "items");

        assertEquals(1, items.getMinItems());
        assertNull(items.getMaxItems(), "the item cap is a platform setting, not a schema bound");
        assertTrue(items.getDescription().contains("`maxItemsPerRequest`"), items.getDescription());
        assertTrue(items.getDescription().contains(String.valueOf(SigningSettingsDto.DEFAULT_MAX_ITEMS_PER_REQUEST)),
                items.getDescription());
    }

    @Test
    void theFingerprintStatesItsFormat() {
        Schema<?> fingerprint = property(REQUEST_SCHEMAS, "SignRawRequest", "certificateFingerprint");

        assertEquals("^[0-9a-fA-F]{64}$", fingerprint.getPattern());
        for (String term : List.of("SHA-256", "DER", "hexadecimal", "422")) {
            assertTrue(fingerprint.getDescription().contains(term),
                    term + " missing from " + fingerprint.getDescription());
        }
    }

    @Test
    void theIdentifierPublishesItsBound() {
        Schema<?> identifier = property(REQUEST_SCHEMAS, "SignRawItem", "identifier");

        assertEquals(ItemIdentifierFormat.MAX_LENGTH, identifier.getMaxLength());
        assertEquals(ItemIdentifierFormat.REGEX, identifier.getPattern());
    }

    /** Both native operations write identifiers into the same logs and records, so one bound serves both. */
    @Test
    void signDocumentPublishesTheSameIdentifierBound() {
        Schema<?> signRaw = property(REQUEST_SCHEMAS, "SignRawItem", "identifier");
        Schema<?> signDocument = property(openApi31Schemas(SignDocumentRequestDto.class), "SignDocumentItem",
                "identifier");

        assertEquals(signRaw.getMaxLength(), signDocument.getMaxLength());
        assertEquals(signRaw.getPattern(), signDocument.getPattern());
    }

    @Test
    void bytesArePublishedAsBase64() {
        assertEquals("byte", property(REQUEST_SCHEMAS, "SignRawItem", "data").getFormat());
        assertEquals("byte", property(RESPONSE_SCHEMAS, "SignRawResult", "signature").getFormat());
        assertEquals("byte",
                property(RESPONSE_SCHEMAS, "SignRawResponse", "signerCertificateChain").getItems().getFormat());
    }

    @Test
    void theResponseCarriesTheAlgorithmAndChainOnceAndTheResultsInOrder() {
        Schema<?> response = schema(RESPONSE_SCHEMAS, "SignRawResponse");
        Schema<?> result = schema(RESPONSE_SCHEMAS, "SignRawResult");

        assertEquals(Set.of("signatureAlgorithm", "signerCertificateChain", "results"),
                response.getProperties().keySet());
        assertEquals(List.of("results", "signatureAlgorithm", "signerCertificateChain"),
                sorted(response.getRequired()));
        assertEquals("#/components/schemas/SignatureAlgorithm",
                property(RESPONSE_SCHEMAS, "SignRawResponse", "signatureAlgorithm").get$ref());
        assertTrue(property(RESPONSE_SCHEMAS, "SignRawResponse", "results").getDescription().contains("request order"));

        assertEquals(Set.of("identifier", "signingRecordUuid", "signature"), result.getProperties().keySet());
        assertEquals(List.of("identifier", "signature"), sorted(result.getRequired()));
    }

    /** Schema names are global in the document, and the native API carries no connector version in its names. */
    @Test
    void theSchemaNamesAreUnversioned() {
        Map<String, Schema> schemas = allSchemas();
        for (String name : SCHEMA_NAMES) {
            schema(schemas, name);
            assertFalse(name.endsWith("V2"), name);
        }
    }

    @Test
    void everyDescriptionIsPlainProse() {
        Map<String, Schema> schemas = allSchemas();
        for (String name : SCHEMA_NAMES) {
            Schema<?> schema = schema(schemas, name);
            assertProse(name, schema.getDescription());
            schema
                    .getProperties()
                    .forEach((property, value) -> assertProse(name + "." + property, value.getDescription()));
        }
    }

    private static void assertProse(String context, String description) {
        assertNotNull(description, context + " has no description");
        assertFalse(description.isBlank(), context + " has a blank description");
        assertNoJargon(context, description);
        assertLanguageNeutral(context, description);
    }

    private static Map<String, Schema> allSchemas() {
        Map<String, Schema> schemas = new HashMap<>(REQUEST_SCHEMAS);
        schemas.putAll(RESPONSE_SCHEMAS);
        return schemas;
    }

    private static Schema<?> schema(Map<String, Schema> schemas, String name) {
        Schema<?> schema = schemas.get(name);
        assertNotNull(schema, "expected a generated schema named " + name + "; found " + schemas.keySet());
        return schema;
    }

    private static Schema<?> property(Map<String, Schema> schemas, String name, String property) {
        Schema<?> value = (Schema<?>) schema(schemas, name).getProperties().get(property);
        assertNotNull(value, name + " does not declare " + property);
        return value;
    }

    private static List<String> sorted(List<String> values) {
        assertNotNull(values, "no required list");
        return values.stream().sorted().toList();
    }
}
