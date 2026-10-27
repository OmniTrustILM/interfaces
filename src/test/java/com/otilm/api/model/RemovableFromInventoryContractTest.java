package com.otilm.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.cryptography.key.KeyDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDetailDto;
import com.otilm.api.model.core.cryptography.key.KeyItemDto;
import com.otilm.api.model.core.cryptography.key.KeyItemListDto;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.OpenApiSchemaTestSupport.openApi31Schema;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Core evaluates {@code removableFromInventory} for the key detail and the key listing only, so only those two carry
 * it.
 */
class RemovableFromInventoryContractTest {

    private static final List<ResponseAttribute> NO_RESPONSE_ATTRIBUTES = List.of();

    private static final String FLAG = "removableFromInventory";

    @Test
    void aKeyDetailBuiltWithItsPreviousConstructorIsNotRemovable() {
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
    void theKeyDetailAndTheListingRowRequireTheFlag() {
        // given
        // when
        List<String> detailRequired = openApi31Schema(KeyDetailDto.class).getRequired();
        List<String> rowRequired = openApi31Schema(KeyItemListDto.class).getRequired();

        // then
        assertTrue(detailRequired.contains(FLAG), detailRequired.toString());
        assertTrue(rowRequired.containsAll(List.of(FLAG, "keyWrapperUuid", "state")), rowRequired.toString());
    }

    @Test
    void aKeyItemOutsideTheListingHasNoFlag() {
        // given
        // when
        Schema<?> item = openApi31Schema(KeyItemDto.class);

        // then
        assertNull(item.getProperties().get(FLAG), item.getProperties().keySet().toString());
        assertFalse(item.getRequired().contains(FLAG), item.getRequired().toString());
    }

    @Test
    void theKeyDetailAndTheListingRowCarryTheFlagOnTheWire() throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        KeyItemListDto row = new KeyItemListDto();
        row.setKeyWrapperUuid("key-uuid");
        row.setRemovableFromInventory(true);
        KeyDetailDto key = new KeyDetailDto();
        key.setRemovableFromInventory(true);

        // when
        JsonNode rowJson = mapper.readTree(mapper.writeValueAsString(row));
        JsonNode keyJson = mapper.readTree(mapper.writeValueAsString(key));

        // then
        assertTrue(rowJson.path(FLAG).asBoolean(), rowJson.toString());
        assertEquals("key-uuid", rowJson.path("keyWrapperUuid").asText(), rowJson.toString());
        assertTrue(keyJson.path(FLAG).asBoolean(), keyJson.toString());
    }
}
