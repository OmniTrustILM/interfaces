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
 * The CBOM sync tunables an operator sets from the Settings UI: optional in the contract (omitted means the platform
 * default), bounded at a floor and at a cap, and carrying that default in the schema so a form can pre-fill it.
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
                    new Tunable("cbomSyncSkipRetentionDays", 1, 90, 3_650),
                    new Tunable("cbomSyncPageSize", 1, 1_000, 1_000),
                    new Tunable("cbomSyncAssetBatchSize", 1, 100, 10_000),
                    new Tunable("cbomSyncIngestRetryAfterSeconds", 0, 1_800, 86_400));

    /** The kill switch: a flag rather than a bounded number, so it is held to the rest of the contract separately. */
    private static final String KILL_SWITCH = "cbomSyncAssetIngestEnabled";

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
     * The kill switch is published as an optional flag defaulting to on, and the field is a {@code Boolean}: a
     * primitive would read an omitted field as {@code false} and stop every ingest on any utils update that left it
     * out.
     */
    @Test
    void theKillSwitchIsAnOptionalFlagDefaultingToOnAndIsNotAPrimitive() throws NoSuchFieldException {
        Schema utils = openApi31Schemas(UtilsSettingsDto.class).get(UtilsSettingsDto.class.getSimpleName());
        Schema property = properties(utils).get(KILL_SWITCH);
        assertNotNull(property, KILL_SWITCH + " must be a property of the utils settings");
        List<String> required = utils.getRequired() == null ? List.of() : utils.getRequired();
        assertFalse(required.contains(KILL_SWITCH), KILL_SWITCH + " is optional: omitted means the default");
        assertEquals(Boolean.TRUE, property.getDefault(), KILL_SWITCH + " defaults to on");

        assertEquals(Boolean.class, UtilsSettingsDto.class.getDeclaredField(KILL_SWITCH).getType());
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
        UtilsSettingsDto belowFloor = new UtilsSettingsDto();
        belowFloor.setCbomSyncOverlapSeconds(-1);
        belowFloor.setCbomSyncSkippedRetryRuns(-1);
        belowFloor.setCbomSyncMaxIngestDocuments(-1);
        belowFloor.setCbomSyncSkipRetentionDays(0);
        belowFloor.setCbomSyncPageSize(0);
        belowFloor.setCbomSyncAssetBatchSize(0);
        belowFloor.setCbomSyncIngestRetryAfterSeconds(-1);

        Set<ConstraintViolation<UtilsSettingsDto>> violations = VALIDATOR.validate(belowFloor);

        assertEquals(TUNABLES.size(), violations.size(), "each tunable is bounded on its own");
        assertHasViolation(violations, "cbomSyncOverlapSeconds", "cbomSyncOverlapSeconds must not be negative");
        assertHasViolation(violations, "cbomSyncSkippedRetryRuns", "cbomSyncSkippedRetryRuns must not be negative");
        assertHasViolation(violations, "cbomSyncMaxIngestDocuments", "cbomSyncMaxIngestDocuments must not be negative");
        assertHasViolation(violations, "cbomSyncSkipRetentionDays", "cbomSyncSkipRetentionDays must be at least 1");
        assertHasViolation(violations, "cbomSyncPageSize", "cbomSyncPageSize must be at least 1");
        assertHasViolation(violations, "cbomSyncAssetBatchSize", "cbomSyncAssetBatchSize must be at least 1");
        assertHasViolation(violations, "cbomSyncIngestRetryAfterSeconds",
                "cbomSyncIngestRetryAfterSeconds must not be negative");

        UtilsSettingsDto aboveCap = new UtilsSettingsDto();
        aboveCap.setCbomSyncOverlapSeconds(UtilsSettingsDto.MAX_CBOM_SYNC_OVERLAP_SECONDS + 1);
        aboveCap.setCbomSyncSkippedRetryRuns(UtilsSettingsDto.MAX_CBOM_SYNC_SKIPPED_RETRY_RUNS + 1);
        aboveCap.setCbomSyncMaxIngestDocuments(UtilsSettingsDto.MAX_CBOM_SYNC_MAX_INGEST_DOCUMENTS + 1);
        aboveCap.setCbomSyncSkipRetentionDays(UtilsSettingsDto.MAX_CBOM_SYNC_SKIP_RETENTION_DAYS + 1);
        aboveCap.setCbomSyncPageSize(UtilsSettingsDto.MAX_CBOM_SYNC_PAGE_SIZE + 1);
        aboveCap.setCbomSyncAssetBatchSize(UtilsSettingsDto.MAX_CBOM_SYNC_ASSET_BATCH_SIZE + 1);
        aboveCap.setCbomSyncIngestRetryAfterSeconds(UtilsSettingsDto.MAX_CBOM_SYNC_INGEST_RETRY_AFTER_SECONDS + 1);

        Set<ConstraintViolation<UtilsSettingsDto>> capViolations = VALIDATOR.validate(aboveCap);

        assertEquals(TUNABLES.size(), capViolations.size(), "each tunable is bounded on its own");
        assertHasViolation(capViolations, "cbomSyncOverlapSeconds", "cbomSyncOverlapSeconds must not exceed 86400");
        assertHasViolation(capViolations, "cbomSyncSkippedRetryRuns", "cbomSyncSkippedRetryRuns must not exceed 1000");
        assertHasViolation(capViolations, "cbomSyncMaxIngestDocuments",
                "cbomSyncMaxIngestDocuments must not exceed 10000");
        assertHasViolation(capViolations, "cbomSyncSkipRetentionDays",
                "cbomSyncSkipRetentionDays must not exceed 3650");
        assertHasViolation(capViolations, "cbomSyncPageSize", "cbomSyncPageSize must not exceed 1000");
        assertHasViolation(capViolations, "cbomSyncAssetBatchSize", "cbomSyncAssetBatchSize must not exceed 10000");
        assertHasViolation(capViolations, "cbomSyncIngestRetryAfterSeconds",
                "cbomSyncIngestRetryAfterSeconds must not exceed 86400");
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
        floor.setCbomSyncSkipRetentionDays(UtilsSettingsDto.MIN_CBOM_SYNC_SKIP_RETENTION_DAYS);
        floor.setCbomSyncPageSize(UtilsSettingsDto.MIN_CBOM_SYNC_PAGE_SIZE);
        floor.setCbomSyncAssetBatchSize(UtilsSettingsDto.MIN_CBOM_SYNC_ASSET_BATCH_SIZE);
        floor.setCbomSyncIngestRetryAfterSeconds(0);
        floor.setCbomSyncAssetIngestEnabled(false);
        assertNoViolations(VALIDATOR.validate(floor));

        UtilsSettingsDto cap = new UtilsSettingsDto();
        cap.setCbomSyncOverlapSeconds(UtilsSettingsDto.MAX_CBOM_SYNC_OVERLAP_SECONDS);
        cap.setCbomSyncSkippedRetryRuns(UtilsSettingsDto.MAX_CBOM_SYNC_SKIPPED_RETRY_RUNS);
        cap.setCbomSyncMaxIngestDocuments(UtilsSettingsDto.MAX_CBOM_SYNC_MAX_INGEST_DOCUMENTS);
        cap.setCbomSyncSkipRetentionDays(UtilsSettingsDto.MAX_CBOM_SYNC_SKIP_RETENTION_DAYS);
        cap.setCbomSyncPageSize(UtilsSettingsDto.MAX_CBOM_SYNC_PAGE_SIZE);
        cap.setCbomSyncAssetBatchSize(UtilsSettingsDto.MAX_CBOM_SYNC_ASSET_BATCH_SIZE);
        cap.setCbomSyncIngestRetryAfterSeconds(UtilsSettingsDto.MAX_CBOM_SYNC_INGEST_RETRY_AFTER_SECONDS);
        cap.setCbomSyncAssetIngestEnabled(true);
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
