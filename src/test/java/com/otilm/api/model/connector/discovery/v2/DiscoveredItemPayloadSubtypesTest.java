package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.otilm.api.model.core.auth.Resource;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Holds {@link DiscoveredItemPayloadDto#DISCOVERABLE} in step with the {@code @JsonSubTypes} it describes.
 *
 * <p>
 * The constant is what Core gates an accepted run on and what the client gates the per-resource attribute route on, so
 * a payload subtype registered without it would leave discovery refusing a resource the contract can now carry — with
 * nothing failing to compile and no other test noticing. Reading the annotation here rather than in production keeps
 * the shipped path a plain constant.
 */
class DiscoveredItemPayloadSubtypesTest {

    @Test
    void theDiscoverableSetIsExactlyTheRegisteredPayloadSubtypes() {
        JsonSubTypes registered = DiscoveredItemPayloadDto.class.getAnnotation(JsonSubTypes.class);
        assertNotNull(registered, "DiscoveredItemPayloadDto registers no payload subtypes");

        Set<Resource> byWireCode = Arrays
                .stream(registered.value())
                .map(JsonSubTypes.Type::name)
                .map(Resource::findByCode)
                .collect(Collectors.toSet());

        assertEquals(byWireCode, DiscoveredItemPayloadDto.DISCOVERABLE,
                "DISCOVERABLE and the registered payload subtypes have diverged. A run may target exactly the "
                        + "resources this contract can carry an item for, so adding a subtype means adding it here "
                        + "too — Core and the connector client both read this set.");
    }
}
