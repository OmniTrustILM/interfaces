package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.common.signature.parameters.SignatureParametersDto;
import com.otilm.api.model.common.validation.OidFormat;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentSigningWorkflowSchemaGenerationTest {

    private static final String UNION_REF = "#/components/schemas/SignatureParameters";
    private static final String GROUP_REF = "#/components/schemas/SignatureParameterGroup";
    private static final String UNION_COMPONENT = "SignatureParameters";

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
    void theRequestPublishesTheAllowListAsAUniqueCollectionOfGroupRefs() {
        assertAllowListPublishesGroupRefs(ContentSigningWorkflowRequestDto.class);
    }

    @Test
    void theResponsePublishesTheAllowListAsAUniqueCollectionOfGroupRefs() {
        assertAllowListPublishesGroupRefs(ContentSigningWorkflowDto.class);
    }

    private static void assertAllowListPublishesGroupRefs(Class<?> surface) {
        Schema<?> groups = generate(surface).get("allowedRequestParameterGroups");

        assertEquals("array", groups.getType());
        assertEquals(Boolean.TRUE, groups.getUniqueItems());
        assertNotNull(groups.getItems(), "the allow-list publishes no item schema");
        assertEquals(GROUP_REF, groups.getItems().get$ref(),
                "the group items must reference the shared enum component");
    }

    @Test
    void theAllowListDescriptionSurvivesTheEnumRefItems() {
        assertNotNull(
                generate(ContentSigningWorkflowRequestDto.class).get("allowedRequestParameterGroups").getDescription(),
                "the allow-list publishes no description");
        assertNotNull(generate(ContentSigningWorkflowDto.class).get("allowedRequestParameterGroups").getDescription(),
                "the allow-list publishes no description");
    }

    @Test
    void bothSurfacesPublishTheTypedDefaultsAsARefToTheSharedUnion() {
        assertEquals(UNION_REF,
                generate(ContentSigningWorkflowRequestDto.class).get("defaultSignatureParameters").get$ref());
        assertEquals(UNION_REF, generate(ContentSigningWorkflowDto.class).get("defaultSignatureParameters").get$ref());
    }

    @Test
    void theSharedUnionComponentIsIdenticalFromBothWorkflowSurfaces() {
        Schema<?> viaRequest = readAll(ContentSigningWorkflowRequestDto.class).get(UNION_COMPONENT);
        Schema<?> viaResponse = readAll(ContentSigningWorkflowDto.class).get(UNION_COMPONENT);
        Schema<?> standalone = readAll(SignatureParametersDto.class).get(UNION_COMPONENT);

        assertNotNull(standalone, "the shared union must be emitted on its own");
        assertNotNull(viaRequest, "the shared union must be emitted on the request path");
        assertNotNull(viaResponse, "the shared union must be emitted on the response path");
        assertTrue(standalone.getDescription().startsWith("Signature parameters a caller may influence"),
                "the union must keep its own description; was: " + standalone.getDescription());
        assertEquals(standalone.getDescription(), viaRequest.getDescription(),
                "the request's field description must not hoist onto the shared union");
        assertEquals(standalone.getDescription(), viaResponse.getDescription(),
                "the response's field description must not hoist onto the shared union");
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
