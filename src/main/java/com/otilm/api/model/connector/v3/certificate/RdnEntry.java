package com.otilm.api.model.connector.v3.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "A single RDN component in an ordered subject DN")
public class RdnEntry {

    @Schema(description = "RDN attribute type: a short code (e.g. \"CN\") or a dotted-decimal OID; resolved via the OID registry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "RDN type is required")
    private String type;

    @Schema(description = "RDN attribute value",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "RDN value is required")
    private String value;
}
