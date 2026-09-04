package com.otilm.api.interfaces.core.web;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Pins the group users route against its annotation values. The Group detail page renders this list and Core implements
 * it, so a changed path, verb, parameter or payload shape has to fail a build rather than surface as an empty section.
 */
class GroupControllerContractTest {

    private static Method getGroupUsers() {
        return Arrays
                .stream(GroupController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("getGroupUsers"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("GroupController declares no getGroupUsers method"));
    }

    @Test
    void groupUsersIsAGetOnTheUsersSubResource() {
        // when
        GetMapping mapping = getGroupUsers().getAnnotation(GetMapping.class);

        // then
        assertNotNull(mapping, "group users must be a GET");
        assertArrayEquals(new String[]{"/{uuid}/users"}, mapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.produces());
    }

    @Test
    void groupUsersReturnsAFlatNameAndUuidList() {
        // given
        Method method = getGroupUsers();

        // when
        ParameterizedType returnType = (ParameterizedType) method.getGenericReturnType();

        // then
        assertEquals(List.class, method.getReturnType());
        assertEquals(NameAndUuidDto.class, returnType.getActualTypeArguments()[0],
                "members are identified by name and uuid only, the projection the platform already uses for users");
    }

    @Test
    void theGroupUuidIsTheOnlyInput() {
        // given
        Method method = getGroupUsers();

        // then
        assertEquals(1, method.getParameterCount(),
                "the listing is unpaginated: auth serves the user directory as one page, so a page parameter here "
                        + "would slice a list Core already holds whole");
        assertEquals(String.class, method.getParameters()[0].getType());
        assertNotNull(method.getParameters()[0].getAnnotation(PathVariable.class), "the group uuid rides the path");
    }

    @Test
    void groupUsersDocumentsSuccessAndUnknownGroup() {
        // when
        Set<String> documentedStatuses = Arrays
                .stream(getGroupUsers().getAnnotation(ApiResponses.class).value())
                .map(ApiResponse::responseCode)
                .collect(Collectors.toSet());

        // then
        assertEquals(Set.of("200", "404"), documentedStatuses);
    }
}
