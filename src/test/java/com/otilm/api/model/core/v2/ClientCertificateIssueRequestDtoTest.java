package com.otilm.api.model.core.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClientCertificateIssueRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void authorizationSecretIsAcceptedOnInputButNeverSerialized() throws Exception {
        ClientCertificateIssueRequestDto dto =
                mapper.readValue("{\"authorizationSecret\":\"s3cret\"}", ClientCertificateIssueRequestDto.class);
        assertEquals("s3cret", dto.getAuthorizationSecret());

        dto.setRequest("Zm9v");
        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("authorizationSecret"),
                "write-only authorizationSecret must never be serialized back to a client");
        assertFalse(json.contains("s3cret"));
    }

    @Test
    void toStringOmitsAuthorizationSecret() {
        ClientCertificateIssueRequestDto dto = new ClientCertificateIssueRequestDto();
        dto.setAuthorizationSecret("s3cret");
        assertFalse(dto.toString().contains("s3cret"), "authorizationSecret must not appear in toString");
    }
}
