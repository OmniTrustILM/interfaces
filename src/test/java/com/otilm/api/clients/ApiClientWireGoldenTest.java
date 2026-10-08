package com.otilm.api.clients;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ApiClientWireGoldenTest {

    @Test
    void apiClientMapperIsConfiguredAsOnThe35Line() throws IOException {
        WireGolden.assertMatches("api-client-mapper.txt", WireGolden.fingerprint(ApiClientCodecs.objectMapper()));
    }

    @Test
    void aModuleTheServiceLoaderListsStaysOffTheMapper() {
        assertFalse(ApiClientCodecs.objectMapper().getRegisteredModuleIds().contains(ServiceLoaderProbeModule.NAME),
                "the mapper registers only the modules Spring's Jackson2ObjectMapperBuilder registers");
    }
}
