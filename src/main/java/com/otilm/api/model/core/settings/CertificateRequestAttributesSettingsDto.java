package com.otilm.api.model.core.settings;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * View of the editable platform default request-attribute set: the terminal fallback set applied when neither
 * the RA-Profile static set nor a connector-supplied set yields definitions. No merge mode — it is not combined with another set.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Editable platform default request-attribute definitions (terminal fallback set).")
public class CertificateRequestAttributesSettingsDto implements Serializable {

    @ArraySchema(arraySchema = @Schema(
            description = "Ordered list of platform default request-attribute definitions",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    private List<BaseAttribute> requestAttributes = new ArrayList<>();

    @Schema(description = "Platform default for external-CSR strictness; an RA Profile inherits this when its own externalCsrValidationStrict is null",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean externalCsrValidationStrict;
}
