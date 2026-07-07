package com.otilm.api.model.client.connector.v2;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConnectorInterfaceCategoryTest {

    @Test
    void everyInterfaceDeclaresCategory() {
        for (ConnectorInterface iface : ConnectorInterface.values()) {
            assertNotNull(iface.getCategory(),
                "ConnectorInterface " + iface + " must declare an InterfaceCategory");
        }
    }

    @Test
    void existingInterfacesClassifiedCorrectly() {
        assertEquals(ConnectorInterface.InterfaceCategory.COMMON, ConnectorInterface.INFO.getCategory());
        assertEquals(ConnectorInterface.InterfaceCategory.COMMON, ConnectorInterface.HEALTH.getCategory());
        assertEquals(ConnectorInterface.InterfaceCategory.COMMON, ConnectorInterface.METRICS.getCategory());
        assertEquals(ConnectorInterface.InterfaceCategory.COMMON, ConnectorInterface.ATTRIBUTES.getCategory());
        assertEquals(ConnectorInterface.InterfaceCategory.FUNCTIONAL, ConnectorInterface.AUTHORITY.getCategory());
        assertEquals(ConnectorInterface.InterfaceCategory.FUNCTIONAL, ConnectorInterface.DISCOVERY.getCategory());
        assertEquals(ConnectorInterface.InterfaceCategory.FUNCTIONAL, ConnectorInterface.SECRET.getCategory());
        assertEquals(ConnectorInterface.InterfaceCategory.FUNCTIONAL, ConnectorInterface.SIGNING.getCategory());
    }
}
