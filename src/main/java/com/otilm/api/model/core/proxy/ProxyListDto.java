package com.otilm.api.model.core.proxy;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.time.OffsetDateTime;

@Setter
@Getter
public class ProxyListDto extends NameAndUuidDto implements Serializable {

    @Schema(description = "Detailed description of the Proxy",
        examples = {"This proxy is used for connecting to external connectors in DMZ network"},
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Status of the Proxy",
        examples = {"connected"},
        requiredMode = Schema.RequiredMode.REQUIRED)
    private ProxyStatus status;

    @Schema(description = "Timestamp of the last activity from the Proxy",
        examples = {"2024-01-15T10:15:30+01:00"},
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private OffsetDateTime lastActivity;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("uuid", uuid)
            .append("name", name)
            .append("description", description)
            .append("status", status)
            .append("lastActivity", lastActivity)
            .toString();
    }
}
