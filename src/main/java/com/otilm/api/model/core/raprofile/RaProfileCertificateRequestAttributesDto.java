package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * View of an RA Profile's static request-attribute set: the ordered platform-owned definitions and the
 * merge mode that governs a combination with a connector-supplied set.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "RA Profile static request-attribute definitions and the merge mode.")
public class RaProfileCertificateRequestAttributesDto {

    @ArraySchema(arraySchema = @Schema(
            description = "Ordered list of platform-owned request-attribute definitions for this RA Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    private List<BaseAttribute> requestAttributes = new ArrayList<>();

    @Schema(description = "How the static set combines with a connector-supplied set; always the effective mode",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private AttributeSetMergeMode mergeMode = AttributeSetMergeMode.MERGE;

    @ArraySchema(arraySchema = @Schema(
            description = "Core-side value-source bindings attached onto connector-supplied (or static) definitions by reference",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    private List<ValueSourceBindingDto> valueSourceBindings = new ArrayList<>();

    @Schema(description = "Whether an external CSR violating the resolved set is rejected (true) or accepted with warnings (false); null inherits the platform default",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean externalCsrValidationStrict;
}
