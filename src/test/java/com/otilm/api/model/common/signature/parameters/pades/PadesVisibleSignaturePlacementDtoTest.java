package com.otilm.api.model.common.signature.parameters.pades;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PadesVisibleSignaturePlacementDtoTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static PadesVisibleSignaturePlacementDto coordinates() {
        PadesVisibleSignaturePlacementDto placement = new PadesVisibleSignaturePlacementDto();
        placement.setPage(1);
        placement.setOriginX(20f);
        placement.setOriginY(30f);
        placement.setWidth(180f);
        placement.setHeight(60f);
        return placement;
    }

    private static PadesVisibleSignaturePlacementDto anchor() {
        PadesVisibleSignaturePlacementDto placement = new PadesVisibleSignaturePlacementDto();
        placement.setPage(2);
        placement.setAlignmentHorizontal(HorizontalAlignment.RIGHT);
        placement.setAlignmentVertical(VerticalAlignment.BOTTOM);
        placement.setZoom(75f);
        return placement;
    }

    private static PadesVisibleSignaturePlacementDto namedField() {
        PadesVisibleSignaturePlacementDto placement = new PadesVisibleSignaturePlacementDto();
        placement.setFieldId("Signature1");
        return placement;
    }

    private static boolean valid(PadesVisibleSignaturePlacementDto placement) {
        return VALIDATOR.validate(placement).isEmpty();
    }

    @Test
    void eachOfTheThreeModesIsValidOnItsOwn() {
        assertTrue(valid(namedField()));
        assertTrue(valid(coordinates()));
        assertTrue(valid(anchor()));
    }

    /**
     * The same type is a partial request fragment during the merge, so completeness is Core's post-merge rule, not a
     * constraint here: page alone must validate.
     */
    @Test
    void aPartialFragmentIsValidBecauseCompletenessIsCheckedAfterTheMerge() {
        PadesVisibleSignaturePlacementDto pageOnly = new PadesVisibleSignaturePlacementDto();
        pageOnly.setPage(4);
        assertTrue(valid(pageOnly));

        PadesVisibleSignaturePlacementDto widthOnly = new PadesVisibleSignaturePlacementDto();
        widthOnly.setWidth(120f);
        assertTrue(valid(widthOnly));

        assertTrue(valid(new PadesVisibleSignaturePlacementDto()));
    }

    @Test
    void aNamedFieldTakesNothingElse() {
        PadesVisibleSignaturePlacementDto withPage = namedField();
        withPage.setPage(1);
        assertFalse(valid(withPage));

        PadesVisibleSignaturePlacementDto withCoordinates = namedField();
        withCoordinates.setOriginX(10f);
        assertFalse(valid(withCoordinates));

        PadesVisibleSignaturePlacementDto withAnchor = namedField();
        withAnchor.setAlignmentVertical(VerticalAlignment.TOP);
        assertFalse(valid(withAnchor));
    }

    @Test
    void coordinatesAndAnAnchorAreNotMixed() {
        PadesVisibleSignaturePlacementDto mixed = coordinates();
        mixed.setAlignmentHorizontal(HorizontalAlignment.CENTER);
        assertFalse(valid(mixed));
    }

    /** Zoom scales an unsized stamp, so it belongs to the anchor mode and contradicts an explicit width and height. */
    @Test
    void zoomIsAnAnchorModeValue() {
        PadesVisibleSignaturePlacementDto zoomed = coordinates();
        zoomed.setZoom(50f);
        assertFalse(valid(zoomed));
    }

    @Test
    void theViolationNamesThePlacementModeMistake() {
        PadesVisibleSignaturePlacementDto mixed = namedField();
        mixed.setPage(1);
        assertEquals(1, VALIDATOR.validate(mixed).size());
        assertTrue(VALIDATOR.validate(mixed).iterator().next().getMessage().contains("addressing mode"));
    }

    @Test
    void aPageBelowOneIsRejected() {
        PadesVisibleSignaturePlacementDto zeroPage = coordinates();
        zeroPage.setPage(0);
        assertFalse(valid(zeroPage));
    }

    @Test
    void aNegativeOriginIsRejected() {
        PadesVisibleSignaturePlacementDto negativeOrigin = coordinates();
        negativeOrigin.setOriginX(-1f);
        assertFalse(valid(negativeOrigin));
    }

    @Test
    void aZeroWidthIsRejected() {
        PadesVisibleSignaturePlacementDto zeroWidth = coordinates();
        zeroWidth.setWidth(0f);
        assertFalse(valid(zeroWidth));
    }

    @Test
    void aZoomAboveOneThousandPercentIsRejected() {
        PadesVisibleSignaturePlacementDto overZoomed = anchor();
        overZoomed.setZoom(1001f);
        assertFalse(valid(overZoomed));
    }

    @Test
    void aZeroZoomIsRejected() {
        PadesVisibleSignaturePlacementDto zeroZoom = anchor();
        zeroZoom.setZoom(0f);
        assertFalse(valid(zeroZoom));
    }

    @Test
    void aFieldIdBeyondTheCapIsRejected() {
        PadesVisibleSignaturePlacementDto longFieldId = new PadesVisibleSignaturePlacementDto();
        longFieldId.setFieldId("f".repeat(257));
        assertFalse(valid(longFieldId));
    }

    /** A mixture belongs to no single field, so the violation stays on the object instead of blaming one. */
    @Test
    void aMixtureViolationStaysOnTheObject() {
        PadesVisibleSignaturePlacementDto zoomedCoordinates = coordinates();
        zoomedCoordinates.setZoom(50f);

        Set<ConstraintViolation<PadesVisibleSignaturePlacementDto>> violations = VALIDATOR.validate(zoomedCoordinates);

        assertEquals(1, violations.size());
        ConstraintViolation<PadesVisibleSignaturePlacementDto> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("addressing mode"), violation.getMessage());
        assertEquals("", violation.getPropertyPath().toString());
    }

    /** A blank fieldId is non-null, so it would put the object into named-field mode and reject a plain page mode. */
    @Test
    void aBlankFieldIdIsRejected() {
        PadesVisibleSignaturePlacementDto blankFieldId = new PadesVisibleSignaturePlacementDto();
        blankFieldId.setFieldId("  ");

        Set<ConstraintViolation<PadesVisibleSignaturePlacementDto>> violations = VALIDATOR.validate(blankFieldId);

        assertEquals(1, violations.size());
        assertEquals("fieldId", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void aNamedFieldCombinedWithAWholeOtherModeIsRejectedAtTheFieldId() {
        PadesVisibleSignaturePlacementDto namedFieldAndCoordinates = coordinates();
        namedFieldAndCoordinates.setFieldId("Signature1");

        Set<ConstraintViolation<PadesVisibleSignaturePlacementDto>> violations = VALIDATOR
                .validate(namedFieldAndCoordinates);

        assertEquals(1, violations.size());
        assertEquals("fieldId", violations.iterator().next().getPropertyPath().toString());
    }
}
