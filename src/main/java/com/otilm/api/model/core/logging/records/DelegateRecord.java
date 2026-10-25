package com.otilm.api.model.core.logging.records;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "The immediate delegate from the outermost act object of a validated RFC 8693 access token. "
        + "Additional members may describe nested delegation in a future version.")
public record DelegateRecord(
        @NotNull @Schema(description = "The value of the provider's configured delegate claim inside act.",
                requiredMode = Schema.RequiredMode.REQUIRED) String identifier,
        @JsonInclude(JsonInclude.Include.NON_NULL) @Schema(
                description = "The delegate subject from act.sub, when present.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED) String subject)
        implements
            Serializable {
}
