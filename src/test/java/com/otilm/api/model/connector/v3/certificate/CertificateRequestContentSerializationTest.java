package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.core.certificate.CertificateType;
import com.otilm.api.model.core.oid.ExtensionValueEncoding;
import com.otilm.api.model.core.certificate.GeneralNameType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CertificateRequestContentSerializationTest {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    // -------------------------------------------------------------------------
    // X509RequestContent
    // -------------------------------------------------------------------------

    @Test
    void x509RequestContent_serializesDiscriminator() throws Exception {
        X509RequestContent content = new X509RequestContent();
        content.setCertificateType(CertificateType.X509);
        RdnEntry rdn = new RdnEntry();
        rdn.setType("CN");
        rdn.setValue("example.com");
        content.setSubject(List.of(rdn));

        JsonNode json = mapper.valueToTree(content);

        assertEquals(CertificateType.Codes.X509, json.get("certificateType").asText());
    }

    @Test
    void x509RequestContent_deserializesViaBaseClass() throws Exception {
        String json = """
                {
                  "certificateType": "X.509",
                  "subject": [{"type": "CN", "value": "example.com"}]
                }
                """;

        CertificateRequestContent base = mapper.readValue(json, CertificateRequestContent.class);

        assertInstanceOf(X509RequestContent.class, base);
        X509RequestContent result = (X509RequestContent) base;
        assertEquals(CertificateType.X509, result.getCertificateType());
        assertEquals(1, result.getSubject().size());
        assertEquals("CN", result.getSubject().get(0).getType());
        assertEquals("example.com", result.getSubject().get(0).getValue());
    }

    @Test
    void x509RequestContent_withSubject_roundTrip() throws Exception {
        X509RequestContent original = new X509RequestContent();
        original.setCertificateType(CertificateType.X509);
        RdnEntry rdn = new RdnEntry();
        rdn.setType("CN");
        rdn.setValue("example.com");
        original.setSubject(List.of(rdn));

        String json = mapper.writeValueAsString(original);
        CertificateRequestContent deserialized = mapper.readValue(json, CertificateRequestContent.class);

        assertInstanceOf(X509RequestContent.class, deserialized);
        X509RequestContent result = (X509RequestContent) deserialized;
        assertEquals(CertificateType.X509, result.getCertificateType());
        assertEquals(1, result.getSubject().size());
        assertEquals("CN", result.getSubject().get(0).getType());
        assertEquals("example.com", result.getSubject().get(0).getValue());
    }

    @Test
    void x509RequestContent_withSan_roundTrip() throws Exception {
        X509RequestContent original = new X509RequestContent();
        original.setCertificateType(CertificateType.X509);
        GeneralNameEntry san = new GeneralNameEntry();
        san.setType(GeneralNameType.DNS);
        san.setValue("example.com");
        original.setSubjectAltNames(List.of(san));

        String json = mapper.writeValueAsString(original);
        CertificateRequestContent deserialized = mapper.readValue(json, CertificateRequestContent.class);

        assertInstanceOf(X509RequestContent.class, deserialized);
        X509RequestContent result = (X509RequestContent) deserialized;
        assertEquals(1, result.getSubjectAltNames().size());
        assertEquals(GeneralNameType.DNS, result.getSubjectAltNames().get(0).getType());
        assertEquals("example.com", result.getSubjectAltNames().get(0).getValue());
    }

    @Test
    void x509RequestContent_withExtension_roundTrip() throws Exception {
        X509RequestContent original = new X509RequestContent();
        original.setCertificateType(CertificateType.X509);
        RequestedExtension ext = new RequestedExtension();
        ext.setOid("2.5.29.37");
        ext.setCritical(false);
        ext.setEncoding(ExtensionValueEncoding.UTF8_STRING);
        ext.setValue("clientAuth");
        original.setExtensions(List.of(ext));

        String json = mapper.writeValueAsString(original);
        CertificateRequestContent deserialized = mapper.readValue(json, CertificateRequestContent.class);

        assertInstanceOf(X509RequestContent.class, deserialized);
        X509RequestContent result = (X509RequestContent) deserialized;
        assertEquals(1, result.getExtensions().size());
        assertEquals("2.5.29.37", result.getExtensions().get(0).getOid());
        assertFalse(result.getExtensions().get(0).getCritical());
        assertEquals(ExtensionValueEncoding.UTF8_STRING, result.getExtensions().get(0).getEncoding());
        assertEquals("clientAuth", result.getExtensions().get(0).getValue());
    }

    // -------------------------------------------------------------------------
    // Unknown / missing certificateType guards
    // -------------------------------------------------------------------------

    @Test
    void unknownCertificateType_throwsOnDeserialization() {
        String json = """
                {
                  "certificateType": "unknown_certificate_type"
                }
                """;

        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(json, CertificateRequestContent.class));
    }

    @Test
    void missingCertificateType_throwsOnDeserialization() {
        String json = """
                {
                  "subject": [{"type": "CN", "value": "example.com"}]
                }
                """;

        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue(json, CertificateRequestContent.class));
    }
}
