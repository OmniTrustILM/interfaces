package com.otilm.api.model.core.settings;

import com.otilm.api.testsupport.ValidatorFixture;
import io.swagger.v3.oas.models.media.Schema;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hibernate.validator.constraints.URL;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertHasViolation;
import static com.otilm.api.testsupport.ConstraintViolationAssertions.assertNoViolations;
import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schemas;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * The CBOM sync policy tunables an operator sets from the Settings UI: optional in the contract (omitted means the
 * platform default), bounded at a floor (zero, or one for the retention) and at a cap, and carrying that default in the
 * schema so a form can pre-fill it.
 */
class UtilsSettingsDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    private record Tunable(String name, int min, int defaultValue, int cap) {
    }

    /** Bounds and defaults as literals: an expectation that read them from the class could not notice a narrowing. */
    private static final List<Tunable> TUNABLES = List
            .of(new Tunable("cbomSyncOverlapSeconds", 0, 60, 86_400),
                    new Tunable("cbomSyncSkippedRetryRuns", 0, 3, 1_000),
                    new Tunable("cbomSyncMaxIngestDocuments", 0, 50, 10_000),
                    new Tunable("cbomSyncSkipRetentionDays", 1, 90, 3_650));

    @Test
    void theSchemaPublishesEachTunableAsOptionalBoundedAndDefaulted() {
        Schema utils = openApi31Schemas(UtilsSettingsDto.class).get(UtilsSettingsDto.class.getSimpleName());
        assertNotNull(utils, "the utils schema must resolve in the mode the published specification uses");
        Map<String, Schema> properties = properties(utils);
        List<String> required = utils.getRequired() == null ? List.of() : utils.getRequired();
        for (Tunable tunable : TUNABLES) {
            Schema property = properties.get(tunable.name());
            assertNotNull(property, tunable.name() + " must be a property of the utils settings");
            assertFalse(required.contains(tunable.name()), tunable.name() + " is optional: omitted means the default");
            assertEquals(tunable.min(), property.getMinimum().intValue(), tunable.name() + " carries its floor");
            assertEquals(tunable.cap(), property.getMaximum().intValue(), tunable.name() + " carries its cap");
            assertNull(property.getExclusiveMinimum(), tunable.name() + ": no exclusiveMinimum beside minimum");
            assertNull(property.getExclusiveMinimumValue(), tunable.name() + ": no exclusiveMinimum beside minimum");
            assertNull(property.getExclusiveMaximum(), tunable.name() + ": no exclusiveMaximum beside maximum");
            assertNull(property.getExclusiveMaximumValue(), tunable.name() + ": no exclusiveMaximum beside maximum");
            assertEquals(tunable.defaultValue(), property.getDefault(),
                    tunable.name() + " carries its default as a number, not as text");
        }
    }

    /**
     * springdoc's runtime resolver, not swagger-core, is what turns {@code @Positive} into the {@code exclusiveMinimum}
     * the Redocly lint rejects beside a {@code minimum}, and no test here runs springdoc -- so the guard sits on the
     * annotations.
     */
    @Test
    void eachTunableIsBoundedWithMinAndMaxAndNeverWithPositive() throws NoSuchFieldException {
        for (Tunable tunable : TUNABLES) {
            Field field = UtilsSettingsDto.class.getDeclaredField(tunable.name());
            Min min = field.getAnnotation(Min.class);
            Max max = field.getAnnotation(Max.class);
            assertNotNull(min, tunable.name() + " must carry @Min");
            assertEquals(tunable.min(), min.value(), tunable.name() + " @Min");
            assertNotNull(max, tunable.name() + " must carry @Max: the contract is published, widening later is free, "
                    + "narrowing is not");
            assertEquals(tunable.cap(), max.value(), tunable.name() + " @Max");
            assertNull(field.getAnnotation(Positive.class),
                    tunable.name() + ": @Positive beside minimum emits exclusiveMinimum and fails the Redocly lint");
        }
    }

    /** Both bounds of every field fire, each naming its field; the cap messages carry the cap as published. */
    @Test
    void aValueBelowTheFloorOrAboveTheCapIsRejectedOnEachField() {
        UtilsSettingsDto dto = new UtilsSettingsDto();
        dto.setCbomSyncOverlapSeconds(-1);
        dto.setCbomSyncSkippedRetryRuns(UtilsSettingsDto.MAX_CBOM_SYNC_SKIPPED_RETRY_RUNS + 1);
        dto.setCbomSyncMaxIngestDocuments(UtilsSettingsDto.MAX_CBOM_SYNC_MAX_INGEST_DOCUMENTS + 1);
        dto.setCbomSyncSkipRetentionDays(0);

        Set<ConstraintViolation<UtilsSettingsDto>> violations = VALIDATOR.validate(dto);

        assertEquals(4, violations.size(), "each tunable is bounded on its own");
        assertHasViolation(violations, "cbomSyncSkipRetentionDays", "cbomSyncSkipRetentionDays must be at least 1");
        assertHasViolation(violations, "cbomSyncOverlapSeconds", "cbomSyncOverlapSeconds must not be negative");
        assertHasViolation(violations, "cbomSyncSkippedRetryRuns", "cbomSyncSkippedRetryRuns must not exceed 1000");
        assertHasViolation(violations, "cbomSyncMaxIngestDocuments",
                "cbomSyncMaxIngestDocuments must not exceed 10000");

        UtilsSettingsDto mirrored = new UtilsSettingsDto();
        mirrored.setCbomSyncOverlapSeconds(UtilsSettingsDto.MAX_CBOM_SYNC_OVERLAP_SECONDS + 1);
        mirrored.setCbomSyncSkippedRetryRuns(-1);
        mirrored.setCbomSyncMaxIngestDocuments(-1);
        mirrored.setCbomSyncSkipRetentionDays(UtilsSettingsDto.MAX_CBOM_SYNC_SKIP_RETENTION_DAYS + 1);

        Set<ConstraintViolation<UtilsSettingsDto>> mirroredViolations = VALIDATOR.validate(mirrored);

        assertEquals(4, mirroredViolations.size(), "each tunable is bounded on its own");
        assertHasViolation(mirroredViolations, "cbomSyncSkipRetentionDays",
                "cbomSyncSkipRetentionDays must not exceed 3650");
        assertHasViolation(mirroredViolations, "cbomSyncOverlapSeconds",
                "cbomSyncOverlapSeconds must not exceed 86400");
        assertHasViolation(mirroredViolations, "cbomSyncSkippedRetryRuns",
                "cbomSyncSkippedRetryRuns must not be negative");
        assertHasViolation(mirroredViolations, "cbomSyncMaxIngestDocuments",
                "cbomSyncMaxIngestDocuments must not be negative");
    }

    /** A constraint on a static field is silently never validated; the URL fields must keep theirs. */
    @Test
    void theUrlFieldsKeepTheirConstraintAndNoConstraintSitsOnAConstant() throws NoSuchFieldException {
        assertNotNull(UtilsSettingsDto.class.getDeclaredField("utilsServiceUrl").getAnnotation(URL.class));
        assertNotNull(UtilsSettingsDto.class.getDeclaredField("cbomRepositoryUrl").getAnnotation(URL.class));
        for (Field field : UtilsSettingsDto.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                assertNull(field.getAnnotation(URL.class), field.getName() + " must not carry a constraint");
                assertNull(field.getAnnotation(Min.class), field.getName() + " must not carry a constraint");
                assertNull(field.getAnnotation(Max.class), field.getName() + " must not carry a constraint");
            }
        }

        UtilsSettingsDto dto = new UtilsSettingsDto();
        dto.setUtilsServiceUrl("not a url");
        assertHasViolation(VALIDATOR.validate(dto), "utilsServiceUrl", "must be a valid URL");
    }

    @Test
    void theFloorTheCapAndUnsetAreAccepted() {
        UtilsSettingsDto floor = new UtilsSettingsDto();
        floor.setCbomSyncOverlapSeconds(0);
        floor.setCbomSyncSkippedRetryRuns(0);
        floor.setCbomSyncMaxIngestDocuments(0);
        floor.setCbomSyncSkipRetentionDays(1);
        assertNoViolations(VALIDATOR.validate(floor));

        UtilsSettingsDto cap = new UtilsSettingsDto();
        cap.setCbomSyncOverlapSeconds(UtilsSettingsDto.MAX_CBOM_SYNC_OVERLAP_SECONDS);
        cap.setCbomSyncSkippedRetryRuns(UtilsSettingsDto.MAX_CBOM_SYNC_SKIPPED_RETRY_RUNS);
        cap.setCbomSyncMaxIngestDocuments(UtilsSettingsDto.MAX_CBOM_SYNC_MAX_INGEST_DOCUMENTS);
        cap.setCbomSyncSkipRetentionDays(UtilsSettingsDto.MAX_CBOM_SYNC_SKIP_RETENTION_DAYS);
        assertNoViolations(VALIDATOR.validate(cap));

        assertNoViolations(VALIDATOR.validate(new UtilsSettingsDto()));
    }

    @Test
    void theBoundsCascadeFromThePlatformUpdate() {
        PlatformSettingsUpdateDto update = new PlatformSettingsUpdateDto();
        UtilsSettingsDto utils = new UtilsSettingsDto();
        utils.setCbomSyncSkippedRetryRuns(-1);
        update.setUtils(utils);

        assertHasViolation(VALIDATOR.validate(update), "utils.cbomSyncSkippedRetryRuns",
                "cbomSyncSkippedRetryRuns must not be negative");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Schema> properties(Schema schema) {
        return (Map<String, Schema>) schema.getProperties();
    }
}
