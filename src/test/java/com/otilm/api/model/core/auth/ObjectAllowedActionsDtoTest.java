package com.otilm.api.model.core.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.interfaces.core.web.ResourceController;
import com.otilm.api.testsupport.OpenApiSchemaTestSupport;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.models.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectAllowedActionsDtoTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static List<UUID> uuids(int count) {
        return IntStream.range(0, count).mapToObj(i -> UUID.randomUUID()).toList();
    }

    private static boolean isValid(List<UUID> uuids) {
        return VALIDATOR.validate(new ObjectAllowedActionsRequestDto(uuids)).isEmpty();
    }

    private static Map<String, Schema> schemas() {
        return OpenApiSchemaTestSupport.openApi31Schemas(ObjectAllowedActionsResponseDto.class);
    }

    @Test
    void isPublishedAsAPostOnTheResourceScopedObjectsRoute() throws NoSuchMethodException {
        Method method = ResourceController.class
                .getMethod("listObjectAllowedActions", Resource.class, ObjectAllowedActionsRequestDto.class);
        PostMapping mapping = method.getAnnotation(PostMapping.class);

        assertNotNull(mapping, "reporting allowed actions must be a POST, so the UUID list travels in the body");
        assertArrayEquals(new String[]{"/{resource}/objects/allowed-actions"}, mapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.produces());
        assertNotNull(method.getParameters()[0].getAnnotation(PathVariable.class));
        assertNotNull(method.getParameters()[1].getAnnotation(RequestBody.class));
        assertNotNull(method.getParameters()[1].getAnnotation(Valid.class),
                "without @Valid the UUID cap is not enforced");
        assertEquals(ObjectAllowedActionsResponseDto.class, method.getReturnType());
    }

    @Test
    void acceptsBetweenOneAndTheLargestListPageOfUuids() {
        assertTrue(isValid(uuids(1)));
        assertTrue(isValid(uuids(ObjectAllowedActionsRequestDto.MAX_UUIDS)));
        assertEquals(1000, ObjectAllowedActionsRequestDto.MAX_UUIDS);
    }

    @Test
    void rejectsAMissingEmptyOversizedOrNullContainingUuidList() {
        List<UUID> withNull = new ArrayList<>(uuids(2));
        withNull.add(null);

        assertFalse(isValid(null));
        assertFalse(isValid(Collections.emptyList()));
        assertFalse(isValid(uuids(ObjectAllowedActionsRequestDto.MAX_UUIDS + 1)));
        assertFalse(isValid(withNull));
    }

    @Test
    void publishesEveryFieldAsRequired() {
        Schema<?> request = OpenApiSchemaTestSupport.openApi31Schema(ObjectAllowedActionsRequestDto.class);

        assertEquals(List.of("uuids"), request.getRequired());
        assertEquals(List.of("items"), schemas().get("ObjectAllowedActionsResponseDto").getRequired());
        assertTrue(
                schemas().get("ObjectAllowedActionsDto").getRequired().containsAll(List.of("uuid", "allowedActions")));
    }

    @Test
    void publishesAllowedActionsAsActionCodes() {
        Schema<?> allowedActions = (Schema<?>) schemas()
                .get("ObjectAllowedActionsDto")
                .getProperties()
                .get("allowedActions");

        assertEquals("#/components/schemas/ResourceAction", allowedActions.getItems().get$ref());
    }

    @Test
    void serializesItemsWithTheirActionCodes() throws Exception {
        UUID uuid = UUID.fromString("3f6c1d2e-0000-4000-8000-000000000001");
        ObjectAllowedActionsResponseDto response = new ObjectAllowedActionsResponseDto(
                List.of(new ObjectAllowedActionsDto(uuid, List.of(ResourceAction.DETAIL, ResourceAction.UPDATE))));

        assertEquals("{\"items\":[{\"uuid\":\"" + uuid + "\",\"allowedActions\":[\"detail\",\"update\"]}]}",
                new ObjectMapper().writeValueAsString(response));
    }

    /** Asserted on the prose deliberately: rewording is allowed, dropping a constraint has to be said here first. */
    @Test
    void documentsTheConstraintsAClientCannotInferFromTheTypes() {
        assertDocuments(
                ((Schema<?>) schemas().get("ObjectAllowedActionsDto").getProperties().get("allowedActions"))
                        .getDescription(),
                "the way the endpoint that performs it is authorized", "on the object itself",
                "on the resource as a whole", "on a parent object first", "accepts for the caller",
                "such as create and list", "remains the only gate");
        assertDocuments(
                ((Schema<?>) schemas().get("ObjectAllowedActionsResponseDto").getProperties().get("items"))
                        .getDescription(),
                "may neither list nor view is left out", "the same as an object that does not exist");
    }

    private static void assertDocuments(String description, String... constraints) {
        Arrays
                .stream(constraints)
                .forEach(constraint -> assertTrue(description.contains(constraint),
                        "the published description no longer states: " + constraint + "\ngot: " + description));
    }
}
