package com.otilm.api.interfaces.core.web;

import com.otilm.api.exception.NotFoundException;
import com.otilm.api.model.core.cbom.CbomIngestFindingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the CBOM ingest report against its annotation values: the frontend's generated client calls this path under this
 * operation id and Core implements it, so a change to either has to fail a build.
 */
class CbomIngestFindingContractTest {

    private static Method listCbomIngestFindings() {
        return Arrays
                .stream(CbomController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("listCbomIngestFindings"))
                .findFirst()
                .orElseThrow(
                        () -> new AssertionError("CbomController declares no method named listCbomIngestFindings"));
    }

    /**
     * A GET of a plain list rather than a paged POST: the report is bounded at a hundred lines of each kind by the
     * ingest that writes it, has a fixed shape, and offers nothing to filter or order on.
     */
    @Test
    void theReportIsAGetUnderTheCbomAnsweringAPlainList() {
        Method list = listCbomIngestFindings();
        GetMapping mapping = list.getAnnotation(GetMapping.class);
        assertNotNull(mapping, "the report must be a GET");
        assertArrayEquals(new String[]{"/{uuid}/ingestFindings"}, mapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.produces());
        assertEquals("listCbomIngestFindings", list.getAnnotation(Operation.class).operationId());

        assertEquals(1, list.getParameterCount());
        assertEquals(UUID.class, list.getParameters()[0].getType());
        assertNotNull(list.getParameters()[0].getAnnotation(PathVariable.class), "the CBOM uuid is the path variable");

        assertEquals(List.class, list.getReturnType());
        ParameterizedType returnType = (ParameterizedType) list.getGenericReturnType();
        assertEquals(CbomIngestFindingDto.class, returnType.getActualTypeArguments()[0],
                "lines must be CbomIngestFindingDto");
    }

    @Test
    void anUnknownCbomIsNotFound() {
        Method list = listCbomIngestFindings();
        assertTrue(Arrays.asList(list.getExceptionTypes()).contains(NotFoundException.class));
        ApiResponses responses = list.getAnnotation(ApiResponses.class);
        assertNotNull(responses);
        assertTrue(Arrays
                .stream(responses.value())
                .map(ApiResponse::responseCode)
                .toList()
                .containsAll(List.of("200", "404")));
    }

    /**
     * The order, the bound, the replacement per attempt and what the list means beside each sync state are contract:
     * Core implements all of it, so the sentences documenting them stay until the behaviour itself changes.
     */
    @Test
    void theDescriptionDocumentsTheOrderTheBoundAndWhatTheListMeansBesideTheSyncState() {
        Operation operation = listCbomIngestFindings().getAnnotation(Operation.class);
        assertNotNull(operation, "missing @Operation on the report");
        String description = operation.description();
        assertTrue(description
                .contains("Ordered by `kind` (`finding` before `skip`), then by `detail`, both ascending in the "
                        + "database's collation"),
                "the description must document the order");
        assertTrue(description.contains("At most 100 lines of each kind are kept"),
                "the description must document the bound");
        assertTrue(description.contains("Each sync attempt that stores a report replaces the whole of it"),
                "the description must say a later attempt replaces the report");
        assertTrue(description.contains("an attempt that fails while storing its report leaves the previous one"),
                "the description must say a failed store keeps the previous report");
        assertTrue(description.contains("never the value of a key or of any other material"),
                "the description must say a line never carries a property's value");
        assertTrue(description
                .contains("A record synced without contributing an asset, or one the sync no longer retries, keeps"),
                "the description must say which superseded records keep their report");
        assertTrue(description.contains("`assetSyncError`") && description.contains("`assetSyncState`"),
                "the description must say which record members the list is read beside");
        assertTrue(description.contains("The list is empty while no attempt has read the document"),
                "the description must say when the list is empty");
    }

    @Test
    void theDescriptionIsFreeOfJargonAndLanguageTerms() {
        Operation operation = listCbomIngestFindings().getAnnotation(Operation.class);
        assertNoJargon("listCbomIngestFindings", operation.summary());
        assertNoJargon("listCbomIngestFindings", operation.description());
        assertLanguageNeutral("listCbomIngestFindings", operation.description());
    }
}
