package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.interfaces.connector.cryptography.v2.CryptographicOperationsController;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.util.Arrays;
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
                    "422 PARAMETER_UNSUPPORTED", "never ignores the field", "prehashedSigning",
                    "defined for RSA PKCS#1 v1.5, RSA-PSS and ECDSA signature algorithm codes only",
                    "any other code answers 422 PARAMETER_UNSUPPORTED",
                    "length is not the output length of the fixed digest answers 422 VALIDATION_FAILED");

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

    @Test
    void signOperationDescribesThePrehashedRefusals() throws NoSuchMethodException {
        // given
        Method signData = CryptographicOperationsController.class.getMethod("signData", SignDataRequestV2Dto.class);

        // when
        String description = Arrays
                .stream(signData.getAnnotation(ApiResponses.class).value())
                .filter(response -> "422".equals(response.responseCode()))
                .findFirst()
                .orElseThrow()
                .description();

        // then
        assertTrue(description
                .contains("signatureInputMode prehashedDigest is not implemented by the connector "
                        + "or not defined for the selected signatureAlgorithm (errorCode PARAMETER_UNSUPPORTED)"));
        assertTrue(description
                .contains("a prehashedDigest item is not the length of the digest the "
                        + "signatureAlgorithm fixes (errorCode VALIDATION_FAILED)"));
    }

    private static void assertRules(String context, String description) {
        for (String rule : RULES) {
            assertTrue(description.contains(rule), context + " description must state: " + rule);
        }
        assertNoJargon(context, description);
        assertLanguageNeutral(context, description);
    }
}
