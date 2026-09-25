package com.otilm.api.interfaces.core.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.certificate.SearchRequestDto;
import com.otilm.api.model.core.cbom.CbomDto;
import com.otilm.api.model.core.certificate.CertificateDto;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDto;
import com.otilm.api.model.core.search.AttributeProjectable;
import com.otilm.api.model.core.search.ConfigurableColumnsDocs;
import com.otilm.api.model.core.search.SearchFieldDataByGroupDto;
import com.otilm.api.model.core.secret.SecretDto;
import com.otilm.api.model.core.signing.signingrecord.SigningRecordListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards the published prose of the configurable-column contract.
 *
 * <p>
 * Ordering, column selection and the two catalogue flags are one contract spread over every configurable-column
 * listing, its field catalogue and its listing object. A caller reads only the generated document, so a listing that
 * quietly loses the wording, or gains {@code sort} and {@code columns} without it, is a contract that is documented
 * everywhere but there. The lists below are deliberately explicit: adding a resource to the contract means adding it
 * here too.
 *
 * <p>
 * Each endpoint is addressed by controller <em>and</em> method, because a controller may serve more than one listing:
 * {@code CbomController} serves the CBOM inventory and, beside it, the fixed-shape list of the entries the sync could
 * not store. Keying by controller alone silently excused every listing after the first, so a second one could gain
 * {@code sort} and {@code columns} with none of this wording and nothing would say so.
 */
class ConfigurableColumnsDocTest {

    /** One listing or catalogue operation, addressed as the contract addresses it: a controller and a method. */
    private record Endpoint(Class<?> controller, String method) {

        @Override
        public String toString() {
            return controller.getSimpleName() + "." + method;
        }
    }

    /** The listings whose request body carries {@code sort} and {@code columns}. */
    private static final List<Endpoint> LISTINGS = List
            .of(new Endpoint(CertificateController.class, "listCertificates"),
                    new Endpoint(CryptographicKeyController.class, "listCryptographicKeys"),
                    new Endpoint(DiscoveryController.class, "listDiscoveries"),
                    new Endpoint(SecretManagementController.class, "listSecrets"),
                    new Endpoint(CbomController.class, "listCboms"),
                    new Endpoint(SigningRecordController.class, "listSigningRecords"),
                    new Endpoint(com.otilm.api.interfaces.core.web.v2.ConnectorController.class, "listConnectors"),
                    new Endpoint(CryptographicAssetController.class, "listCryptographicAssets"));

    /** The field catalogues that feed those listings. Every one declares the operation under the same method name. */
    private static final List<Endpoint> CATALOGUES = LISTINGS
            .stream()
            .map(listing -> new Endpoint(listing.controller(), "getSearchableFieldInformation"))
            .toList();

    /**
     * Listings and catalogues on the same controllers that are deliberately outside this contract, each with the reason
     * it is out.
     *
     * <p>
     * The entries the CBOM sync could not store are a fixed-shape list: every row has the same members, so there is
     * nothing to choose as a column, and its catalogue serves two filter fields and three ordering keys by hand rather
     * than through {@code FilterField}. It therefore carries neither the ordering-and-columns wording nor the
     * catalogue-flags wording. Naming it here is what keeps the guard able to notice the next second listing, which may
     * well not be exempt.
     */
    private static final List<Endpoint> OUTSIDE_THE_CONTRACT = List
            .of(new Endpoint(CbomController.class, "listSyncSkips"),
                    new Endpoint(CbomController.class, "getSyncSkipSearchableFields"));

    /** The listing objects that carry the projected attribute values. */
    private static final List<Class<?>> PROJECTION_CARRIERS = List
            .of(CertificateDto.class, KeyItemDto.class, com.otilm.api.model.core.connector.v2.ConnectorDto.class,
                    SecretDto.class, CbomDto.class, com.otilm.api.model.client.discovery.DiscoveryListDto.class,
                    SigningRecordListDto.class, CryptographicAssetDto.class);

    /**
     * The carriers whose resource the platform registers no custom, metadata or data attributes against. They implement
     * the projection like the rest - the contract is one shape for all of them - but showing the shared example on them
     * would document a payload the listing cannot produce, so they carry the description alone.
     */
    private static final List<Class<?>> CARRIERS_WITHOUT_ATTRIBUTE_SOURCES = List
            .of(CbomDto.class, SigningRecordListDto.class, CryptographicAssetDto.class);

    /** The form the field catalogue publishes an attribute-sourced identifier under. */
    private static final Pattern ATTRIBUTE_IDENTIFIER = Pattern.compile("^[^|]+\\|[A-Z]+$");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void everyListingDocumentsOrderingAndColumns() {
        LISTINGS.forEach(listing -> {
            Operation op = operation(listing);
            assertTrue(op.description().contains(ConfigurableColumnsDocs.SORT_AND_COLUMNS),
                    listing + " does not carry the shared ordering and columns wording");
            assertTrue(op.description().contains(ConfigurableColumnsDocs.ATTRIBUTE_PROJECTION),
                    listing + " does not carry the shared attribute projection wording");
            assertNoJargon(listing.toString(), op.description());
        });
    }

    /**
     * A listing or a catalogue on one of these controllers is either in the contract or named as being out of it. The
     * guard reads the controllers themselves rather than this file's lists, so a second listing added later cannot
     * quietly fall outside every assertion above.
     */
    @Test
    void everySearchListingAndCatalogueOnTheseControllersIsAccountedFor() {
        Set<Endpoint> accounted = new HashSet<>(LISTINGS);
        accounted.addAll(CATALOGUES);
        accounted.addAll(OUTSIDE_THE_CONTRACT);

        Stream
                .concat(LISTINGS.stream(), CATALOGUES.stream())
                .map(Endpoint::controller)
                .distinct()
                .forEach(controller -> {
                    for (Method method : controller.getDeclaredMethods()) {
                        if (!takesASearchRequest(method) && !servesAFieldCatalogue(method)) {
                            continue;
                        }
                        Endpoint endpoint = new Endpoint(controller, method.getName());
                        assertTrue(accounted.contains(endpoint), endpoint
                                + " is a listing or a field catalogue that this guard does not know about: add it to"
                                + " LISTINGS or CATALOGUES, or to OUTSIDE_THE_CONTRACT with the reason it is exempt");
                    }
                });
    }

    @Test
    void everyListingCarriesAWorkedRequestExample() {
        LISTINGS.forEach(listing -> {
            ExampleObject[] examples = requestBodyExamples(listing);
            assertTrue(examples.length > 0, listing + " declares no request example");
            for (ExampleObject example : examples) {
                JsonNode body = assertDoesNotThrow(() -> MAPPER.readTree(example.value()),
                        listing + " request example is not valid JSON, so it reaches the document as a string");
                assertTrue(body.has("sort"), listing + " request example does not show sort");
                assertTrue(body.has("columns"), listing + " request example does not show columns");
                for (JsonNode column : body.get("columns")) {
                    assertTrue(column.has("fieldSource") && column.has("fieldIdentifier"),
                            listing + " request example addresses a column without both halves of its address");
                    assertIdentifierMatchesItsSource(listing.toString(), column.get("fieldSource").asText(),
                            column.get("fieldIdentifier").asText());
                }
            }
        });
    }

    @Test
    void everyFieldCatalogueDocumentsTheCapabilityFlags() {
        for (Endpoint catalogue : CATALOGUES) {
            Operation op = operation(catalogue);
            assertTrue(op.description().contains(ConfigurableColumnsDocs.CATALOGUE_FLAGS),
                    catalogue + " does not document displayable and sortable");
            assertNoJargon(catalogue.toString(), op.description());
        }
    }

    @Test
    void everyProjectionCarrierDescribesTheProjectedValuesTheSameWay() {
        for (Class<?> carrier : PROJECTION_CARRIERS) {
            assertTrue(AttributeProjectable.class.isAssignableFrom(carrier),
                    carrier.getSimpleName() + " does not implement AttributeProjectable");
            Field field = assertDoesNotThrow(() -> carrier.getDeclaredField("attributeValues"),
                    carrier.getSimpleName() + " has no attributeValues field");
            Schema schema = field.getAnnotation(Schema.class);
            assertNotNull(schema, carrier.getSimpleName() + " does not document attributeValues");
            assertEquals(AttributeProjectable.ATTRIBUTE_VALUES_DESCRIPTION, schema.description(),
                    carrier.getSimpleName() + " describes attributeValues in its own words");
            if (CARRIERS_WITHOUT_ATTRIBUTE_SOURCES.contains(carrier)) {
                assertEquals("", schema.example(),
                        carrier.getSimpleName() + " shows an attribute example for a resource that has no attributes");
            } else {
                assertEquals(AttributeProjectable.ATTRIBUTE_VALUES_EXAMPLE, schema.example(),
                        carrier.getSimpleName() + " does not show the shared attributeValues example");
            }
        }
    }

    @Test
    void theSharedAttributeValuesExampleIsValidJsonNestedBySourceThenIdentifier() {
        JsonNode example = assertDoesNotThrow(() -> MAPPER.readTree(AttributeProjectable.ATTRIBUTE_VALUES_EXAMPLE),
                "the attributeValues example is not valid JSON, so it reaches the document as a string");
        assertTrue(example.fieldNames().hasNext(), "the attributeValues example shows no field source");
        Map.Entry<String, JsonNode> source = example.fields().next();
        JsonNode bySource = source.getValue();
        assertTrue(bySource.fieldNames().hasNext(), "the attributeValues example shows no field identifier");
        assertTrue(bySource.fields().next().getValue().isArray(),
                "the attributeValues example does not show values as a list");
        bySource
                .fieldNames()
                .forEachRemaining(identifier -> assertIdentifierMatchesItsSource("the attributeValues example",
                        source.getKey(), identifier));
    }

    /**
     * An attribute-sourced field is published under {@code name|CONTENT_TYPE}, because a name alone is ambiguous when
     * one attribute name is registered against two content types - and a column addressed by the bare name therefore
     * matches nothing. A property field is published under its own identifier and carries no suffix.
     */
    private static void assertIdentifierMatchesItsSource(String context, String fieldSource, String fieldIdentifier) {
        if ("property".equals(fieldSource)) {
            assertFalse(fieldIdentifier.contains("|"),
                    context + " suffixes the property identifier " + fieldIdentifier + " with a content type");
            return;
        }
        assertTrue(ATTRIBUTE_IDENTIFIER.matcher(fieldIdentifier).matches(), context + " addresses " + fieldSource
                + " field " + fieldIdentifier + " without the name|CONTENT_TYPE form the catalogue publishes");
    }

    @Test
    void theUnpagedConnectorListingPointsAtTheOneThatTakesColumns() {
        Operation op = operation(new Endpoint(ConnectorController.class, "listConnectors"));
        assertTrue(op.description().contains("POST /v2/connectors/list"),
                "the v1 Connector listing does not say where filters, ordering and columns live");
        assertNoJargon("v1 listConnectors", op.description());
    }

    /** A listing takes the platform's search request; a catalogue answers with the platform's field groups. */
    private static boolean takesASearchRequest(Method method) {
        return Arrays.stream(method.getParameterTypes()).anyMatch(SearchRequestDto.class::isAssignableFrom);
    }

    private static boolean servesAFieldCatalogue(Method method) {
        return List.class.equals(method.getReturnType())
                && method.getGenericReturnType() instanceof ParameterizedType parameterized
                && SearchFieldDataByGroupDto.class.equals(parameterized.getActualTypeArguments()[0]);
    }

    private static Operation operation(Endpoint endpoint) {
        Operation op = declaredMethod(endpoint).getAnnotation(Operation.class);
        assertNotNull(op, "missing @Operation on " + endpoint);
        return op;
    }

    private static Method declaredMethod(Endpoint endpoint) {
        return Arrays
                .stream(endpoint.controller().getDeclaredMethods())
                .filter(m -> m.getName().equals(endpoint.method()))
                .findFirst()
                .orElseThrow(() -> new AssertionError(endpoint + " does not exist"));
    }

    private static ExampleObject[] requestBodyExamples(Endpoint endpoint) {
        Method method = declaredMethod(endpoint);
        return Arrays
                .stream(method.getParameters())
                .map(p -> p.getAnnotation(io.swagger.v3.oas.annotations.parameters.RequestBody.class))
                .filter(Objects::nonNull)
                .flatMap(rb -> Arrays.stream(rb.content()))
                .flatMap(content -> Arrays.stream(content.examples()))
                .toArray(ExampleObject[]::new);
    }
}
