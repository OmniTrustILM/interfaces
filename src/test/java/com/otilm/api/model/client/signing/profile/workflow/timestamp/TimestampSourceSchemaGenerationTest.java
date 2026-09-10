package com.otilm.api.model.client.signing.profile.workflow.timestamp;

import io.swagger.v3.oas.models.media.Discriminator;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Schema generation goes through swagger-core's {@code ModelConverters}, the resolver springdoc uses at runtime,
 * because this module has no OpenAPI build plugin to invoke.
 */
class TimestampSourceSchemaGenerationTest {

    private static final String RESPONSE_BASE = "TimestampSourceDto";
    private static final String RESPONSE_ARM = "InternalTimestampSourceDto";
    private static final String REQUEST_BASE = "TimestampSourceRequestDto";
    private static final String REQUEST_ARM = "InternalTimestampSourceRequestDto";

    /** The union is a {@code oneOf} over its arms, so an arm that composes it is a cycle no generator can express. */
    private static final String COMPOSITION_IS_A_CYCLE = "an arm must not compose the union it belongs to";

    /**
     * The arm declares {@code type} with a getter and no mutator, the shape swagger-core publishes as {@code readOnly}
     * — which would tell a generated client not to send the discriminator the union requires.
     */
    private static final String DISCRIMINATOR_STAYS_WRITABLE = "the discriminator must stay writable: it is sent in a "
            + "request body";

    @Test
    void theResponseUnionCarriesADiscriminatorWithMapping() {
        Schema<?> base = readAll(TimestampSourceDto.class).get(RESPONSE_BASE);
        assertNotNull(base, "expected a generated schema named " + RESPONSE_BASE);

        Discriminator discriminator = base.getDiscriminator();
        assertNotNull(discriminator, RESPONSE_BASE + " must carry a discriminator stanza");
        assertEquals("type", discriminator.getPropertyName());
        assertEquals(Map.of(TimestampSourceType.Codes.INTERNAL, "#/components/schemas/" + RESPONSE_ARM),
                discriminator.getMapping());
    }

    @Test
    void theRequestUnionCarriesADiscriminatorWithMapping() {
        Schema<?> base = readAll(TimestampSourceRequestDto.class).get(REQUEST_BASE);
        assertNotNull(base, "expected a generated schema named " + REQUEST_BASE);

        Discriminator discriminator = base.getDiscriminator();
        assertNotNull(discriminator, REQUEST_BASE + " must carry a discriminator stanza");
        assertEquals("type", discriminator.getPropertyName());
        assertEquals(Map.of(TimestampSourceType.Codes.INTERNAL, "#/components/schemas/" + REQUEST_ARM),
                discriminator.getMapping());
    }

    @Test
    void theResponseUnionListsItsArmInOneOf() {
        Schema<?> base = readAll(TimestampSourceDto.class).get(RESPONSE_BASE);

        assertEquals(List.of("#/components/schemas/" + RESPONSE_ARM),
                base.getOneOf().stream().map(Schema::get$ref).toList());
    }

    @Test
    void theRequestUnionListsItsArmInOneOf() {
        Schema<?> base = readAll(TimestampSourceRequestDto.class).get(REQUEST_BASE);

        assertEquals(List.of("#/components/schemas/" + REQUEST_ARM),
                base.getOneOf().stream().map(Schema::get$ref).toList());
    }

    @Test
    void theResponseArmIsSelfContainedAndCarriesTheDiscriminator() {
        Schema<?> arm = readAll(TimestampSourceDto.class).get(RESPONSE_ARM);
        assertNotNull(arm, "expected a generated schema named " + RESPONSE_ARM);

        assertNull(arm.getAllOf(), COMPOSITION_IS_A_CYCLE);
        assertEquals(Set.of("type", "signingProfile"), arm.getProperties().keySet());
        assertTrue(arm.getRequired().contains("type"), RESPONSE_ARM + " must require the type discriminator");
        assertNotEquals(Boolean.TRUE, arm.getProperties().get("type").getReadOnly(), DISCRIMINATOR_STAYS_WRITABLE);
    }

    @Test
    void theRequestArmIsSelfContainedAndCarriesTheDiscriminator() {
        Schema<?> arm = readAll(TimestampSourceRequestDto.class).get(REQUEST_ARM);
        assertNotNull(arm, "expected a generated schema named " + REQUEST_ARM);

        assertNull(arm.getAllOf(), COMPOSITION_IS_A_CYCLE);
        assertEquals(Set.of("type", "signingProfileUuid"), arm.getProperties().keySet());
        assertTrue(arm.getRequired().contains("type"), REQUEST_ARM + " must require the type discriminator");
        assertNotEquals(Boolean.TRUE, arm.getProperties().get("type").getReadOnly(), DISCRIMINATOR_STAYS_WRITABLE);
    }

    /** Renaming a published schema breaks every generated client, so the four names are pinned here. */
    @Test
    void theUnionPublishesTheDtoSuffixedNames() {
        assertTrue(readAll(TimestampSourceDto.class).keySet().containsAll(List.of(RESPONSE_BASE, RESPONSE_ARM)),
                "response union publishes " + readAll(TimestampSourceDto.class).keySet());
        assertTrue(readAll(TimestampSourceRequestDto.class).keySet().containsAll(List.of(REQUEST_BASE, REQUEST_ARM)),
                "request union publishes " + readAll(TimestampSourceRequestDto.class).keySet());
    }

    /** {@code readAll} is declared with a raw {@code Schema}; the cast is contained here. */
    @SuppressWarnings("unchecked")
    private static Map<String, Schema<?>> readAll(Class<?> type) {
        return (Map<String, Schema<?>>) (Map<String, ?>) openApi31Schemas(type);
    }
}
