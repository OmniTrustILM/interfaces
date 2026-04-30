package com.czertainly.api.model.client.proxy;

import com.czertainly.api.model.common.Named;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
public class ProxyRequestDto implements Named {

    @Schema(description = "Name of the Proxy",
            examples = {"MyProxy123"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
    @Schema(description = "Detailed description of the Proxy",
            examples = {"This proxy is used for connecting to external connectors in DMZ network"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("description", description)
                .toString();
    }
}
