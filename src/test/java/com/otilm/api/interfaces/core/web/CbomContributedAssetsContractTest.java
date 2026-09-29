package com.otilm.api.interfaces.core.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.NotFoundException;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.common.PaginationResponseDto;
import com.otilm.api.model.core.cbom.CbomContributedAssetDto;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDto;
import com.otilm.api.model.core.cryptoasset.PqcVerdict;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.annotations.Operation;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        // Only the member this class declares is asserted on its schema: swagger-core may publish the inherited
        // members inline or through an allOf reference, and the parent's own contract test already pins them.
        List<String> required = requiredOf(CbomContributedAssetDto.class);
        assertTrue(required.contains("bomRefs"),
                "bomRefs is always present, empty when nothing links, got " + required);
        assertFalse(required.contains("name"), "the inherited absent-name rule still holds, got " + required);
    }

    /**
     * The scope and the ordering are contract: a client pages the list and Core implements both, so the sentences stay
     * until the behaviour changes.
     */
    @Test
    void theDescriptionStatesTheScopeTheRefsAndTheOrdering() {
        Operation op = listCbomAssets().getAnnotation(Operation.class);
        assertNotNull(op, "missing @Operation");
        assertFalse(op.summary().isBlank());
        assertTrue(op.description().contains("bomRefs"), "the description must name the refs member");
        assertTrue(op.description().contains("version"), "the description must say the list is scoped to one version");
        assertTrue(op.description().contains("ordered by name ascending, then UUID ascending"),
                "the description must document the default ordering");
        assertNoJargon("listCbomAssets", op.summary());
        assertNoJargon("listCbomAssets", op.description());
        assertLanguageNeutral("listCbomAssets", op.description());
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
    }

    private static List<String> requiredOf(Class<?> type) {
        Schema<?> schema = ModelConverters.getInstance().readAll(type).get(type.getSimpleName());
        assertNotNull(schema, "no schema resolved for " + type.getSimpleName());
        return schema.getRequired() == null ? List.of() : schema.getRequired();
    }
}
