package com.czertainly.api.clients.mq.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * Authentication configuration for proxy connector requests.
 * Maps to the proxy's expected authentication format.
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConnectorAuth implements Serializable {

    @Schema(description = "Authentication type",
            examples = {"NONE", "BASIC", "API_KEY", "BEARER", "JWT", "CERTIFICATE"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String type;

    @ToString.Exclude
    @Schema(description = "Authentication attributes based on type. " +
            "BASIC: {username, password}. " +
            "API_KEY: {headerName, apiKey}. " +
            "BEARER/JWT: {token}. " +
            "CERTIFICATE: {keystore, keystorePassword, truststore, truststorePassword}",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> attributes;

}
