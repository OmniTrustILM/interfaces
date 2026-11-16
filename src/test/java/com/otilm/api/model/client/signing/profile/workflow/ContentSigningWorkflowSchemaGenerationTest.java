package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.common.validation.OidFormat;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ContentSigningWorkflowSchemaGenerationTest {

    @Test
    void theRequestPublishesTheEkuOidsAsAUniqueCollectionOfPatternedStrings() {
        Schema<?> oids = generate(ContentSigningWorkflowRequestDto.class).get("requiredExtendedKeyUsageOids");

        assertEquals("array", oids.getType());
        assertEquals(Boolean.TRUE, oids.getUniqueItems());
        assertNotNull(oids.getItems(), "the OID collection publishes no item schema");
        assertEquals("string", oids.getItems().getType());
        assertEquals(OidFormat.REGEX, oids.getItems().getPattern());
    }

    @Test
    void theResponsePublishesTheEkuOidsAsAUniqueCollectionOfStrings() {
        Schema<?> oids = generate(ContentSigningWorkflowDto.class).get("requiredExtendedKeyUsageOids");

        assertEquals("array", oids.getType());
        assertEquals(Boolean.TRUE, oids.getUniqueItems());
        assertNotNull(oids.getItems(), "the OID collection publishes no item schema");
        assertEquals("string", oids.getItems().getType());
    }

    @Test
    void theRequestPublishesTheNonRepudiationFlagWithNoDefault() {
        assertNull(generate(ContentSigningWorkflowRequestDto.class).get("requireNonRepudiation").getDefault());
    }

    @Test
    void theResponsePublishesTheNonRepudiationFlagAsDefaultingToFalse() {
        assertEquals(Boolean.FALSE,
                generate(ContentSigningWorkflowDto.class).get("requireNonRepudiation").getDefault());
    }

    @Test
    void theRequestPublishesItsConfigurationFields() {
        assertEquals(
                Set
                        .of("type", "signatureFormattingConnectorUuid", "signatureFormattingConnectorAttributes",
                                "family", "maxLevel", "timestampSource", "requireNonRepudiation",
                                "requiredExtendedKeyUsageOids", "allowedRequestAttributes"),
                generate(ContentSigningWorkflowRequestDto.class).keySet());
    }

    @Test
    void theResponsePublishesItsConfigurationFields() {
        assertEquals(Set
                .of("type", "signatureFormattingConnector", "signatureFormattingConnectorAttributes", "family",
                        "maxLevel", "timestampSource", "requireNonRepudiation", "requiredExtendedKeyUsageOids",
                        "allowedRequestAttributes"),
                generate(ContentSigningWorkflowDto.class).keySet());
    }

    @Test
    void theRequestPublishesTheAllowListAsAUniqueCollectionOfNames() {
        assertAllowListPublishesNames(ContentSigningWorkflowRequestDto.class);
    }

    @Test
    void theResponsePublishesTheAllowListAsAUniqueCollectionOfNames() {
        assertAllowListPublishesNames(ContentSigningWorkflowDto.class);
    }

    private static void assertAllowListPublishesNames(Class<?> surface) {
        Schema<?> names = generate(surface).get("allowedRequestAttributes");

        assertEquals("array", names.getType());
        assertEquals(Boolean.TRUE, names.getUniqueItems());
        assertNotNull(names.getItems(), "the allow-list publishes no item schema");
        assertEquals("string", names.getItems().getType());
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
