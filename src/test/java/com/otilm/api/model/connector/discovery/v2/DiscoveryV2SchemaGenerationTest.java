package com.otilm.api.model.connector.discovery.v2;

import com.otilm.api.interfaces.core.web.DiscoveryController;
import com.otilm.api.model.client.discovery.DiscoveryDetailDto;
import com.otilm.api.model.client.discovery.DiscoveryListDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.authority.AuthorityInstanceDto;
import com.otilm.api.model.core.connector.v2.ConnectorDto;
import com.otilm.api.model.core.connector.v2.ConnectorInterfaceDto;
import com.otilm.api.model.core.discovery.DiscoveryItemDto;
import com.otilm.api.model.core.discovery.DiscoveryMessageSeverity;
import com.otilm.api.model.core.vault.VaultInstanceDto;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Generates the OpenAPI schema and asserts each union base carries a {@code discriminator} stanza with a
 * {@code mapping}, and that the discriminator property appears in every {@code oneOf} subschema — the condition client
 * generators require, and the reason the discriminator sits inside the payload rather than on its container (see
 * {@code DiscoveredItemPayloadDto} and {@code DiscoveryEvent}). Generation goes through swagger-core's
 * {@code ModelConverters}, the resolver springdoc uses at runtime, because this module has no OpenAPI build plugin to
 * invoke.
 */
class DiscoveryV2SchemaGenerationTest {

    @Test
    void discoveredItemPayloadSchemaCarriesADiscriminatorWithMapping() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(DiscoveredItemPayloadDto.class);

        Schema<?> base = schemas.get("DiscoveredItemPayload");
        assertNotNull(base, "expected a generated schema named DiscoveredItemPayload; found " + schemas.keySet());

        Discriminator discriminator = base.getDiscriminator();
        assertNotNull(discriminator, "DiscoveredItemPayload must carry a discriminator stanza");
        assertEquals("resource", discriminator.getPropertyName());
        assertEquals(
                Map
                        .of("certificates", "#/components/schemas/DiscoveredCertificateDto", "keys",
                                "#/components/schemas/DiscoveredKeyDto"),
                discriminator.getMapping(),
                "discriminator.mapping must name both concrete payload schemas by their wire codes");

        assertSubschemaDeclaresProperty(schemas, "DiscoveredCertificateDto", "resource");
        assertSubschemaDeclaresProperty(schemas, "DiscoveredKeyDto", "resource");
    }

    @Test
    void discoveryEventSchemaCarriesADiscriminatorWithMapping() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(DiscoveryEvent.class);

        Schema<?> base = schemas.get("DiscoveryEvent");
        assertNotNull(base, "expected a generated schema named DiscoveryEvent; found " + schemas.keySet());

        Discriminator discriminator = base.getDiscriminator();
        assertNotNull(discriminator, "DiscoveryEvent must carry a discriminator stanza");
        assertEquals("type", discriminator.getPropertyName());
        assertEquals(
                Map
                        .of("progress", "#/components/schemas/DiscoveryProgressEvent", "resultBatch",
                                "#/components/schemas/DiscoveryResultBatchEvent", "stateChanged",
                                "#/components/schemas/DiscoveryStateChangedEvent", "heartbeat",
                                "#/components/schemas/DiscoveryHeartbeatEvent", "error",
                                "#/components/schemas/DiscoveryErrorEvent"),
                discriminator.getMapping(),
                "discriminator.mapping must name all five concrete event schemas by their wire codes");

        assertSubschemaDeclaresProperty(schemas, "DiscoveryProgressEvent", "type");
        assertSubschemaDeclaresProperty(schemas, "DiscoveryResultBatchEvent", "type");
        assertSubschemaDeclaresProperty(schemas, "DiscoveryStateChangedEvent", "type");
        assertSubschemaDeclaresProperty(schemas, "DiscoveryHeartbeatEvent", "type");
        assertSubschemaDeclaresProperty(schemas, "DiscoveryErrorEvent", "type");
    }

    /**
     * The OpenAPI rule every generated discriminator must satisfy: {@code discriminator.propertyName} must name a
     * property present in the subschema. springdoc/swagger-core will happily generate a discriminator that fails this
     * rule, so it is checked against the generated subschema rather than assumed.
     *
     * <p>
     * Verified empirically that "present" here means "resolvable across the schema graph": each concrete subtype
     * implements the annotated base interface, so swagger-core emits it as a {@code ComposedSchema} —
     * {@code allOf: [{$ref: <base>}, {type: object, properties: {<own
     * fields>}}]} — with the discriminator property living on the referenced base (declared once, as the interface's
     * own abstract accessor) rather than duplicated onto every subtype's local properties. That is the same shape the
     * OpenAPI spec's own Pet/Cat/Dog discriminator example uses, and {@code allOf} composition is exactly how a JSON
     * Schema validator (and codegen) resolves a property "present in" a subschema — so this walks
     * {@code allOf}/{@code $ref} rather than checking {@link Schema#getProperties()} directly.
     */
    private void assertSubschemaDeclaresProperty(Map<String, Schema> schemas, String schemaName, String property) {
        Schema<?> sub = schemas.get(schemaName);
        assertNotNull(sub, "expected a generated schema named " + schemaName + "; found " + schemas.keySet());
        assertTrue(resolvesProperty(sub, property, schemas), schemaName + " must resolve property '" + property
                + "' (directly or via allOf/$ref) for the " + "discriminator to be resolvable against it");
    }

    /**
     * A component must not be published differently depending on which endpoint's schema graph reached it, because the
     * assembled document keeps one definition per name and whichever entry wins is what SDK generators consume.
     *
     * <p>
     * Two ways that broke here. A self-referential {@code byResource} made the event path emit a
     * {@code DiscoveryProgressDto} with {@code byResource} silently missing, and a {@code description} on a
     * {@code $ref} field was hoisted onto the referenced component, overwriting its own — OpenAPI 3.0 cannot carry a
     * sibling description, so swagger-core pushes it down. Both would have shipped a wrong contract to Go and Python
     * connector authors while every Java test stayed green.
     */
    @Test
    void progressComponentsAreIdenticalFromEveryEntryPoint() {
        // The leaf's own description, read standalone: asserting the rule rather than today's wording, which a
        // literal prefix would pin until someone rewords the schema and has to edit the guard to match.
        String leafOwnDescription = ModelConverters
                .getInstance()
                .readAll(DiscoveryResourceProgressDto.class)
                .get("DiscoveryResourceProgressDto")
                .getDescription();
        Map<String, Schema> viaEvent = ModelConverters.getInstance().readAll(DiscoveryEvent.class);
        Map<String, Schema> viaStatus = ModelConverters.getInstance().readAll(DiscoveryStatusResponseDto.class);
        Map<String, Schema> viaDetail = ModelConverters.getInstance().readAll(DiscoveryDetailDto.class);

        // The core-web run detail is the third entry point into the progress components; its field-level
        // prose must not leak onto the shared components. DiscoveryProgressDto is the direct $ref target
        // of the detail's progress field, so its description is compared against the status path's — a
        // hoisted field description shows up exactly there.
        assertTrue(resolvesProperty(viaDetail.get("DiscoveryProgressDto"), "byResource", viaDetail),
                "DiscoveryProgressDto must carry byResource on the run-detail path");
        assertEquals(viaStatus.get("DiscoveryProgressDto").getDescription(),
                viaDetail.get("DiscoveryProgressDto").getDescription(),
                "DiscoveryProgressDto's description must not depend on being reached through the run detail");
        Schema<?> leafViaDetail = viaDetail.get("DiscoveryResourceProgressDto");
        assertNotNull(leafViaDetail, "the per-resource leaf component must be emitted on the run-detail path");
        assertEquals(leafOwnDescription, leafViaDetail.getDescription(),
                "the leaf must keep its own description on the run-detail path");

        // If the event path emits the run-level component at all, it must be the whole thing.
        Schema<?> progressViaEvent = viaEvent.get("DiscoveryProgressDto");
        if (progressViaEvent != null) {
            assertTrue(resolvesProperty(progressViaEvent, "byResource", viaEvent),
                    "DiscoveryProgressDto reached through DiscoveryEvent must not drop byResource");
        }

        assertTrue(resolvesProperty(viaStatus.get("DiscoveryProgressDto"), "byResource", viaStatus),
                "DiscoveryProgressDto must carry byResource on the status path");

        Schema<?> leafViaEvent = viaEvent.get("DiscoveryResourceProgressDto");
        Schema<?> leafViaStatus = viaStatus.get("DiscoveryResourceProgressDto");
        assertNotNull(leafViaEvent, "the per-resource leaf component must be emitted on the event path");
        assertNotNull(leafViaStatus, "the per-resource leaf component must be emitted on the status path");
        assertEquals(leafViaStatus.getDescription(), leafViaEvent.getDescription(),
                "the leaf component's description must not depend on which endpoint reached it");
        assertEquals(leafViaStatus.getProperties().keySet(), leafViaEvent.getProperties().keySet(),
                "the leaf component's properties must not depend on which endpoint reached it");
        assertEquals(leafOwnDescription, leafViaEvent.getDescription(),
                "the leaf must keep its own description, not one hoisted from a referencing field");
    }

    /**
     * Discovery must not rewrite a component it does not own. {@code Resource} is platform-wide, so a description
     * hoisted onto it from a discovery field would follow it into every other API that references it — the same
     * {@code $ref} hoisting as above, but with a blast radius outside this contract entirely. Asserted against the
     * component as generated from {@code Resource} alone, which is the definition every other API contributes.
     */
    @Test
    void discoveryDoesNotRewriteThePlatformWideResourceComponent() {
        String ownDescription = ModelConverters.getInstance().readAll(Resource.class).get("Resource").getDescription();

        for (Class<?> discoveryRoot : new Class<?>[]{
                DiscoveredItemPayloadDto.class,
                DiscoveredCertificateDto.class,
                DiscoveredKeyDto.class,
                DiscoveredItemDto.class,
                DiscoveryEvent.class,
                DiscoverySupportedResourceDto.class,
                DiscoveryItemDto.class,
                DiscoveryDetailDto.class,
                DiscoveryRunRequestDto.class,
                DiscoveryInitiateRequestDto.class}) {
            Schema<?> resource = ModelConverters.getInstance().readAll(discoveryRoot).get("Resource");
            if (resource == null) {
                continue;
            }
            assertEquals(ownDescription, resource.getDescription(),
                    "Resource is a platform-wide component; reaching it through " + discoveryRoot.getSimpleName()
                            + " must not change its description. A description on the referencing field gets "
                            + "hoisted onto it, because OpenAPI 3.0 cannot carry one beside a $ref.");
        }
    }

    /**
     * The same guard for {@code ConnectorInterfaceDto}, which the discovery detail and list both reach through a
     * {@code $ref}. Its own description is asserted rather than a literal, so the test states the rule — reaching a
     * shared component through discovery must not rewrite it — rather than today's value.
     */
    @Test
    void discoveryDoesNotRewriteThePlatformWideConnectorInterfaceComponent() {
        Schema<?> standalone = ModelConverters
                .getInstance()
                .readAll(ConnectorInterfaceDto.class)
                .get("ConnectorInterfaceDto");

        assertNotNull(standalone, "ConnectorInterfaceDto publishes no schema of its own");
        assertNotNull(standalone.getDescription(),
                "ConnectorInterfaceDto must describe itself at class level. Without that, every reached copy is null "
                        + "too and this guard compares null against null -- passing while the component says nothing "
                        + "at all.");

        // Authority and vault are guarded here too, not because they are discovery: they reference the same
        // component, and both used to hoist onto it, so discovery published whichever prose won.
        for (Class<?> discoveryRoot : new Class<?>[]{
                DiscoveryDetailDto.class,
                DiscoveryListDto.class,
                AuthorityInstanceDto.class,
                VaultInstanceDto.class,
                ConnectorDto.class}) {
            Schema<?> reached = ModelConverters.getInstance().readAll(discoveryRoot).get("ConnectorInterfaceDto");
            assertNotNull(reached,
                    discoveryRoot.getSimpleName() + " no longer emits the ConnectorInterfaceDto component at all. "
                            + "Skipping that case would let a schema that stopped publishing the component pass this "
                            + "guard, which exists to catch exactly that kind of contract regression.");
            assertEquals(standalone.getDescription(), reached.getDescription(),
                    "ConnectorInterfaceDto is a platform-wide component; reaching it through "
                            + discoveryRoot.getSimpleName() + " must not change its description. A description on "
                            + "the referencing field gets hoisted onto it, because OpenAPI 3.0 cannot carry one "
                            + "beside a $ref.");
            assertEquals(standalone.getNullable(), reached.getNullable(),
                    "nullable is hoisted onto the shared component the same way a description is, so "
                            + discoveryRoot.getSimpleName() + " must not set it either.");
        }
    }

    /**
     * The other half of the rule above. Keeping the shared component intact is trivially satisfied by describing the
     * referencing field nowhere at all, which is how the description was lost once already — swagger-core resolves no
     * javadoc on this classpath, so prose moved into a doc comment leaves the published field blank. Each of these
     * carries its own description through {@code ALL_OF_REF}, which parks it beside the {@code $ref} inside an
     * {@code allOf} rather than on the component. A plain {@code ALL_OF} drops it silently, so assert it is there.
     */
    @Test
    void eachReferencingFieldStillPublishesItsOwnDescription() {
        record Field(Class<?> root, String schemaName, String property) {
        }
        for (Field field : List
                .of(new Field(DiscoveryDetailDto.class, "DiscoveryDetailDto", "connectorInterface"),
                        new Field(DiscoveryListDto.class, "DiscoveryListDto", "connectorInterface"),
                        new Field(AuthorityInstanceDto.class, "AuthorityInstanceDto", "connectorInterface"),
                        new Field(VaultInstanceDto.class, "VaultInstanceDto", "connectorInterface"),
                        new Field(DiscoveryDetailDto.class, "DiscoveryDetailDto", "progress"),
                        new Field(DiscoveryStatusResponseDto.class, "DiscoveryStatusResponseDto", "progress"),
                        // @ArraySchema, so the description sits on the array rather than the item. Needed here and
                        // not on the other collections in this contract because Resource carries no class-level
                        // description of its own: any prose reaching that component is a change to it, which
                        // discoveryDoesNotRewriteThePlatformWideResourceComponent refuses. This row is the other
                        // half — that the description exists at all.
                        new Field(DiscoveryRunRequestDto.class, "DiscoveryRunRequestDto", "resources"))) {
            Schema<?> root = ModelConverters.getInstance().readAll(field.root()).get(field.schemaName());
            assertNotNull(root, "expected a generated schema for " + field.schemaName());
            Schema<?> property = (Schema<?>) root.getProperties().get(field.property());
            assertNotNull(property, field.schemaName() + " publishes no " + field.property() + " property");
            assertNotNull(property.getDescription(),
                    field.schemaName() + "." + field.property() + " publishes no description. Moving the prose into "
                            + "a doc comment does not reach the OpenAPI document -- there is no javadoc processor on "
                            + "this classpath -- and a plain ALL_OF drops it. Use ALL_OF_REF.");
        }
    }

    @SuppressWarnings("unchecked")
    private boolean resolvesProperty(Schema<?> schema, String property, Map<String, Schema> allSchemas) {
        if (schema == null) {
            return false;
        }
        if (schema.getProperties() != null && schema.getProperties().containsKey(property)) {
            return true;
        }
        if (schema.get$ref() != null) {
            String refName = schema.get$ref().substring(schema.get$ref().lastIndexOf('/') + 1);
            if (resolvesProperty(allSchemas.get(refName), property, allSchemas)) {
                return true;
            }
        }
        if (schema.getAllOf() != null) {
            for (Schema<?> member : (List<Schema<?>>) (List<?>) schema.getAllOf()) {
                if (resolvesProperty(member, property, allSchemas)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The discovered-items listing returns {@code PaginationResponseDto<DiscoveryItemDto>} rather than a
     * listing-specific DTO. Resolved from the controller method's own generic return type, so the assertion tracks the
     * published signature instead of a restatement of it.
     *
     * <p>
     * Two things are worth pinning. The component name is what client generators turn into a type name, and it is
     * derived from the type argument, so changing the element type silently renames the generated type. And the item's
     * payload discriminator has to survive being nested inside an erased generic — nothing else in the platform puts a
     * polymorphic type inside this envelope, so that combination is unproven anywhere else.
     */
    @Test
    void itemsListingResolvesToAPageComponentKeepingThePayloadDiscriminator() throws Exception {
        Method listing = DiscoveryController.class
                .getDeclaredMethod("getDiscoveryItems", String.class, Resource.class, Boolean.class, int.class,
                        int.class);

        ResolvedSchema resolved = ModelConverters
                .getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(listing.getGenericReturnType()).resolveAsRef(true));

        assertEquals("#/components/schemas/PaginationResponseDtoDiscoveryItemDto", resolved.schema.get$ref(),
                "the generated type name client generators see is derived from the element type");

        Schema<?> page = resolved.referencedSchemas.get("PaginationResponseDtoDiscoveryItemDto");
        assertNotNull(page, "no page component was generated");
        assertEquals(java.util.Set.of("items", "itemsPerPage", "pageNumber", "totalPages", "totalItems"),
                page.getProperties().keySet(), "property order is not part of the OpenAPI contract, names are");

        Schema<?> payload = resolved.referencedSchemas.get("DiscoveredItemPayload");
        assertNotNull(payload, "the item payload union did not survive into the page's referenced schemas");
        assertNotNull(payload.getDiscriminator(), "the payload lost its discriminator inside the generic envelope");
        assertEquals("resource", payload.getDiscriminator().getPropertyName());
    }

    /**
     * The run-messages listing, resolved the same way and for the same two reasons: the component name is what client
     * generators turn into a type name, and the severity enum reaches a client through the erased generic.
     *
     * <p>
     * {@code DiscoveryMessageDto} omits a description on its {@code severity} field because OpenAPI 3.0 cannot carry
     * one beside a {@code $ref}, and swagger-core hoists it onto the shared component instead — the failure
     * {@link #discoveryDoesNotRewriteThePlatformWideResourceComponent} pins for {@code Resource}.
     */
    @Test
    void runMessagesListingResolvesToAPageKeepingTheSeverityComponentIntact() throws Exception {
        String ownDescription = ModelConverters
                .getInstance()
                .readAll(DiscoveryMessageSeverity.class)
                .get("DiscoveryMessageSeverity")
                .getDescription();

        Method listing = DiscoveryController.class
                .getDeclaredMethod("getDiscoveryRunMessages", String.class, int.class, int.class);

        ResolvedSchema resolved = ModelConverters
                .getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(listing.getGenericReturnType()).resolveAsRef(true));

        assertEquals("#/components/schemas/PaginationResponseDtoDiscoveryMessageDto", resolved.schema.get$ref(),
                "the generated type name client generators see is derived from the element type");

        Schema<?> page = resolved.referencedSchemas.get("PaginationResponseDtoDiscoveryMessageDto");
        assertNotNull(page, "no page component was generated");
        assertEquals(java.util.Set.of("items", "itemsPerPage", "pageNumber", "totalPages", "totalItems"),
                page.getProperties().keySet(), "property order is not part of the OpenAPI contract, names are");

        Schema<?> severity = resolved.referencedSchemas.get("DiscoveryMessageSeverity");
        assertNotNull(severity, "the severity enum did not survive into the page's referenced schemas");
        // Null on both sides under this repo's enum pattern -- the @Schema description sits on the enum's code
        // field, not on the type, so the component carries none. That is what makes the equality meaningful
        // rather than vacuous: a description added on the referencing field would be hoisted onto the shared
        // component, turning this side non-null while a standalone read stays null.
        assertNull(ownDescription, "DiscoveryMessageSeverity is expected to carry no component-level description");
        assertEquals(ownDescription, severity.getDescription(),
                "reaching DiscoveryMessageSeverity through the listing must not give it a description: one on the "
                        + "referencing field gets hoisted onto the shared component, because OpenAPI 3.0 cannot "
                        + "carry a description beside a $ref");
        assertEquals(List.of("info", "warning", "error"), severity.getEnum(),
                "the enum ships its wire codes, not its Java member names");
    }
}
