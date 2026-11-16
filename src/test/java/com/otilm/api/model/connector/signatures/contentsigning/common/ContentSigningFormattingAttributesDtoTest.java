package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentSigningFormattingAttributesDtoTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String SPLIT_LISTING = """
            {
              "profileAttributes": [
                { "uuid": "11111111-1111-1111-1111-111111111111", "name": "font", "type": "data",
                  "contentType": "file", "version": 3 }
              ],
              "requestAttributes": [
                { "uuid": "22222222-2222-2222-2222-222222222222", "name": "reason", "type": "data",
                  "contentType": "string", "version": 3,
                  "properties": { "label": "Reason", "group": "signatureContext" } }
              ]
            }
            """;

    @Test
    void decodesBothListsWithTheRequestAttributesGroup() throws Exception {
        ContentSigningFormattingAttributesDto listing = MAPPER
                .readValue(SPLIT_LISTING, ContentSigningFormattingAttributesDto.class);

        assertEquals("font", listing.getProfileAttributes().getFirst().getName());
        BaseAttribute reason = listing.getRequestAttributes().getFirst();
        assertEquals("reason", reason.getName());
        assertEquals("signatureContext", MAPPER.valueToTree(reason).path("properties").path("group").asText());
    }

    @Test
    void decodesAnAbsentListAsEmpty() throws Exception {
        ContentSigningFormattingAttributesDto listing = MAPPER
                .readValue("{\"profileAttributes\":[]}", ContentSigningFormattingAttributesDto.class);

        assertEquals(List.of(), listing.getRequestAttributes());
    }

    @Test
    void refusesANullList() {
        ContentSigningFormattingAttributesDto listing = new ContentSigningFormattingAttributesDto(new ArrayList<>(),
                null);

        Set<ConstraintViolation<ContentSigningFormattingAttributesDto>> violations = VALIDATOR.validate(listing);

        assertEquals("requestAttributes", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void refusesANullDefinition() {
        List<BaseAttribute> withNull = new ArrayList<>();
        withNull.add(null);
        ContentSigningFormattingAttributesDto listing = new ContentSigningFormattingAttributesDto(withNull,
                new ArrayList<>());

        Set<ConstraintViolation<ContentSigningFormattingAttributesDto>> violations = VALIDATOR.validate(listing);

        assertTrue(
                violations.stream().anyMatch(v -> v.getPropertyPath().toString().startsWith("profileAttributes[0]")));
    }
}
