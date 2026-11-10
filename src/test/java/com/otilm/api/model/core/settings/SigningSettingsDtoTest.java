package com.otilm.api.model.core.settings;

import com.otilm.api.testsupport.ValidatorFixture;
import io.swagger.v3.oas.models.media.Schema;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertHasViolation;
import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertNoViolations;
import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class SigningSettingsDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private static final String MAX_ITEMS_PER_REQUEST = "maxItemsPerRequest";

    // The expectations are literals. One read from the class would follow a narrowing instead of catching it.
    private static final int FLOOR = 1;
    private static final int DEFAULT = 100;
    private static final int CEILING = 1_000;

    @Test
    void publishesTheLimitAsOptionalBoundedAndDefaulted() {
        Schema signing = openApi31Schemas(SigningSettingsDto.class).get(SigningSettingsDto.class.getSimpleName());

        assertNotNull(signing, "the signing schema must resolve in the mode the published specification uses");
        Schema property = properties(signing).get(MAX_ITEMS_PER_REQUEST);
        assertNotNull(property, MAX_ITEMS_PER_REQUEST + " must be a property of the signing settings");
        assertFalse(required(signing).contains(MAX_ITEMS_PER_REQUEST), "the limit is optional");
        assertEquals(FLOOR, property.getMinimum().intValue());
        assertEquals(CEILING, property.getMaximum().intValue());
        assertNull(property.getExclusiveMinimum(), "no exclusiveMinimum beside minimum");
        assertNull(property.getExclusiveMinimumValue(), "no exclusiveMinimum beside minimum");
        assertNull(property.getExclusiveMaximum(), "no exclusiveMaximum beside maximum");
        assertNull(property.getExclusiveMaximumValue(), "no exclusiveMaximum beside maximum");
        assertEquals(DEFAULT, property.getDefault(), "the default is published as a number");
    }

    @Test
    void publishesTheSigningSectionAsOptionalOnTheReadAndTheUpdate() {
        for (Class<?> platformSettings : List.of(PlatformSettingsDto.class, PlatformSettingsUpdateDto.class)) {
            Schema schema = openApi31Schemas(platformSettings).get(platformSettings.getSimpleName());

            assertNotNull(properties(schema).get("signing"), platformSettings.getSimpleName() + " carries signing");
            assertFalse(required(schema).contains("signing"),
                    platformSettings.getSimpleName() + ": signing is optional");
        }
    }

    /**
     * springdoc would publish {@code @Positive} as an {@code exclusiveMinimum}, which Redocly rejects beside
     * {@code minimum}. The guard reads the annotations because no test here runs springdoc.
     */
    @Test
    void boundsTheLimitWithMinAndMaxAndNeverWithPositive() throws NoSuchFieldException {
        Field field = SigningSettingsDto.class.getDeclaredField(MAX_ITEMS_PER_REQUEST);

        assertEquals(FLOOR, field.getAnnotation(Min.class).value());
        assertEquals(CEILING, field.getAnnotation(Max.class).value());
        assertNull(field.getAnnotation(Positive.class));
    }

    @Test
    void rejectsZero() {
        SigningSettingsDto belowFloor = signing(0);

        assertHasViolation(VALIDATOR.validate(belowFloor), MAX_ITEMS_PER_REQUEST, "must be at least 1");
    }

    @Test
    void rejectsOneAboveTheCeiling() {
        SigningSettingsDto aboveCeiling = signing(CEILING + 1);

        assertHasViolation(VALIDATOR.validate(aboveCeiling), MAX_ITEMS_PER_REQUEST, "must not exceed 1000");
    }

    @Test
    void acceptsTheFloorTheCeilingAndUnset() {
        assertNoViolations(VALIDATOR.validate(signing(FLOOR)));
        assertNoViolations(VALIDATOR.validate(signing(CEILING)));
        assertNoViolations(VALIDATOR.validate(new SigningSettingsDto()));
    }

    /** Core's 422 carries this path and message. The administrator UI shows the message on the field. */
    @Test
    void cascadesTheBoundsFromThePlatformUpdate() {
        PlatformSettingsUpdateDto update = new PlatformSettingsUpdateDto();
        update.setSigning(signing(CEILING + 1));

        assertHasViolation(VALIDATOR.validate(update), "signing." + MAX_ITEMS_PER_REQUEST, "must not exceed 1000");
    }

    private static SigningSettingsDto signing(int maxItemsPerRequest) {
        SigningSettingsDto signing = new SigningSettingsDto();
        signing.setMaxItemsPerRequest(maxItemsPerRequest);
        return signing;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Schema> properties(Schema schema) {
        return (Map<String, Schema>) schema.getProperties();
    }

    private static List<String> required(Schema schema) {
        return schema.getRequired() == null ? List.of() : schema.getRequired();
    }
}
