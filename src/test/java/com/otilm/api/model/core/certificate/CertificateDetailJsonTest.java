package com.otilm.api.model.core.certificate;

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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateDetailJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void writesTheX509FieldsFlatAtTheTopLevel_whenTheCertificateIsX509() throws Exception {
        // given
        CertificateDetail detail = x509Detail();

        // when
        JsonNode json = mapper.readTree(mapper.writeValueAsString(detail));

        // then
        assertEquals("X.509", json.get("certificateType").asText());
        assertEquals("CN=holder", json.get("subjectDn").asText());
        assertEquals("digitalSignature", json.get("keyUsage").get(0).asText());
        assertFalse(json.has("typeDetails"));
    }

    @Test
    void writesTheCertificateTypePropertyOnce_whenTheCertificateIsX509() throws Exception {
        // given
        CertificateDetail detail = x509Detail();

        // when
        String json = mapper.writeValueAsString(detail);

        // then
        assertEquals(1, json.split("\"certificateType\"", -1).length - 1);
    }

    @Test
    void readsTheX509Subclass_whenTheJsonCarriesTheX509CertificateType() throws Exception {
        // given
        String json = mapper.writeValueAsString(x509Detail());

        // when
        CertificateDetail read = mapper.readValue(json, CertificateDetail.class);

        // then
        X509CertificateDetail x509 = assertInstanceOf(X509CertificateDetail.class, read);
        assertEquals("CN=holder", x509.getSubjectDn());
        assertEquals(List.of(CertificateKeyUsage.DIGITAL_SIGNATURE), x509.getKeyUsage());
        assertTrue(x509.isHybridCertificate());
        assertEquals("ML-DSA-65", x509.getAltSignatureAlgorithm());
    }

    @Test
    void readsAResponseOfBefore_whenItCarriesTheFlatX509Fields() throws Exception {
        // given — the shape the certificate detail had before the type became a subclass
        String olderResponse = """
                {"uuid":"u","certificateType":"X.509","subjectDn":"CN=holder","keyUsage":["digitalSignature"],
                 "extendedKeyUsage":["1.3.6.1.5.5.7.3.1"],"subjectAlternativeNames":{"dnsName":["holder.example"]},
                 "keystoreAvailable":false}
                """;

        // when
        CertificateDetail read = mapper.readValue(olderResponse, CertificateDetail.class);

        // then
        X509CertificateDetail x509 = assertInstanceOf(X509CertificateDetail.class, read);
        assertEquals(List.of(CertificateKeyUsage.DIGITAL_SIGNATURE), x509.getKeyUsage());
        assertEquals(List.of("1.3.6.1.5.5.7.3.1"), x509.getExtendedKeyUsage());
        assertEquals(List.of("holder.example"), x509.getSubjectAlternativeNames().get("dnsName"));
        assertTrue(x509.getSupportedOperations().isEmpty());
    }

    @Test
    void readsTheX509RequestSubclass_whenTheRequestIsX509() throws Exception {
        // given
        String json = "{\"certificateType\":\"X.509\",\"subjectDn\":\"CN=holder\"}";

        // when
        CertificateRequestDetail read = mapper.readValue(json, CertificateRequestDetail.class);

        // then
        X509CertificateRequestDetail x509 = assertInstanceOf(X509CertificateRequestDetail.class, read);
        assertEquals("CN=holder", x509.getSubjectDn());
    }

    @Test
    void readsNoSignatureAlgorithm_whenARequestOmitsIt() throws Exception {
        // given
        String json = "{\"certificateType\":\"X.509\",\"displayName\":\"holder\"}";

        // when
        CertificateRequestDetail read = mapper.readValue(json, CertificateRequestDetail.class);

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
        CertificateDetail detail = x509Detail();
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

    private static CertificateDetail x509Detail() {
        X509CertificateDetail detail = new X509CertificateDetail();
        detail.setUuid("u");
        detail.setCertificateType(CertificateType.X509);
        detail.setSubjectDn("CN=holder");
        detail.setHybridCertificate(true);
        detail.setAltSignatureAlgorithm("ML-DSA-65");
        detail.setKeyUsage(List.of(CertificateKeyUsage.DIGITAL_SIGNATURE));
        detail.setSubjectAlternativeNames(Map.of("dnsName", List.of("holder.example")));
        return detail;
    }
}
