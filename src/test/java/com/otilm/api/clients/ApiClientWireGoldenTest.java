package com.otilm.api.clients;

import java.io.IOException;
import org.junit.jupiter.api.Test;

class ApiClientWireGoldenTest {

    @Test
    void apiClientMapperIsConfiguredAsOnThe35Line() throws IOException {
        WireGolden.assertMatches("api-client-mapper.txt", WireGolden.fingerprint(ApiClientCodecs.objectMapper()));
    }
}
