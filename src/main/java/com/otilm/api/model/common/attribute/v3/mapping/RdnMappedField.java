package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Maps an attribute value to an X.509 RDN component")
public class RdnMappedField extends MappedField {

    @Schema(description = "RDN code (e.g. \"CN\") or dotted-decimal OID; resolved via the OID registry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String rdn;
}
