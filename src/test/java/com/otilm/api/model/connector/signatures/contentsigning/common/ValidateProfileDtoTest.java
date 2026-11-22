package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidateProfileDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final ProfileAttributeErrorDto FONT_ERROR = new ProfileAttributeErrorDto("font",
            "The font file cannot be parsed");

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void aValidProfileMustNameATransferMode() {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(List.of(), Set.of());

        assertEquals(Set.of("transferModeNamedForValidProfile"), paths(VALIDATOR.validate(response)));
    }

    @Test
    void aValidProfileNamingItsTransferModesIsAccepted() {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(List.of(),
                Set.of(DocumentTransferMode.INLINE));

        assertTrue(VALIDATOR.validate(response).isEmpty());
    }

    @Test
    void aProfileWithErrorsMayNameNoTransferMode() {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(List.of(FONT_ERROR), Set.of());

        assertTrue(VALIDATOR.validate(response).isEmpty());
    }

    /**
     * A Go connector serializes an unset list as null. The client must refuse that null rather than read it as empty.
     */
    @Test
    void aResponseRequiresBothLists() {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(null, null);

        assertEquals(Set.of("errors", "transferModes"), paths(VALIDATOR.validate(response)));
    }

    @Test
    void aResponseRefusesANullError() {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(Collections.singletonList(null),
                Set.of(DocumentTransferMode.INLINE));

        assertEquals(Set.of("errors must not contain null items"), messages(VALIDATOR.validate(response)));
    }

    @Test
    void aResponseRefusesANullTransferMode() {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(List.of(FONT_ERROR),
                Collections.singleton(null));

        assertEquals(Set.of("transferModes must not contain null items"), messages(VALIDATOR.validate(response)));
    }

    @Test
    void anErrorMustNameItsAttributeAndReason() {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(
                List.of(new ProfileAttributeErrorDto(" ", null)), Set.of());

        assertEquals(Set.of("errors[0].attributeName", "errors[0].reason"), paths(VALIDATOR.validate(response)));
    }

    @Test
    void theResponseCarriesTransferModesAsWireCodes() throws Exception {
        ValidateProfileResponseDto response = new ValidateProfileResponseDto(List.of(FONT_ERROR),
                Set.of(DocumentTransferMode.DIGEST_ONLY));

        assertEquals("{\"errors\":[{\"attributeName\":\"font\",\"reason\":\"The font file cannot be parsed\"}],"
                + "\"transferModes\":[\"digestOnly\"]}", mapper.writeValueAsString(response));
    }

    @Test
    void theResponseReadsFromTheWire() throws Exception {
        ValidateProfileResponseDto response = mapper.readValue("""
                { "errors": [ { "attributeName": "font", "reason": "The font file cannot be parsed" } ],
                  "transferModes": [ "inline" ] }
                """, ValidateProfileResponseDto.class);

        assertEquals("font", response.getErrors().getFirst().getAttributeName());
        assertEquals("The font file cannot be parsed", response.getErrors().getFirst().getReason());
        assertEquals(Set.of(DocumentTransferMode.INLINE), response.getTransferModes());
    }

    @Test
    void aRequestNamingWhatARequestMaySetIsValid() {
        assertTrue(VALIDATOR.validate(validRequest()).isEmpty());
    }

    @Test
    void aRequestRequiresItsAllowedRequestAttributes() {
        ValidateProfileRequestDto request = validRequest();
        request.setAllowedRequestAttributes(null);

        assertEquals(Set.of("allowedRequestAttributes"), paths(VALIDATOR.validate(request)));
    }

    @Test
    void aRequestRefusesABlankAllowedName() {
        ValidateProfileRequestDto request = validRequest();
        request.setAllowedRequestAttributes(Set.of(" "));

        Set<String> paths = paths(VALIDATOR.validate(request));

        assertEquals(1, paths.size());
        assertTrue(paths.iterator().next().startsWith("allowedRequestAttributes"), paths.toString());
    }

    @Test
    void aRequestCarriesItsAllowedNamesOverTheWire() {
        JsonNode json = mapper.valueToTree(validRequest());

        assertEquals("pades", json.path("family").asText());
        assertEquals("signatureContext", json.path("allowedRequestAttributes").get(0).asText());
    }

    private static ValidateProfileRequestDto validRequest() {
        ValidateProfileRequestDto request = new ValidateProfileRequestDto();
        request.setFamily("pades");
        request.setFormattingAttributes(List.of());
        request.setAllowedRequestAttributes(Set.of("signatureContext"));
        return request;
    }

    private static <T> Set<String> messages(Set<ConstraintViolation<T>> violations) {
        return violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.toSet());
    }

    private static <T> Set<String> paths(Set<ConstraintViolation<T>> violations) {
        return violations.stream().map(v -> v.getPropertyPath().toString()).collect(Collectors.toSet());
    }
}
