package com.otilm.api.model.client.connector.v2;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConnectorInterfaceCategoryTest {

    private static final Set<ConnectorInterface> COMMON = EnumSet.of(
        ConnectorInterface.INFO,
        ConnectorInterface.HEALTH,
        ConnectorInterface.METRICS,
        ConnectorInterface.ATTRIBUTES);

    @Test
    void everyInterfaceIsCategorizedCorrectly() {
        for (ConnectorInterface iface : ConnectorInterface.values()) {
            ConnectorInterface.InterfaceCategory expected = COMMON.contains(iface)
                ? ConnectorInterface.InterfaceCategory.COMMON
                : ConnectorInterface.InterfaceCategory.FUNCTIONAL;
            assertEquals(expected, iface.getCategory(), iface.name());
        }
    }

    @Test
    void findByCodeRoundTripsAllValues() {
        for (ConnectorInterface iface : ConnectorInterface.values()) {
            assertEquals(iface, ConnectorInterface.findByCode(iface.getCode()), iface.name());
        }
    }
}
