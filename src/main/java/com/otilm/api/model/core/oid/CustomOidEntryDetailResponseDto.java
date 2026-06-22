package com.otilm.api.model.core.oid;

import com.otilm.api.model.core.oid.properties.AdditionalOidPropertiesDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomOidEntryDetailResponseDto extends CustomOidEntryResponseDto implements Serializable {

    @Schema(description = "Additional properties depending on the OID category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AdditionalOidPropertiesDto additionalProperties;
}
