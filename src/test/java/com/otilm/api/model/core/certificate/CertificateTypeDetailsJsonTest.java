package com.otilm.api.model.core.certificate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import com.otilm.api.model.core.v2.ClientCertificateIssueRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRekeyRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRenewRequestDto;
import com.otilm.api.model.core.v2.ClientCertificateRequestDto;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateTypeDetailsJsonTest {

    // the wire mapper of Core, which leaves a missing typeDetails empty instead of failing on the type id
    private final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY);

    @Test
    void writesTheTypeDetailsNextToTheCertificateTypeProperty_whenTheCertificateIsX509() throws Exception {
        // given
        CertificateDetailDto detail = x509Detail();

        // when
        JsonNode json = mapper.readTree(mapper.writeValueAsString(detail));

        // then
        assertEquals("X.509", json.get("certificateType").asText());
        assertEquals("CN=holder", json.get("typeDetails").get("subjectDn").asText());
        assertEquals("digitalSignature", json.get("typeDetails").get("keyUsage").get(0).asText());
    }

    @Test
    void writesTheCertificateTypePropertyOnce_whenTheCertificateHasTypeDetails() throws Exception {
        // given
        CertificateDetailDto detail = x509Detail();

        // when
        String json = mapper.writeValueAsString(detail);

        // then
        assertEquals(1, json.split("\"certificateType\"", -1).length - 1);
    }

    @Test
    void readsTheX509Subtype_whenTheJsonCarriesTheX509CertificateType() throws Exception {
        // given
        String json = mapper.writeValueAsString(x509Detail());

        // when
        CertificateDetailDto read = mapper.readValue(json, CertificateDetailDto.class);

        // then
        X509CertificateTypeDetailsDto details = assertInstanceOf(X509CertificateTypeDetailsDto.class,
                read.getTypeDetails());
        assertEquals("CN=holder", details.getSubjectDn());
        assertEquals(List.of(CertificateKeyUsage.DIGITAL_SIGNATURE), details.getKeyUsage());
        assertTrue(details.isHybridCertificate());
        assertEquals("ML-DSA-65", details.getAltSignatureAlgorithm());
    }

    @Test
    void leavesTypeDetailsEmpty_whenAnOlderResponseHasNone() throws Exception {
        // given
        String olderResponse = "{\"uuid\":\"u\",\"certificateType\":\"X.509\",\"subjectDn\":\"CN=holder\"}";

        // when
        CertificateDetailDto read = mapper.readValue(olderResponse, CertificateDetailDto.class);

        // then
        assertNull(read.getTypeDetails());
        assertEquals("CN=holder", read.getSubjectDn());
        assertTrue(read.getSupportedOperations().isEmpty());
    }

    @Test
    void readsTheRequestTypeDetails_whenTheRequestIsX509() throws Exception {
        // given
        String json = "{\"certificateType\":\"X.509\",\"typeDetails\":{\"subjectDn\":\"CN=holder\"}}";

        // when
        CertificateRequestDto read = mapper.readValue(json, CertificateRequestDto.class);

        // then
        X509CertificateRequestTypeDetailsDto details = assertInstanceOf(X509CertificateRequestTypeDetailsDto.class,
                read.getTypeDetails());
        assertEquals("CN=holder", details.getSubjectDn());
    }

    @Test
    void readsNoSignatureAlgorithm_whenARequestOfATypeWithoutSignedRequestsOmitsIt() throws Exception {
        // given
        String json = "{\"certificateType\":\"SSH\",\"displayName\":\"ssh-key-id\"}";

        // when
        CertificateRequestDto read = mapper.readValue(json, CertificateRequestDto.class);

        // then
        assertNull(read.getSignatureAlgorithm());
    }

    @Test
    void readsTheDisplayNameButNoCommonNameSubjectDnOrKey_whenAResponseOfATypeWithoutThemOmitsThem() throws Exception {
        // given
        String json = "{\"uuid\":\"u\",\"certificateType\":\"SSH\",\"displayName\":\"ssh-key-id\"}";

        // when
        CertificateDto read = mapper.readValue(json, CertificateDto.class);

        // then
        assertNull(read.getCommonName());
        assertEquals("ssh-key-id", read.getDisplayName());
        assertNull(read.getSubjectDn());
        assertNull(read.getPublicKeyAlgorithm());
        assertNull(read.getKeySize());
    }

    @Test
    void readsNoSerialNumber_whenTheContentOfATypeWithoutOneOmitsIt() throws Exception {
        // given
        String json = "{\"uuid\":\"u\",\"displayName\":\"holder-ref\",\"certificateContent\":\"Zm9v\"}";

        // when
        CertificateContentDto read = mapper.readValue(json, CertificateContentDto.class);

        // then
        assertNull(read.getSerialNumber());
        assertEquals("holder-ref", read.getDisplayName());
    }

    @Test
    void leavesFormatEmpty_whenAnIssueRequestOmitsIt() throws Exception {
        // given
        String json = "{\"request\":\"Zm9v\"}";

        // when
        ClientCertificateIssueRequestDto read = mapper.readValue(json, ClientCertificateIssueRequestDto.class);

        // then
        assertNull(read.getFormat());
        assertNull(read.getCertificateType());
    }

    @Test
    void keepsAnExplicitFormat_whenAnIssueRequestNamesPkcs10() throws Exception {
        // given
        String json = "{\"request\":\"Zm9v\",\"format\":\"pkcs10\",\"certificateType\":\"X.509\"}";

        // when
        ClientCertificateIssueRequestDto read = mapper.readValue(json, ClientCertificateIssueRequestDto.class);

        // then
        assertEquals(CertificateRequestFormat.PKCS10, read.getFormat());
        assertEquals(CertificateType.X509, read.getCertificateType());
    }

    @Test
    void leavesFormatEmpty_whenRenewRekeyAndSignRequestsOmitIt() throws Exception {
        // given
        String json = "{}";

        // when
        ClientCertificateRenewRequestDto renew = mapper.readValue(json, ClientCertificateRenewRequestDto.class);
        ClientCertificateRekeyRequestDto rekey = mapper.readValue(json, ClientCertificateRekeyRequestDto.class);
        ClientCertificateRequestDto sign = mapper.readValue(json, ClientCertificateRequestDto.class);

        // then
        assertNull(renew.getFormat());
        assertNull(rekey.getFormat());
        assertNull(sign.getFormat());
    }

    @Test
    void roundTripsTheOperationCode_whenWritingTheSupportedOperations() throws Exception {
        // given
        CertificateDetailDto detail = x509Detail();
        detail.setSupportedOperations(List.of(CertificateOperation.REVOKE, CertificateOperation.ENROLL_ACME));

        // when
        JsonNode json = mapper.readTree(mapper.writeValueAsString(detail));

        // then
        assertEquals("revoke", json.get("supportedOperations").get(0).asText());
        assertEquals("enrollAcme", json.get("supportedOperations").get(1).asText());
        assertEquals(CertificateOperation.ENROLL_ACME, CertificateOperation.fromCode("enrollAcme"));
    }

    @Test
    void rejectsTheCode_whenItIsNotACertificateOperation() {
        // given
        String unknownCode = "hologram";

        // when / then
        assertThrows(IllegalArgumentException.class, () -> CertificateOperation.fromCode(unknownCode));
    }

    private static CertificateDetailDto x509Detail() {
        X509CertificateTypeDetailsDto details = new X509CertificateTypeDetailsDto();
        details.setSubjectDn("CN=holder");
        details.setHybridCertificate(true);
        details.setAltSignatureAlgorithm("ML-DSA-65");
        details.setKeyUsage(List.of(CertificateKeyUsage.DIGITAL_SIGNATURE));
        details.setSubjectAlternativeNames(Map.of("dnsName", List.of("holder.example")));
        CertificateDetailDto detail = new CertificateDetailDto();
        detail.setUuid("u");
        detail.setCertificateType(CertificateType.X509);
        detail.setTypeDetails(details);
        return detail;
    }
}
