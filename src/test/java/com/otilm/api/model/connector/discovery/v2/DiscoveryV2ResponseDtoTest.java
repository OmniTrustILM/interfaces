package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscoveryV2ResponseDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void statusResponseOmitsAbsentProgress() throws Exception {
        DiscoveryStatusResponseDto dto = new DiscoveryStatusResponseDto();
        dto.setState(DiscoveryRunState.RUNNING);
        dto.setHighestSequence(10L);
        // progress intentionally left unset.

        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("\"progress\""), "absent progress must be omitted, not serialized as null");
        assertTrue(json.contains("\"state\":\"running\""));
        assertTrue(json.contains("\"highestSequence\":10"));

        DiscoveryStatusResponseDto back = mapper.readValue(json, DiscoveryStatusResponseDto.class);
        assertEquals(DiscoveryRunState.RUNNING, back.getState());
        assertEquals(10L, back.getHighestSequence());
        assertNull(back.getProgress());
    }

    @Test
    void resultsResponseCarriesRunWideHighestSequenceUntouched() throws Exception {
        // highestSequence (7) intentionally exceeds the highest item sequence in this page (5):
        // it is run-wide, not page-scoped, and must survive a round trip unchanged.
        DiscoveredItemDto certItem = new DiscoveredItemDto();
        certItem.setSequence(3L);
        certItem.setUniqueRef("cert-ref-1");
        DiscoveredCertificateDto certPayload = new DiscoveredCertificateDto();
        certPayload.setCertificateData("Y2VydC1kYXRh");
        certItem.setPayload(certPayload);

        DiscoveredItemDto keyItem = new DiscoveredItemDto();
        keyItem.setSequence(5L);
        keyItem.setUniqueRef("key-ref-1");
        DiscoveredKeyDto keyPayload = new DiscoveredKeyDto();
        keyPayload.setType(KeyType.PUBLIC_KEY);
        keyPayload.setAlgorithm(KeyAlgorithm.RSA);
        keyItem.setPayload(keyPayload);

        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setItems(List.of(certItem, keyItem));
        dto.setHighestSequence(7L);
        dto.setMore(true);

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"highestSequence\":7"));

        DiscoveryResultsResponseDto back = mapper.readValue(json, DiscoveryResultsResponseDto.class);
        assertEquals(7L, back.getHighestSequence(),
                "highestSequence must remain run-wide, not corrected to the page max");
        assertTrue(back.getMore());
        assertEquals(2, back.getItems().size());
        assertEquals(3L, back.getItems().get(0).getSequence());
        assertEquals(5L, back.getItems().get(1).getSequence());
    }

    @Test
    void resultsResponseDoesNotDefaultItemsToAnEmptyList() {
        // An unset items must stay null so @NotNull catches it. A field initializer would make an
        // omitted items validate as "no discoveries" and let Core stop or drain on a malformed
        // response.
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setHighestSequence(7L);
        dto.setMore(false);

        assertNull(dto.getItems(), "items must not be defaulted to an empty list");

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")),
                "an unset items must fail the @NotNull constraint rather than pass as an empty page");
    }

    @Test
    void resultsResponseOmittingItemsOnTheWireFailsValidation() throws Exception {
        // The connector-response case: a body that simply has no items key at all.
        String json = "{\"highestSequence\":7,\"more\":false}";

        DiscoveryResultsResponseDto dto = mapper.readValue(json, DiscoveryResultsResponseDto.class);
        assertNull(dto.getItems(), "an omitted items must deserialize to null, not to an empty list");

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")),
                "a response omitting items must be rejected, not read as \"no discoveries\"");
    }

    @Test
    void resultsResponseEmptyFinalPageSerializesItemsAsEmptyArray() throws Exception {
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setItems(List.of()); // the empty final page: explicit [], which is what the contract requires
        dto.setHighestSequence(7L);
        dto.setMore(false);

        assertTrue(VALIDATOR.validate(dto).isEmpty(), "an explicit empty page must be valid");

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"items\":[]"), "an empty final page must serialize items as [], not omit it");
        assertTrue(json.contains("\"highestSequence\":7"));
        assertTrue(json.contains("\"more\":false"));

        DiscoveryResultsResponseDto back = mapper.readValue(json, DiscoveryResultsResponseDto.class);
        assertTrue(back.getItems().isEmpty());
        assertEquals(7L, back.getHighestSequence());
        assertFalse(back.getMore());
    }

    @Test
    void resultsResponseMissingItemsFailsNotNullValidation() {
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setItems(null);
        dto.setHighestSequence(7L);
        dto.setMore(false);

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items")),
                "a null items list must fail the @NotNull constraint");
    }

    @Test
    void resultsResponseNullItemElementIsRejected() {
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setItems(Collections.singletonList(null));
        dto.setHighestSequence(7L);
        dto.setMore(false);

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        // A container-element constraint reports the element node, so the path a caller sees for a
        // null entry is items[0].<list element>, not items[0].
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items[0].<list element>")),
                "a null entry inside items must be rejected, not counted as a discovered item");
    }

    @Test
    void resultsResponseNegativeHighestSequenceIsRejected() {
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setItems(List.of());
        dto.setHighestSequence(-1L);
        dto.setMore(false);

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("highestSequence")),
                "a negative highestSequence must be rejected: 0 is the empty-run cursor, and item "
                        + "sequences start at 1");
    }

    @Test
    void resultsResponseMissingHighestSequenceFailsNotNullValidation() {
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setMore(false);
        // highestSequence intentionally left unset (null Long, not a defaulted 0).

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("highestSequence")),
                "an omitted highestSequence must fail the @NotNull constraint rather than silently default to 0");
    }

    @Test
    void resultsResponseMissingMoreFailsNotNullValidation() {
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setHighestSequence(7L);
        // more intentionally left unset (null Boolean, not a defaulted false).

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("more")),
                "an omitted more must fail the @NotNull constraint rather than silently default to false, "
                        + "which would make Core stop draining early");
    }

    @Test
    void resultsResponseItemConstraintsCascadeViaValid() {
        DiscoveryResultsResponseDto dto = new DiscoveryResultsResponseDto();
        dto.setItems(List.of(new DiscoveredItemDto())); // sequence/resource/uniqueRef/payload all unset
        dto.setHighestSequence(1L);
        dto.setMore(false);

        Set<ConstraintViolation<DiscoveryResultsResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("items[0].uniqueRef")),
                "@Valid must cascade into items so each DiscoveredItemDto's own constraints are evaluated");
    }

    @Test
    void statusResponseMissingStateFailsNotNullValidation() {
        DiscoveryStatusResponseDto dto = new DiscoveryStatusResponseDto();
        dto.setHighestSequence(3L);
        // state intentionally left unset.

        Set<ConstraintViolation<DiscoveryStatusResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")),
                "a null state must fail the @NotNull constraint");
    }

    @Test
    void statusResponseMissingHighestSequenceFailsNotNullValidation() {
        DiscoveryStatusResponseDto dto = new DiscoveryStatusResponseDto();
        dto.setState(DiscoveryRunState.RUNNING);
        // highestSequence intentionally left unset (null Long, not a defaulted 0).

        Set<ConstraintViolation<DiscoveryStatusResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("highestSequence")),
                "an omitted highestSequence must fail the @NotNull constraint rather than silently default to 0");
    }

    @Test
    void statusResponseNegativeHighestSequenceIsRejected() {
        DiscoveryStatusResponseDto dto = new DiscoveryStatusResponseDto();
        dto.setState(DiscoveryRunState.RUNNING);
        dto.setHighestSequence(-1L);

        Set<ConstraintViolation<DiscoveryStatusResponseDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("highestSequence")),
                "a negative highestSequence must be rejected: 0 is the empty-run cursor, and item "
                        + "sequences start at 1");
    }

    @Test
    void statusResponseAcceptsZeroHighestSequenceForARunWithNoItemsYet() {
        DiscoveryStatusResponseDto dto = new DiscoveryStatusResponseDto();
        dto.setState(DiscoveryRunState.RUNNING);
        dto.setHighestSequence(0L);

        assertTrue(VALIDATOR.validate(dto).isEmpty(),
                "0 is the legitimate cursor for a run that has produced no items yet");
    }

    @Test
    void supportedResourceMissingResourceFailsNotNullValidation() {
        DiscoverySupportedResourceDto dto = new DiscoverySupportedResourceDto();
        // resource intentionally left unset.

        Set<ConstraintViolation<DiscoverySupportedResourceDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("resource")),
                "a null resource must fail the @NotNull constraint");
    }

    @Test
    void supportedResourceRoundTripsAsResourceOnly() throws Exception {
        DiscoverySupportedResourceDto dto = new DiscoverySupportedResourceDto();
        dto.setResource(Resource.CERTIFICATE);

        String json = mapper.writeValueAsString(dto);

        assertEquals(Resource.CERTIFICATE, mapper.readValue(json, DiscoverySupportedResourceDto.class).getResource());
    }

    @Test
    void initiateResponseDistinguishesUndeclaredAndDeclaredStoppability() throws Exception {
        // Undeclared (null) means Core falls back to the interface-level flag; it must stay off the wire
        // rather than serialize as null, so a tolerant reader sees a clean absence.
        DiscoveryInitiateResponseDto undeclared = new DiscoveryInitiateResponseDto();

        DiscoveryInitiateResponseDto declared = new DiscoveryInitiateResponseDto();
        declared.setStoppable(false);

        String undeclaredJson = mapper.writeValueAsString(undeclared);
        String declaredJson = mapper.writeValueAsString(declared);

        assertFalse(undeclaredJson.contains("\"stoppable\""), "undeclared stoppable must not appear as null");
        assertTrue(declaredJson.contains("\"stoppable\":false"));
        assertNull(mapper.readValue(undeclaredJson, DiscoveryInitiateResponseDto.class).getStoppable());
        assertEquals(Boolean.FALSE, mapper.readValue(declaredJson, DiscoveryInitiateResponseDto.class).getStoppable());
    }

    @Test
    void byResourceProgressMapRoundTripsWithWireCodes() throws Exception {
        DiscoveryResourceProgressDto certProgress = new DiscoveryResourceProgressDto();
        certProgress.setProcessed(60L);
        certProgress.setTotalEstimate(200L);

        DiscoveryResourceProgressDto keyProgress = new DiscoveryResourceProgressDto();
        keyProgress.setProcessed(40L);
        keyProgress.setTotalEstimate(300L);

        DiscoveryProgressDto dto = new DiscoveryProgressDto();
        dto.setTargetsProcessed(100L);
        dto.setTargetsTotal(500L);
        dto.setPhase("scanning");
        dto.setByResource(Map.of(Resource.CERTIFICATE, certProgress, Resource.CRYPTOGRAPHIC_KEY, keyProgress));

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"certificates\":{"), "byResource keys must serialize as Resource wire codes");
        assertTrue(json.contains("\"keys\":{"), "byResource keys must serialize as Resource wire codes");
        assertFalse(json.contains("CERTIFICATE"), "byResource keys must not fall back to the enum's Java name");
        assertFalse(json.contains("CRYPTOGRAPHIC_KEY"), "byResource keys must not fall back to the enum's Java name");

        DiscoveryProgressDto back = mapper.readValue(json, DiscoveryProgressDto.class);
        assertEquals(100L, back.getTargetsProcessed());
        assertEquals(500L, back.getTargetsTotal());
        assertEquals("scanning", back.getPhase());
        assertTrue(back.getByResource().containsKey(Resource.CERTIFICATE));
        assertTrue(back.getByResource().containsKey(Resource.CRYPTOGRAPHIC_KEY));
        assertEquals(60L, back.getByResource().get(Resource.CERTIFICATE).getProcessed());
        assertEquals(200L, back.getByResource().get(Resource.CERTIFICATE).getTotalEstimate());
        assertEquals(40L, back.getByResource().get(Resource.CRYPTOGRAPHIC_KEY).getProcessed());
        assertEquals(300L, back.getByResource().get(Resource.CRYPTOGRAPHIC_KEY).getTotalEstimate());
    }

    /** Work and yield travel together on one object without sharing a denominator. */
    @Test
    void progressCountsWorkRunWideAndYieldPerResource() throws Exception {
        DiscoveryResourceProgressDto certYield = new DiscoveryResourceProgressDto();
        certYield.setProcessed(12L);

        DiscoveryProgressDto dto = new DiscoveryProgressDto();
        dto.setTargetsProcessed(4_200L);
        dto.setTargetsTotal(16_645_890L);
        dto.setTargetsFailed(4_188L);
        dto.setByResource(Map.of(Resource.CERTIFICATE, certYield));

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"targetsProcessed\":4200"), json);
        assertTrue(json.contains("\"targetsTotal\":16645890"), json);
        assertTrue(json.contains("\"targetsFailed\":4188"), json);

        DiscoveryProgressDto back = mapper.readValue(json, DiscoveryProgressDto.class);
        assertEquals(4_200L, back.getTargetsProcessed());
        assertEquals(16_645_890L, back.getTargetsTotal());
        assertEquals(4_188L, back.getTargetsFailed());
        assertEquals(12L, back.getByResource().get(Resource.CERTIFICATE).getProcessed(),
                "yield is counted in items, per resource, and never mixed with the run's work counters");
    }

    /**
     * The two denominators must not be confused for each other on the wire: a run-level object carries no item counts,
     * and a per-resource one carries no work counts.
     */
    @Test
    void workAndYieldCountersDoNotAppearOnEachOthersObjects() throws Exception {
        DiscoveryProgressDto run = new DiscoveryProgressDto();
        run.setTargetsProcessed(7L);
        String runJson = mapper.writeValueAsString(run);
        assertFalse(runJson.contains("\"processed\""), "the run level counts work, not items: " + runJson);

        DiscoveryResourceProgressDto leaf = new DiscoveryResourceProgressDto();
        leaf.setProcessed(7L);
        String leafJson = mapper.writeValueAsString(leaf);
        assertFalse(leafJson.contains("targets"), "a resource counts items, not the work that found them: " + leafJson);
    }

    @Test
    void progressOmitsAbsentFieldsIndependently() throws Exception {
        DiscoveryProgressDto dto = new DiscoveryProgressDto();
        dto.setTargetsProcessed(42L);
        dto.setPhase("scanning");
        // targetsTotal is left unset on purpose: a sweep of an unbounded range has no completion ratio, and the
        // contract says a consumer must not invent one.
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"targetsProcessed\":42"));
        assertTrue(json.contains("\"phase\":\"scanning\""));
        assertFalse(json.contains("\"targetsTotal\""));
        assertFalse(json.contains("\"targetsFailed\""));
        assertFalse(json.contains("\"byResource\""));

        DiscoveryProgressDto back = mapper.readValue(json, DiscoveryProgressDto.class);
        assertEquals(42L, back.getTargetsProcessed());
        assertEquals("scanning", back.getPhase());
        assertNull(back.getTargetsTotal());
        assertNull(back.getTargetsFailed());
        assertNull(back.getByResource());
    }

    @Test
    void initiateResponseRoundTripsMetaAndOmitsWhenAbsent() throws Exception {
        DiscoveryInitiateResponseDto empty = new DiscoveryInitiateResponseDto();
        assertEquals("{}", mapper.writeValueAsString(empty), "absent meta must be omitted, not serialized as null");

        DiscoveryInitiateResponseDto dto = new DiscoveryInitiateResponseDto();
        dto.setMeta(List.of(metadataAttribute("cursor", "abc123")));

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"name\":\"cursor\""));

        DiscoveryInitiateResponseDto back = mapper.readValue(json, DiscoveryInitiateResponseDto.class);
        assertEquals(1, back.getMeta().size());
        MetadataAttributeV3 backMeta = assertInstanceOf(MetadataAttributeV3.class, back.getMeta().get(0));
        assertEquals("cursor", backMeta.getName());
        assertEquals("abc123", ((StringAttributeContentV3) backMeta.getContent().get(0)).getData());
    }

    @Test
    void stopResponseRoundTripsMetaAndOmitsWhenAbsent() throws Exception {
        DiscoveryStopResponseDto empty = new DiscoveryStopResponseDto();
        assertEquals("{}", mapper.writeValueAsString(empty), "absent meta must be omitted, not serialized as null");

        DiscoveryStopResponseDto dto = new DiscoveryStopResponseDto();
        dto.setMeta(List.of(metadataAttribute("cursor", "def456")));

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"name\":\"cursor\""));

        DiscoveryStopResponseDto back = mapper.readValue(json, DiscoveryStopResponseDto.class);
        assertEquals(1, back.getMeta().size());
        MetadataAttributeV3 backMeta = assertInstanceOf(MetadataAttributeV3.class, back.getMeta().get(0));
        assertEquals("cursor", backMeta.getName());
        assertEquals("def456", ((StringAttributeContentV3) backMeta.getContent().get(0)).getData());
    }

    @Test
    void initiateResponseToStringExcludesMeta() {
        // The handle is dropped from toString outright, not trusted to render harmlessly:
        // MetadataAttributeV3's own toString walks content and properties, so every entry's
        // structure and label reaches the log line, one nested record per attribute.
        DiscoveryInitiateResponseDto dto = new DiscoveryInitiateResponseDto();
        dto.setMeta(List.of(metadataAttribute("runHandleCursor", "opaque-run-handle-blob")));

        String str = dto.toString();

        assertFalse(str.contains("meta"),
                "toString must not mention the opaque run handle, which can reach 64 KB: " + str);
        assertFalse(str.contains("runHandleCursor"), "toString must not name the handle's entries: " + str);
        assertFalse(str.contains("opaque-run-handle-blob"), "toString must not write handle content: " + str);
    }

    @Test
    void stopResponseToStringExcludesMeta() {
        DiscoveryStopResponseDto dto = new DiscoveryStopResponseDto();
        dto.setMeta(List.of(metadataAttribute("resumeCheckpointCursor", "resume-checkpoint-blob")));

        String str = dto.toString();

        assertFalse(str.contains("meta"),
                "toString must not mention the resume checkpoint, which can reach 64 KB: " + str);
        assertFalse(str.contains("resumeCheckpointCursor"), "toString must not name the checkpoint's entries: " + str);
        assertFalse(str.contains("resume-checkpoint-blob"), "toString must not write checkpoint content: " + str);
    }

    private MetadataAttribute metadataAttribute(String name, String value) {
        MetadataAttributeV3 attribute = new MetadataAttributeV3();
        attribute.setUuid("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        attribute.setName(name);
        attribute.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel(name);
        attribute.setProperties(properties);
        attribute.setContent(List.of(new StringAttributeContentV3(value)));
        return attribute;
    }
}
