package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import com.otilm.api.model.core.v2.ClientCertificateIssueRequestDto;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CvcCertificateDetailJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void writesTheCvcFieldsFlatWithDatesAsDates_whenTheCertificateIsCvc() throws Exception {
        // given
        CvcCertificateDetail detail = cvcDetail();

        // when
        JsonNode json = mapper.readTree(mapper.writeValueAsString(detail));

        // then
        assertEquals("CVC", json.get("certificateType").asText());
        assertEquals("SEILMTERM00001", json.get("holderReference").asText());
        assertEquals("SEILMDV00001", json.get("authorityReference").asText());
        assertEquals("terminal", json.get("role").asText());
        assertEquals("03", json.get("accessRights").asText());
        assertEquals("2026-01-15", json.get("effectiveDate").asText());
        assertEquals("2026-04-14", json.get("expirationDate").asText());
    }

    @Test
    void readsTheCvcSubclass_whenTheJsonCarriesTheCvcCertificateType() throws Exception {
        // given
        String json = mapper.writeValueAsString(cvcDetail());

        // when
        CertificateDetail read = mapper.readValue(json, CertificateDetail.class);

        // then
        CvcCertificateDetail cvc = assertInstanceOf(CvcCertificateDetail.class, read);
        assertEquals("SEILMTERM00001", cvc.getHolderReference());
        assertEquals(LocalDate.of(2026, 1, 15), cvc.getEffectiveDate());
        assertEquals(LocalDate.of(2026, 4, 14), cvc.getExpirationDate());
    }

    @Test
    void readsTheCvcRequestSubclass_whenTheRequestIsCvc() throws Exception {
        // given
        String json = "{\"certificateType\":\"CVC\",\"certificateRequestFormat\":\"cvc\","
                + "\"holderReference\":\"SEILMTERM00002\",\"authorityReference\":\"SEILMTERM00001\"}";

        // when
        CertificateRequestDetail read = mapper.readValue(json, CertificateRequestDetail.class);

        // then
        CvcCertificateRequestDetail cvc = assertInstanceOf(CvcCertificateRequestDetail.class, read);
        assertEquals("SEILMTERM00002", cvc.getHolderReference());
        assertEquals("SEILMTERM00001", cvc.getAuthorityReference());
        assertEquals(CertificateRequestFormat.CVC, cvc.getCertificateRequestFormat());
    }

    @Test
    void readsACvcRequestWithoutAnAuthorityReference_whenTheRequestIsInitial() throws Exception {
        // given
        String json = "{\"certificateType\":\"CVC\",\"holderReference\":\"SEILMTERM00001\"}";

        // when
        CertificateRequestDetail read = mapper.readValue(json, CertificateRequestDetail.class);

        // then
        CvcCertificateRequestDetail cvc = assertInstanceOf(CvcCertificateRequestDetail.class, read);
        assertEquals("SEILMTERM00001", cvc.getHolderReference());
        assertNull(cvc.getAuthorityReference());
    }

    @Test
    void readsTheCvcTypeAndFormat_whenAnIssueRequestNamesThem() throws Exception {
        // given
        String json = "{\"request\":\"Zm9v\",\"format\":\"cvc\",\"certificateType\":\"CVC\"}";

        // when
        ClientCertificateIssueRequestDto read = mapper.readValue(json, ClientCertificateIssueRequestDto.class);

        // then
        assertEquals(CertificateRequestFormat.CVC, read.getFormat());
        assertEquals(CertificateType.CVC, read.getCertificateType());
    }

    @Test
    void roundTripsTheCvcCode_whenTheTypeAndTheFormatAreRead() {
        // given
        String typeCode = "CVC";
        String formatCode = "cvc";

        // when / then
        assertEquals(CertificateType.CVC, CertificateType.fromCode(typeCode));
        assertEquals(typeCode, CertificateType.CVC.getCode());
        assertEquals(CertificateRequestFormat.CVC, CertificateRequestFormat.fromCode(formatCode));
        assertEquals(formatCode, CertificateRequestFormat.CVC.getCode());
    }

    @Test
    void writesTheLinkRelationType_whenARelatedCertificateIsALinkCertificate() throws Exception {
        // given
        CertificateSimpleDto related = new CertificateSimpleDto();
        related.setRelationType(CertificateRelationType.LINK);

        // when
        String written = mapper.writeValueAsString(related);
        JsonNode json = mapper.readTree(written);
        CertificateSimpleDto read = mapper.readValue(written, CertificateSimpleDto.class);

        // then
        assertEquals("link", json.get("relationType").asText());
        assertEquals(CertificateRelationType.LINK, read.getRelationType());
    }

    @Test
    void keepsTheExistingRelationTypes_whenTheLinkTypeIsAdded() {
        // given
        String[] existingCodes = {"renewal", "rekey", "replacement", "pending"};

        // when
        String[] codes = Arrays
                .stream(CertificateRelationType.values())
                .map(CertificateRelationType::getCode)
                .toArray(String[]::new);

        // then
        for (String code : existingCodes) {
            assertTrue(Arrays.asList(codes).contains(code), code);
        }
    }

    @Test
    void mapsTheCvcSubtypes_whenTheDetailAndRequestSchemasAreDescribed() {
        // given
        Schema detailSchema = CertificateDetailDto.class.getAnnotation(Schema.class);
        Schema requestSchema = CertificateRequestDto.class.getAnnotation(Schema.class);

        // when / then
        assertEquals(CvcCertificateDetail.class, mappedSchema(detailSchema, CertificateType.Codes.CVC));
        assertEquals(CvcCertificateRequestDetail.class, mappedSchema(requestSchema, CertificateType.Codes.CVC));
        assertTrue(Arrays.asList(detailSchema.oneOf()).contains(CvcCertificateDetail.class));
        assertTrue(Arrays.asList(requestSchema.oneOf()).contains(CvcCertificateRequestDetail.class));
    }

    private static Class<?> mappedSchema(Schema schema, String value) {
        return Arrays
                .stream(schema.discriminatorMapping())
                .filter(mapping -> mapping.value().equals(value))
                .map(DiscriminatorMapping::schema)
                .findFirst()
                .orElse(null);
    }

    private static CvcCertificateDetail cvcDetail() {
        CvcCertificateDetail detail = new CvcCertificateDetail();
        detail.setUuid("u");
        detail.setCertificateType(CertificateType.CVC);
        detail.setHolderReference("SEILMTERM00001");
        detail.setAuthorityReference("SEILMDV00001");
        detail.setRole("terminal");
        detail.setAccessRights("03");
        detail.setEffectiveDate(LocalDate.of(2026, 1, 15));
        detail.setExpirationDate(LocalDate.of(2026, 4, 14));
        return detail;
    }
}
