package com.otilm.api.model.core.oid;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CustomOidEntryUpdateRequestDto {

    @Schema(description = "Display name of the custom OID entry", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String displayName;

    @Schema(description = "Description of the custom OID entry", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Category of the OID entry. When updating OID entry, this property does not change the category and is instead used to determine type of additional properties.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    private OidCategory category;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "category"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = RdnAttributeTypeOidPropertiesDto.class, name = OidCategory.Codes.RDN_ATTRIBUTE_TYPE)
    })
    @Schema(description = "Additional properties depending on OID category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private AdditionalOidPropertiesDto additionalProperties;
}
