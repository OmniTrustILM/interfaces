package com.otilm.api.model.client.connector.v2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FeatureFlagTest {

    @Test
    void secretContentVerification_codeAndInterface() {
        Assertions.assertEquals("secretContentVerification", FeatureFlag.SECRET_CONTENT_VERIFICATION.getCode());
        Assertions.assertTrue(FeatureFlag.SECRET_CONTENT_VERIFICATION.getApplicableInterfaces().contains(ConnectorInterface.SECRET));
    }

    @Test
    void resolvesByCode() {
        Assertions.assertEquals(FeatureFlag.SECRET_CONTENT_VERIFICATION, FeatureFlag.findByCode("secretContentVerification"));
    }
}
