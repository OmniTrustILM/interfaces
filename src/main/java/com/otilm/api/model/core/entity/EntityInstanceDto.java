package com.otilm.api.model.core.entity;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class EntityInstanceDto extends NameAndUuidDto {

    @Schema(description = "List of Entity instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Status of Entity instance",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String status;

    @Schema(description = "UUID of Entity Provider",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String connectorUuid;

    @Schema(description = "Name of Entity Provider",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String connectorName;

    @Schema(description = "Entity instance Kind",
            examples = {"Keystore, etc."},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .append("status", status)
                .append("connectorUuid", connectorUuid)
                .append("connectorName", connectorName)
                .append("kind", kind)
                .toString();
    }
}
