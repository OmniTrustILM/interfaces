package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.core.cbom.CbomSyncSkipDto;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the endpoints of the documents the CBOM sync could not store against their annotation values: the frontend's
 * generated client calls these paths under these operation ids and Core implements them, so a change to either has to
 * fail a build.
 */
class CbomSyncSkipContractTest {

    private static Method method(String name) {
        return Arrays
                .stream(CbomController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new AssertionError("CbomController declares no method named " + name));
    }

    @Test
    void theEndpointsLiveUnderTheCbomPathWithPinnedOperationIds() {
        RequestMapping mapping = CbomController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "missing @RequestMapping");
        assertArrayEquals(new String[]{"/v1/cboms"}, mapping.value());
        assertEquals("listCbomSyncSkips", method("listSyncSkips").getAnnotation(Operation.class).operationId());
        assertEquals("getCbomSyncSkipSearchableFields",
                method("getSyncSkipSearchableFields").getAnnotation(Operation.class).operationId());
        assertEquals("retryCbomSyncSkip", method("retrySyncSkip").getAnnotation(Operation.class).operationId());
    }

    @Test
    void theListIsAPostOfTheCanonicalSearchRequest() {
        Method list = method("listSyncSkips");
        PostMapping mapping = list.getAnnotation(PostMapping.class);
        assertNotNull(mapping, "the list must be a POST");
        assertArrayEquals(new String[]{"/syncSkips"}, mapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.produces());

        assertEquals(1, list.getParameterCount());
        assertEquals(SearchRequestDto.class, list.getParameters()[0].getType());
        assertNotNull(list.getParameters()[0].getAnnotation(RequestBody.class), "the search request is the body");
        assertNotNull(list.getParameters()[0].getAnnotation(Valid.class), "the search request must be validated");
        assertNotNull(list.getParameters()[0].getAnnotation(io.swagger.v3.oas.annotations.parameters.RequestBody.class),
                "the list documents an example request, as the other listings do");

        assertEquals(PaginationResponseDto.class, list.getReturnType());
        ParameterizedType returnType = (ParameterizedType) list.getGenericReturnType();
        assertEquals(CbomSyncSkipDto.class, returnType.getActualTypeArguments()[0], "rows must be CbomSyncSkipDto");
    }

    /**
     * The default order, the sort keys, the two filters and the fixed shape are contract: consumers page the list and
     * Core implements all of it, so the sentences documenting them stay until the behaviour itself changes.
     */
    @Test
    void theListDocumentsItsDefaultOrderItsSortKeysItsFiltersAndItsFixedShape() {
        Operation operation = method("listSyncSkips").getAnnotation(Operation.class);
        assertNotNull(operation, "missing @Operation on the list");
        String description = operation.description();
        assertTrue(description
                .contains("By default the list is ordered newest failure first: last attempt descending, then UUID "
                        + "ascending"),
                "the description must document the default order");
        for (String sortKey : List
                .of("CBOM_SYNC_SKIP_LAST_ATTEMPT_AT", "CBOM_SYNC_SKIP_FIRST_SKIPPED_AT", "CBOM_SYNC_SKIP_ATTEMPTS",
                        "CBOM_SYNC_SKIP_SERIAL_NUMBER")) {
            assertTrue(description.contains("`" + sortKey + "`"), "the description must name the sort key " + sortKey);
        }
        assertTrue(description
                .contains("`filters` may use `CBOM_SYNC_SKIP_STATE` (equals, not equals) and "
                        + "`CBOM_SYNC_SKIP_SERIAL_NUMBER` (equals, not equals, contains, not contains, starts with)"),
                "the description must name the filter fields with their conditions");
        assertTrue(description.contains("source `property`"), "the description must name the field source");
        assertTrue(description.contains("`columns` is accepted and ignored"),
                "the description must say the rows have a fixed shape");
        assertTrue(description.contains("An entry leaves the list on the run that manages to store its document"),
                "the description must say what becomes of an entry the sync finally stores");
    }

    /**
     * A consumer reads the sortable identifiers from the catalogue, not from prose: the platform binds that rule to
     * {@code SearchSortRequestDto.fieldIdentifier} and reads an absent flag as false. So the catalogue has to carry the
     * ordering keys that take no filter, and say that it does.
     */
    @Test
    void theCatalogueSaysItCarriesTheOrderingKeysThatTakeNoFilter() {
        String description = method("getSyncSkipSearchableFields").getAnnotation(Operation.class).description();
        assertTrue(description.contains("may be filtered or ordered by"),
                "the catalogue must say it names both what may be filtered and what may be ordered on");
        assertTrue(description
                .contains("a field that is an ordering key only carries `sortable` with an empty list of conditions"),
                "the catalogue must say how an ordering-only field is published");
        assertTrue(
                method("listSyncSkips")
                        .getAnnotation(Operation.class)
                        .description()
                        .contains("the three that are ordering keys only among them"),
                "the list must point at the catalogue for every identifier its sort accepts");
    }

    /** The retry hands back a budget; it does not take the entry out of the lifecycle. */
    @Test
    void theRetrySaysTheEntryCanBeWrittenOffAgain() {
        String description = method("retrySyncSkip").getAnnotation(Operation.class).description();
        assertTrue(description.contains("written off again"),
                "the retry must say a document that still cannot be stored is written off again");
        assertTrue(description.contains("the retention then runs from that last attempt"),
                "the retry must say when the retention starts counting again");
    }

    @Test
    void everyNewDescriptionIsFreeOfJargonAndLanguageTerms() {
        for (String name : List.of("listSyncSkips", "getSyncSkipSearchableFields", "retrySyncSkip")) {
            String text = method(name).getAnnotation(Operation.class).description();
            assertNoJargon(name, text);
            assertLanguageNeutral(name, text);
        }
    }

    @Test
    void theSearchableFieldsSiblingIsAGetBesideTheList() {
        Method search = method("getSyncSkipSearchableFields");
        GetMapping mapping = search.getAnnotation(GetMapping.class);
        assertNotNull(mapping, "searchable fields must be a GET");
        assertArrayEquals(new String[]{"/syncSkips/search"}, mapping.path());
        assertEquals(List.class, search.getReturnType());
        ParameterizedType returnType = (ParameterizedType) search.getGenericReturnType();
        assertEquals(SearchFieldDataByGroupDto.class, returnType.getActualTypeArguments()[0]);
        assertTrue(search.getAnnotation(Operation.class).description().contains("no field is offered as a column"),
                "the catalogue must say the rows have a fixed shape rather than promise columns");
    }

    @Test
    void theRetryIsAPostOnTheRowThatReturnsItSaysWhatTheResetDoesAndDocumentsNotFound() {
        Method retry = method("retrySyncSkip");
        PostMapping mapping = retry.getAnnotation(PostMapping.class);
        assertNotNull(mapping, "the retry must be a POST");
        assertArrayEquals(new String[]{"/syncSkips/{uuid}/retry"}, mapping.path());
        assertEquals(1, retry.getParameterCount());
        assertEquals(UUID.class, retry.getParameters()[0].getType());
        assertNotNull(retry.getParameters()[0].getAnnotation(PathVariable.class), "the row is named in the path");
        assertEquals(CbomSyncSkipDto.class, retry.getReturnType(), "the retry answers with the row as it now stands");
        String description = retry.getAnnotation(Operation.class).description();
        assertTrue(description.contains("`attempts` becomes 0") && description.contains("keep the values of the last"),
                "the retry must say what the reset does to each member");
        assertTrue(Arrays.asList(retry.getExceptionTypes()).contains(NotFoundException.class));
        ApiResponses responses = retry.getAnnotation(ApiResponses.class);
        assertNotNull(responses);
        assertTrue(Arrays
                .stream(responses.value())
                .map(ApiResponse::responseCode)
                .toList()
                .containsAll(List.of("200", "404")));
    }
}
