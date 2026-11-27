package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import com.otilm.api.model.core.v2.ClientCertificateIssueRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRekeyRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRenewRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRequestDto;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

    @Test
    void namesTheCvcDefault_whenTheFormatOfAClientRequestIsDescribed() throws Exception {
        // given
        List<Class<?>> requests = List
                .of(ClientCertificateIssueRequestDto.class, ClientCertificateRenewRequestDto.class,
                        ClientCertificateRekeyRequestDto.class);

        // when / then
        for (Class<?> request : requests) {
            String description = request.getDeclaredField("format").getAnnotation(Schema.class).description();
            assertTrue(description.contains("pkcs10 for X.509"), request.getSimpleName());
            assertTrue(description.contains("cvc for CVC"), request.getSimpleName());
            assertTrue(description.contains("pkcs10 and crmf apply to X.509"), request.getSimpleName());
        }
    }

    @Test
    void namesPkcs10AsTheOnlyDefault_whenTheStandaloneSubmissionHasNoCertificateType() throws Exception {
        // given
        String description = ClientCertificateRequestDto.class
                .getDeclaredField("format")
                .getAnnotation(Schema.class)
                .description();

        // when / then
        assertTrue(description.contains("pkcs10 applies"));
        assertFalse(description.contains("cvc"));
    }

    @Test
    void readsTheOuterAndInnerReferences_whenAnAuthenticatedRequestCarriesBoth() throws Exception {
        // given
        String json = "{\"certificateType\":\"CVC\",\"holderReference\":\"SEILMTERM00004\","
                + "\"authorityReference\":\"SEILMDV00001\",\"authenticatedBy\":\"SEILMTERM00002\"}";

        // when
        CvcCertificateRequestDetail read = assertInstanceOf(CvcCertificateRequestDetail.class,
                mapper.readValue(json, CertificateRequestDetail.class));

        // then
        assertEquals("SEILMDV00001", read.getAuthorityReference());
        assertEquals("SEILMTERM00002", read.getAuthenticatedBy());
    }

    @Test
    void leavesTheOuterReferenceUnset_whenTheRequestIsNotAuthenticated() throws Exception {
        // given
        String json = "{\"certificateType\":\"CVC\",\"holderReference\":\"SEILMTERM00001\","
                + "\"authorityReference\":\"SEILMDV00001\"}";

        // when
        CvcCertificateRequestDetail read = assertInstanceOf(CvcCertificateRequestDetail.class,
                mapper.readValue(json, CertificateRequestDetail.class));

        // then
        assertEquals("SEILMDV00001", read.getAuthorityReference());
        assertNull(read.getAuthenticatedBy());
    }

    @Test
    void writesTheTerminalType_whenTheCertificateStatesOne() throws Exception {
        // given
        CvcCertificateDetail detail = cvcDetail();
        detail.setTerminalType("0.4.0.127.0.7.3.1.2.2");

        // when
        JsonNode json = mapper.readTree(mapper.writeValueAsString(detail));

        // then
        assertEquals("0.4.0.127.0.7.3.1.2.2", json.get("terminalType").asText());
    }

    @Test
    void fixesTheCertificateTypeOfEachBranch_whenTheUnionsAreDescribed() throws Exception {
        // given
        List<Class<?>> branches = List
                .of(X509CertificateDetail.class, CvcCertificateDetail.class, X509CertificateRequestDetail.class,
                        CvcCertificateRequestDetail.class);
        List<String> expected = List
                .of(CertificateType.Codes.X509, CertificateType.Codes.CVC, CertificateType.Codes.X509,
                        CertificateType.Codes.CVC);

        // when / then
        for (int i = 0; i < branches.size(); i++) {
            Schema schema = branches.get(i).getMethod("getCertificateType").getAnnotation(Schema.class);
            assertEquals(List.of(expected.get(i)), Arrays.asList(schema.allowableValues()),
                    branches.get(i).getSimpleName());
        }
    }

    @Test
    void listsTheRoleValues_whenTheCvcDetailIsDescribed() throws Exception {
        // given
        Schema role = CvcCertificateDetail.class.getDeclaredField("role").getAnnotation(Schema.class);

        // when / then
        assertEquals(List.of("cvca", "dv-domestic", "dv-foreign", "terminal"), Arrays.asList(role.allowableValues()));
    }

    @Test
    void describesTheCvcEnumValues_whenTheCatalogueIsRead() {
        // given / when / then
        assertEquals("Card verifiable certificate, BSI TR-03110 Part 3", CertificateType.CVC.getDescription());
        assertTrue(CertificateRequestFormat.class.getAnnotation(Schema.class).description().contains("Base64"));
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
