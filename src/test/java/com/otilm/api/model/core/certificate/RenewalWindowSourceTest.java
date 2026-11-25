package com.otilm.api.model.core.certificate;

import com.otilm.api.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RenewalWindowSourceTest {

    @ParameterizedTest
    @EnumSource(RenewalWindowSource.class)
    void findByCodeReturnsTheSource_whenGivenItsOwnCode(RenewalWindowSource source) {
        assertEquals(source, RenewalWindowSource.findByCode(source.getCode()));
    }

    @Test
    void exposesTheCodesAndLabels() {
        assertEquals("policy", RenewalWindowSource.POLICY.getCode());
        assertEquals("authority", RenewalWindowSource.AUTHORITY.getCode());
        assertEquals("Certificate authority", RenewalWindowSource.AUTHORITY.getLabel());
        assertEquals("forced", RenewalWindowSource.FORCED.getCode());
    }

    @Test
    void findByCodeRejects_whenTheCodeIsUnknown() {
        assertThrows(ValidationException.class, () -> RenewalWindowSource.findByCode("hologram"));
    }
}
