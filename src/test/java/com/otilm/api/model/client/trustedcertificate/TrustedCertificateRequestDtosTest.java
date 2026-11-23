package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrustedCertificateRequestDtosTest {

    @Test
    void addRequestRequiresPurposes() throws Exception {
        Field purposes = TrustedCertificateAddRequestDto.class.getDeclaredField("purposes");
        assertNotNull(purposes.getAnnotation(NotEmpty.class));
        Schema schema = TrustedCertificateAddRequestDto.class.getAnnotation(Schema.class);
        assertTrue(schema.description().contains("Exactly one of certificateUuids and certificateContent"));
    }

    @Test
    void updateRequestLogsItsUuids() {
        TrustedCertificateUpdateRequestDto dto = new TrustedCertificateUpdateRequestDto();
        dto.setUuids(List.of("5f0b1c2e-0000-4000-8000-000000000001"));
        assertEquals(1, dto.toLogResourceObjectsUuids().size());
    }

    @Test
    void applyRequestAllowsNoUuids() throws Exception {
        TrustedCertificateApplyRequestDto dto = new ObjectMapper()
                .readValue("{}", TrustedCertificateApplyRequestDto.class);
        assertNull(dto.getUuids());
    }

    @Test
    void absentUuidsLogNoResourceObjects() {
        assertTrue(new TrustedCertificateAddRequestDto().toLogResourceObjectsUuids().isEmpty());
        assertTrue(new TrustedCertificateApplyRequestDto().toLogResourceObjectsUuids().isEmpty());
    }
}
