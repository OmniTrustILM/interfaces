package com.otilm.api.clients.mq.model;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectorResponseTest {

    @Test
    void toStringLeavesTheHeadersTheBodyAndTheErrorOut() {
        ConnectorResponse response = ConnectorResponse
                .builder()
                .statusCode(500)
                .headers(Map.of("Set-Cookie", "session=echoed-header-secret"))
                .body(Map.of("passphrase", "echoed-body-secret"))
                .bodyText("echoed-text-secret")
                .error("refused for echoed-error-secret")
                .build();

        String text = response.toString();

        assertTrue(text.contains("500"));
        assertFalse(text.contains("echoed-header-secret"));
        assertFalse(text.contains("echoed-body-secret"));
        assertFalse(text.contains("echoed-text-secret"));
        assertFalse(text.contains("echoed-error-secret"));
    }
}
