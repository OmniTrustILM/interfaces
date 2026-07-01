package com.otilm.api.model.connector.v3.certificate;

import com.otilm.api.model.core.certificate.CertificateType;
import com.otilm.api.model.core.certificate.GeneralNameType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class RequestContentDualWireTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void retainsFlatSubjectDn_whenStructuredContentAlsoProvidedOnRegistration() throws Exception {
        // given — a register body carrying both the flat subjectDn and the structured content
        var subjectDn = "CN=device-7";
        CertificateRegistrationRequestDtoV3 dto = new CertificateRegistrationRequestDtoV3();
        dto.setAuthorityAttributes(List.of());
        dto.setRaProfileAttributes(List.of());
        dto.setSubjectDn(subjectDn);
        dto.setRequestContent(x509Content());

        // when
        CertificateRegistrationRequestDtoV3 back =
                mapper.readValue(mapper.writeValueAsString(dto), CertificateRegistrationRequestDtoV3.class);

        // then — the flat field survives and the structured content resolves to its concrete subtype
        assertEquals(subjectDn, back.getSubjectDn());
        assertInstanceOf(X509RequestContent.class, back.getRequestContent());
        X509RequestContent x509 = (X509RequestContent) back.getRequestContent();
        assertEquals(CertificateType.X509, x509.getCertificateType());
        assertEquals("device-7", x509.getSubject().get(0).getValue());
    }

    @Test
    void omitsRequestContentFromJson_whenNull() throws Exception {
        // given — an issue body with no structured content
        CertificateSignRequestDtoV3 sign = new CertificateSignRequestDtoV3();
        sign.setAuthorityAttributes(List.of());
        sign.setRaProfileAttributes(List.of());
        sign.setRequest("Zm9v");
        assertNull(sign.getRequestContent());

        // when
        String json = mapper.writeValueAsString(sign);

        // then — the null field is absent from the wire and the body still round-trips
        assertFalse(mapper.readTree(json).has("requestContent"), "null requestContent must be omitted from the wire");
        CertificateSignRequestDtoV3 back = mapper.readValue(json, CertificateSignRequestDtoV3.class);
        assertNull(back.getRequestContent());
    }

    @Test
    void roundTripsStructuredContent_onRenew() throws Exception {
        // given — a renew body carrying structured content
        CertificateRenewRequestDtoV3 renew = new CertificateRenewRequestDtoV3();
        renew.setAuthorityAttributes(List.of());
        renew.setRaProfileAttributes(List.of());
        renew.setExistingCertificate("Y2VydA==");
        renew.setReuseKey(true);
        renew.setRequestContent(x509Content());

        // when
        CertificateRenewRequestDtoV3 back =
                mapper.readValue(mapper.writeValueAsString(renew), CertificateRenewRequestDtoV3.class);

        // then
        assertInstanceOf(X509RequestContent.class, back.getRequestContent());
        assertEquals(CertificateType.X509, back.getRequestContent().getCertificateType());
    }

    @Test
    void deserializesLegacyJson_whenRequestContentAbsent() throws Exception {
        // given — a legacy register body written before requestContent existed
        var legacy = "{\"authorityAttributes\":[],\"raProfileAttributes\":[],\"subjectDn\":\"CN=x\"}";

        // when
        CertificateRegistrationRequestDtoV3 back =
                mapper.readValue(legacy, CertificateRegistrationRequestDtoV3.class);

        // then — the absent field deserializes to null with no contract change
        assertEquals("CN=x", back.getSubjectDn());
        assertNull(back.getRequestContent());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private static X509RequestContent x509Content() {
        RdnEntry cn = new RdnEntry();
        cn.setType("CN");
        cn.setValue("device-7");
        GeneralNameEntry san = new GeneralNameEntry();
        san.setType(GeneralNameType.DNS);
        san.setValue("device-7.acme.test");
        X509RequestContent content = new X509RequestContent();
        content.setCertificateType(CertificateType.X509);
        content.setSubject(List.of(cn));
        content.setSubjectAltNames(List.of(san));
        return content;
    }
}
