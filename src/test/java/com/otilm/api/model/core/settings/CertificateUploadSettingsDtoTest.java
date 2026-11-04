package com.otilm.api.model.core.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateUploadSettingsDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void updateDefaultsToOff() {
        assertFalse(new CertificateUploadSettingsUpdateDto().isAutomaticOwnership());
    }

    @Test
    void updateWithoutUploadBlockStaysAbsent() throws Exception {
        CertificateSettingsUpdateDto dto = mapper.readValue("{}", CertificateSettingsUpdateDto.class);

        assertNull(dto.getUpload());
    }

    @Test
    void updateReadsEnabledValue() throws Exception {
        CertificateSettingsUpdateDto dto = mapper
                .readValue("{\"upload\":{\"automaticOwnership\":true}}", CertificateSettingsUpdateDto.class);

        assertTrue(dto.getUpload().isAutomaticOwnership());
    }

    @Test
    void updateWithEmptyUploadBlockResetsToOff() throws Exception {
        CertificateSettingsUpdateDto dto = mapper.readValue("{\"upload\":{}}", CertificateSettingsUpdateDto.class);

        assertFalse(dto.getUpload().isAutomaticOwnership());
    }

    @Test
    void roundTripsReadView() throws Exception {
        CertificateSettingsDto dto = new CertificateSettingsDto();
        CertificateUploadSettingsDto upload = new CertificateUploadSettingsDto();
        upload.setAutomaticOwnership(true);
        dto.setUpload(upload);

        CertificateSettingsDto back = mapper.readValue(mapper.writeValueAsString(dto), CertificateSettingsDto.class);

        assertTrue(back.getUpload().isAutomaticOwnership());
    }
}
