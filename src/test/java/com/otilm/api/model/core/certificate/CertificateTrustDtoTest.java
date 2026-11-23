package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.certificate.CertificateUpdateObjectsDto;
import com.otilm.api.model.client.trustedcertificate.TrustedCertificatePurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import java.util.EnumSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateTrustDtoTest {

    @Test
    void detailCarriesTrustWhenSet() throws Exception {
        X509CertificateDetail detail = new X509CertificateDetail();
        detail.setUuid("u");
        detail.setCertificateType(CertificateType.X509);
        CertificateTrustDto trust = new CertificateTrustDto();
        trust.setTrustedCertificateUuid("t1");
        trust.setPurposes(EnumSet.of(TrustedCertificatePurpose.PLATFORM));
        detail.setTrust(trust);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(detail);
        assertTrue(json.contains("\"trust\":{") && json.contains("\"purposes\":[\"platform\"]"));
        CertificateDetail back = mapper.readValue(json, CertificateDetail.class);
        assertEquals(EnumSet.of(TrustedCertificatePurpose.PLATFORM), back.getTrust().getPurposes());
    }

    @Test
    void untrustedDetailReadsBackWithoutTrust() throws Exception {
        X509CertificateDetail detail = new X509CertificateDetail();
        detail.setUuid("u");
        detail.setCertificateType(CertificateType.X509);
        ObjectMapper mapper = new ObjectMapper();
        CertificateDetail back = mapper.readValue(mapper.writeValueAsString(detail), CertificateDetail.class);
        assertNull(back.getTrust());
    }

    @Test
    void trustIsOptionalOnTheDetail() throws Exception {
        Schema schema = CertificateDetail.class.getDeclaredField("trust").getAnnotation(Schema.class);
        assertEquals(Schema.RequiredMode.NOT_REQUIRED, schema.requiredMode());
        assertTrue(schema.description().contains("waiting for a platform removal"));
    }

    @Test
    void trustDescribesAPendingPlatformRemoval() throws Exception {
        assertTrue(CertificateTrustDto.class
                .getDeclaredField("purposes")
                .getAnnotation(Schema.class)
                .description()
                .contains("empty only while a platform removal is pending"));
        assertTrue(CertificateTrustDto.class
                .getDeclaredField("platformState")
                .getAnnotation(Schema.class)
                .description()
                .contains("a pending platform removal"));
    }

    @Test
    @SuppressWarnings("removal")
    void trustedCaOnUpdateIsDeprecatedButStillSerialized() throws Exception {
        Field f = CertificateUpdateObjectsDto.class.getDeclaredField("trustedCa");
        assertTrue(f.getAnnotation(Schema.class).deprecated());
        assertNotNull(f.getAnnotation(Deprecated.class));
        CertificateUpdateObjectsDto dto = new CertificateUpdateObjectsDto();
        dto.setTrustedCa(true);
        assertTrue(new ObjectMapper().writeValueAsString(dto).contains("\"trustedCa\":true"));
    }
}
