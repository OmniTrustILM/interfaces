package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.common.NameAndUuidDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TspBasicCredentialDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void omitsPassword_fromSerializedResponse() throws Exception {
        // given — a response DTO that has no password field at all
        var username = "svc-account";
        var response = new TspBasicCredentialDto();
        response.setUuid(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        response.setUsername(username);
        var mapped = new NameAndUuidDto();
        mapped.setUuid("22222222-2222-2222-2222-222222222222");
        mapped.setName("real-user");
        response.setMappedUser(mapped);

        // when
        var json = mapper.writeValueAsString(response);

        // then
        assertFalse(json.toLowerCase().contains("password"),
                "Response DTO must never carry a password field: " + json);
        assertTrue(json.contains(username));
    }
}
