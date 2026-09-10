package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewAccountRequestTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void externalAccountBindingDeserializesWithTheRfcMemberNames() throws Exception {
        NewAccountRequest request = mapper
                .readValue(
                        """
                                {"termsOfServiceAgreed":true,
                                 "externalAccountBinding":{"protected":"eyJhbGciOiJIUzI1NiJ9","payload":"eyJrdHkiOiJFQyJ9","signature":"c2ln"}}
                                """,
                        NewAccountRequest.class);

        ExternalAccountBinding eab = request.getExternalAccountBinding();
        assertEquals("eyJhbGciOiJIUzI1NiJ9", eab.getProtectedHeader());
        assertEquals("eyJrdHkiOiJFQyJ9", eab.getPayload());
        assertEquals("c2ln", eab.getSignature());
    }

    @Test
    void externalAccountBindingSerializesProtectedUnderTheRfcMemberName() throws Exception {
        ExternalAccountBinding eab = new ExternalAccountBinding();
        eab.setProtectedHeader("hdr");
        eab.setPayload("pl");
        eab.setSignature("sig");
        NewAccountRequest request = new NewAccountRequest();
        request.setExternalAccountBinding(eab);

        String json = mapper.writeValueAsString(request);

        assertTrue(json.contains("\"protected\":\"hdr\""));
        assertFalse(json.contains("protectedHeader"), "the Java field name must not leak into the wire format");
    }

    @Test
    void externalAccountBindingIsOptional() throws Exception {
        NewAccountRequest request = mapper.readValue("{\"termsOfServiceAgreed\":true}", NewAccountRequest.class);

        assertNull(request.getExternalAccountBinding());
    }

    @Test
    void toStringCarriesTheBindingHeaderButNotItsMac() {
        ExternalAccountBinding eab = new ExternalAccountBinding();
        eab.setProtectedHeader("hdr-sentinel");
        eab.setSignature("mac-sentinel");
        NewAccountRequest request = new NewAccountRequest();
        request.setExternalAccountBinding(eab);

        assertTrue(request.toString().contains("hdr-sentinel"));
        assertFalse(request.toString().contains("mac-sentinel"), "the binding MAC must not reach a log line");
    }
}
