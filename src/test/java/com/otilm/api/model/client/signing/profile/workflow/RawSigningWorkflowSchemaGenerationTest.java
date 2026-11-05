package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.common.validation.OidFormat;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RawSigningWorkflowSchemaGenerationTest {

    @Test
    void theRequestPublishesTheItemCapWithAMinimumOfOne() {
        assertEquals(BigDecimal.ONE,
                generate(RawSigningWorkflowRequestDto.class).get("maxItemsPerRequest").getMinimum());
    }

    @Test
    void theResponsePublishesTheItemCapWithAMinimumOfOne() {
        assertEquals(BigDecimal.ONE, generate(RawSigningWorkflowDto.class).get("maxItemsPerRequest").getMinimum());
    }

    @Test
    void theRequestPublishesTheEkuOidsAsAUniqueCollectionOfPatternedStrings() {
        Schema<?> oids = generate(RawSigningWorkflowRequestDto.class).get("requiredExtendedKeyUsageOids");

        assertEquals("array", oids.getType());
        assertEquals(Boolean.TRUE, oids.getUniqueItems());
        assertNotNull(oids.getItems(), "the OID collection publishes no item schema");
        assertEquals("string", oids.getItems().getType());
        assertEquals(OidFormat.REGEX, oids.getItems().getPattern());
    }

    @Test
    void theResponsePublishesTheEkuOidsAsAUniqueCollectionOfStrings() {
        Schema<?> oids = generate(RawSigningWorkflowDto.class).get("requiredExtendedKeyUsageOids");

        assertEquals("array", oids.getType());
        assertEquals(Boolean.TRUE, oids.getUniqueItems());
        assertNotNull(oids.getItems(), "the OID collection publishes no item schema");
        assertEquals("string", oids.getItems().getType());
    }

    @Test
    void theRequestPublishesTheNonRepudiationFlagWithNoDefault() {
        assertNull(generate(RawSigningWorkflowRequestDto.class).get("requireNonRepudiation").getDefault());
    }

    @Test
    void theResponsePublishesTheNonRepudiationFlagAsDefaultingToFalse() {
        assertEquals(Boolean.FALSE, generate(RawSigningWorkflowDto.class).get("requireNonRepudiation").getDefault());
    }

    @Test
    void neitherSchemaPublishesADigestAlgorithmList() {
        assertFalse(generate(RawSigningWorkflowRequestDto.class).containsKey("allowedDigestAlgorithms"));
        assertFalse(generate(RawSigningWorkflowDto.class).containsKey("allowedDigestAlgorithms"));
    }

    private static Map<String, Schema<?>> generate(Class<?> type) {
        Schema<?> schema = readAll(type)
                .get(type.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class).name());
        assertNotNull(schema, "expected a generated schema for " + type.getSimpleName());
        return properties(schema);
    }

    /** {@code ModelConverters.readAll} and {@code Schema.getProperties} are raw; the casts are contained here. */
    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> readAll(Class<?> type) {
        return (Map<String, Schema<?>>) (Map<String, ?>) ModelConverters.getInstance().readAll(type);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> properties(Schema<?> schema) {
        return (Map<String, Schema<?>>) (Map<String, ?>) schema.getProperties();
    }
}
