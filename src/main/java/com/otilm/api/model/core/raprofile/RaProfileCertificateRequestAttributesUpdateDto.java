package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Request body to author an RA Profile's static request-attribute set. The set is an <em>ordered</em>
 * {@link BaseAttribute} list (rendering order is preserved).
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Request to set the RA Profile static request-attribute definitions and the merge mode.")
public class RaProfileCertificateRequestAttributesUpdateDto {

    @ArraySchema(arraySchema = @Schema(
            description = "Ordered list of platform-owned request-attribute definitions for this RA Profile",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    @Valid
    private List<BaseAttribute> requestAttributes = new ArrayList<>();

    @Schema(description = "How the static set combines with a connector-supplied set; defaults to MERGE when omitted",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "merge")
    private AttributeSetMergeMode mergeMode;

    @ArraySchema(arraySchema = @Schema(
            description = "Core-side value-source bindings to attach onto connector-supplied (or static) definitions by reference",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED))
    @Valid
    private List<ValueSourceBindingDto> valueSourceBindings = new ArrayList<>();

    @Schema(description = "Whether an external CSR violating the resolved set is rejected (true) or accepted with warnings (false); null inherits the platform default",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean externalCsrValidationStrict;

    /**
     * Each attribute may carry at most one value-source binding. Two bindings targeting the same attribute are ambiguous.
     */
    @AssertTrue(message = "Value-source bindings must not target the same attribute more than once.")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isValueSourceBindingsUnique() {
        if (valueSourceBindings == null) {
            return true;
        }
        Set<String> uuids = new HashSet<>();
        Set<String> names = new HashSet<>();
        for (ValueSourceBindingDto binding : valueSourceBindings) {
            if (binding == null) {
                continue;
            }
            String uuid = binding.getAttributeUuid();
            if (uuid != null && !uuid.isBlank() && !uuids.add(uuid)) {
                return false;
            }
            String name = binding.getAttributeName();
            if (name != null && !name.isBlank() && !names.add(name)) {
                return false;
            }
        }
        return true;
    }
}
