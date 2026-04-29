package com.otilm.api.model.client.location;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Class representing client request to push certificate to location
 */
public class PushToLocationRequestDto {

    @Schema(
            description = "List of push Attributes for Location",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>attributes;


    public List<RequestAttribute>getAttributes() {
        return attributes;
    }

    public void setAttributes(List<RequestAttribute>attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("attributes", attributes)
                .toString();
    }
}
