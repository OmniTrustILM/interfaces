package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrustedCertificateDtoTest {

    @Test
    void platformCheckTimestampsKeepTheirInstant() throws Exception {
        ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        Instant expected = Instant.parse("2026-11-23T09:15:30Z");
        TrustedCertificateDto dto = mapper
                .readValue("{\"platformStateCheckedAt\":\"2026-11-23T10:15:30+01:00\"}", TrustedCertificateDto.class);
        TrustedCertificateSyncDto sync = mapper
                .readValue("{\"lastSuccessfulCheckAt\":\"2026-11-23T10:15:30+01:00\"}",
                        TrustedCertificateSyncDto.class);
        assertEquals(expected, dto.getPlatformStateCheckedAt().toInstant());
        assertEquals(expected, sync.getLastSuccessfulCheckAt().toInstant());
    }

    @Test
    void readsAnItemFromACoreThatSendsNoPlatformState() throws Exception {
        TrustedCertificateDto dto = new ObjectMapper()
                .findAndRegisterModules()
                .readValue(
                        "{\"uuid\":\"u1\",\"certificateUuid\":\"c1\",\"purposes\":[\"validation\"],\"expiring\":false}",
                        TrustedCertificateDto.class);
        assertNull(dto.getPlatformState());
        assertNull(dto.getPlatformStateCheckedAt());
        assertEquals(Set.of(TrustedCertificatePurpose.VALIDATION), dto.getPurposes());
    }

    @Test
    void purposesAndPlatformStateDescribeAPendingPlatformRemoval() throws Exception {
        assertTrue(TrustedCertificateDto.class
                .getDeclaredField("purposes")
                .getAnnotation(Schema.class)
                .description()
                .contains("empty only while a platform removal is pending"));
        assertTrue(TrustedCertificateDto.class
                .getDeclaredField("platformState")
                .getAnnotation(Schema.class)
                .description()
                .contains("a pending platform removal"));
    }

    @Test
    void dtoWithoutPlatformStateRoundTrips() throws Exception {
        TrustedCertificateDto dto = new TrustedCertificateDto();
        dto.setUuid("u1");
        dto.setCertificateUuid("c1");
        dto.setPurposes(EnumSet.of(TrustedCertificatePurpose.VALIDATION));
        String json = new ObjectMapper().findAndRegisterModules().writeValueAsString(dto);
        assertTrue(json.contains("\"purposes\":[\"validation\"]"));
        assertFalse(json.contains("platformState\":\""));
        TrustedCertificateDto back = new ObjectMapper()
                .findAndRegisterModules()
                .readValue(json, TrustedCertificateDto.class);
        assertNull(back.getPlatformState());
    }

    @Test
    void bulkResultSerializesItemReferenceAndSuccess() throws Exception {
        TrustedCertificateBulkItemDto item = new TrustedCertificateBulkItemDto();
        item.setReference("c1");
        item.setSuccess(false);
        item.setMessage("Certificate is already trusted");
        TrustedCertificateBulkResultDto result = new TrustedCertificateBulkResultDto();
        result.setItems(List.of(item));
        String json = new ObjectMapper().writeValueAsString(result);
        assertTrue(json.contains("\"reference\":\"c1\"") && json.contains("\"success\":false"));
    }

    @Test
    void platformDtoSerializesSourceAndSync() throws Exception {
        TrustedCertificatePlatformDto dto = new TrustedCertificatePlatformDto();
        dto.setManaged(true);
        dto.setSource(TrustedCertificatePlatformSource.PROVISIONING);
        dto.setCertificates(List.of());
        TrustedCertificateSyncDto sync = new TrustedCertificateSyncDto();
        sync.setInitialImportDone(false);
        dto.setSync(sync);
        String json = new ObjectMapper().findAndRegisterModules().writeValueAsString(dto);
        assertTrue(json.contains("\"source\":\"provisioning\"") && json.contains("\"initialImportDone\":false"));
    }
}
