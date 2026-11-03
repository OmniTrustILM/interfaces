package com.otilm.api.model.connector.cryptography.v2.operations;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Connector authors read only the generated OpenAPI document, so the conformance rules for the prehashed input mode
 * must be in the published schema text and not only in source comments.
 */
class SignatureInputModeSchemaTest {

    private static final List<String> RULES = List
            .of("never hashed again", "RFC 8017 section 9.2", "explicit NULL", "RSA-PSS", "ECDSA",
                    "422 PARAMETER_UNSUPPORTED", "never ignores the field", "prehashedSigning");

    @Test
    void fieldSchemaCarriesTheConformanceRules() throws NoSuchFieldException {
        // given
        Schema schema = SignDataRequestV2Dto.class.getDeclaredField("signatureInputMode").getAnnotation(Schema.class);

        // then
        assertNotNull(schema);
        assertRules("signatureInputMode field", schema.description());
        assertTrue(schema.description().contains("Absent means data"));
        assertTrue(schema.description().contains("omit the field for data mode"));
    }

    @Test
    void enumSchemaCarriesTheConformanceRules() {
        // given
        Schema schema = SignatureInputMode.class.getAnnotation(Schema.class);

        // then
        assertNotNull(schema);
        assertRules("SignatureInputMode enum", schema.description());
    }

    private static void assertRules(String context, String description) {
        for (String rule : RULES) {
            assertTrue(description.contains(rule), context + " description must state: " + rule);
        }
        assertNoJargon(context, description);
        assertLanguageNeutral(context, description);
    }
}
