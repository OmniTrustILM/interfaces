package com.otilm.api.model.client.authority;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class AuthorityInstanceRequestDto {

    @Schema(description = "Authority instance name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "List of Authority instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

    @Schema(description = "List of Custom Attributes")
    private List<RequestAttribute> customAttributes;

    @Schema(description = "UUID of Authority provider",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String connectorUuid;

    @Schema(description = "Authority instance Kind",
            examples = {"LegacyEjbca, ADCS, etc"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("attributes", attributes)
                .append("connectorUuid", connectorUuid)
                .append("kind", kind)
                .append("customAttributes", customAttributes)
                .toString();
    }
}
