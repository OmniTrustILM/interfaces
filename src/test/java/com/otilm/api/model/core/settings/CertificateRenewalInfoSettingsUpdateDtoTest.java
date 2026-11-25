package com.otilm.api.model.core.settings;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateRenewalInfoSettingsUpdateDtoTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void defaultsAreValid() {
        CertificateRenewalInfoSettingsUpdateDto dto = new CertificateRenewalInfoSettingsUpdateDto();
        assertTrue(VALIDATOR.validate(dto).isEmpty());
        assertEquals(3600, dto.getRefreshMinInterval());
        assertEquals(86400, dto.getRefreshMaxInterval());
        assertEquals(21600, dto.getRefreshDefaultInterval());
        assertEquals(20, dto.getRefreshBatchSize());
        assertEquals(604800, dto.getRefreshRecheckInterval());
    }

    @Test
    void rejectsBatchSizeOutsideOneToFifty() {
        CertificateRenewalInfoSettingsUpdateDto dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshBatchSize(0);
        assertFalse(VALIDATOR.validate(dto).isEmpty());
        dto.setRefreshBatchSize(51);
        assertFalse(VALIDATOR.validate(dto).isEmpty());
        dto.setRefreshBatchSize(50);
        assertTrue(VALIDATOR.validate(dto).isEmpty());
    }

    @Test
    void rejectsIntervalsOutOfRange() {
        CertificateRenewalInfoSettingsUpdateDto dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMinInterval(59);
        assertFalse(VALIDATOR.validate(dto).isEmpty());
        dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMaxInterval(604801);
        assertFalse(VALIDATOR.validate(dto).isEmpty());
        dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshRecheckInterval(3599);
        assertFalse(VALIDATOR.validate(dto).isEmpty());
    }

    @Test
    void rejectsMinAboveDefaultOrDefaultAboveMax() {
        CertificateRenewalInfoSettingsUpdateDto dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMinInterval(30000);
        assertFalse(VALIDATOR.validate(dto).isEmpty(), "min must not exceed default");
        dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMaxInterval(7200);
        assertFalse(VALIDATOR.validate(dto).isEmpty(), "default must not exceed max");
    }

    @Test
    void cascadesFromCertificateSettingsUpdate() {
        CertificateSettingsUpdateDto parent = new CertificateSettingsUpdateDto();
        CertificateRenewalInfoSettingsUpdateDto child = new CertificateRenewalInfoSettingsUpdateDto();
        child.setRefreshBatchSize(0);
        parent.setRenewalInfo(child);
        assertFalse(VALIDATOR.validate(parent).isEmpty());
    }

    @Test
    void intervalOrderIsSkippedWhileAnyIntervalIsNull() {
        CertificateRenewalInfoSettingsUpdateDto dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMinInterval(null);
        assertTrue(dto.isIntervalOrderValid());
        dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshDefaultInterval(null);
        assertTrue(dto.isIntervalOrderValid());
        dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMaxInterval(null);
        assertTrue(dto.isIntervalOrderValid());
    }

    @Test
    void intervalOrderChecksBothComparisons() {
        CertificateRenewalInfoSettingsUpdateDto dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMinInterval(30000);
        assertFalse(dto.isIntervalOrderValid());
        dto = new CertificateRenewalInfoSettingsUpdateDto();
        dto.setRefreshMaxInterval(7200);
        assertFalse(dto.isIntervalOrderValid());
    }
}
