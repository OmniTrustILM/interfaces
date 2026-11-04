package com.otilm.api.model.common.signature;

import com.otilm.api.exception.ValidationException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureParameterGroupTest {

    @Test
    void theSevenGroupsAreTheWholeEnum() {
        assertEquals(7, SignatureParameterGroup.values().length);
    }

    @Test
    void everyGroupTravelsAsItsSnakeCaseCode() {
        assertEquals("signature_context", SignatureParameterGroup.SIGNATURE_CONTEXT.getCode());
        assertEquals("signer_identity", SignatureParameterGroup.SIGNER_IDENTITY.getCode());
        assertEquals("signed_attributes", SignatureParameterGroup.SIGNED_ATTRIBUTES.getCode());
        assertEquals("signature_scope", SignatureParameterGroup.SIGNATURE_SCOPE.getCode());
        assertEquals("visible_signature_placement", SignatureParameterGroup.VISIBLE_SIGNATURE_PLACEMENT.getCode());
        assertEquals("visible_signature_content", SignatureParameterGroup.VISIBLE_SIGNATURE_CONTENT.getCode());
        assertEquals("visible_signature_style", SignatureParameterGroup.VISIBLE_SIGNATURE_STYLE.getCode());
    }

    /** appliesTo declares what is implemented, not what ETSI allows: PAdES is the only family with parameters in v1. */
    @Test
    void everyGroupAppliesToPadesAndToNoOtherFamily() {
        for (SignatureParameterGroup group : SignatureParameterGroup.values()) {
            assertTrue(group.appliesTo(SignatureFamily.PADES), group.name());
            assertFalse(group.appliesTo(SignatureFamily.XADES), group.name());
            assertFalse(group.appliesTo(SignatureFamily.CADES), group.name());
            assertFalse(group.appliesTo(SignatureFamily.JADES), group.name());
        }
    }

    @Test
    void everyGroupIsFoundByItsOwnCode() {
        Arrays
                .stream(SignatureParameterGroup.values())
                .forEach(group -> assertEquals(group, SignatureParameterGroup.findByCode(group.getCode())));
    }

    @Test
    void anUnknownCodeIsRejected() {
        assertThrows(ValidationException.class, () -> SignatureParameterGroup.findByCode("signature_policy"));
    }

    @Test
    void everyGroupCarriesALabelAndDescriptionForTheOperatorScreens() {
        for (SignatureParameterGroup group : SignatureParameterGroup.values()) {
            assertFalse(group.getLabel().isBlank(), group.name());
            assertFalse(group.getDescription().isBlank(), group.name());
        }
    }
}
