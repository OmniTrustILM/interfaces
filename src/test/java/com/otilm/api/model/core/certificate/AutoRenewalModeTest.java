package com.otilm.api.model.core.certificate;

import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AutoRenewalModeTest {

    @ParameterizedTest
    @EnumSource(AutoRenewalMode.class)
    void findByCodeReturnsTheMode_whenGivenItsOwnCode(AutoRenewalMode mode) {
        assertEquals(mode, AutoRenewalMode.findByCode(mode.getCode()));
        assertFalse(mode.getLabel().isBlank());
        assertFalse(mode.getDescription().isBlank());
    }

    @Test
    void exposesTheCodes() {
        assertEquals("location", AutoRenewalMode.LOCATION.getCode());
        assertEquals("ilmKey", AutoRenewalMode.ILM_KEY.getCode());
        assertEquals("none", AutoRenewalMode.NONE.getCode());
    }

    @Test
    void findByCodeRejects_whenTheCodeIsUnknown() {
        assertThrows(ValidationException.class, () -> AutoRenewalMode.findByCode("hologram"));
    }
}
