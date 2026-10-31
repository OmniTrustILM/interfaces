package com.otilm.api.model.common.signature.parameters;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Generates the OpenAPI schema for the signature-parameters union and asserts that a stock generated client can pick a
 * family and see every cap this contract enforces.
 *
 * <p>
 * Generation goes through swagger-core's {@code ModelConverters}, the resolver springdoc uses at runtime, because this
 * module has no OpenAPI build plugin to invoke.
 * </p>
 */
class SignatureParametersSchemaGenerationTest {

    private static final String BASE = "SignatureParameters";
    private static final String PADES = "PadesSignatureParameters";
    private static final String VISIBLE = "PadesVisibleSignature";
    private static final String PLACEMENT = "PadesVisibleSignaturePlacement";
    private static final String STYLE = "PadesVisibleSignatureStyle";
    private static final String REF = "#/components/schemas/";

    @Test
    void theUnionPublishesItsFamilyChoiceAsADiscriminatedUnion() {
        Schema<?> base = generate().get(BASE);
        assertNotNull(base, "expected a generated schema named " + BASE);

        assertEquals(List.of(REF + PADES), base.getOneOf().stream().map(Schema::get$ref).toList(),
                BASE + " must publish the family choice as oneOf over the registered subtypes");

        Discriminator discriminator = base.getDiscriminator();
        assertNotNull(discriminator, BASE + " must carry a discriminator stanza");
        assertEquals("family", discriminator.getPropertyName());
    }

    /**
     * A family is registered in two places — {@code @JsonSubTypes} for Jackson and {@code @DiscriminatorMapping} for
     * the schema. Editing one and forgetting the other publishes a union that does not match what binds.
     */
    @Test
    void theJacksonRegistryAndThePublishedMappingCannotDrift() {
        Map<String, String> registered = new LinkedHashMap<>();
        for (JsonSubTypes.Type type : SignatureParametersDto.class.getAnnotation(JsonSubTypes.class).value()) {
            registered.put(type.name(), REF + schemaName(type.value()));
        }

        assertEquals(registered, generate().get(BASE).getDiscriminator().getMapping(),
                "discriminator.mapping must name exactly the subtypes @JsonSubTypes registers, by wire code");
    }

    @Test
    void theSubschemaDeclaresTheDiscriminatorProperty() {
        Schema<?> pades = generate().get(PADES);
        assertNotNull(pades, "expected a generated schema named " + PADES);

        assertTrue(pades.getProperties().containsKey("family"),
                PADES + " does not declare the family discriminator property");
        assertEquals(List.of("family"), pades.getRequired(), "family is the only required parameter");
    }

    @Test
    void theCapsThatBoundEachParameterReachTheWire() {
        Map<String, Schema> schemas = generate();

        Map<String, Schema> pades = schemas.get(PADES).getProperties();
        for (String field : List.of("reason", "location", "contactInfo", "signerName")) {
            assertEquals(Integer.valueOf(512), pades.get(field).getMaxLength(),
                    field + " publishes no 512-character cap");
        }
        assertEquals(Integer.valueOf(10), pades.get("claimedRoles").getMaxItems(),
                "claimedRoles publishes no cap on how many roles it carries");
        assertEquals(Integer.valueOf(1), pades.get("claimedRoles").getMinItems(),
                "claimedRoles publishes no floor, so an empty list reads as legal");
        assertEquals(Integer.valueOf(256), pades.get("claimedRoles").getItems().getMaxLength(),
                "claimedRoles publishes no cap on a single role");

        Map<String, Schema> visible = schemas.get(VISIBLE).getProperties();
        assertEquals(Integer.valueOf(4096), visible.get("text").getMaxLength(), "text publishes no cap");

        Map<String, Schema> placement = schemas.get(PLACEMENT).getProperties();
        assertEquals(Integer.valueOf(256), placement.get("fieldId").getMaxLength(), "fieldId publishes no cap");
        assertEquals(List.of(0, 90, 180, 270), placement.get("rotation").getEnum(),
                "rotation publishes no quarter-turn enumeration");

        assertNotNull(placement.get("page").getMinimum(), "page publishes no lower bound");
        assertEquals(1, placement.get("page").getMinimum().intValue());
        assertNotNull(placement.get("zoom").getMaximum(), "zoom publishes no upper bound");
        assertEquals(1000, placement.get("zoom").getMaximum().intValue());

        for (String field : List.of("originX", "originY")) {
            assertEquals(BigDecimal.ZERO, placement.get(field).getMinimum(), field + " publishes no floor");
            assertEquals(Boolean.FALSE, placement.get(field).getExclusiveMinimum(),
                    field + " must admit the page edge");
        }
        for (String field : List.of("width", "height", "zoom")) {
            assertEquals(BigDecimal.ZERO, placement.get(field).getMinimum(), field + " publishes no floor");
            assertEquals(Boolean.TRUE, placement.get(field).getExclusiveMinimum(), field + " must not admit zero");
        }

        Map<String, Schema> style = schemas.get(STYLE).getProperties();
        for (String field : List.of("textColor", "textBackgroundColor", "backgroundColor")) {
            assertEquals("^#[0-9A-Fa-f]{6}$", style.get(field).getPattern(), field + " publishes no colour pattern");
        }
        assertEquals(BigDecimal.ZERO, style.get("padding").getMinimum(), "padding publishes no floor");
        assertEquals(BigDecimal.valueOf(100), style.get("padding").getMaximum(), "padding publishes no cap");
        assertEquals(BigDecimal.valueOf(72), style.get("dpi").getMinimum(), "dpi publishes no floor");
        assertEquals(BigDecimal.valueOf(1200), style.get("dpi").getMaximum(), "dpi publishes no cap");
    }

    /**
     * {@code maxLength} bounds the base64 text a client sends, while {@code @StampImageSize} bounds the bytes it
     * decodes to. Both must state the same 256 KiB, or a client obeying the schema is held to a cap the server does not
     * apply.
     */
    @Test
    void theStampImageCapIsPublishedInBase64Characters() {
        Map<String, Schema> visible = generate().get(VISIBLE).getProperties();
        Schema<?> image = visible.get("image");

        assertEquals("string", image.getType());
        assertEquals("byte", image.getFormat());
        assertEquals(Integer.valueOf(349528), image.getMaxLength(),
                "maxLength must be the base64 length of the 262144-byte cap");
        assertEquals(Integer.valueOf(4), image.getMinLength(),
                "an empty image must not validate against the published schema; base64 comes in groups of four");
    }

    private static Map<String, Schema> generate() {
        return ModelConverters.getInstance().readAll(SignatureParametersInterface.class);
    }

    private static String schemaName(Class<?> subtype) {
        io.swagger.v3.oas.annotations.media.Schema annotation = subtype
                .getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        return annotation != null && !annotation.name().isEmpty() ? annotation.name() : subtype.getSimpleName();
    }
}
