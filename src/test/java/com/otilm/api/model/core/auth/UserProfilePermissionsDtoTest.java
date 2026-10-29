package com.otilm.api.model.core.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.testsupport.OpenApiSchemaTestSupport;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserProfilePermissionsDtoTest {

    private static final String PROFILE_SCHEMA = "UserProfilePermissionsDto";
    private static final String ACTIONS_SCHEMA = "ResourceActionsDto";
    private static final String OBJECT_ACTIONS_SCHEMA = "ObjectActionsDto";
    private static final String SECRET_UUID = "2f8c1f4e-3b7a-4d2e-9c61-0a5e8d7b4f13";

    private final ObjectMapper mapper = new ObjectMapper();

    private static Map<String, Schema> schemas() {
        return OpenApiSchemaTestSupport.openApi31Schemas(UserProfilePermissionsDto.class);
    }

    private static Schema<?> property(String schemaName, String propertyName) {
        Schema<?> schema = schemas().get(schemaName);

        assertTrue(schema != null, schemaName + " is not published at all");
        Schema<?> property = (Schema<?>) schema.getProperties().get(propertyName);

        assertTrue(property != null, schemaName + "." + propertyName + " is not published");
        return property;
    }

    private static UserProfilePermissionsDto settingsEditor() {
        return new UserProfilePermissionsDto(List.of(Resource.SETTINGS),
                List
                        .of(new ResourceActionsDto(Resource.SETTINGS,
                                List.of(ResourceAction.UPDATE, ResourceAction.UPDATE_BRANDING))),
                List.of(new ObjectActionsDto(Resource.SECRET, SECRET_UUID, List.of(ResourceAction.DETAIL))));
    }

    @Test
    void serializesResourcesAndActionsAsTheirWireCodes() throws Exception {
        // @JsonValue on both enums is the only reason UPDATE_BRANDING arrives as updateBranding.
        assertEquals("{\"allowedListings\":[\"settings\"],"
                + "\"allowedActions\":[{\"resource\":\"settings\",\"actions\":[\"update\",\"updateBranding\"]}],"
                + "\"objectActions\":[{\"resource\":\"secrets\",\"objectUuid\":\"" + SECRET_UUID
                + "\",\"actions\":[\"detail\"]}]}", mapper.writeValueAsString(settingsEditor()));
    }

    /** A client gating a control on an absent field would fail open, so no list may be optional. */
    @Test
    void publishesEveryPermissionListAsRequired() {
        assertEquals(Set.of("allowedListings", "allowedActions", "objectActions"),
                Set.copyOf(schemas().get(PROFILE_SCHEMA).getRequired()));
    }

    @Test
    void publishesBothResourceActionFieldsAsRequired() {
        assertEquals(Set.of("resource", "actions"), Set.copyOf(schemas().get(ACTIONS_SCHEMA).getRequired()));
    }

    @Test
    void publishesEveryObjectActionFieldAsRequired() {
        assertEquals(Set.of("resource", "objectUuid", "actions"),
                Set.copyOf(schemas().get(OBJECT_ACTIONS_SCHEMA).getRequired()));
    }

    @Test
    void publishesTheResourceActionsSemanticsOnTheSchemaRatherThanInJavadoc() {
        assertTrue(
                schemas().get(ACTIONS_SCHEMA).getDescription().contains("authorization service remains the only gate"),
                "the published ResourceActionsDto description no longer states that this is a hint, not a gate");
    }

    /** A level that resolves to a bare array leaves a generated client untyped maps and loose strings, not enums. */
    @Test
    void publishesTypedReferencesThroughEveryNestedList() {
        assertEquals("#/components/schemas/Resource", property(PROFILE_SCHEMA, "allowedListings").getItems().get$ref());
        assertEquals("#/components/schemas/" + ACTIONS_SCHEMA,
                property(PROFILE_SCHEMA, "allowedActions").getItems().get$ref());
        assertEquals("#/components/schemas/Resource", property(ACTIONS_SCHEMA, "resource").get$ref());
        assertEquals("#/components/schemas/ResourceAction", property(ACTIONS_SCHEMA, "actions").getItems().get$ref());
        assertEquals("#/components/schemas/" + OBJECT_ACTIONS_SCHEMA,
                property(PROFILE_SCHEMA, "objectActions").getItems().get$ref());
        assertEquals("#/components/schemas/Resource", property(OBJECT_ACTIONS_SCHEMA, "resource").get$ref());
        assertEquals("#/components/schemas/ResourceAction",
                property(OBJECT_ACTIONS_SCHEMA, "actions").getItems().get$ref());

        assertTrue(schemas().get("ResourceAction").getEnum().contains(ResourceAction.UPDATE_BRANDING.getCode()),
                "the referenced action enum does not publish the code clients gate branding on");
    }

    /** Asserted on the prose deliberately: rewording is allowed, dropping a constraint has to be said here first. */
    @Test
    void documentsTheConstraintsAClientCannotInferFromTheTypes() {
        assertDocuments("allowedActions", "resource-level grants only", "Object-scoped grants are excluded",
                "reported in objectActions", "omitted rather than reported with an empty list",
                "not derivable from allowedListings");
        assertDocuments("objectActions", "explicit object-scoped grants only", "belongs to its owning group",
                "Ownership is not listed", "Denials are not reported",
                "omitted rather than reported with an empty list");
        assertDocuments("allowedListings", "may manage group members", "granted to every user by default",
                "Certificate, Cryptographic Key, Secret", "owns at least one", "belongs to a group that owns one",
                "not listed for a user who has none of these");
    }

    private static void assertDocuments(String propertyName, String... constraints) {
        String description = property(PROFILE_SCHEMA, propertyName).getDescription();

        for (String constraint : constraints) {
            assertTrue(description.contains(constraint), "the published " + propertyName
                    + " description no longer states: " + constraint + "\ngot: " + description);
        }
    }
}
