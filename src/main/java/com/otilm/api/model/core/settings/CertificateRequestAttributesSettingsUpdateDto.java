package com.otilm.api.model.core.settings;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Request to edit the platform default request-attribute set. Definitions are platform-owned (connectorUuid = null);
 * only definitions are stored, never request values.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Request to set the editable platform default request-attribute definitions.")
public class CertificateRequestAttributesSettingsUpdateDto implements Serializable {

    @ArraySchema(arraySchema = @Schema(
            description = "Ordered list of platform default request-attribute definitions",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    @Valid
    private List<BaseAttribute> requestAttributes = new ArrayList<>();

    @Schema(description = "Platform default for external-CSR strictness; an RA Profile inherits this when its own externalCsrValidationStrict is null",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean externalCsrValidationStrict;
}
