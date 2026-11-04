package com.otilm.api.model.common.signature.parameters.pades;

import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PadesVisibleSignatureStyleDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final Map<String, BiConsumer<PadesVisibleSignatureStyleDto, String>> COLOUR_SETTERS = Map
            .of("textColor", PadesVisibleSignatureStyleDto::setTextColor, "textBackgroundColor",
                    PadesVisibleSignatureStyleDto::setTextBackgroundColor, "backgroundColor",
                    PadesVisibleSignatureStyleDto::setBackgroundColor);

    private static PadesVisibleSignatureStyleDto branded() {
        PadesVisibleSignatureStyleDto style = new PadesVisibleSignatureStyleDto();
        style.setTextColor("#171717");
        style.setTextBackgroundColor("#ffffff");
        style.setBackgroundColor("#F5F5F5");
        style.setPadding(5);
        style.setTextWrapping(TextWrapping.FONT_BASED);
        style.setTextPosition(TextPosition.LEFT);
        style.setTextHorizontalAlignment(TextHorizontalAlignment.LEFT);
        style.setTextVerticalAlignment(TextVerticalAlignment.MIDDLE);
        style.setImageScaling(ImageScaling.STRETCH);
        style.setDpi(300);
        return style;
    }

    private static boolean valid(Object node) {
        return VALIDATOR.validate(node).isEmpty();
    }

    @Test
    void aFullyPopulatedStyleIsValid() {
        assertTrue(valid(branded()));
    }

    @Test
    void everyFieldIsOptional() {
        assertTrue(valid(new PadesVisibleSignatureStyleDto()));
    }

    /** A renderer reads exactly one colour syntax, so the contract admits exactly that one. */
    @Test
    void aColourIsSixHexDigitsBehindAHash() {
        for (String colour : new String[]{"#000000", "#FFFFFF", "#ffffff", "#1a2B3c"}) {
            PadesVisibleSignatureStyleDto style = branded();
            style.setTextColor(colour);
            style.setTextBackgroundColor(colour);
            style.setBackgroundColor(colour);
            assertTrue(valid(style), colour);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"textColor", "textBackgroundColor", "backgroundColor"})
    void aMalformedColourIsRefusedOnTheFieldCarryingIt(String field) {
        for (String colour : new String[]{"#FFF", "FFFFFF", "#GGGGGG", "#FFFFFF ", "#FFFFFF80", "red"}) {
            PadesVisibleSignatureStyleDto style = branded();
            COLOUR_SETTERS.get(field).accept(style, colour);

            Set<ConstraintViolation<PadesVisibleSignatureStyleDto>> violations = VALIDATOR.validate(style);

            assertTrue(violations.stream().anyMatch(v -> field.equals(v.getPropertyPath().toString())),
                    field + " accepted " + colour);
        }
    }

    @Test
    void theBoundsOnPaddingAndResolutionAreEnforced() {
        PadesVisibleSignatureStyleDto negativePadding = branded();
        negativePadding.setPadding(-1);
        assertFalse(valid(negativePadding));

        PadesVisibleSignatureStyleDto atThePaddingCap = branded();
        atThePaddingCap.setPadding(100);
        assertTrue(valid(atThePaddingCap));

        PadesVisibleSignatureStyleDto overThePaddingCap = branded();
        overThePaddingCap.setPadding(101);
        assertFalse(valid(overThePaddingCap));

        PadesVisibleSignatureStyleDto belowScreenResolution = branded();
        belowScreenResolution.setDpi(71);
        assertFalse(valid(belowScreenResolution));

        PadesVisibleSignatureStyleDto atTheResolutionCap = branded();
        atTheResolutionCap.setDpi(1200);
        assertTrue(valid(atTheResolutionCap));

        PadesVisibleSignatureStyleDto overTheResolutionCap = branded();
        overTheResolutionCap.setDpi(1201);
        assertFalse(valid(overTheResolutionCap));
    }

    @Test
    void validationCascadesFromTheVisibleSignature() {
        PadesVisibleSignatureStyleDto style = branded();
        style.setBackgroundColor("transparent");
        PadesVisibleSignatureDto visibleSignature = new PadesVisibleSignatureDto();
        visibleSignature.setStyle(style);

        Set<ConstraintViolation<PadesVisibleSignatureDto>> violations = VALIDATOR.validate(visibleSignature);

        assertTrue(violations.stream().anyMatch(v -> "style.backgroundColor".equals(v.getPropertyPath().toString())),
                "the violation does not point at the offending style field");
    }
}
