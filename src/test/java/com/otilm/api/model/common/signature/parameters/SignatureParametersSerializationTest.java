package com.otilm.api.model.common.signature.parameters;

import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.parameters.pades.CommitmentType;
import com.otilm.api.model.common.signature.parameters.pades.HorizontalAlignment;
import com.otilm.api.model.common.signature.parameters.pades.ImageScaling;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureParametersDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureScope;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignatureDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignaturePlacementDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignatureStyleDto;
import com.otilm.api.model.common.signature.parameters.pades.TextHorizontalAlignment;
import com.otilm.api.model.common.signature.parameters.pades.TextPosition;
import com.otilm.api.model.common.signature.parameters.pades.TextVerticalAlignment;
import com.otilm.api.model.common.signature.parameters.pades.TextWrapping;
import com.otilm.api.model.common.signature.parameters.pades.VerticalAlignment;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.Validator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureParametersSerializationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();

    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final byte[] STAMP = {(byte) 0x89, 'P', 'N', 'G'};

    /** Addressing modes are mutually exclusive, so "fully populated" is every field of one legal mode. */
    private static PadesSignatureParametersDto fullyPopulatedInCoordinateMode() {
        PadesVisibleSignaturePlacementDto placement = new PadesVisibleSignaturePlacementDto();
        placement.setPage(3);
        placement.setOriginX(20f);
        placement.setOriginY(30f);
        placement.setWidth(180f);
        placement.setHeight(60f);
        placement.setRotation(90);

        PadesVisibleSignatureStyleDto style = new PadesVisibleSignatureStyleDto();
        style.setTextColor("#171717");
        style.setTextBackgroundColor("#FFFFFF");
        style.setBackgroundColor("#F5F5F5");
        style.setPadding(5);
        style.setTextWrapping(TextWrapping.FILL_BOX_AND_LINEBREAK);
        style.setTextPosition(TextPosition.RIGHT);
        style.setTextHorizontalAlignment(TextHorizontalAlignment.CENTER);
        style.setTextVerticalAlignment(TextVerticalAlignment.MIDDLE);
        style.setImageScaling(ImageScaling.ZOOM_AND_CENTER);
        style.setDpi(300);

        PadesVisibleSignatureDto visibleSignature = new PadesVisibleSignatureDto();
        visibleSignature.setVisible(true);
        visibleSignature.setText("Signed by Jane Doe");
        visibleSignature.setImage(STAMP);
        visibleSignature.setImageMimeType("image/png");
        visibleSignature.setPlacement(placement);
        visibleSignature.setStyle(style);

        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setReason("Contract approval");
        parameters.setLocation("Prague");
        parameters.setContactInfo("jane.doe@example.com");
        parameters.setSignerName("Jane Doe");
        parameters.setSignatureScope(PadesSignatureScope.CERTIFICATION);
        parameters.setCommitmentType(CommitmentType.PROOF_OF_APPROVAL);
        parameters.setClaimedRoles(List.of("Head of Legal", "Board member"));
        parameters.setVisibleSignature(visibleSignature);
        return parameters;
    }

    @Test
    void theFullyPopulatedParametersSurviveAJavaSerializationRoundTrip() throws Exception {
        PadesSignatureParametersDto original = fullyPopulatedInCoordinateMode();
        assertTrue(VALIDATOR.validate(original).isEmpty(), "the fixture is not a legal parameters object");

        PadesSignatureParametersDto restored = assertInstanceOf(PadesSignatureParametersDto.class, roundTrip(original));

        assertEquals(SignatureFamily.PADES, restored.getFamily());
        assertEquals("Contract approval", restored.getReason());
        assertEquals("Prague", restored.getLocation());
        assertEquals("jane.doe@example.com", restored.getContactInfo());
        assertEquals("Jane Doe", restored.getSignerName());
        assertEquals(PadesSignatureScope.CERTIFICATION, restored.getSignatureScope());
        assertEquals(CommitmentType.PROOF_OF_APPROVAL, restored.getCommitmentType());
        assertEquals(List.of("Head of Legal", "Board member"), restored.getClaimedRoles());

        PadesVisibleSignatureDto visibleSignature = restored.getVisibleSignature();
        assertEquals(Boolean.TRUE, visibleSignature.getVisible());
        assertEquals("Signed by Jane Doe", visibleSignature.getText());
        assertArrayEquals(STAMP, visibleSignature.getImage());
        assertEquals("image/png", visibleSignature.getImageMimeType());

        PadesVisibleSignaturePlacementDto placement = visibleSignature.getPlacement();
        assertEquals(3, placement.getPage());
        assertEquals(20f, placement.getOriginX());
        assertEquals(30f, placement.getOriginY());
        assertEquals(180f, placement.getWidth());
        assertEquals(60f, placement.getHeight());
        assertEquals(90, placement.getRotation());

        PadesVisibleSignatureStyleDto style = visibleSignature.getStyle();
        assertEquals("#171717", style.getTextColor());
        assertEquals("#FFFFFF", style.getTextBackgroundColor());
        assertEquals("#F5F5F5", style.getBackgroundColor());
        assertEquals(5, style.getPadding());
        assertEquals(TextWrapping.FILL_BOX_AND_LINEBREAK, style.getTextWrapping());
        assertEquals(TextPosition.RIGHT, style.getTextPosition());
        assertEquals(TextHorizontalAlignment.CENTER, style.getTextHorizontalAlignment());
        assertEquals(TextVerticalAlignment.MIDDLE, style.getTextVerticalAlignment());
        assertEquals(ImageScaling.ZOOM_AND_CENTER, style.getImageScaling());
        assertEquals(300, style.getDpi());
    }

    @Test
    void theAnchorAddressedPlacementSurvivesAJavaSerializationRoundTrip() throws Exception {
        PadesVisibleSignaturePlacementDto original = new PadesVisibleSignaturePlacementDto();
        original.setAlignmentHorizontal(HorizontalAlignment.RIGHT);
        original.setAlignmentVertical(VerticalAlignment.BOTTOM);
        original.setZoom(75f);

        PadesVisibleSignaturePlacementDto restored = (PadesVisibleSignaturePlacementDto) roundTrip(original);

        assertEquals(HorizontalAlignment.RIGHT, restored.getAlignmentHorizontal());
        assertEquals(VerticalAlignment.BOTTOM, restored.getAlignmentVertical());
        assertEquals(75f, restored.getZoom());
    }

    @Test
    void theNamedFieldPlacementSurvivesAJavaSerializationRoundTrip() throws Exception {
        PadesVisibleSignaturePlacementDto original = new PadesVisibleSignaturePlacementDto();
        original.setFieldId("Signature1");

        assertEquals("Signature1", ((PadesVisibleSignaturePlacementDto) roundTrip(original)).getFieldId());
    }

    private static Serializable roundTrip(Serializable original) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(original);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (Serializable) in.readObject();
        }
    }
}
