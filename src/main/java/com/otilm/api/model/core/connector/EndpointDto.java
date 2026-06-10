package com.otilm.api.model.core.connector;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@EqualsAndHashCode(callSuper = true)
public class EndpointDto extends NameAndUuidDto {


    @Schema(
            description = "Context of the Endpoint",
            examples = {"/v1"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String context;
    @Schema(
            description = "Method to be used for the Endpoint",
            examples = {"POST"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String method;
    @Schema(
            description = "True if the Endpoint is required for implementation",
            example = "true",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean required;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("context", context)
                .append("method", method)
                .append("required", required)
                .toString();
    }
}
