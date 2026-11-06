package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.parameters.pades.PadesSignatureParametersDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignatureDto;
import com.otilm.api.model.common.signature.parameters.pades.PadesVisibleSignaturePlacementDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComputeDtbsRequestDtoTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static ComputeDtbsRequestDto requestFor(SignatureFamily family) {
        ComputeDtbsRequestDto request = new ComputeDtbsRequestDto();
        request.setFamily(family);
        request.setDocument(new InlineDocumentTransferDto(new byte[]{1, 2, 3}));
        request.setSignerCertificateChain(List.of(new byte[]{9}));
        request.setSigningTime(OffsetDateTime.parse("2026-08-11T10:15:30Z"));
        request.setSignatureAlgorithm(SignatureAlgorithm.SHA256_WITH_RSA);
        request.setFormattingAttributes(List.of());
        return request;
    }

    @Test
    void everyFamilyRoundTripsThroughTheOneRequest() throws Exception {
        for (SignatureFamily family : SignatureFamily.values()) {
            ComputeDtbsRequestDto original = requestFor(family);

            ComputeDtbsRequestDto decoded = mapper
                    .readValue(mapper.writeValueAsString(original), ComputeDtbsRequestDto.class);

            assertEquals(family, decoded.getFamily());
            assertEquals(original.getSigningTime(), decoded.getSigningTime());
            assertEquals(original.getDocument(), decoded.getDocument());
        }
    }

    @Test
    void anUnknownFamilyIsRejected() {
        String json = "{\"family\":\"pkcs7\",\"formattingAttributes\":[]}";
        assertThrows(JsonMappingException.class, () -> mapper.readValue(json, ComputeDtbsRequestDto.class));
    }

    @Test
    void aMissingFamilyFailsValidation() {
        ComputeDtbsRequestDto request = requestFor(null);

        Set<ConstraintViolation<ComputeDtbsRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size());
        assertEquals("family", violations.iterator().next().getPropertyPath().toString());
    }

    /** A profile with no defaults and a request with no parameters leave the field absent. */
    @Test
    void theParametersAreOptional() {
        ComputeDtbsRequestDto request = requestFor(SignatureFamily.PADES);
        assertNull(request.getSignatureParameters());
        assertTrue(VALIDATOR.validate(request).isEmpty());
    }

    @Test
    void theAbsentParametersAreLeftOffTheWire() throws Exception {
        String json = mapper.writeValueAsString(requestFor(SignatureFamily.PADES));

        assertFalse(json.contains("signatureParameters"), json);
    }

    @Test
    void theParametersRoundTripThroughTheRequest() throws Exception {
        ComputeDtbsRequestDto request = requestFor(SignatureFamily.PADES);
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setReason("Contract approval");
        request.setSignatureParameters(parameters);

        ComputeDtbsRequestDto decoded = mapper
                .readValue(mapper.writeValueAsString(request), ComputeDtbsRequestDto.class);

        PadesSignatureParametersDto pades = assertInstanceOf(PadesSignatureParametersDto.class,
                decoded.getSignatureParameters());
        assertEquals("Contract approval", pades.getReason());
    }

    /** The parameters object stays self-describing wherever it travels, so it carries its own discriminator. */
    @Test
    void theParametersCarryTheirOwnFamilyOnTheWire() throws Exception {
        ComputeDtbsRequestDto request = requestFor(SignatureFamily.PADES);
        request.setSignatureParameters(new PadesSignatureParametersDto());

        String json = mapper.writeValueAsString(request);

        assertEquals(2, json.split("\"family\":\"pades\"", -1).length - 1, json);
    }

    /** A connector reads the parameters as its own family's. */
    @Test
    void parametersOfAnotherFamilyFailValidation() {
        ComputeDtbsRequestDto request = requestFor(SignatureFamily.XADES);
        request.setSignatureParameters(new PadesSignatureParametersDto());

        Set<ConstraintViolation<ComputeDtbsRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<ComputeDtbsRequestDto> violation = violations.iterator().next();
        assertEquals("signatureParameters", violation.getPropertyPath().toString());
        assertEquals("signatureParameters must belong to the request's family", violation.getMessage());
    }

    /** The parameters union resolves on its family alone. */
    @Test
    void aParametersObjectWithoutItsFamilyIsRejected() {
        String json = "{\"family\":\"pades\",\"signatureParameters\":{\"reason\":\"x\"}}";
        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(json, ComputeDtbsRequestDto.class));
    }

    @Test
    void validationCascadesIntoTheParameters() {
        ComputeDtbsRequestDto request = requestFor(SignatureFamily.PADES);
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setReason("r".repeat(513));
        request.setSignatureParameters(parameters);

        Set<ConstraintViolation<ComputeDtbsRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size());
        assertEquals("signatureParameters.reason", violations.iterator().next().getPropertyPath().toString());
    }

    /**
     * A {@code @Valid} missing on any nested container silences every constraint below it, so the request validates
     * clean with a page number no PDF has.
     */
    @Test
    void validationCascadesToTheDeepestParameter() {
        ComputeDtbsRequestDto request = requestFor(SignatureFamily.PADES);
        PadesVisibleSignaturePlacementDto placement = new PadesVisibleSignaturePlacementDto();
        placement.setPage(0);
        PadesVisibleSignatureDto visibleSignature = new PadesVisibleSignatureDto();
        visibleSignature.setPlacement(placement);
        PadesSignatureParametersDto parameters = new PadesSignatureParametersDto();
        parameters.setVisibleSignature(visibleSignature);
        request.setSignatureParameters(parameters);

        Set<ConstraintViolation<ComputeDtbsRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size());
        assertEquals("signatureParameters.visibleSignature.placement.page",
                violations.iterator().next().getPropertyPath().toString());
    }
}
