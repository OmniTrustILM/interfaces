package com.otilm.api.model.core.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SettingsSectionCategoryTest {

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * The section a category belongs to is bound at the declaration and the enum exposes no getter for it, so the code
     * is the only part of that binding a test can reach.
     */
    @Test
    void brandingIsAddressedByItsOwnCode() {
        assertEquals("branding", SettingsSectionCategory.PLATFORM_BRANDING.getCode());
    }

    /** Core stores the signing rows under this code. A rename would orphan them. */
    @Test
    void signingIsAddressedByItsOwnCode() {
        assertEquals("signing", SettingsSectionCategory.PLATFORM_SIGNING.getCode());
    }

    @ParameterizedTest
    @EnumSource(SettingsSectionCategory.class)
    void everyCategoryRoundTripsThroughItsCode(SettingsSectionCategory category) throws Exception {
        assertEquals(category, SettingsSectionCategory.findByCode(category.getCode()));
        assertEquals("\"%s\"".formatted(category.getCode()), mapper.writeValueAsString(category));
        assertEquals(category, mapper.readValue("\"%s\"".formatted(category.getCode()), SettingsSectionCategory.class));
    }

    @ParameterizedTest
    @EnumSource(SettingsSectionCategory.class)
    void everyCategoryDeclaresALabelAndDescription(SettingsSectionCategory category) {
        assertFalse(category.getLabel().isBlank(), "label missing for " + category.name());
        assertFalse(category.getDescription().isBlank(), "description missing for " + category.name());
    }

    /** findByCode resolves with findFirst, so a duplicate code would be shadowed rather than rejected. */
    @Test
    void categoryCodesAreUnique() {
        long distinctCodes = Arrays
                .stream(SettingsSectionCategory.values())
                .map(SettingsSectionCategory::getCode)
                .distinct()
                .count();

        assertEquals(SettingsSectionCategory.values().length, distinctCodes);
    }

    @Test
    void throwsOnUnknownCode() {
        assertThrows(ValidationException.class, () -> SettingsSectionCategory.findByCode("appearance"));
    }
}
