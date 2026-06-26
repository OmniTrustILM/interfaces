package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.v3.mapping.SourceParam;
import com.otilm.api.model.common.attribute.v3.mapping.ValueSourceType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * A Core-side value-source binding attached to a request-attribute definition on an RA Profile.
 * It binds to a definition by {@code attributeUuid}, falling back to {@code attributeName} — at least one of the two must be set.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Core-side value-source binding onto a request-attribute definition; binds by attribute UUID (name fallback).")
public class ValueSourceBindingDto {

    @Schema(description = "UUID of the target attribute definition; primary binding key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String attributeUuid;

    @Schema(description = "Name of the target attribute definition; fallback binding key when the UUID is absent or rotated",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String attributeName;

    @Schema(description = "Value-source provider type to bind onto the target definition", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Value-source binding valueSourceType is required")
    private ValueSourceType valueSourceType;

    @Schema(description = "Reference to a registered collection/source; used by collection-style value sources",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String collectionRef;

    @ArraySchema(arraySchema = @Schema(
            description = "Optional cascading dependency parameters (from other attributes)",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    @Valid
    private List<SourceParam> params;

    @AssertTrue(message = "Value-source binding requires attributeUuid or attributeName to be set.")
    @JsonIgnore
    public boolean isValid() {
        return (attributeUuid != null && !attributeUuid.isBlank())
                || (attributeName != null && !attributeName.isBlank());
    }
}
