package com.otilm.api.model;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import com.otilm.api.model.core.cryptography.key.KeyTransferCapabilityDto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import com.otilm.api.model.core.cryptography.tokenprofile.TokenProfileDetailDto;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Other repositories still build these classes with the constructors from before key listing added its fields.
 */
class KeyListingConstructorCompatibilityTest {

    private static final List<ResponseAttribute> NO_RESPONSE_ATTRIBUTES = List.of();

    @Test
    void aTokenProfileBuiltWithoutKeyListingReportsItUnavailable() {
        // given
        KeyTransferCapabilityDto keyTransfer = new KeyTransferCapabilityDto();

        // when
        TokenProfileDetailDto profile = new TokenProfileDetailDto("description", "token-uuid", "token",
                NO_RESPONSE_ATTRIBUTES, NO_RESPONSE_ATTRIBUTES, TokenInstanceStatus.CONNECTED, true,
                List.of(KeyUsage.SIGN), keyTransfer);

        // then
        assertSame(keyTransfer, profile.getKeyTransfer());
        assertFalse(profile.isKeyListAvailable());
    }
}
