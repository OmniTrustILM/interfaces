package com.otilm.api.model.client.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.connector.v2.ConnectorInterfaceDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The listing's own wire shape. It carries the connector interface for the same reason the detail does — a client
 * reading a page of runs can tell which generation each one is driven by without opening it. It carries the item total
 * and the resources for the same reason: a run that found only keys must not read as a run that found nothing.
 */
class DiscoveryListDtoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void carriesTheConnectorInterfaceOfAV2Run() throws Exception {
        ConnectorInterfaceDto iface = new ConnectorInterfaceDto();
        iface.setUuid(UUID.randomUUID());
        iface.setCode(ConnectorInterface.DISCOVERY);
        iface.setVersion("v2");
        DiscoveryListDto dto = new DiscoveryListDto();
        dto.setName("nightly-scan");
        dto.setConnectorInterface(iface);

        String json = mapper.writeValueAsString(dto);
        DiscoveryListDto back = mapper.readValue(json, DiscoveryListDto.class);

        assertTrue(json.contains("\"connectorInterface\""), json);
        assertEquals(iface.getUuid(), back.getConnectorInterface().getUuid());
        assertEquals("v2", back.getConnectorInterface().getVersion());
    }

    @Test
    void carriesTheItemTotalAndTheResourcesAsWireCodes() throws Exception {
        DiscoveryListDto dto = new DiscoveryListDto();
        dto.setName("keys-scan");
        dto.setTotalItemsDiscovered(52);
        dto.setResources(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY));

        String json = mapper.writeValueAsString(dto);
        DiscoveryListDto back = mapper.readValue(json, DiscoveryListDto.class);

        assertTrue(json.contains("\"totalItemsDiscovered\":52"), json);
        assertTrue(json.contains("\"resources\":[\"certificates\",\"keys\"]"), json);
        assertEquals(52, back.getTotalItemsDiscovered());
        assertEquals(List.of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY), back.getResources());
    }

    @Test
    void sendsAZeroItemTotalRatherThanLeavingItOut() throws Exception {
        String json = mapper.writeValueAsString(new DiscoveryListDto());

        assertTrue(json.contains("\"totalItemsDiscovered\":0"), json);
    }

    @Test
    void omitsTheConnectorInterfaceForAV1Run() throws Exception {
        DiscoveryListDto dto = new DiscoveryListDto();
        dto.setName("legacy-scan");

        String json = mapper.writeValueAsString(dto);

        assertFalse(json.contains("connectorInterface"), json);
        assertNull(mapper.readValue(json, DiscoveryListDto.class).getConnectorInterface());
    }
}
