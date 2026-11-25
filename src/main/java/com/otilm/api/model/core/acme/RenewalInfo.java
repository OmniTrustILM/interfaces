package com.otilm.api.model.core.acme;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ACME Renewal Information resource as defined in RFC 9773.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RenewalInfo {

    @Schema(description = "Time window in which the ACME client is suggested to start renewing the certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private SuggestedWindow suggestedWindow;

    @JsonProperty("explanationURL")
    @Schema(description = "URL of a page explaining why the suggested window was chosen",
            examples = {"https://some-server.com/renewal-explanation"})
    private String explanationUrl;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestedWindow {

        @Schema(description = "Start of the suggested renewal window (RFC 3339)", format = "date-time", type = "string",
                requiredMode = Schema.RequiredMode.REQUIRED, examples = {"2026-01-01T00:00:00Z"})
        private String start;

        @Schema(description = "End of the suggested renewal window (RFC 3339)", format = "date-time", type = "string",
                requiredMode = Schema.RequiredMode.REQUIRED, examples = {"2026-01-02T00:00:00Z"})
        private String end;
    }
}
