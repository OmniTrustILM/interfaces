package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.ProtectionLevel;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AttributeDefinitionDto implements Loggable {

    /**
     * Name of the Attribute
     */
    @Schema(description = "UUID of the Attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    /**
     * Name of the Attribute
     */
    @Schema(description = "Name of the Attribute", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * Content Type of the Attribute
     */
    @Schema(description = "Attribute Content Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private AttributeContentType contentType;

    /**
     * Description of the Attribute
     */
    @Schema(description = "Attribute description", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    /**
     * Boolean determining if the Attribute is enabled.
     **/
    @Schema(
            description = "Boolean determining if the Attribute is enabled. Required only for Custom Attribute"
    )
    private Boolean enabled;

    @Schema(description = "Protection level of attribute content")
    private ProtectionLevel protectionLevel;

    @Schema(description = "Version of the attribute")
    private AttributeVersion version;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("contentType", contentType)
                .append("description", description)
                .append("enabled", enabled)
                .toString();
    }

    @Override
    public Serializable toLogData() {
        return new NameAndUuidDto(this.uuid, this.name);
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of(name);
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of(UUID.fromString(uuid));
    }

    public boolean isEnabled() {
        return enabled;
    }
}
