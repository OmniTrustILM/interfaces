package com.otilm.api.model.connector.entity;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class GenerateCsrRequestDto {

    @Schema(
            description = "List of Location Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>locationAttributes;

    @Schema(
            description = "List of Attributes to generate CSR",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>csrAttributes;

    @Schema(description = "Is the request for renewal of Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean renewal;

    public List<RequestAttribute>getLocationAttributes() {
        return locationAttributes;
    }

    public void setLocationAttributes(List<RequestAttribute>locationAttributes) {
        this.locationAttributes = locationAttributes;
    }

    public List<RequestAttribute>getCsrAttributes() {
        return csrAttributes;
    }

    public void setCsrAttributes(List<RequestAttribute>csrAttributes) {
        this.csrAttributes = csrAttributes;
    }

    public Boolean isRenewal() {
        return renewal;
    }

    public void setRenewal(Boolean renewal) {
        this.renewal = renewal;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("locationAttributes", locationAttributes)
                .append("csrAttributes", csrAttributes)
                .append("isRenewalRequest", renewal)
                .toString();
    }
}
