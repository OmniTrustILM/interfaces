package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.util.Json31;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Generates the OpenAPI schema for the {@code computeDtbs} union and asserts it carries a {@code discriminator} stanza
 * with a complete {@code mapping}, and that the discriminator property appears in every {@code oneOf} subschema.
 *
 * <p>
 * Generation goes through swagger-core's {@code ModelConverters}, the resolver springdoc uses at runtime, because this
 * module has no OpenAPI build plugin to invoke.
 * </p>
 */
class ComputeDtbsSchemaGenerationTest {

    private static final String BASE = "ComputeDtbsRequest";

    @Test
    void theComputeDtbsUnionCarriesADiscriminatorWithMapping() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(ComputeDtbsInterface.class);

        Schema<?> base = schemas.get(BASE);
        assertNotNull(base, "expected a generated schema named " + BASE + "; found " + schemas.keySet());

        Discriminator discriminator = base.getDiscriminator();
        assertNotNull(discriminator, BASE + " must carry a discriminator stanza");
        assertEquals("family", discriminator.getPropertyName());
        assertEquals(
                Map
                        .of("pades", "#/components/schemas/PadesComputeDtbsRequestDto", "xades",
                                "#/components/schemas/XadesComputeDtbsRequestDto", "cades",
                                "#/components/schemas/CadesComputeDtbsRequestDto", "jades",
                                "#/components/schemas/JadesComputeDtbsRequestDto"),
                discriminator.getMapping(), "discriminator.mapping must name all four family schemas by wire code");
    }

    @Test
    void everySubschemaDeclaresTheDiscriminatorProperty() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(ComputeDtbsInterface.class);

        for (String subtype : List
                .of("PadesComputeDtbsRequestDto", "XadesComputeDtbsRequestDto", "CadesComputeDtbsRequestDto",
                        "JadesComputeDtbsRequestDto")) {
            assertArmDeclares(schemas, subtype, "family");
        }
    }

    /**
     * A subtype must also carry the fields the base declares, or a generated client would emit a request missing them.
     */
    @Test
    void everySubschemaCarriesTheSharedRequestFields() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(ComputeDtbsInterface.class);

        for (String subtype : List
                .of("PadesComputeDtbsRequestDto", "XadesComputeDtbsRequestDto", "CadesComputeDtbsRequestDto",
                        "JadesComputeDtbsRequestDto")) {
            for (String property : List
                    .of("document", "signerCertificateChain", "signingTime", "signatureAlgorithm",
                            "formattingAttributes")) {
                assertArmDeclares(schemas, subtype, property);
            }
        }
    }

    /**
     * Presence is not the part a generated client keys off: the algorithm resolves through a {@code $ref} and across
     * the interface indirection, either of which can drop the requiredMode silently.
     */
    @Test
    void everySubschemaMarksTheSignatureAlgorithmRequired() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(ComputeDtbsInterface.class);

        for (String subtype : List
                .of("PadesComputeDtbsRequestDto", "XadesComputeDtbsRequestDto", "CadesComputeDtbsRequestDto",
                        "JadesComputeDtbsRequestDto")) {
            assertTrue(declaresRequired(schemas.get(subtype), "signatureAlgorithm"),
                    subtype + " does not publish signatureAlgorithm as required");
        }
    }

    /**
     * The embed request declares its own {@code signatureAlgorithm} rather than inheriting one, so the assertion above
     * never reaches it and a requiredMode relaxed on that half alone would publish unnoticed.
     */
    @Test
    void theEmbedRequestMarksTheSignatureAlgorithmRequired() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(EmbedSignatureValueRequestDto.class);

        Schema<?> embed = schemas.get("EmbedSignatureValueRequest");
        assertNotNull(embed, "expected a generated schema named EmbedSignatureValueRequest; found " + schemas.keySet());
        assertTrue(declaresRequired(embed, "signatureAlgorithm"),
                "EmbedSignatureValueRequest does not publish signatureAlgorithm as required");
    }

    /**
     * The digest an algorithm commits to lives in {@code SignatureAlgorithm}'s Java fields, which the document does not
     * publish, so the component description spells the mapping out. Prose written by hand rots the day the enum gains a
     * member, so the enum is what this asserts against.
     */
    @Test
    void thePublishedEnumNamesTheDigestEachAlgorithmCommitsTo() {
        String description = signatureAlgorithmDescription();

        for (SignatureAlgorithm algorithm : SignatureAlgorithm.values()) {
            DigestAlgorithm committed = committedDigestOrNull(algorithm);
            if (!algorithm.isDigestAlgorithmIsImplicit()) {
                assertNotNull(committed, algorithm.getCode() + " commits to a digest the platform cannot name");
                assertTrue(algorithm.getCode().toUpperCase(Locale.ROOT).contains(committed.getCode().replace("-", "")),
                        algorithm.getCode() + " does not spell its own digest, so the description must map it too");
            } else if (committed != null) {
                assertTrue(description.contains(algorithm.getCode() + " commits to " + committed.getCode()),
                        "the description does not say that " + algorithm.getCode() + " commits to "
                                + committed.getCode());
            } else {
                Pattern clause = Pattern
                        .compile(Pattern.quote(algorithm.getCode() + " commits to ")
                                + "\\S+, which is not a DigestAlgorithm value");
                assertTrue(clause.matcher(description).find(),
                        algorithm.getCode() + " commits to a digest that fills no documentDigestAlgorithm, which the "
                                + "description must say of that algorithm by name");
            }
        }
    }

    /**
     * Every algorithm stays published, including the post-quantum names no connector formats yet: capability is a
     * per-connector matter it refuses with PARAMETER_UNSUPPORTED, not a hole cut in the shared contract.
     */
    @Test
    void thePublishedEnumOffersEveryAlgorithmThePlatformKnows() {
        Schema<?> component = ModelConverters
                .getInstance()
                .readAll(ComputeDtbsInterface.class)
                .get("SignatureAlgorithm");

        assertNotNull(component, "SignatureAlgorithm is not published as a component of the computeDtbs union");
        assertEquals(Arrays.stream(SignatureAlgorithm.values()).map(SignatureAlgorithm::getCode).toList(),
                component.getEnum(), "the published algorithm list is not the enum");
    }

    /**
     * The document transport is one component reached from several places. Resolved in 3.1 mode, the mode the published
     * specification uses: 3.0 drops every {@code $ref} sibling, so a difference in one would be invisible here.
     */
    @Test
    void documentTransferIsPublishedIdenticallyFromEveryEntryPoint() {
        Schema<?> viaCompute = openApi31Schemas(ComputeDtbsInterface.class).get("DocumentTransfer");
        Schema<?> viaSignedDocument = openApi31Schemas(SignedDocumentRequestDto.class).get("DocumentTransfer");
        Schema<?> standalone = openApi31Schemas(DocumentTransferDto.class).get("DocumentTransfer");

        assertNotNull(viaCompute, "DocumentTransfer not reached from the computeDtbs union");
        assertNotNull(viaSignedDocument, "DocumentTransfer not reached from SignedDocumentRequestDto");
        assertNotNull(standalone, "DocumentTransfer not generated on its own");

        assertNotNull(standalone.getDescription(), "DocumentTransfer publishes no description at all");
        assertFalse(standalone.getDescription().isBlank(), "DocumentTransfer publishes a blank description");

        assertEquals(Json31.pretty(standalone), Json31.pretty(viaCompute),
                "DocumentTransfer is published differently when reached through the computeDtbs union");
        assertEquals(Json31.pretty(standalone), Json31.pretty(viaSignedDocument),
                "DocumentTransfer is published differently when reached through SignedDocumentRequestDto");
    }

    /**
     * The transport choice is a discriminated union, so a generated client picks an arm by reading {@code transferMode}
     * rather than by testing which of three optional fields happens to be set.
     */
    @Test
    void documentTransferPublishesItsTransportChoiceAsADiscriminatedUnion() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(DocumentTransferDto.class);
        Schema<?> transfer = schemas.get("DocumentTransfer");

        assertEquals(List
                .of("#/components/schemas/InlineDocumentTransfer", "#/components/schemas/DigestOnlyDocumentTransfer"),
                transfer.getOneOf().stream().map(Schema::get$ref).toList(),
                "DocumentTransfer must publish the transport choice as oneOf over the two arms");

        Discriminator discriminator = transfer.getDiscriminator();
        assertNotNull(discriminator, "DocumentTransfer must carry a discriminator stanza");
        assertEquals("transferMode", discriminator.getPropertyName());
        assertEquals(
                Map
                        .of("inline", "#/components/schemas/InlineDocumentTransfer", "digestOnly",
                                "#/components/schemas/DigestOnlyDocumentTransfer"),
                discriminator.getMapping(), "discriminator.mapping must name both arms by wire code");
    }

    /**
     * Each arm owns the fields its transport needs, which is what makes a transfer carrying both — or neither — an
     * unrepresentable body rather than one refused after binding.
     */
    @Test
    void eachDocumentTransferArmCarriesOnlyItsOwnFields() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(DocumentTransferDto.class);

        assertEquals(List.of("document", "transferMode"), schemas.get("InlineDocumentTransfer").getRequired());
        assertEquals(List.of("digestAlgorithm", "documentDigest", "transferMode"),
                schemas.get("DigestOnlyDocumentTransfer").getRequired());

        assertArmDeclares(schemas, "InlineDocumentTransfer", "document");
        assertFalse(declaresProperty(schemas.get("InlineDocumentTransfer"), "documentDigest"),
                "the inline arm must not offer a digest field at all");
        assertArmDeclares(schemas, "DigestOnlyDocumentTransfer", "documentDigest");
        assertArmDeclares(schemas, "DigestOnlyDocumentTransfer", "digestAlgorithm");
        assertFalse(declaresProperty(schemas.get("DigestOnlyDocumentTransfer"), "document"),
                "the digest arm must not offer an inline document field at all");
    }

    /** An unsuppressed boolean validation getter reaches the wire as a property a generated client tries to send. */
    @Test
    void neitherArmPublishesAnythingBeyondItsOwnFields() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(DocumentTransferDto.class);

        assertEquals(Set.of("document", "transferMode"),
                schemas.get("InlineDocumentTransfer").getProperties().keySet());
        assertEquals(Set.of("documentDigest", "digestAlgorithm", "transferMode"),
                schemas.get("DigestOnlyDocumentTransfer").getProperties().keySet());
    }

    /** The discriminator property must be declared on every arm, or a generated client cannot set it. */
    @Test
    void everyDocumentTransferArmDeclaresTheDiscriminatorProperty() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(DocumentTransferDto.class);

        for (String arm : List.of("InlineDocumentTransfer", "DigestOnlyDocumentTransfer")) {
            assertArmDeclares(schemas, arm, "transferMode");
        }
    }

    /**
     * The arms are request bodies and declare {@code transferMode} with a getter and no mutator, the shape swagger-core
     * publishes as {@code readOnly} — which would tell a generated client not to send the discriminator the union
     * requires.
     */
    @Test
    void neitherDocumentTransferArmPublishesTheDiscriminatorReadOnly() {
        Map<String, Schema> schemas = openApi31Schemas(DocumentTransferDto.class);

        for (String arm : List.of("InlineDocumentTransfer", "DigestOnlyDocumentTransfer")) {
            assertNotEquals(Boolean.TRUE, transferMode(schemas, arm).getReadOnly(),
                    arm + " must keep the discriminator writable: it is sent in a request body");
        }
    }

    /**
     * Both arms override one shared accessor and carry no schema annotation of their own, so anything naming a code
     * there is published on both arms and can only ever be right for one of them.
     */
    @Test
    void neitherDocumentTransferArmNamesTheOtherArmsCode() {
        Map<String, Schema> schemas = openApi31Schemas(DocumentTransferDto.class);

        assertNamesOnlyItsOwnCode(schemas, "InlineDocumentTransfer", DocumentTransferMode.Codes.INLINE);
        assertNamesOnlyItsOwnCode(schemas, "DigestOnlyDocumentTransfer", DocumentTransferMode.Codes.DIGEST_ONLY);
    }

    private static void assertNamesOnlyItsOwnCode(Map<String, Schema> schemas, String arm, String code) {
        Schema<?> transferMode = transferMode(schemas, arm);
        Stream
                .of(transferMode.getExamples(), transferMode.getEnum())
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .forEach(named -> assertEquals(code, named,
                        arm + " names a transferMode it cannot carry; its own code is " + code));
    }

    private static Schema<?> transferMode(Map<String, Schema> schemas, String arm) {
        Schema<?> schema = schemas.get(arm);
        assertNotNull(schema, "expected a generated schema named " + arm + "; found " + schemas.keySet());
        Schema<?> property = (Schema<?>) schema.getProperties().get("transferMode");
        assertNotNull(property, arm + " does not declare transferMode; found " + schema.getProperties().keySet());
        return property;
    }

    /**
     * An arm has to declare the property itself: composing the union it belongs to is a cycle no generator can emit.
     */
    private static void assertArmDeclares(Map<String, Schema> schemas, String arm, String property) {
        Schema<?> schema = schemas.get(arm);
        assertNotNull(schema, "expected a generated schema named " + arm + "; found " + schemas.keySet());
        assertNull(schema.getAllOf(), arm + " must not compose the union it is an arm of");
        assertTrue(declaresProperty(schema, property), arm + " does not declare property '" + property + "'");
    }

    private static boolean declaresProperty(Schema<?> schema, String property) {
        return schema != null && schema.getProperties() != null && schema.getProperties().containsKey(property);
    }

    private static boolean declaresRequired(Schema<?> schema, String property) {
        return schema != null && schema.getRequired() != null && schema.getRequired().contains(property);
    }

    /** The description the document actually publishes for the algorithm. */
    private static String signatureAlgorithmDescription() {
        Schema<?> component = ModelConverters
                .getInstance()
                .readAll(ComputeDtbsInterface.class)
                .get("SignatureAlgorithm");
        if (component == null || component.getDescription() == null) {
            return fail("the SignatureAlgorithm component publishes no description");
        }
        return component.getDescription();
    }

    /** The digest algorithm an algorithm's signatures carry, or {@code null} when the platform can name none. */
    private static DigestAlgorithm committedDigestOrNull(SignatureAlgorithm algorithm) {
        try {
            return DigestAlgorithm.findByOid(algorithm.getDigestAlgorithmIdentifier().getAlgorithm().getId());
        } catch (ValidationException e) {
            return null;
        }
    }
}
