package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.core.signing.TspAuthenticationMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class TspProfileDtoSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void requestDto_carriesMethodsAndCredentials() throws Exception {
        String json = """
                {"name":"p1",
                 "allowedAuthenticationMethods":["clientCertificate","basicPassword"],
                 "basicCredentials":[{"username":"u","password":"p","mappedUserUuid":"6b55de1c-844f-11ec-a8a3-0242ac120002"}]}
                """;
        TspProfileRequestDto dto = mapper.readValue(json, TspProfileRequestDto.class);

        Assertions.assertEquals(
                List.of(TspAuthenticationMethod.CLIENT_CERTIFICATE, TspAuthenticationMethod.BASIC_PASSWORD),
                dto.getAllowedAuthenticationMethods());
        Assertions.assertEquals(1, dto.getBasicCredentials().size());
        Assertions.assertEquals("u", dto.getBasicCredentials().get(0).getUsername());
    }

    @Test
    void responseDto_serializesMethodsAsWireCodesAndDefaultsEmptyCredentials() throws Exception {
        TspProfileDto dto = new TspProfileDto();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.CLIENT_CERTIFICATE));

        String json = mapper.writeValueAsString(dto);

        Assertions.assertTrue(json.contains("\"clientCertificate\""),
                "enum must serialize to its @JsonValue wire code, not the enum name");
        Assertions.assertTrue(json.contains("\"basicCredentials\":[]"),
                "basicCredentials must serialize as an empty array by default");
    }
}
