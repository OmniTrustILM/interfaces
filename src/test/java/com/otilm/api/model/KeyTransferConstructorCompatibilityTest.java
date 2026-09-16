package com.otilm.api.model;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.cryptography.key.KeyCompromiseReason;
import com.otilm.api.model.client.cryptography.key.KeyRequestDto;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyState;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import com.otilm.api.model.core.cryptography.token.TokenInstanceDetailDto;
import com.otilm.api.model.core.cryptography.token.TokenInstanceStatusDetailDto;
import com.otilm.api.model.core.cryptography.tokenprofile.TokenProfileDetailDto;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Holds the constructor signature each of these classes carried before it gained a key-transfer field. Each call below
 * compiles only while the previous signature exists, and states what the new field is when nobody supplies it.
 */
class KeyTransferConstructorCompatibilityTest {

    private static final List<ResponseAttribute> NO_RESPONSE_ATTRIBUTES = List.of();

    @Test
    void aTokenInstanceBuiltWithoutKeyTransferReportsNone() {
        // given
        // when
        TokenInstanceDetailDto token = new TokenInstanceDetailDto("connector", "connector-uuid", "kind",
                new TokenInstanceStatusDetailDto(), 2, NO_RESPONSE_ATTRIBUTES, List.<MetadataResponseDto>of(),
                NO_RESPONSE_ATTRIBUTES);

        // then
        assertEquals("kind", token.getKind());
        assertEquals(2, token.getTokenProfiles());
        assertNull(token.getKeyTransfer(), "key transfer is unreported rather than reported as unavailable");
    }

    @Test
    void aTokenProfileBuiltWithoutKeyTransferReportsNone() {
        // given
        // when
        TokenProfileDetailDto profile = new TokenProfileDetailDto("description", "token-uuid", "token",
                NO_RESPONSE_ATTRIBUTES, NO_RESPONSE_ATTRIBUTES, TokenInstanceStatus.CONNECTED, true,
                List.of(KeyUsage.SIGN));

        // then
        assertEquals("token", profile.getTokenInstanceName());
        assertEquals(List.of(KeyUsage.SIGN), profile.getUsages());
        assertNull(profile.getKeyTransfer(), "key transfer is unreported rather than reported as unavailable");
    }

    @Test
    void aKeyItemBuiltWithoutTheExportableFlagIsNotExportable() {
        // given
        // when
        KeyItemDetailDto item = new KeyItemDetailDto("key-reference", KeyType.PRIVATE_KEY, KeyAlgorithm.RSA,
                KeyFormat.PRKI, "key-data", 2048, List.<MetadataResponseDto>of(), List.of(KeyUsage.SIGN), true,
                KeyState.ACTIVE, KeyCompromiseReason.UNAUTHORIZED_DISCLOSURE, ComplianceStatus.NOT_CHECKED);

        // then
        assertEquals(2048, item.getLength());
        assertEquals(KeyState.ACTIVE, item.getState());
        assertFalse(item.isExportable(), "a key item is not exportable unless it is said to be");
    }

    @Test
    void aKeyRequestBuiltWithoutTheExportableIntentLeavesItUnstated() {
        // given
        // when
        KeyRequestDto request = new KeyRequestDto("name", "description", List.of("group-uuid"),
                List.<RequestAttribute>of(), List.<RequestAttribute>of(), true);

        // then
        assertEquals("name", request.getName());
        assertEquals(List.of("group-uuid"), request.getGroupUuids());
        assertFalse(request.getExportable(), "an unstated exportable intent is read as false by the platform");
    }
}
