package com.otilm.api.model.connector.cryptography.v2.operations;

import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureInputModeGeneratedSchemaTest {

    @Test
    void generatedSchemaPublishesTheOptionalModeAsAnEnumReference() {
        // given
        Map<String, Schema> schemas = openApi31Schemas(SignDataRequestV2Dto.class);

        // when
        Schema<?> requestSchema = schemas.get("SignDataRequestV2Dto");
        Schema<?> modeSchema = schemas.get("SignatureInputMode");

        // then
        assertNotNull(modeSchema);
        assertEquals(List.of("data", "prehashedDigest"), modeSchema.getEnum());
        assertTrue(requestSchema.getProperties().get("signatureInputMode").get$ref().endsWith("/SignatureInputMode"));
        assertFalse(requestSchema.getRequired().contains("signatureInputMode"));
    }
}
