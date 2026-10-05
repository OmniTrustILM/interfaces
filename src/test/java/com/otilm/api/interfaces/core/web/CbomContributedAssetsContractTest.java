package com.otilm.api.interfaces.core.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.core.cbom.CbomContributedAssetDto;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDto;
import com.otilm.api.model.core.cryptoasset.PqcVerdict;
import com.otilm.api.model.core.search.AttributeProjectable;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.models.media.Schema;
import jakarta.validation.Valid;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pins the CBOM-scoped asset listing against its annotation values: the frontend's generated client calls this path
 * under this operation id and renders its rows with the inventory's row rendering, so a change to either has to fail a
 * build.
 */
class CbomContributedAssetsContractTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    private static Method listCbomAssets() {
        return Arrays
                .stream(CbomController.class.getDeclaredMethods())
                .filter(m -> m.getName().equals("listCbomAssets"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("CbomController declares no method named listCbomAssets"));
    }

    @Test
    void theListingIsAPostUnderTheCbomTakingTheCanonicalSearchRequest() {
        Method list = listCbomAssets();
        PostMapping mapping = list.getAnnotation(PostMapping.class);
        assertNotNull(mapping, "the listing must be a POST: paging and filters travel in the search request body");
        assertArrayEquals(new String[]{"/{uuid}/assets"}, mapping.path());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.consumes());
        assertArrayEquals(new String[]{MediaType.APPLICATION_JSON_VALUE}, mapping.produces());
        assertEquals("listCbomCryptographicAssets", list.getAnnotation(Operation.class).operationId());

        Parameter[] parameters = list.getParameters();
        assertEquals(2, parameters.length);
        assertEquals(UUID.class, parameters[0].getType());
        assertNotNull(parameters[0].getAnnotation(PathVariable.class), "the CBOM uuid is the path variable");
        assertEquals(SearchRequestDto.class, parameters[1].getType());
        assertNotNull(parameters[1].getAnnotation(RequestBody.class), "the search request is the body");
        assertNotNull(parameters[1].getAnnotation(Valid.class), "the search request must be validated");
        assertNotNull(parameters[1].getAnnotation(io.swagger.v3.oas.annotations.parameters.RequestBody.class),
                "the listing documents an example request, as the other listings do");
        assertTrue(Arrays.asList(list.getExceptionTypes()).contains(NotFoundException.class),
                "an unknown CBOM is not found");
    }

    @Test
    void theRowsAreInventoryRowsCarryingTheirBomRefs() {
        Method list = listCbomAssets();
        assertEquals(PaginationResponseDto.class, list.getReturnType());
        ParameterizedType returnType = (ParameterizedType) list.getGenericReturnType();
        assertEquals(CbomContributedAssetDto.class, returnType.getActualTypeArguments()[0]);
        assertTrue(CryptographicAssetDto.class.isAssignableFrom(CbomContributedAssetDto.class),
                "a row is an inventory row, so a client renders it with the inventory's row rendering");
        assertTrue(AttributeProjectable.class.isAssignableFrom(CbomContributedAssetDto.class),
                "a row carries the attribute values its requested columns project, as an inventory row does");
        // Only the member this class declares is asserted on its schema: swagger-core may publish the inherited
        // members inline or through an allOf reference, and the parent's own contract test already pins them.
        List<String> required = requiredOf(CbomContributedAssetDto.class);
        assertTrue(required.contains("bomRefs"),
                "bomRefs is always present, empty when nothing links, got " + required);
        assertFalse(required.contains("name"), "the inherited absent-name rule still holds, got " + required);
    }

    /**
     * Filters, ordering and columns are documented on the inventory listing and its catalogue, not here, so the
     * description has to say where.
     */
    @Test
    void theDescriptionPointsToTheInventoryListingAndItsCatalogue() {
        Operation op = listCbomAssets().getAnnotation(Operation.class);
        assertNotNull(op, "missing @Operation");
        assertFalse(op.summary().isBlank());
        assertTrue(op.description().contains("`GET /v1/cryptoAssets/search`"),
                "the description must name the catalogue its filters, ordering and columns come from");
        assertTrue(op.description().contains("`POST /v1/cryptoAssets`"),
                "the description must name the inventory listing it behaves like");
        assertNoJargon("listCbomAssets", op.summary());
        assertNoJargon("listCbomAssets", op.description());
        assertLanguageNeutral("listCbomAssets", op.description());
    }

    /**
     * The description says {@code sort} and {@code columns} apply here, so the worked example shows both, and it must
     * be a request a client can send rather than text that reaches the document as a string. Its columns follow the
     * addressing rule {@code ConfigurableColumnsDocTest} holds the other listings to.
     */
    @Test
    void theRequestExampleIsASearchRequestShowingSortAndColumns() {
        ExampleObject[] examples = Arrays
                .stream(listCbomAssets().getParameters()[1]
                        .getAnnotation(io.swagger.v3.oas.annotations.parameters.RequestBody.class)
                        .content())
                .flatMap(content -> Arrays.stream(content.examples()))
                .toArray(ExampleObject[]::new);
        assertTrue(examples.length > 0, "the listing declares no request example");
        for (ExampleObject example : examples) {
            JsonNode body = assertDoesNotThrow(() -> mapper.readTree(example.value()),
                    "the request example is not valid JSON, so it reaches the document as a string");
            assertTrue(body.has("sort"), "the request example does not show sort");
            assertTrue(body.has("columns"), "the request example does not show columns");
            for (JsonNode column : body.get("columns")) {
                assertTrue(column.has("fieldSource") && column.has("fieldIdentifier"),
                        "the request example addresses a column without both halves of its address");
                ConfigurableColumnsDocTest
                        .assertIdentifierMatchesItsSource("the request example", column.get("fieldSource").asText(),
                                column.get("fieldIdentifier").asText());
            }
            SearchRequestDto request = assertDoesNotThrow(() -> mapper.treeToValue(body, SearchRequestDto.class),
                    "the request example is not a search request");
            assertNotNull(request.getSort(), "the request example's sort does not bind");
            assertFalse(request.getColumns().isEmpty(), "the request example names no column");
        }
    }

    @Test
    void anEmptyRefListIsSentRatherThanOmitted() throws Exception {
        CbomContributedAssetDto row = new CbomContributedAssetDto();
        row.setUuid(UUID.fromString("d3adbeef-0000-4000-8000-000000002316"));
        row.setPqcVerdict(PqcVerdict.UNKNOWN);

        JsonNode json = mapper.readTree(mapper.writeValueAsString(row));

        assertTrue(json.has("bomRefs"), "a row with no linkable component still carries the member");
        assertTrue(json.get("bomRefs").isArray());
        assertEquals(0, json.get("bomRefs").size());
        assertFalse(json.has("name"), "the inherited absent-member rule still holds on the subclass");
        assertFalse(json.has("attributeValues"), "a row projected for no attribute column carries no values member");
    }

    @Test
    void theRefsRoundTripInDocumentOrder() throws Exception {
        CbomContributedAssetDto row = new CbomContributedAssetDto();
        row.setUuid(UUID.fromString("d3adbeef-0000-4000-8000-000000002316"));
        row.setName("aes-256");
        row.setPqcVerdict(PqcVerdict.NOT_READY);
        row.setBomRefs(List.of("crypto/algorithm/aes@first", "crypto/algorithm/aes@second"));

        String json = mapper.writeValueAsString(row);
        CbomContributedAssetDto back = mapper.readValue(json, CbomContributedAssetDto.class);

        assertEquals(List.of("crypto/algorithm/aes@first", "crypto/algorithm/aes@second"), back.getBomRefs());
        assertEquals(row, back);
        back.setName("aes-128");
        assertNotEquals(row, back, "rows that differ only in an inherited member must not be equal");
    }

    private static List<String> requiredOf(Class<?> type) {
        Schema<?> schema = ModelConverters.getInstance().readAll(type).get(type.getSimpleName());
        assertNotNull(schema, "no schema resolved for " + type.getSimpleName());
        return schema.getRequired() == null ? List.of() : schema.getRequired();
    }
}
