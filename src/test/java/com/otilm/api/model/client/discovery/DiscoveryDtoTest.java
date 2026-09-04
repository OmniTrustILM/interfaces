package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the discovery v2 additions to {@link DiscoveryDto} and, more importantly, the compatibility guarantee they are
 * built on: a request body written against the v1 shape is still a valid request body that deserializes and violates no
 * constraint.
 */
class DiscoveryDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private final ObjectMapper mapper = new ObjectMapper();

    /** Exactly the fields a v1 consumer sends today, and nothing else. */
    private static final String V1_ONLY_PAYLOAD = """
            {
              "name": "nightly-scan",
              "attributes": [],
              "customAttributes": [],
              "connectorUuid": "c2f685d4-6a3e-11ec-90d6-0242ac120003",
              "kind": "IP-HostName",
              "triggers": ["b9b09548-a97c-4c6a-a06a-e4ee6fc2da98"]
            }""";

    @Test
    void roundTripsResourcesAndResourceAttributes() throws Exception {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName("keyStoreType");
        DiscoveryDto dto = new DiscoveryDto();
        dto.setName("nightly-scan");
        dto.setConnectorUuid("c2f685d4-6a3e-11ec-90d6-0242ac120003");
        dto.setKind("IP-HostName");
        dto.setResources(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY));
        dto.setResourceAttributes(Map.of(Resource.CRYPTOGRAPHIC_KEY, List.of(attribute)));

        String json = mapper.writeValueAsString(dto);
        DiscoveryDto back = mapper.readValue(json, DiscoveryDto.class);

        assertEquals(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY), back.getResources());
        assertTrue(back.getResourceAttributes().containsKey(Resource.CRYPTOGRAPHIC_KEY));
        assertEquals("keyStoreType", back.getResourceAttributes().get(Resource.CRYPTOGRAPHIC_KEY).get(0).getName());
    }

    @Test
    void resourcesAndResourceAttributeKeysUseWireCodesNotEnumNames() throws Exception {
        DiscoveryDto dto = new DiscoveryDto();
        dto.setResources(List.of(Resource.CRYPTOGRAPHIC_KEY));
        dto.setResourceAttributes(Map.of(Resource.CERTIFICATE, List.of()));

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"resources\":[\"keys\"]"), json);
        assertTrue(json.contains("\"resourceAttributes\":{\"certificates\":[]}"), json);
        assertFalse(json.contains("CRYPTOGRAPHIC_KEY"), json);
        assertFalse(json.contains("CERTIFICATE"), json);
    }

    @Test
    void omitsV2FieldsWhenUnset() throws Exception {
        DiscoveryDto dto = new DiscoveryDto();
        dto.setName("nightly-scan");
        dto.setKind("IP-HostName");

        String json = mapper.writeValueAsString(dto);

        // absent, not null: a v1 consumer's request shape is unchanged
        assertFalse(json.contains("resources"), json);
        assertFalse(json.contains("resourceAttributes"), json);
        assertFalse(json.contains("interfaceUuid"), json);
    }

    @Test
    void v1OnlyPayloadStillDeserializesAndValidates() {
        DiscoveryDto dto = assertDoesNotThrow(() -> mapper.readValue(V1_ONLY_PAYLOAD, DiscoveryDto.class),
                "a v1-only create request must still deserialize");

        assertEquals("nightly-scan", dto.getName());
        assertEquals("IP-HostName", dto.getKind());
        assertEquals(1, dto.getTriggers().size());
        assertEquals(UUID.fromString("b9b09548-a97c-4c6a-a06a-e4ee6fc2da98"), dto.getTriggers().get(0));

        // the v2 fields are simply absent, never defaulted into something the caller did not ask for
        assertNull(dto.getResources());
        assertNull(dto.getResourceAttributes());

        // no constraint fires on a body that omits them
        Set<ConstraintViolation<DiscoveryDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.isEmpty(), "a v1-only create request must stay valid; violations: " + violations);
    }

    /**
     * The bean-validated shape rules: @Size skips null, so only an explicit empty list can fire it, and the
     * type-use @NotNull rejects a null element. Everything connector-dependent (v2 requires the field, v1 refuses it)
     * is Core's service boundary, not bean validation.
     */
    @Test
    void explicitlyEmptyResourcesViolatesTheSizeConstraint() {
        DiscoveryDto dto = new DiscoveryDto();
        dto.setResources(List.of());

        Set<ConstraintViolation<DiscoveryDto>> violations = VALIDATOR.validate(dto);
        assertEquals(1, violations.size(),
                "an explicit empty resources list must fail with exactly the @Size violation; got: " + violations);
        assertTrue(violations.iterator().next().getMessage().contains("at least one resource type"),
                "unexpected violation message: " + violations.iterator().next().getMessage());
    }

    @Test
    void nullResourceElementViolatesTheElementConstraint() {
        DiscoveryDto dto = new DiscoveryDto();
        // Arrays.asList, not List.of: the fixture must be able to hold the null the constraint rejects.
        dto.setResources(Arrays.asList(Resource.CERTIFICATE, null));

        Set<ConstraintViolation<DiscoveryDto>> violations = VALIDATOR.validate(dto);
        assertEquals(1, violations.size(),
                "a null element must fail with exactly the element @NotNull violation; got: " + violations);
        assertTrue(violations.iterator().next().getMessage().contains("null resource type"),
                "unexpected violation message: " + violations.iterator().next().getMessage());
    }
}
