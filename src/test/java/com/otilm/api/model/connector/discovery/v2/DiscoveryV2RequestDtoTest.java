package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscoveryV2RequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void resourceAttributesKeysUseWireCodes() throws Exception {
        DiscoveryInitiateRequestDto dto = new DiscoveryInitiateRequestDto();
        dto.setRunId(UUID.randomUUID());
        dto.setResources(List.of(Resource.CERTIFICATE));
        dto.setResourceAttributes(Map.of(Resource.CERTIFICATE, List.of()));
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"certificates\""));
        DiscoveryInitiateRequestDto back = mapper.readValue(json, DiscoveryInitiateRequestDto.class);
        assertTrue(back.getResourceAttributes().containsKey(Resource.CERTIFICATE));
    }

    @Test
    void runRequestRoundTripsBaseFields() throws Exception {
        DiscoveryRunRequestDto dto = new DiscoveryRunRequestDto();
        UUID runId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        dto.setRunId(runId);

        MetadataAttributeV3 metaAttribute = new MetadataAttributeV3();
        metaAttribute.setUuid("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        metaAttribute.setName("cursor");
        metaAttribute.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel("Cursor");
        metaAttribute.setProperties(properties);
        metaAttribute.setContent(List.of(new StringAttributeContentV3("abc123")));
        dto.setMeta(List.of(metaAttribute));

        dto.setResources(List.of(Resource.CRYPTOGRAPHIC_KEY));
        dto.setAttributes(List.of());
        dto.setResourceAttributes(Map.of(Resource.CRYPTOGRAPHIC_KEY, List.of()));

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"runId\":\"11111111-1111-1111-1111-111111111111\""));
        assertTrue(json.contains("\"name\":\"cursor\""));
        assertTrue(json.contains("\"resources\":[\"keys\"]"));
        assertTrue(json.contains("\"resourceAttributes\":{\"keys\""));

        DiscoveryRunRequestDto back = mapper.readValue(json, DiscoveryRunRequestDto.class);
        assertEquals(runId, back.getRunId());
        assertEquals(List.of(Resource.CRYPTOGRAPHIC_KEY), back.getResources());
        assertEquals(1, back.getMeta().size());
        MetadataAttributeV3 backMeta = assertInstanceOf(MetadataAttributeV3.class, back.getMeta().get(0));
        assertEquals("cursor", backMeta.getName());
        assertEquals("abc123", ((StringAttributeContentV3) backMeta.getContent().get(0)).getData());
        assertTrue(back.getAttributes().isEmpty());
        assertTrue(back.getResourceAttributes().containsKey(Resource.CRYPTOGRAPHIC_KEY));
    }

    @Test
    void scopedRequestHasNoNameField() throws Exception {
        DiscoveryRunRequestDto dto = new DiscoveryRunRequestDto();
        dto.setRunId(UUID.randomUUID());
        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("\"name\""));
    }

    @Test
    void initiateRequestRoundTripsResources() throws Exception {
        DiscoveryInitiateRequestDto dto = new DiscoveryInitiateRequestDto();
        UUID runId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        dto.setRunId(runId);
        dto.setResources(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY));

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"resources\":[\"certificates\",\"keys\"]"));

        DiscoveryInitiateRequestDto back = mapper.readValue(json, DiscoveryInitiateRequestDto.class);
        assertEquals(runId, back.getRunId());
        assertEquals(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY), back.getResources());
    }

    @Test
    void nullResourceElementIsRejected() {
        // @NotEmpty only rejects a missing or empty list, so [null] would otherwise pass while the
        // contract requires every requested resource to be validated as supported.
        DiscoveryInitiateRequestDto onlyNull = new DiscoveryInitiateRequestDto();
        onlyNull.setRunId(UUID.randomUUID());
        onlyNull.setResources(Collections.singletonList(null));

        DiscoveryInitiateRequestDto trailingNull = new DiscoveryInitiateRequestDto();
        trailingNull.setRunId(UUID.randomUUID());
        trailingNull.setResources(Arrays.asList(Resource.CERTIFICATE, null));

        // A container-element constraint reports the element node, so the path a caller sees for a
        // null entry is resources[i].<list element>, not resources[i].
        Set<ConstraintViolation<DiscoveryInitiateRequestDto>> onlyNullViolations = VALIDATOR.validate(onlyNull);
        assertTrue(
                onlyNullViolations
                        .stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("resources[0].<list element>")),
                "a resources list holding only null must be rejected");

        Set<ConstraintViolation<DiscoveryInitiateRequestDto>> trailingNullViolations = VALIDATOR.validate(trailingNull);
        assertTrue(
                trailingNullViolations
                        .stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("resources[1].<list element>")),
                "a null alongside a real resource type must still be rejected");
    }

    @Test
    void initiateRequestWithRealResourcesIsValid() {
        DiscoveryInitiateRequestDto dto = new DiscoveryInitiateRequestDto();
        dto.setRunId(UUID.randomUUID());
        dto.setResources(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY));

        assertTrue(VALIDATOR.validate(dto).isEmpty(), "a fully populated initiate request must be valid");
    }

    /**
     * The resource set is run configuration, so every lifecycle call replays it. A connector holding nothing between
     * calls has no other way to know a resumed run's scope, and {@code resourceAttributes} is not a substitute: Core
     * omits from it any resource that declares no attributes of its own.
     */
    @Test
    void everyLifecycleRequestCarriesTheResourceSet() {
        DiscoveryRunRequestDto withoutResources = new DiscoveryRunRequestDto();
        withoutResources.setRunId(UUID.randomUUID());

        assertTrue(
                VALIDATOR
                        .validate(withoutResources)
                        .stream()
                        .anyMatch(v -> v.getPropertyPath().toString().equals("resources")),
                "a status/stop/resume/cancel body without resources must be rejected, not accepted and left "
                        + "for the connector to guess the run's scope");

        withoutResources.setResources(List.of(Resource.CERTIFICATE));
        assertTrue(VALIDATOR.validate(withoutResources).isEmpty(),
                "a lifecycle body carrying runId and resources must be valid on its own");
    }

    @Test
    void drainRequestMaxBytesAboveTheTransportCapIsRejected() {
        // The published schema maximum and the bean-validation bound both come from MAX_BYTES_CAP,
        // so a value the document forbids is one the validator refuses too.
        DiscoveryDrainRequestDto atCap = new DiscoveryDrainRequestDto();
        atCap.setRunId(UUID.randomUUID());
        atCap.setResources(List.of(Resource.CERTIFICATE));
        atCap.setMaxBytes(DiscoveryDrainRequestDto.MAX_BYTES_CAP);

        DiscoveryDrainRequestDto overCap = new DiscoveryDrainRequestDto();
        overCap.setRunId(UUID.randomUUID());
        overCap.setResources(List.of(Resource.CERTIFICATE));
        overCap.setMaxBytes(DiscoveryDrainRequestDto.MAX_BYTES_CAP + 1);

        assertTrue(VALIDATOR.validate(atCap).isEmpty(), "maxBytes exactly at the cap must be accepted");
        assertTrue(
                VALIDATOR.validate(overCap).stream().anyMatch(v -> v.getPropertyPath().toString().equals("maxBytes")),
                "maxBytes above the transport cap must be rejected, not merely documented as too large");
    }

    @Test
    void drainRequestNegativeCursorAndNonPositiveLimitsAreRejected() {
        DiscoveryDrainRequestDto dto = new DiscoveryDrainRequestDto();
        dto.setRunId(UUID.randomUUID());
        dto.setAfterSequence(-1L);
        dto.setMaxItems(0);
        dto.setMaxBytes(0L);

        Set<String> paths = VALIDATOR
                .validate(dto)
                .stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
        assertTrue(paths.contains("afterSequence"), "a negative cursor must be rejected");
        assertTrue(paths.contains("maxItems"), "a non-positive maxItems must be rejected");
        assertTrue(paths.contains("maxBytes"), "a non-positive maxBytes must be rejected");
    }

    @Test
    void drainRequestRoundTripsCursorAndLimits() throws Exception {
        DiscoveryDrainRequestDto dto = new DiscoveryDrainRequestDto();
        dto.setRunId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        dto.setAfterSequence(0L);
        dto.setMaxItems(100);
        dto.setMaxBytes(65536L);

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"afterSequence\":0"));
        assertTrue(json.contains("\"maxItems\":100"));
        assertTrue(json.contains("\"maxBytes\":65536"));

        DiscoveryDrainRequestDto back = mapper.readValue(json, DiscoveryDrainRequestDto.class);
        assertEquals(0L, back.getAfterSequence());
        assertEquals(100, back.getMaxItems());
        assertEquals(65536L, back.getMaxBytes());
    }

    @Test
    void toStringIncludesRunIdButExcludesAttributeValues() {
        // DiscoveryRunRequestDto carries no fields of its own, so its toString is entirely
        // whatever DiscoveryV2ScopedRequestDto's own @ToString(callSuper = true) chain produces.
        // Without a real @ToString on the base, callSuper falls back to Object.toString() and
        // runId never appears at all -- exactly the bug this pins against.
        DiscoveryRunRequestDto dto = new DiscoveryRunRequestDto();
        UUID runId = UUID.fromString("55555555-5555-5555-5555-555555555555");
        dto.setRunId(runId);
        dto
                .setAttributes(List
                        .of(new RequestAttributeV3(UUID.randomUUID(), "targetPassword", AttributeContentType.STRING,
                                List.of(new StringAttributeContentV3("super-secret-target-password")))));

        String str = dto.toString();

        assertTrue(str.contains(runId.toString()),
                "toString must include runId, the one field worth correlating log lines by: " + str);
        assertFalse(str.contains("super-secret-target-password"),
                "toString must not leak run-level attribute values, which can carry target credentials: " + str);
    }

    @Test
    void streamRequestRoundTripsCursor() throws Exception {
        DiscoveryStreamRequestDto dto = new DiscoveryStreamRequestDto();
        dto.setRunId(UUID.fromString("44444444-4444-4444-4444-444444444444"));
        dto.setAfterSequence(42L);

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"afterSequence\":42"));

        DiscoveryStreamRequestDto back = mapper.readValue(json, DiscoveryStreamRequestDto.class);
        assertEquals(42L, back.getAfterSequence());
    }
}
