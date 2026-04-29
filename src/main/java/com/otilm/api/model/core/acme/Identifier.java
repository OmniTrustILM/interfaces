package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Identifiers used across the ACME implementation
 */
public class Identifier {
    /**
     * Field represents the type of the Identifier. This field primarily defined the
     * DNS Identifier types
     */
    @Schema(description = "Type of the Identifier",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"dns"})
    private String type;

    /**
     * Represents the value of the Identifier in {@link Identifier#type}
     * This field is the Identifier itself
     */
    @Schema(description = "Value of Identifier",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"www.some-domain.com"})
    private String value;

    public Identifier() {
    }

    /**
     * Below are the default getters and setters for the above defined parameters
     */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("value", value)
                .toString();
    }
}
