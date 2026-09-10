package com.otilm.api.model.client.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.core.acme.AcmeEabKeyDto;
import com.otilm.api.model.core.acme.AcmeProfileDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcmeProfileEabSecretsTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void secretUuidsDefaultToAnEmptyListRatherThanNull() {
        assertEquals(List.of(), new AcmeProfileRequestDto().getEabSecretUuids());
        assertEquals(List.of(), new AcmeProfileDto().getEabSecretUuids());
    }

    @Test
    void anOmittedEditListStaysDistinguishableFromAClearedOne() throws Exception {
        // Defaulting to empty on a partial edit would let an unrelated change switch the requirement off.
        assertNull(new AcmeProfileEditRequestDto().getEabSecretUuids());
        assertNull(mapper
                .readValue("{\"description\":\"unrelated\"}", AcmeProfileEditRequestDto.class)
                .getEabSecretUuids());
        assertEquals(List.of(),
                mapper.readValue("{\"eabSecretUuids\":[]}", AcmeProfileEditRequestDto.class).getEabSecretUuids(),
                "an explicit empty array is how the requirement is turned off");
    }

    @Test
    void secretUuidsRoundTrip() throws Exception {
        UUID secret = UUID.fromString("c2f685d4-6a3e-11ec-90d6-0242ac120003");
        AcmeProfileRequestDto request = new AcmeProfileRequestDto();
        request.setEabSecretUuids(List.of(secret));

        AcmeProfileRequestDto back = mapper.readValue(mapper.writeValueAsString(request), AcmeProfileRequestDto.class);

        assertEquals(List.of(secret), back.getEabSecretUuids());
    }

    @Test
    void secretUuidsAppearInToString() {
        AcmeProfileEditRequestDto request = new AcmeProfileEditRequestDto();
        request.setEabSecretUuids(List.of(UUID.fromString("c2f685d4-6a3e-11ec-90d6-0242ac120003")));

        assertTrue(request.toString().contains("c2f685d4-6a3e-11ec-90d6-0242ac120003"));
    }

    @Test
    void generatedKeySerializesButNeverReachesToString() throws Exception {
        AcmeEabKeyDto dto = new AcmeEabKeyDto();
        dto.setKey("key-sentinel");

        assertTrue(mapper.writeValueAsString(dto).contains("key-sentinel"),
                "the generated key is the whole point of the response body");
        assertFalse(dto.toString().contains("key-sentinel"), "a generated HMAC key must not reach a log line");
    }
}
