package com.otilm.api.model.core.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.connector.v3.certificate.CertificateExtension;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientCertificateRegistrationDtoTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void bothEmpty_failsRfc5280Constraint() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        assertFalse(dto.isSubjectIdentificationProvided(),
                "An empty subject AND empty SAN must fail RFC 5280 §4.1.2.6");
    }

    @Test
    void subjectDnOnly_passes() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=device-7,O=Acme");
        assertTrue(dto.isSubjectIdentificationProvided());
    }

    @Test
    void subjectAltNameOnly_passes_perRfc5280() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectAltName("DNS:device-7.example.com");
        assertTrue(dto.isSubjectIdentificationProvided(),
                "SAN-only registration is valid per RFC 5280 §4.1.2.6 (subjectAltName must be marked critical at issuance)");
    }

    @Test
    void bothPresent_passes() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=device-7");
        dto.setSubjectAltName("DNS:device-7.example.com");
        assertTrue(dto.isSubjectIdentificationProvided());
    }

    @Test
    void csrAttributesOnly_providesIdentity() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setCsrAttributes(List.of(new RequestAttributeV2()));
        assertTrue(dto.isSubjectIdentificationProvided(),
                "structured csrAttributes is an accepted identity source alongside flat subjectDn/subjectAltName");
    }

    @Test
    void csrAttributesEmptyList_doesNotProvideIdentity() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setCsrAttributes(List.of());
        assertFalse(dto.isSubjectIdentificationProvided(),
                "an empty csrAttributes list is not an identity source");
    }

    @Test
    void csrAttributesOnlyNullElements_doesNotProvideIdentity() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setCsrAttributes(Collections.singletonList(null));
        assertFalse(dto.isSubjectIdentificationProvided(),
                "a csrAttributes list of only null elements carries no identity");
    }

    @Test
    void csrAttributesWithFlatFields_violatesMutualExclusion() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=device-7");
        dto.setCsrAttributes(List.of(new RequestAttributeV2()));
        assertFalse(dto.isSingleIdentitySource(),
                "supplying both csrAttributes and a flat identity field is ambiguous and must be rejected");
        assertFalse(mutualExclusionViolations(dto).isEmpty(),
                "the @AssertTrue constraint must surface as a bean-validation violation");
    }

    @Test
    void csrAttributesWithFlatExtensionsOnly_violatesMutualExclusion() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setExtensions(List.of(new CertificateExtension()));
        dto.setCsrAttributes(List.of(new RequestAttributeV2()));
        assertFalse(dto.isSingleIdentitySource(),
                "flat extensions alongside csrAttributes is still the mixed form");
    }

    @Test
    void csrAttributesAlone_satisfiesMutualExclusion() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setCsrAttributes(List.of(new RequestAttributeV2()));
        assertTrue(dto.isSingleIdentitySource());
        assertTrue(mutualExclusionViolations(dto).isEmpty());
    }

    @Test
    void flatFieldsAlone_satisfyMutualExclusion() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=device-7");
        dto.setSubjectAltName("DNS:device-7.example.com");
        assertTrue(dto.isSingleIdentitySource());
        assertTrue(mutualExclusionViolations(dto).isEmpty());
    }

    @Test
    void nullElementCsrAttributesWithFlat_notMixed() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=device-7");
        dto.setCsrAttributes(Collections.singletonList(null));
        assertTrue(dto.isSingleIdentitySource(),
                "a csrAttributes list carrying no real element is not the structured form, so no conflict with flat");
    }

    private static Set<ConstraintViolation<ClientCertificateRegistrationDto>> mutualExclusionViolations(
            ClientCertificateRegistrationDto dto) {
        return VALIDATOR.validate(dto).stream()
                .filter(v -> v.getPropertyPath().toString().equals("singleIdentitySource"))
                .collect(Collectors.toSet());
    }

    @Test
    void whitespaceOnlyValues_treatedAsEmpty() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("   ");
        dto.setSubjectAltName("\t\n  ");
        assertFalse(dto.isSubjectIdentificationProvided(),
                "Whitespace-only strings must not satisfy the subject-identification constraint");
    }

    @Test
    void authorizationSecretIsAcceptedOnInputButNeverSerialized() throws Exception {
        ClientCertificateRegistrationDto dto = mapper.readValue(
                "{\"subjectDn\":\"CN=x\",\"authorizationSecret\":\"s3cret-value-1234\"}",
                ClientCertificateRegistrationDto.class);
        assertEquals("s3cret-value-1234", dto.getAuthorizationSecret());

        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("authorizationSecret"), "write-only secret must never serialize back");
        assertFalse(json.contains("s3cret-value-1234"));
        assertTrue(json.contains("subjectDn"), "non-write-only fields must still serialize");
        assertFalse(json.contains("subjectIdentificationProvided"),
                "the @JsonIgnore'd @AssertTrue getter must not serialize");
    }

    @Test
    void toStringOmitsAuthorizationSecret() {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setAuthorizationSecret("s3cret-value-1234");
        assertFalse(dto.toString().contains("s3cret-value-1234"), "authorizationSecret must not appear in toString");
    }

    @Test
    void expiresAtRoundTrips() throws Exception {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=x");
        dto.setExpiresAt(OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        ClientCertificateRegistrationDto back =
                mapper.readValue(mapper.writeValueAsString(dto), ClientCertificateRegistrationDto.class);
        assertEquals(dto.getExpiresAt(), back.getExpiresAt());
    }

    @Test
    void authorizationSecretFormatIsEnforced() {
        assertTrue(secretViolations("abcdefghijkl").isEmpty(), "a 12-char printable secret is valid");
        assertFalse(secretViolations("abcdefghijk").isEmpty(), "an 11-char secret is too short");
        assertFalse(secretViolations("a".repeat(256)).isEmpty(), "a 256-char secret is too long");
        assertFalse(secretViolations("abcdefghijklé").isEmpty(), "a character outside printable ASCII is rejected");
    }

    private static Set<ConstraintViolation<ClientCertificateRegistrationDto>> secretViolations(String secret) {
        ClientCertificateRegistrationDto dto = new ClientCertificateRegistrationDto();
        dto.setSubjectDn("CN=x");
        dto.setAuthorizationSecret(secret);
        return VALIDATOR.validate(dto).stream()
                .filter(v -> v.getPropertyPath().toString().equals("authorizationSecret"))
                .collect(Collectors.toSet());
    }
}
