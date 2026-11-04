package com.otilm.api.model;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import com.otilm.api.model.core.certificate.group.GroupDto;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.cryptography.key.KeyAssociationDto;
import com.otilm.api.model.core.cryptography.key.KeyDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyTransferAvailabilityDto;
import com.otilm.api.model.core.cryptography.key.KeyTransferCapabilityDto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import com.otilm.api.model.core.cryptography.token.TokenInstanceDetailDto;
import com.otilm.api.model.core.cryptography.token.TokenInstanceStatusDetailDto;
import com.otilm.api.model.core.cryptography.tokenprofile.TokenProfileDetailDto;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds the constructor signature each of these classes carried before it gained allowedActions. Each call below
 * compiles only while the previous signature exists.
 */
class AllowedActionsConstructorCompatibilityTest {

    private static final List<ResponseAttribute> NO_RESPONSE_ATTRIBUTES = List.of();

    @Test
    void aKeyBuiltWithoutAllowedActionsKeepsItsRemovableFlagAndReportsNone() {
        KeyDetailDto key = new KeyDetailDto("description", OffsetDateTime.now(), "profile-uuid", "profile",
                "token-uuid", "token", NO_RESPONSE_ATTRIBUTES, NO_RESPONSE_ATTRIBUTES, "owner", "owner-uuid",
                List.<GroupDto>of(), List.<KeyItemDetailDto>of(), List.<KeyAssociationDto>of(), ComplianceStatus.OK,
                true);

        assertTrue(key.isRemovableFromInventory());
        assertNull(key.getAllowedActions());
    }

    @Test
    void aTokenInstanceBuiltWithoutAllowedActionsKeepsItsKeyTransferAndReportsNone() {
        KeyTransferAvailabilityDto keyTransfer = new KeyTransferAvailabilityDto();

        TokenInstanceDetailDto token = new TokenInstanceDetailDto("connector", "connector-uuid", "kind",
                new TokenInstanceStatusDetailDto(), 2, NO_RESPONSE_ATTRIBUTES, List.<MetadataResponseDto>of(),
                NO_RESPONSE_ATTRIBUTES, keyTransfer);

        assertSame(keyTransfer, token.getKeyTransfer());
        assertNull(token.getAllowedActions());
    }

    @Test
    void aTokenProfileBuiltWithoutAllowedActionsKeepsItsKeyTransferAndReportsNone() {
        KeyTransferCapabilityDto keyTransfer = new KeyTransferCapabilityDto();

        TokenProfileDetailDto profile = new TokenProfileDetailDto("description", "token-uuid", "token",
                NO_RESPONSE_ATTRIBUTES, NO_RESPONSE_ATTRIBUTES, TokenInstanceStatus.CONNECTED, true,
                List.of(KeyUsage.SIGN), keyTransfer);

        assertSame(keyTransfer, profile.getKeyTransfer());
        assertNull(profile.getAllowedActions());
    }
}
