package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.core.signing.TspAuthenticationMethod;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class TspProfileDtoSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void requestDto_carriesMethods() throws Exception {
        String json = """
                {"name":"p1",
                 "allowedAuthenticationMethods":["clientCertificate","basicPassword"],
                 "vaultProfileUuid":"6b55de1c-844f-11ec-a8a3-0242ac120002"}
                """;
        TspProfileRequestDto dto = mapper.readValue(json, TspProfileRequestDto.class);

        Assertions.assertEquals(
                List.of(TspAuthenticationMethod.CLIENT_CERTIFICATE, TspAuthenticationMethod.BASIC_PASSWORD),
                dto.getAllowedAuthenticationMethods());
    }

    @Test
    void responseDto_serializesMethodsAsWireCodes() throws Exception {
        TspProfileDto dto = new TspProfileDto();
        dto.setAllowedAuthenticationMethods(List.of(TspAuthenticationMethod.CLIENT_CERTIFICATE));

        String json = mapper.writeValueAsString(dto);

        Assertions.assertTrue(json.contains("\"clientCertificate\""),
                "enum must serialize to its @JsonValue wire code, not the enum name");
    }

    @Test
    void findByCode_unknownCode_throwsValidationExceptionEchoingTheCode() {
        ValidationException ex = Assertions.assertThrows(ValidationException.class,
                () -> TspAuthenticationMethod.findByCode("bogus"));
        Assertions.assertTrue(ex.getMessage().contains("bogus"),
                "validation error must echo the rejected code: " + ex.getMessage());
    }
}
