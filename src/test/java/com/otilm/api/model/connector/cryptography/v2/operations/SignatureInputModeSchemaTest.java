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
 * Go connector models are generated from the published OpenAPI document, so a conformance rule for the prehashed input
 * mode that drops out of the schema text never reaches a Go connector.
 */
class SignatureInputModeSchemaTest {

    private static final List<String> RULES = List
            .of("never hashed again", "RFC 8017 section 9.2", "explicit NULL",
                    "RSA PKCS#1 v1.5, RSA-PSS and ECDSA signature algorithm codes only", "PARAMETER_UNSUPPORTED",
                    "VALIDATION_FAILED", "any signatureInputMode it does not implement", "never ignores the field",
                    "prehashedSigning");

    @Test
    void fieldSchemaCarriesTheConformanceRules() throws NoSuchFieldException {
        // given
        Schema schema = SignDataRequestV2Dto.class.getDeclaredField("signatureInputMode").getAnnotation(Schema.class);

        // then
        assertNotNull(schema);
        String description = schema.description();
        for (String rule : RULES) {
            assertTrue(description.contains(rule), "signatureInputMode description must state: " + rule);
        }
        assertTrue(description.contains("Absent means data"));
        assertTrue(description.contains("omit the field for data mode"));
        assertNoJargon("signatureInputMode field", description);
        assertLanguageNeutral("signatureInputMode field", description);
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
                .contains("the connector does not implement the requested signatureInputMode, or prehashedDigest "
                        + "is not defined for the selected signatureAlgorithm (errorCode PARAMETER_UNSUPPORTED)"));
        assertTrue(description
                .contains("a prehashedDigest item is not the length of the digest the "
                        + "signatureAlgorithm fixes (errorCode VALIDATION_FAILED)"));
    }
}
