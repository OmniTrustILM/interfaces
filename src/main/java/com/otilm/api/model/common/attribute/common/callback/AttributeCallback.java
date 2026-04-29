package com.otilm.api.model.common.attribute.common.callback;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("callbackContext", callbackContext)
                .append("callbackMethod", callbackMethod)
                .append("mappings", mappings)
                .toString();
    }
}
