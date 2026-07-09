package com.otilm.api.model.core.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ClientCertificateRekeyRequestDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void authorizationSecretIsAcceptedOnInputButNeverSerialized() throws Exception {
        ClientCertificateRekeyRequestDto dto =
                mapper.readValue("{\"authorizationSecret\":\"s3cret\"}", ClientCertificateRekeyRequestDto.class);
        assertEquals("s3cret", dto.getAuthorizationSecret());

        String json = mapper.writeValueAsString(dto);
        assertFalse(json.contains("authorizationSecret"),
                "write-only authorizationSecret must never be serialized back to a client");
        assertFalse(json.contains("s3cret"));
    }

    @Test
    void toStringOmitsAuthorizationSecret() {
        ClientCertificateRekeyRequestDto dto = new ClientCertificateRekeyRequestDto();
        dto.setAuthorizationSecret("s3cret");
        assertFalse(dto.toString().contains("s3cret"),
                "authorizationSecret is @ToString.Exclude and must not appear in the generated toString");
    }

    @Test
    void authorizationSecretIsExcludedFromEqualsAndHashCode() {
        ClientCertificateRekeyRequestDto a = new ClientCertificateRekeyRequestDto();
        a.setAuthorizationSecret("secret-a");
        ClientCertificateRekeyRequestDto b = new ClientCertificateRekeyRequestDto();
        b.setAuthorizationSecret("secret-b");
        assertEquals(a, b, "authorizationSecret must not affect equals()");
        assertEquals(a.hashCode(), b.hashCode(), "authorizationSecret must not affect hashCode()");
    }
}
