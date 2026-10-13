package com.otilm.api.model.common.events.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.core.cryptoasset.CryptographicAssetType;
import com.otilm.api.model.core.cryptoasset.PqcVerdict;
import com.otilm.api.testsupport.OpenApiSchemaTestSupport;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CryptoAssetAddedEventDataTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void anUntypedAssetOmitsItsTypeFromEventData() throws Exception {
        CryptoAssetAddedEventData event = event();

        var json = mapper.readTree(mapper.writeValueAsString(event));

        assertFalse(json.has("assetType"));
        assertEquals(event.getCryptoAssetUuid().toString(), json.get("cryptoAssetUuid").asText());
        assertEquals("unknown", json.get("pqcVerdict").asText());
    }

    @Test
    void aTypedAssetKeepsItsTypeInEventData() throws Exception {
        CryptoAssetAddedEventData event = event();
        event.setAssetType(CryptographicAssetType.ALGORITHM);

        var json = mapper.readTree(mapper.writeValueAsString(event));

        assertEquals("algorithm", json.get("assetType").asText());
    }

    @Test
    void thePublishedSchemaAllowsAnEventWithoutAnAssetType() {
        var schema = OpenApiSchemaTestSupport.openApi31Schema(CryptoAssetAddedEventData.class);

        assertTrue(schema.getProperties().containsKey("assetType"));
        assertFalse(schema.getRequired().contains("assetType"));
        assertTrue(schema.getRequired().containsAll(List.of("cryptoAssetUuid", "cbomUuid")));
    }

    @Test
    void thePublishedSchemaRequiresTheSettledPqcVerdict() {
        var schema = OpenApiSchemaTestSupport.openApi31Schema(CryptoAssetAddedEventData.class);

        assertTrue(schema.getRequired().contains("pqcVerdict"));
    }

    private static CryptoAssetAddedEventData event() {
        CryptoAssetAddedEventData event = new CryptoAssetAddedEventData();
        event.setCryptoAssetUuid(UUID.randomUUID());
        event.setCbomUuid(UUID.randomUUID());
        event.setPqcVerdict(PqcVerdict.UNKNOWN);
        return event;
    }
}
