package com.otilm.api.model.common.attribute.common.callback;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class AttributeCallback implements Serializable {

    @Schema(
            description = "Context part of callback URL",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String callbackContext;

    @Schema(
            description = "HTTP method of the callback. This value is required for connector callbacks and optional only for callbacks defined on resource objects.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String callbackMethod;

    @Schema(
            description = "Mappings for the callback method",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Set<AttributeCallbackMapping> mappings;

    /**
     * Presence of {@code dependsOn} is what marks this as an Attributes v2 (NG) callback;
     * {@code callbackContext} marks the legacy one. Intentionally carries no bean-validation: the
     * "at most one of dependsOn/callbackContext" rule is cross-field and is enforced in Core
     * (NG callback dispatch), not here — a constraint on this shared class would also (wrongly) fire on
     * the legacy v1/v2 deserialization path. An empty (non-null) list means "fire on form open".
     */
    @Schema(
            description = "Names of the attributes this Attributes v2 callback consumes and is triggered by. "
                    + "Marks an NG (Attributes v2) callback; at most one of dependsOn / callbackContext may be set "
                    + "(neither = no callback). Forbidden on RESOURCE attributes. Enforced by Core.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<String> dependsOn;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("callbackContext", callbackContext)
                .append("callbackMethod", callbackMethod)
                .append("mappings", mappings)
                .append("dependsOn", dependsOn)
                .toString();
    }
}
