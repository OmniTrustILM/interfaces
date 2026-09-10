package com.otilm.api.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.otilm.api.model.client.certificate.CertificateImportRequestDto;
import com.otilm.api.model.client.certificate.CertificateImportResponseDto;
import com.otilm.api.model.client.certificate.CertificateKeystoreRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyExportRequestDto;
import com.otilm.api.model.client.cryptography.key.KeyImportRequestDto;
import com.otilm.api.model.client.inspection.InspectionRequestDto;
import com.otilm.api.model.client.inspection.InspectionResponseDto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyResultRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.material.EncryptedKeyMaterialV2Dto;
import com.otilm.api.model.connector.discovery.v2.DiscoveredItemPayloadInterface;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.token.TokenInstanceDetailDto;
import com.otilm.api.model.core.cryptography.tokenprofile.TokenProfileDetailDto;
import io.swagger.v3.core.util.Json31;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiProseAssertions.assertLanguageNeutral;
import static com.otilm.api.testsupport.OpenApiProseAssertions.assertNoJargon;
import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static com.otilm.api.testsupport.PublishedUnions.arms;
import static com.otilm.api.testsupport.PublishedUnions.declaringClasses;
import static com.otilm.api.testsupport.PublishedUnions.publishedName;
import static com.otilm.api.testsupport.PublishedUnions.publishedSchemaName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds the key-transfer schemas to what an OpenAPI generator can consume without hand edits: an untyped property, a
 * dangling reference or a name two schemas claim becomes broken generated code rather than a build failure here.
 * Running the generators themselves needs the assembled document; {@code scripts/openapi-generator-check.sh} does that.
 *
 * <p>
 * {@link #noChildOfAPublishedUnionComposesTheUnionItBelongsTo()} is the one invariant here not scoped to those roots:
 * it reaches every union the model package declares.
 * </p>
 */
class OpenApiGeneratorCompatibilityTest {

    private static final List<Class<?>> CONTRACT_ROOTS = List
            .of(EncryptedKeyMaterialV2Dto.class, ImportKeyRequestV2Dto.class, ImportKeyAttributesRequestV2Dto.class,
                    ImportKeyResultRequestV2Dto.class, ImportableKeyTypeV2Dto.class, ExportableKeyTypeV2Dto.class,
                    ExportKeyRequestV2Dto.class, ExportKeyResponseV2Dto.class, KeyImportRequestDto.class,
                    KeyExportRequestDto.class, InspectionRequestDto.class, InspectionResponseDto.class,
                    CertificateImportRequestDto.class, CertificateImportResponseDto.class,
                    CertificateKeystoreRequestDto.class, KeyItemDetailDto.class, TokenInstanceDetailDto.class,
                    TokenProfileDetailDto.class);

    /**
     * {@code ResponseMetadata} binds its version subtypes under "2" and "3" while both the published mapping and the
     * wire value its discriminator carries are "v2" and "v3", so no value it publishes resolves to a subtype at all.
     * Correcting that changes what the metadata contract binds, so it is pinned as the one divergence rather than
     * exempted: repairing the registry fails this too, and takes the entry with it.
     */
    private static final Set<String> KNOWN_DIVERGENT_MAPPINGS = Set.of("ResponseMetadataDto");

    /** Schemas these contracts introduce. Anything else a root pulls in belongs to a contract of its own. */
    private static final Set<String> CONTRIBUTED_SCHEMAS = Set
            .of("EncryptedKeyMaterialV2Dto", "ImportKeyRequestV2Dto", "ImportKeyAttributesRequestV2Dto",
                    "ImportKeyResultRequestV2Dto", "ImportableKeyTypeV2Dto", "ExportableKeyTypeV2Dto",
                    "ExportKeyRequestV2Dto", "ExportKeyResponseV2Dto", "KeyImportRequestDto", "KeyExportRequestDto",
                    "InspectionRequestDto", "InspectionResponseDto", "CertificateImportRequestDto",
                    "CertificateImportEntryDto", "CertificateImportResponseDto", "CertificateImportResultDto",
                    "InspectedEntryDto", "CertificateEntryKeyDestinationDto", "CertificateKeystoreRequestDto",
                    "KeyTransferAvailabilityDto", "KeyTransferCapabilityDto");

    private static Map<String, Schema> resolveAll() {
        Map<String, Schema> all = new TreeMap<>();
        CONTRACT_ROOTS.forEach(root -> all.putAll(openApi31Schemas(root)));
        return all;
    }

    @Test
    void everySchemaTheseContractsIntroduceIsPublished() {
        Map<String, Schema> all = resolveAll();

        Set<String> missing = new TreeSet<>(CONTRIBUTED_SCHEMAS);
        missing.removeAll(all.keySet());

        assertTrue(missing.isEmpty(), () -> "schemas this contract introduces but does not publish: " + missing
                + ". A schema no root reaches never appears in the document, so no client is generated for it.");
    }

    /**
     * A property with no type, no reference and no composition generates an opaque value a caller cannot fill in.
     */
    @Test
    void everyPropertyIsSomethingAGeneratorCanType() {
        Map<String, Schema> all = resolveAll();

        List<String> untyped = new ArrayList<>();
        contributed(all).forEach((name, schema) -> {
            if (schema.getProperties() == null) {
                return;
            }
            schema.getProperties().forEach((property, value) -> {
                if (!isTypeable((Schema<?>) value)) {
                    untyped.add(name + "." + property);
                }
            });
        });

        assertTrue(untyped.isEmpty(), () -> "properties a generator cannot type: " + untyped);
    }

    /**
     * A reference the document does not carry aborts generation, and the generator reports it against the operation
     * rather than the schema, so it is worth catching here.
     */
    @Test
    void everyReferenceResolvesWithinTheDocument() {
        Map<String, Schema> all = resolveAll();

        Set<String> referenced = new TreeSet<>();
        contributed(all).values().forEach(schema -> collectReferences(schema, referenced));
        referenced.removeAll(all.keySet());

        assertTrue(referenced.isEmpty(), () -> "references nothing in the document resolves: " + referenced
                + ". Publish the schema alongside, or reference one that is already published.");
    }

    /**
     * Two schemas of the same name collapse into one in the document, so the generated model silently describes the
     * wrong thing. Each root is resolved on its own and the definitions found under one name are compared; two
     * same-named classes reached from a single root collapse before this can see them, which is why every schema these
     * contracts introduce is reached from a root of its own.
     */
    @Test
    void noContributedSchemaClaimsANameAnotherClassOwns() {
        Map<String, String> published = new LinkedHashMap<>();
        Map<String, Class<?>> reachedFrom = new LinkedHashMap<>();
        Map<String, String> collisions = new LinkedHashMap<>();

        for (Class<?> root : CONTRACT_ROOTS) {
            contributed(openApi31Schemas(root)).forEach((name, schema) -> {
                String definition = Json31.pretty(schema);
                String previous = published.putIfAbsent(name, definition);
                if (previous == null) {
                    reachedFrom.put(name, root);
                } else if (!previous.equals(definition)) {
                    collisions
                            .put(name, "reached from " + reachedFrom.get(name).getName() + " and from " + root.getName()
                                    + " with different definitions");
                }
            });
        }

        assertTrue(collisions.isEmpty(), () -> "schema names claimed twice: " + collisions);
    }

    /**
     * The passphrase is a wrapper in Java and a string on the wire. A generator that saw the wrapper would emit a type
     * no caller can construct.
     */
    @Test
    void aPassphraseIsAWriteOnlyStringEverywhereItAppears() {
        Map<String, Schema> all = resolveAll();
        List<String> checked = new ArrayList<>();

        contributed(all).forEach((name, schema) -> {
            if (schema.getProperties() == null) {
                return;
            }
            schema.getProperties().forEach((property, value) -> {
                String propertyName = String.valueOf(property);
                if (!propertyName.toLowerCase(Locale.ROOT).contains("passphrase")) {
                    return;
                }
                Schema<?> passphrase = (Schema<?>) value;
                String where = name + "." + propertyName;
                assertEquals("string", declaredType(passphrase), where + " must be published as a string");
                assertNotNull(passphrase.getWriteOnly(), where + " must be published write-only");
                assertTrue(passphrase.getWriteOnly(), where + " must be published write-only");
                checked.add(where);
            });
        });

        assertTrue(checked.size() >= 4, () -> "expected every passphrase-bearing body to be covered, saw " + checked);
    }

    /**
     * The uploaded file is a wrapper in Java and a base64 string on the wire, like the passphrase; a generator that saw
     * the wrapper would emit a type no caller can fill in, and a file published readable would be echoed back.
     */
    @Test
    void anUploadedFileIsAWriteOnlyBase64StringEverywhereItAppears() {
        Map<String, Schema> all = resolveAll();
        List<String> checked = new ArrayList<>();

        assertFalse(all.containsKey("UploadedFile"), "the wrapper must not be published as a schema of its own");
        contributed(all).forEach((name, schema) -> {
            if (schema.getProperties() == null || !schema.getProperties().containsKey("file")) {
                return;
            }
            Schema<?> file = (Schema<?>) schema.getProperties().get("file");
            String where = name + ".file";
            assertEquals("string", declaredType(file), where + " must be published as a string");
            assertEquals("byte", file.getFormat(), where + " must be published as base64 content");
            assertEquals(Boolean.TRUE, file.getWriteOnly(), where + " must be published write-only");
            checked.add(where);
        });

        assertEquals(3, checked.size(), () -> "expected every upload body to be covered, saw " + checked);
    }

    /**
     * A schema with no description of its own leaves a caller reading the property list to guess what the whole thing
     * is for, and the explanation that exists in source is not published.
     */
    @Test
    void everySchemaAndPropertyIsDescribed() {
        Map<String, Schema> all = resolveAll();

        List<String> undescribed = new ArrayList<>();
        contributed(all).forEach((name, schema) -> {
            if (isBlank(schema.getDescription())) {
                undescribed.add(name);
            }
            if (schema.getProperties() == null) {
                return;
            }
            schema.getProperties().forEach((property, value) -> {
                if (isBlank(((Schema<?>) value).getDescription())) {
                    undescribed.add(name + "." + property);
                }
            });
        });

        assertTrue(undescribed.isEmpty(), () -> "published without a description: " + undescribed);
    }

    /**
     * The document is the only thing a connector or client author reads, whatever language they work in.
     */
    @Test
    void noDescriptionNamesTheImplementationLanguage() {
        Map<String, Schema> all = resolveAll();

        contributed(all).forEach((name, schema) -> {
            if (schema.getDescription() != null) {
                assertLanguageNeutral(name, schema.getDescription());
                assertNoJargon(name, schema.getDescription());
            }
            if (schema.getProperties() == null) {
                return;
            }
            schema.getProperties().forEach((property, value) -> {
                String description = ((Schema<?>) value).getDescription();
                if (description != null) {
                    assertLanguageNeutral(name + "." + property, description);
                    assertNoJargon(name + "." + property, description);
                }
            });
        });
    }

    /**
     * Every child has to be a shape a client can build — its own fields, and the discriminator among them wherever the
     * union declares one — rather than a composition of the union it belongs to, which is the cycle
     * {@link DiscoveredItemPayloadInterface} describes.
     */
    @Test
    void noChildOfAPublishedUnionComposesTheUnionItBelongsTo() {
        Set<String> broken = new TreeSet<>();
        Set<String> checked = new TreeSet<>();

        for (Class<?> declaring : declaringClasses()) {
            Map<String, Schema> schemas = openApi31Schemas(declaring);
            schemas.forEach((union, schema) -> {
                if (schema.getOneOf() == null) {
                    return;
                }
                checked.add(union);
                namedChildren(schema)
                        .forEach(child -> auditChild(schemas, union, child, schema.getDiscriminator(), broken));
            });
        }

        assertTrue(broken.isEmpty(), () -> "children that a client generator cannot resolve: " + broken);
        assertReachedEveryDeclaredUnion(checked);
    }

    /**
     * A union registers its arms twice — {@code @JsonSubTypes} for Jackson, {@code discriminatorMapping} for the schema
     * — and once the schema is published from an interface of its own the two sit in different files. Editing one and
     * forgetting the other publishes a union that does not match what binds, so the expected mapping is derived from
     * the registry rather than restated.
     */
    @Test
    void noPublishedMappingDiffersFromTheSubtypesJacksonBinds() {
        Map<String, String> drifted = new TreeMap<>();
        Set<String> checked = new TreeSet<>();

        for (Class<?> declaring : declaringClasses()) {
            String union = publishedName(declaring);
            Map<String, String> registered = jacksonMapping(declaring);
            Schema<?> published = openApi31Schemas(declaring).get(union);
            if (registered.isEmpty() || published == null || published.getDiscriminator() == null) {
                continue;
            }
            checked.add(union);
            if (!registered.equals(published.getDiscriminator().getMapping())) {
                drifted
                        .put(union, "Jackson binds " + registered + ", the document publishes "
                                + published.getDiscriminator().getMapping());
            }
        }

        assertEquals(KNOWN_DIVERGENT_MAPPINGS, drifted.keySet(),
                () -> "unions whose published mapping is not what Jackson binds: " + drifted);
        assertFalse(checked.isEmpty(), "no union with a Jackson registry was reached at all");
    }

    /**
     * The registry the arms are actually bound by, keyed by wire code. Empty when the arms are resolved some other way:
     * the nearest registry above them then binds types that are not these arms, as it does for a union nested inside
     * another one, whose second level is a custom deserializer.
     */
    private static Map<String, String> jacksonMapping(Class<?> declaring) {
        List<Class<?>> arms = arms(declaring);
        JsonSubTypes registry = arms.isEmpty() ? null : nearestRegistryAbove(arms.get(0));
        if (registry == null) {
            return Map.of();
        }
        Map<String, String> registered = new LinkedHashMap<>();
        for (JsonSubTypes.Type type : registry.value()) {
            registered.put(type.name(), "#/components/schemas/" + publishedSchemaName(type.value()));
        }
        if (!new TreeSet<>(registered.values()).equals(armReferences(arms))) {
            return Map.of();
        }
        return registered;
    }

    private static Set<String> armReferences(List<Class<?>> arms) {
        Set<String> references = new TreeSet<>();
        arms.forEach(arm -> references.add("#/components/schemas/" + publishedSchemaName(arm)));
        return references;
    }

    /** Breadth first, so a union nested inside another one finds its own level before the outer one. */
    private static JsonSubTypes nearestRegistryAbove(Class<?> arm) {
        Deque<Class<?>> pending = new ArrayDeque<>(supertypesOf(arm));
        while (!pending.isEmpty()) {
            Class<?> supertype = pending.removeFirst();
            JsonSubTypes registry = supertype.getDeclaredAnnotation(JsonSubTypes.class);
            if (registry != null) {
                return registry;
            }
            pending.addAll(supertypesOf(supertype));
        }
        return null;
    }

    private static List<Class<?>> supertypesOf(Class<?> type) {
        List<Class<?>> supertypes = new ArrayList<>(List.of(type.getInterfaces()));
        if (type.getSuperclass() != null) {
            supertypes.add(type.getSuperclass());
        }
        return supertypes;
    }

    /**
     * A union that stops being reachable covers nothing while the guard above still passes, which is the failure mode
     * the per-family tests kept hitting.
     */
    private static void assertReachedEveryDeclaredUnion(Set<String> checked) {
        Set<String> unreached = new TreeSet<>();
        declaringClasses().forEach(declaring -> unreached.add(publishedName(declaring)));
        unreached.removeAll(checked);

        assertTrue(unreached.isEmpty(), () -> "unions this module declares that the scan never reached: " + unreached);
    }

    private static boolean isBlank(String text) {
        return text == null || text.isBlank();
    }

    private static Map<String, Schema> contributed(Map<String, Schema> all) {
        Map<String, Schema> mine = new TreeMap<>();
        all.forEach((name, schema) -> {
            if (CONTRIBUTED_SCHEMAS.contains(name)) {
                mine.put(name, schema);
            }
        });
        return mine;
    }

    private static boolean isTypeable(Schema<?> schema) {
        return schema.get$ref() != null || declaredType(schema) != null || schema.getOneOf() != null
                || schema.getAnyOf() != null || schema.getAllOf() != null || schema.getEnum() != null;
    }

    private static String declaredType(Schema<?> schema) {
        if (schema.getType() != null) {
            return schema.getType();
        }
        return schema.getTypes() == null || schema.getTypes().isEmpty() ? null : schema.getTypes().iterator().next();
    }

    private static void auditChild(Map<String, Schema> schemas, String union, String child, Discriminator discriminator,
            Set<String> broken) {
        Schema<?> arm = schemas.get(child);
        String where = union + " -> " + child;
        if (arm == null) {
            broken.add(where + ": the union maps a child the document does not carry");
            return;
        }
        if (arm.getAllOf() != null) {
            broken.add(where + ": the child composes its own union with allOf");
            return;
        }
        // A child that is itself a union carries no properties; it is audited under its own name instead.
        if (arm.getOneOf() != null) {
            return;
        }
        if (arm.getProperties() == null) {
            broken.add(where + ": the child declares no properties of its own");
        } else if (discriminator != null && !arm.getProperties().containsKey(discriminator.getPropertyName())) {
            broken.add(where + ": the child does not declare " + discriminator.getPropertyName() + " itself");
        }
    }

    /** Every child the union names, whether the {@code mapping} or the {@code oneOf} list names it. */
    private static Set<String> namedChildren(Schema<?> union) {
        Set<String> children = new TreeSet<>();
        if (union.getDiscriminator() != null && union.getDiscriminator().getMapping() != null) {
            union.getDiscriminator().getMapping().values().forEach(reference -> children.add(schemaName(reference)));
        }
        union
                .getOneOf()
                .stream()
                .map(Schema::get$ref)
                .filter(Objects::nonNull)
                .forEach(reference -> children.add(schemaName(reference)));
        return children;
    }

    private static String schemaName(String reference) {
        return reference.substring(reference.lastIndexOf('/') + 1);
    }

    private static void collectReferences(Schema<?> schema, Set<String> into) {
        if (schema == null) {
            return;
        }
        if (schema.get$ref() != null && schema.get$ref().startsWith("#/components/schemas/")) {
            into.add(schema.get$ref().substring("#/components/schemas/".length()));
        }
        collectReferences(schema.getItems(), into);
        if (schema.getProperties() != null) {
            schema.getProperties().values().forEach(value -> collectReferences((Schema<?>) value, into));
        }
        Stream
                .of(schema.getOneOf(), schema.getAnyOf(), schema.getAllOf())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(value -> collectReferences(value, into));
        if (schema.getAdditionalProperties() instanceof Schema<?> additional) {
            collectReferences(additional, into);
        }
    }
}
