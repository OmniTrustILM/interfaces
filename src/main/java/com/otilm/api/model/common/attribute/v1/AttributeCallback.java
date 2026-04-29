package com.otilm.api.model.common.attribute.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Set;

public class AttributeCallback {

    @Schema(
            description = "Context part of callback URL",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String callbackContext;

    @Schema(
            description = "HTTP method of the callback",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String callbackMethod;

    @Schema(
            description = "Mappings for the callback method",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Set<AttributeCallbackMapping> mappings;

    public String getCallbackContext() {
        return callbackContext;
    }

    public void setCallbackContext(String callbackContext) {
        this.callbackContext = callbackContext;
    }

    public String getCallbackMethod() {
        return callbackMethod;
    }

    public void setCallbackMethod(String callbackMethod) {
        this.callbackMethod = callbackMethod;
    }

    public Set<AttributeCallbackMapping> getMappings() {
        return mappings;
    }

    public void setMappings(Set<AttributeCallbackMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("callbackContext", callbackContext)
                .append("callbackMethod", callbackMethod)
                .append("mappings", mappings)
                .toString();
    }
}
