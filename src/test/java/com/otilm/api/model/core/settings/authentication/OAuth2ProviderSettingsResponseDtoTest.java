package com.otilm.api.model.core.settings.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.otilm.api.model.common.enums.PlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OAuth2ProviderSettingsResponseDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void jwkSetLoadFailureRoundTripsAsStableEnumValue() throws Exception {
        OAuth2ProviderSettingsResponseDto response = new OAuth2ProviderSettingsResponseDto();
        response.setName("provider");
        response.setJwkSetKeys(List.of());
        response.setJwkSetLoadFailure(JwkSetLoadFailure.UNAVAILABLE);

        String json = mapper.writeValueAsString(response);
        OAuth2ProviderSettingsResponseDto decoded = mapper.readValue(json, OAuth2ProviderSettingsResponseDto.class);

        Assertions.assertTrue(json.contains("\"jwkSetLoadFailure\":\"unavailable\""), json);
        Assertions.assertEquals(JwkSetLoadFailure.UNAVAILABLE, decoded.getJwkSetLoadFailure());
    }

    @Test
    void jwkSetLoadFailureIsPublishedAsAReusablePlatformEnum() {
        for (JwkSetLoadFailure failure : JwkSetLoadFailure.values()) {
            Assertions.assertInstanceOf(IPlatformEnum.class, failure);
            Assertions.assertEquals(failure, JwkSetLoadFailure.findByCode(failure.getCode()));
            Assertions.assertFalse(failure.getLabel().isBlank());
            Assertions.assertFalse(failure.getDescription().isBlank());
        }

        Assertions.assertEquals("unavailable", JwkSetLoadFailure.UNAVAILABLE.getCode());
        Assertions.assertEquals("invalid", JwkSetLoadFailure.INVALID.getCode());
        Assertions.assertEquals("tooLarge", JwkSetLoadFailure.TOO_LARGE.getCode());
        Assertions.assertEquals(JwkSetLoadFailure.class, PlatformEnum.JWK_SET_LOAD_FAILURE.getEnumClass());
        Assertions.assertThrows(ValidationException.class, () -> JwkSetLoadFailure.findByCode("unknown"));

        Schema schema = JwkSetLoadFailure.class.getAnnotation(Schema.class);
        Assertions.assertNotNull(schema);
        Assertions.assertTrue(schema.enumAsRef());
    }
}
