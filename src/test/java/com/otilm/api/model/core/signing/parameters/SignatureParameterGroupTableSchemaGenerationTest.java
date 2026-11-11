package com.otilm.api.model.core.signing.parameters;

import com.fasterxml.jackson.databind.JsonNode;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Guards the group table's OpenAPI schema as a generated client reads it. */
class SignatureParameterGroupTableSchemaGenerationTest {

    private static final String FAMILY_GROUPS = "SignatureFamilyParameterGroups";
    private static final String GROUP_FIELDS = "SignatureParameterGroupFields";
    private static final String REF = "#/components/schemas/";

    @Test
    void familyIsPublishedAsAPatternedCodeAndGroupAsTheSharedEnum() {
        Map<String, Schema> schemas = openApi31Schemas(SignatureFamilyParameterGroupsDto.class);

        Schema<?> family = property(schemas, FAMILY_GROUPS, "family");
        assertEquals(Set.of("string"), family.getTypes());
        assertEquals(SignatureFamily.CODE_PATTERN, family.getPattern());
        assertEquals(REF + "SignatureParameterGroup", property(schemas, GROUP_FIELDS, "group").get$ref());
        assertEquals(REF + GROUP_FIELDS, property(schemas, FAMILY_GROUPS, "groups").getItems().get$ref());
    }

    @Test
    void everyPropertyIsRequired() {
        Map<String, Schema> schemas = openApi31Schemas(SignatureFamilyParameterGroupsDto.class);

        assertEquals(Set.of("family", "groups"), Set.copyOf(schemas.get(FAMILY_GROUPS).getRequired()));
        assertEquals(Set.of("group", "fields"), Set.copyOf(schemas.get(GROUP_FIELDS).getRequired()));
    }

    @Test
    void theFieldsExampleIsAPathTheTableLists() {
        Schema<?> fields = property(openApi31Schemas(SignatureFamilyParameterGroupsDto.class), GROUP_FIELDS, "fields");
        assertNotNull(fields.getExample(), "fields must show a path as its example");
        List<String> examplePaths = new ArrayList<>();
        ((JsonNode) fields.getExample()).forEach(path -> examplePaths.add(path.asText()));

        List<String> listed = SignatureParameterGroupTable
                .forEveryFamily()
                .stream()
                .flatMap(family -> family.getGroups().stream())
                .filter(group -> group.getGroup() == SignatureParameterGroup.VISIBLE_SIGNATURE_PLACEMENT)
                .flatMap(group -> group.getFields().stream())
                .toList();
        assertTrue(listed.containsAll(examplePaths), examplePaths + " must be placement paths the table lists");
    }

    @Test
    void descriptionsReadForAnyClient() {
        Map<String, Schema> schemas = openApi31Schemas(SignatureFamilyParameterGroupsDto.class);

        for (String schemaName : List.of(FAMILY_GROUPS, GROUP_FIELDS)) {
            Schema<?> schema = schemas.get(schemaName);
            assertLanguageNeutral(schemaName, schema.getDescription());
            schema.getProperties().forEach((name, property) -> {
                assertNotNull(property.getDescription(), schemaName + "." + name + " has no description");
                assertLanguageNeutral(schemaName + "." + name, property.getDescription());
            });
        }
    }

    private static Schema<?> property(Map<String, Schema> schemas, String schemaName, String propertyName) {
        Schema<?> schema = schemas.get(schemaName);
        assertNotNull(schema, "expected a generated schema named " + schemaName);
        Schema<?> property = (Schema<?>) schema.getProperties().get(propertyName);
        assertNotNull(property, schemaName + " has no property " + propertyName);
        return property;
    }
}
