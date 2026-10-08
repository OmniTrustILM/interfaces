package com.otilm.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiClientWireGoldenTest {

    @Test
    void apiClientMapperIsConfiguredAsOnThe35Line() throws IOException {
        WireGolden.assertFingerprintMatches("api-client-mapper.txt", ApiClientCodecs.objectMapper());
    }

    @Test
    void aModuleTheServiceLoaderListsStaysOffTheMapper() {
        boolean listedForTheServiceLoader = ObjectMapper
                .findModules()
                .stream()
                .anyMatch(module -> ServiceLoaderProbeModule.NAME.equals(module.getModuleName()));
        assertTrue(listedForTheServiceLoader, "the probe's absence from the mapper proves nothing unless it is listed");
        assertFalse(ApiClientCodecs.objectMapper().getRegisteredModuleIds().contains(ServiceLoaderProbeModule.NAME),
                "the mapper registers only the modules Spring's Jackson2ObjectMapperBuilder registers");
    }
}
