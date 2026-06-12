package com.otilm.api.model.client.connector.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectorInterfaceInfoTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void allArgsConstructorBuildsInstance() {
        ConnectorInterfaceInfo info = new ConnectorInterfaceInfo(
                ConnectorInterface.AUTHORITY,
                "v3",
                List.of(FeatureFlag.CERTIFICATE_REGISTRATION));
        assertEquals(ConnectorInterface.AUTHORITY, info.getCode());
        assertEquals("v3", info.getVersion());
        assertEquals(1, info.getFeatures().size());
    }

    @Test
    void roundTripsThroughJackson() throws Exception {
        ConnectorInterfaceInfo info = new ConnectorInterfaceInfo(
                ConnectorInterface.AUTHORITY, "v3",
                List.of(FeatureFlag.CERTIFICATE_REGISTRATION));
        String json = mapper.writeValueAsString(info);
        ConnectorInterfaceInfo back = mapper.readValue(json, ConnectorInterfaceInfo.class);
        assertEquals(info, back);
    }
}
