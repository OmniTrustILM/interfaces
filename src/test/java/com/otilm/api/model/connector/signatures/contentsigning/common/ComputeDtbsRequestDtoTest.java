package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.common.signature.SignatureFamily;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        request.setRequestAttributes(List.of());
        return request;
    }

    private static Stream<SignatureFamily> families() {
        return Stream.of(new SignatureFamily("pades"), new SignatureFamily("mrtd"));
    }

    @Test
    void everyFamilyRoundTripsThroughTheOneRequest() throws Exception {
        for (SignatureFamily family : families().toList()) {
            ComputeDtbsRequestDto original = requestFor(family);

            ComputeDtbsRequestDto decoded = mapper
                    .readValue(mapper.writeValueAsString(original), ComputeDtbsRequestDto.class);

            assertEquals(family, decoded.getFamily());
            assertEquals(original.getSigningTime(), decoded.getSigningTime());
            assertEquals(original.getDocument(), decoded.getDocument());
        }
    }

    @Test
    void aMalformedFamilyIsRejected() {
        String json = "{\"family\":\"PKCS#7\",\"formattingAttributes\":[]}";
        assertThrows(JsonMappingException.class, () -> mapper.readValue(json, ComputeDtbsRequestDto.class));
    }

    @Test
    void aMissingFamilyFailsValidation() {
        ComputeDtbsRequestDto request = requestFor(null);

        Set<ConstraintViolation<ComputeDtbsRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size());
        assertEquals("family", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void theRequestAttributesRoundTrip() throws Exception {
        ComputeDtbsRequestDto request = requestFor(new SignatureFamily("pades"));
        request.setRequestAttributes(List.of(reason("Contract approval")));

        ComputeDtbsRequestDto decoded = mapper
                .readValue(mapper.writeValueAsString(request), ComputeDtbsRequestDto.class);

        RequestAttributeV3 attribute = assertInstanceOf(RequestAttributeV3.class,
                decoded.getRequestAttributes().getFirst());
        assertEquals("reason", attribute.getName());
        assertEquals("Contract approval", attribute.getContent().getFirst().getData());
    }

    @Test
    void aMissingRequestAttributesListFailsValidation() {
        ComputeDtbsRequestDto request = requestFor(new SignatureFamily("pades"));
        request.setRequestAttributes(null);

        Set<ConstraintViolation<ComputeDtbsRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size());
        ConstraintViolation<ComputeDtbsRequestDto> violation = violations.iterator().next();
        assertEquals("requestAttributes", violation.getPropertyPath().toString());
        assertEquals("requestAttributes may be an empty list, but must be present", violation.getMessage());
    }

    @Test
    void aNullRequestAttributeIsRejected() {
        ComputeDtbsRequestDto request = requestFor(new SignatureFamily("pades"));
        List<RequestAttribute> withNull = new ArrayList<>();
        withNull.add(null);
        request.setRequestAttributes(withNull);

        Set<ConstraintViolation<ComputeDtbsRequestDto>> violations = VALIDATOR.validate(request);

        assertEquals(1, violations.size());
        assertEquals("requestAttributes must not contain null items", violations.iterator().next().getMessage());
    }

    /** A signing request's values can be personal, such as a signer's reason or contact. They stay out of logs. */
    @Test
    void toStringOmitsTheRequestAttributes() {
        ComputeDtbsRequestDto request = requestFor(new SignatureFamily("pades"));
        request.setRequestAttributes(List.of(reason("private-reason-value")));

        String rendered = request.toString();

        assertFalse(rendered.contains("requestAttributes="), rendered);
        assertFalse(rendered.contains("private-reason-value"), rendered);
    }

    private static RequestAttributeV3 reason(String value) {
        return new RequestAttributeV3(UUID.fromString("22222222-2222-2222-2222-222222222222"), "reason",
                AttributeContentType.STRING, List.of(new StringAttributeContentV3(value)));
    }
}
