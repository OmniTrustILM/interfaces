package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.connector.discovery.v2.DiscoveryProgressDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResourceProgressDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.discovery.DiscoveryStatus;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Covers the discovery v2 additions to {@link DiscoveryDetailDto}: they round-trip, a v1 run's payload carries the
 * synthesized always-present fields while the optional one disappears instead of showing up as null, the run's message
 * log is counted here rather than carried here, and every {@link Resource}-typed or {@link Resource}-keyed value they
 * carry travels as a wire code.
 */
class DiscoveryDetailDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /** A run against a v1 connector, with resources and stoppable exactly as Core synthesizes them. */
    private DiscoveryDetailDto v1Run() {
        DiscoveryDetailDto dto = new DiscoveryDetailDto();
        dto.setUuid("c2f685d4-6a3e-11ec-90d6-0242ac120003");
        dto.setName("nightly-scan");
        dto.setKind("IP-HostName");
        dto.setStatus(DiscoveryStatus.COMPLETED);
        dto.setConnectorStatus(DiscoveryStatus.COMPLETED);
        dto.setConnectorUuid("b9b09548-a97c-4c6a-a06a-e4ee6fc2da98");
        dto.setConnectorName("network-discovery");
        dto.setResources(List.of(Resource.CERTIFICATE));
        dto.setStoppable(false);
        return dto;
    }

    @Test
    void roundTripsAllFourV2Fields() throws Exception {
        DiscoveryResourceProgressDto keyProgress = new DiscoveryResourceProgressDto();
        keyProgress.setProcessed(3L);
        DiscoveryProgressDto progress = new DiscoveryProgressDto();
        progress.setTargetsProcessed(11L);
        progress.setTargetsTotal(40L);
        progress.setPhase("scanning");
        progress.setByResource(Map.of(Resource.CRYPTOGRAPHIC_KEY, keyProgress));

        DiscoveryDetailDto dto = v1Run();
        dto.setStatus(DiscoveryStatus.STOPPED);
        dto.setResources(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY));
        dto.setProgress(progress);
        dto.setRunMessageCount(2L);
        dto.setStoppable(true);

        String json = mapper.writeValueAsString(dto);
        DiscoveryDetailDto back = mapper.readValue(json, DiscoveryDetailDto.class);

        // pinned by literal name: a round-trip alone would survive a rename, since it renames both ends at once
        assertTrue(json.contains("\"resources\":"), json);
        assertTrue(json.contains("\"progress\":"), json);
        assertTrue(json.contains("\"runMessageCount\":"), json);
        assertTrue(json.contains("\"stoppable\":"), json);

        assertEquals(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY), back.getResources());
        assertEquals(11L, back.getProgress().getTargetsProcessed());
        assertEquals(40L, back.getProgress().getTargetsTotal());
        assertEquals("scanning", back.getProgress().getPhase());
        assertEquals(3L, back.getProgress().getByResource().get(Resource.CRYPTOGRAPHIC_KEY).getProcessed());
        assertEquals(2L, back.getRunMessageCount());
        assertEquals(Boolean.TRUE, back.getStoppable());
        assertEquals(DiscoveryStatus.STOPPED, back.getStatus());
    }

    @Test
    void reintroducingTheRunMessageLogOnTheDetailIsCaught() throws Exception {
        // Guards the shape rather than today's behaviour, which cannot break while the field is absent: a client
        // polls this detail while a run is live, so a bounded-but-large log riding along on every poll is the
        // regression worth failing on. The count is what the detail owes a client.
        String json = mapper.writeValueAsString(v1Run());

        assertFalse(json.contains("runMessages\""), json);
    }

    @Test
    void resourceValuesUseWireCodes() throws Exception {
        DiscoveryResourceProgressDto certProgress = new DiscoveryResourceProgressDto();
        certProgress.setProcessed(1L);
        DiscoveryProgressDto progress = new DiscoveryProgressDto();
        progress.setByResource(Map.of(Resource.CERTIFICATE, certProgress));

        DiscoveryDetailDto dto = v1Run();
        dto.setResources(List.of(Resource.CRYPTOGRAPHIC_KEY));
        dto.setProgress(progress);

        String json = mapper.writeValueAsString(dto);

        assertTrue(json.contains("\"resources\":[\"keys\"]"), json);
        assertTrue(json.contains("\"certificates\""), json);
        assertFalse(json.contains("CRYPTOGRAPHIC_KEY"), json);
    }

    @Test
    void v1RunCarriesTheSynthesizedFieldsAndOmitsTheOptionalOne() throws Exception {
        String json = mapper.writeValueAsString(v1Run());
        DiscoveryDetailDto back = mapper.readValue(json, DiscoveryDetailDto.class);

        // the always-present values, as Core synthesizes them for a run against a v1 connector
        assertTrue(json.contains("\"resources\":[\"certificates\"]"), json);
        assertTrue(json.contains("\"stoppable\":false"), json);
        assertEquals(List.of(Resource.CERTIFICATE), back.getResources());
        assertEquals(Boolean.FALSE, back.getStoppable());

        // A v1 run has no message log at all, which is a count of zero rather than an absent field: the
        // primitive is what stops "no messages" and "not reported" being the same value on the wire.
        assertTrue(json.contains("\"runMessageCount\":0"), json);
        assertEquals(0L, back.getRunMessageCount());

        // progress and connectorInterface are the genuinely optional fields, and both promise absence rather
        // than null: a v1 run declares no connector interface, which is how a client tells the generations apart.
        assertFalse(json.contains("progress"), json);
        assertNull(back.getProgress());
        assertFalse(json.contains("connectorInterface"), json);
        assertNull(back.getConnectorInterface());
    }

    @Test
    void keepsEmittingTheExistingNullableV1Fields() throws Exception {
        // @JsonInclude(NON_NULL) must stay field-scoped on the v2 additions: class-scoped it would
        // silently drop these nullable v1 fields from every response.
        String json = mapper.writeValueAsString(v1Run());

        assertTrue(json.contains("\"message\":null"), json);
        assertTrue(json.contains("\"startTime\":null"), json);
        assertTrue(json.contains("\"endTime\":null"), json);
    }
}
