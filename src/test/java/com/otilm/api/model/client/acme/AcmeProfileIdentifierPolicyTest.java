package com.otilm.api.model.client.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.core.acme.AcmeIdentifierAuthorizationMode;
import com.otilm.api.model.core.acme.AcmeIdentifierMatchType;
import com.otilm.api.model.core.acme.AcmePreauthorizedIdentifierDto;
import com.otilm.api.model.core.acme.AcmeProfileDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcmeProfileIdentifierPolicyTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void theEnumsTravelAsTheirCodes() throws Exception {
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        request.setIdentifierAuthorizationMode(AcmeIdentifierAuthorizationMode.PREAUTHORIZED_ONLY);
        request.setPreauthorizedIdentifiers(List.of(entry("apps.example.com", AcmeIdentifierMatchType.SUBDOMAIN)));

        String json = mapper.writeValueAsString(request);

        assertTrue(json.contains("\"identifierAuthorizationMode\":\"preauthorizedOnly\""));
        assertTrue(json.contains("\"matchType\":\"subdomain\""));
        assertFalse(json.contains("PREAUTHORIZED_ONLY"), "the enum constant name must not reach the wire");
        assertFalse(json.contains("SUBDOMAIN"));
    }

    @Test
    void aPolicyRoundTrips() throws Exception {
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        request.setIdentifierAuthorizationMode(AcmeIdentifierAuthorizationMode.PREAUTHORIZED_OR_CHALLENGE);
        AcmePreauthorizedIdentifierDto wildcarded = entry("apps.example.com", AcmeIdentifierMatchType.SUBDOMAIN);
        wildcarded.setAllowWildcard(true);
        request
                .setPreauthorizedIdentifiers(
                        List.of(entry("server01.example.com", AcmeIdentifierMatchType.EXACT), wildcarded));

        AcmeProfileRequestDto back = mapper.readValue(mapper.writeValueAsString(request), AcmeProfileRequestDto.class);

        assertEquals(request.getIdentifierAuthorizationMode(), back.getIdentifierAuthorizationMode());
        assertEquals(request.getPreauthorizedIdentifiers(), back.getPreauthorizedIdentifiers());
    }

    @Test
    void theWildcardFlagDefaultsToFalseAndThePolicyToEmpty() {
        assertFalse(new AcmePreauthorizedIdentifierDto().isAllowWildcard());
        assertEquals(List.of(), new AcmeProfileRequestDto().getPreauthorizedIdentifiers());
        assertEquals(List.of(), new AcmeProfileEditRequestDto().getPreauthorizedIdentifiers());
        assertEquals(List.of(), new AcmeProfileDto().getPreauthorizedIdentifiers());
    }

    @Test
    void preauthorizedOnlyWithNoEntriesIsRefused() {
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        request.setIdentifierAuthorizationMode(AcmeIdentifierAuthorizationMode.PREAUTHORIZED_ONLY);

        assertFalse(request.isPreauthorizedOnlyBackedByEntries(),
                "the mode refuses every identifier it does not cover, so with no entries it refuses every order");
        assertFalse(modeViolations(request).isEmpty(), "the constraint must surface as a bean-validation violation");
    }

    @Test
    void preauthorizedOnlyWithAnEntryIsAccepted() {
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        request.setIdentifierAuthorizationMode(AcmeIdentifierAuthorizationMode.PREAUTHORIZED_ONLY);
        request.setPreauthorizedIdentifiers(List.of(entry("server01.example.com", AcmeIdentifierMatchType.EXACT)));

        assertTrue(request.isPreauthorizedOnlyBackedByEntries());
        assertTrue(modeViolations(request).isEmpty());
    }

    @Test
    void anEmptyPolicyIsFineInEveryOtherMode() {
        AcmeProfileRequestDto orChallenge = new AcmeProfileRequestDto();
        orChallenge.setIdentifierAuthorizationMode(AcmeIdentifierAuthorizationMode.PREAUTHORIZED_OR_CHALLENGE);

        assertTrue(orChallenge.isPreauthorizedOnlyBackedByEntries());
        // A profile that names neither, which is every profile that exists before this ships.
        assertTrue(new AcmeProfileRequestDto().isPreauthorizedOnlyBackedByEntries());
    }

    @Test
    void anEntryWithoutAValueOrMatchTypeIsRefused() {
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        request.setName("policy");
        request.setPreauthorizedIdentifiers(List.of(new AcmePreauthorizedIdentifierDto()));

        Set<String> paths = VALIDATOR
                .validate(request)
                .stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(paths.contains("preauthorizedIdentifiers[0].value"), "@Valid must cascade into the entries");
        assertTrue(paths.contains("preauthorizedIdentifiers[0].matchType"));
    }

    @Test
    void theConstraintGetterDoesNotReachTheWire() throws Exception {
        String json = mapper.writeValueAsString(new AcmeProfileRequestDto());

        assertFalse(json.contains("preauthorizedOnlyBackedByEntries"));
    }

    @Test
    void thePolicyAppearsInToString() {
        AcmeProfileEditRequestDto request = new AcmeProfileEditRequestDto();
        request.setIdentifierAuthorizationMode(AcmeIdentifierAuthorizationMode.PREAUTHORIZED_ONLY);
        request.setPreauthorizedIdentifiers(List.of(entry("server01.example.com", AcmeIdentifierMatchType.EXACT)));

        assertTrue(request.toString().contains("server01.example.com"));
        assertTrue(request.toString().contains("PREAUTHORIZED_ONLY"));
    }

    private static AcmePreauthorizedIdentifierDto entry(String value, AcmeIdentifierMatchType matchType) {
        AcmePreauthorizedIdentifierDto dto = new AcmePreauthorizedIdentifierDto();
        dto.setValue(value);
        dto.setMatchType(matchType);
        return dto;
    }

    private static Set<ConstraintViolation<AcmeProfileRequestDto>> modeViolations(AcmeProfileRequestDto request) {
        return VALIDATOR
                .validate(request)
                .stream()
                .filter(v -> v.getPropertyPath().toString().equals("preauthorizedOnlyBackedByEntries"))
                .collect(Collectors.toSet());
    }
}
