package com.otilm.api.model.core.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.events.data.CryptoAssetPqcNotReadyEventData;
import com.otilm.api.model.core.auth.Resource;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CryptographicAssetResourceEventTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void cryptographicAssetsPublishTheNotReadyEvent() {
        assertEquals(List.of("crypto_asset_pqc_not_ready"),
                ResourceEvent
                        .listEventsByResource(Resource.CRYPTO_ASSET)
                        .stream()
                        .map(ResourceEvent::getCode)
                        .toList());
    }

    @Test
    void cbomRulesOnlyReceiveTheCbomSyncedEvent() {
        assertEquals(List.of("cbom_synced"),
                ResourceEvent.listEventsByResource(Resource.CBOM).stream().map(ResourceEvent::getCode).toList());
    }

    @Test
    void readsAndWritesTheNotReadyEventCode() throws Exception {
        ResourceEvent event = mapper.readValue("\"crypto_asset_pqc_not_ready\"", ResourceEvent.class);

        assertEquals(Resource.CRYPTO_ASSET, event.getResource());
        assertEquals(List.of(), event.getOverridingResources());
        assertEquals(CryptoAssetPqcNotReadyEventData.class, event.getEventData());
        assertEquals("\"crypto_asset_pqc_not_ready\"", mapper.writeValueAsString(event));
    }

    @Test
    void rejectsTheSupersededAssetAddedCode() {
        assertThrows(ValidationException.class, () -> ResourceEvent.findByCode("crypto_asset_added"));
    }
}
