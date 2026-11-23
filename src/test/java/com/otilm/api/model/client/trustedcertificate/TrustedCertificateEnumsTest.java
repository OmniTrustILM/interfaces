package com.otilm.api.model.client.trustedcertificate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TrustedCertificateEnumsTest {

    @Test
    void purposeCodesAreStable() {
        assertEquals("validation", TrustedCertificatePurpose.VALIDATION.getCode());
        assertEquals("platform", TrustedCertificatePurpose.PLATFORM.getCode());
        assertEquals(TrustedCertificatePurpose.PLATFORM, TrustedCertificatePurpose.findByCode("platform"));
    }

    @Test
    void platformStateHasEightValuesAndRejectsUnknownCode() {
        assertEquals(8, TrustedCertificatePlatformState.values().length);
        assertEquals("removalFailed", TrustedCertificatePlatformState.REMOVAL_FAILED.getCode());
        assertThrows(ValidationException.class, () -> TrustedCertificatePlatformState.findByCode("gone"));
    }

    @Test
    void platformSourceRoundTripsThroughJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        assertEquals("\"deployment\"", mapper.writeValueAsString(TrustedCertificatePlatformSource.DEPLOYMENT));
        assertEquals(TrustedCertificatePlatformSource.NONE,
                mapper.readValue("\"none\"", TrustedCertificatePlatformSource.class));
    }

    static Stream<Arguments> enums() {
        return Stream
                .of(Arguments
                        .of(TrustedCertificatePurpose.values(),
                                (Function<String, IPlatformEnum>) TrustedCertificatePurpose::findByCode),
                        Arguments
                                .of(TrustedCertificatePlatformState.values(),
                                        (Function<String, IPlatformEnum>) TrustedCertificatePlatformState::findByCode),
                        Arguments
                                .of(TrustedCertificatePlatformSource.values(),
                                        (Function<String, IPlatformEnum>) TrustedCertificatePlatformSource::findByCode));
    }

    @ParameterizedTest
    @MethodSource("enums")
    void everyValueIsFoundByItsCodeAndCarriesALabelAndDescription(IPlatformEnum[] values,
            Function<String, IPlatformEnum> findByCode) {
        for (IPlatformEnum value : values) {
            assertEquals(value, findByCode.apply(value.getCode()));
            assertFalse(value.getLabel().isBlank(), value.getCode());
            assertFalse(value.getDescription().isBlank(), value.getCode());
        }
    }

    @ParameterizedTest
    @MethodSource("enums")
    void unknownCodeIsRefused(IPlatformEnum[] values, Function<String, IPlatformEnum> findByCode) {
        assertThrows(ValidationException.class, () -> findByCode.apply("unknownCode"));
    }
}
