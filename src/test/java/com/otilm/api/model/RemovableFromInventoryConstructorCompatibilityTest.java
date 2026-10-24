package com.otilm.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.cryptography.key.KeyDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDto;
import com.otilm.api.model.core.cryptography.key.KeyState;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Holds the constructor signature each key DTO carried before it gained {@code removableFromInventory}. Each call below
 * compiles only while the previous signature exists, and states what the flag is when nobody supplies it.
 */
class RemovableFromInventoryConstructorCompatibilityTest {

    private static final List<ResponseAttribute> NO_RESPONSE_ATTRIBUTES = List.of();

    @Test
    void aKeyItemBuiltWithoutTheFlagIsNotRemovable() {
        // given
        // when
        KeyItemDto item = new KeyItemDto("description", null, "key-uuid", null, null, null, null, null, null, List.of(),
                0, null, KeyType.PRIVATE_KEY, KeyAlgorithm.RSA, KeyFormat.PRKI, 2048, List.of(), true, KeyState.ACTIVE,
                ComplianceStatus.NOT_CHECKED, null);

        // then
        assertEquals("key-uuid", item.getKeyWrapperUuid());
        assertEquals(KeyState.ACTIVE, item.getState());
        assertFalse(item.isRemovableFromInventory(), "an unstated flag reads as not removable");
    }

    @Test
    void aKeyDetailBuiltWithoutTheFlagIsNotRemovable() {
        // given
        // when
        KeyDetailDto key = new KeyDetailDto("description", null, "profile-uuid", "profile", "token-uuid", "token",
                NO_RESPONSE_ATTRIBUTES, NO_RESPONSE_ATTRIBUTES, null, null, List.of(), List.<KeyItemDetailDto>of(),
                List.of(), ComplianceStatus.NOT_CHECKED);

        // then
        assertEquals("token", key.getTokenInstanceName());
        assertEquals(ComplianceStatus.NOT_CHECKED, key.getComplianceStatus());
        assertFalse(key.isRemovableFromInventory(), "an unstated flag reads as not removable");
    }

    @Test
    void bothCarryTheFlagOnTheWire() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        KeyItemDto item = new KeyItemDto();
        item.setRemovableFromInventory(true);
        KeyDetailDto key = new KeyDetailDto();
        key.setRemovableFromInventory(true);

        // when
        JsonNode itemJson = mapper.readTree(mapper.writeValueAsString(item));
        JsonNode keyJson = mapper.readTree(mapper.writeValueAsString(key));

        // then
        assertTrue(itemJson.path("removableFromInventory").asBoolean(), itemJson.toString());
        assertTrue(keyJson.path("removableFromInventory").asBoolean(), keyJson.toString());
    }
}
