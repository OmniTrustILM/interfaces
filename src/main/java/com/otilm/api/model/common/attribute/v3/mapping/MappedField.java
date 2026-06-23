package com.otilm.api.model.common.attribute.v3.mapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "fieldType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RdnMappedField.class, name = FieldType.Codes.RDN),
        @JsonSubTypes.Type(value = SanMappedField.class, name = FieldType.Codes.SAN),
        @JsonSubTypes.Type(value = ExtensionMappedField.class, name = FieldType.Codes.EXTENSION)
})
@Schema(
        description = "Describes a single target field within an object; concrete type is determined by fieldType",
        subTypes = {RdnMappedField.class, SanMappedField.class, ExtensionMappedField.class}
)
public abstract class MappedField implements Serializable {

    @Schema(description = "Field type, determines the concrete subtype", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Field type is required")
    private FieldType fieldType;

    @Schema(description = "Ordering index for fields of the same type (e.g. RDN components); ascending")
    private Integer order;

    @Schema(description = "Provenance of the value when reconciling CSR vs platform-projected values")
    private FieldSource source;
}
