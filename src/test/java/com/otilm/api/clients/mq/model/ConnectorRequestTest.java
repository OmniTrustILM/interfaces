package com.otilm.api.clients.mq.model;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConnectorRequestTest {

    @Test
    void toStringLeavesTheHeadersTheBodyAndTheCredentialsOut() {
        ConnectorRequest request = ConnectorRequest
                .builder()
                .method("POST")
                .path("/v2/keys")
                .headers(Map.of("Authorization", "Bearer sent-header-secret"))
                .body(Map.of("passphrase", "sent-body-secret"))
                .credentialData(Map.of("password", "sent-credential-secret"))
                .build();

        String text = request.toString();

        assertTrue(text.contains("/v2/keys"));
        assertFalse(text.contains("sent-header-secret"));
        assertFalse(text.contains("sent-body-secret"));
        assertFalse(text.contains("sent-credential-secret"));
    }
}
