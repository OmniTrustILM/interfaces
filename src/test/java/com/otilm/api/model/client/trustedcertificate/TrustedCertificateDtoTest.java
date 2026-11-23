package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrustedCertificateDtoTest {

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
    void bulkResultCarriesOneItemPerRequest() throws Exception {
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
