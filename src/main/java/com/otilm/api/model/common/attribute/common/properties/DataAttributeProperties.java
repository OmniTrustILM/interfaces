package com.otilm.api.model.common.attribute.common.properties;

import com.otilm.api.model.common.attribute.common.content.data.ProtectionLevel;
import com.otilm.api.model.core.auth.AttributeResource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class DataAttributeProperties extends BaseAttributeProperties {

    /**
     * Boolean determining if the Attribute is required. If true, the Attribute must be provided.
     **/
    @Schema(
            description = "Boolean determining if the Attribute is required. If true, the Attribute must be provided.",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean required = false;

    /**
     * Boolean determining if the Attribute is read only. If true, the Attribute content cannot be changed.
     **/
    @Schema(
            description = "Boolean determining if the Attribute is read only. If true, the Attribute content cannot be changed.",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean readOnly = false;


    /**
     * Boolean determining if the Attribute contains list of values in the content
     **/
    @Schema(
            description = "Boolean determining if the Attribute contains list of values in the content",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean list = false;

    /**
     * Boolean determining if the Attribute can have multiple values
     **/
    @Schema(
            description = "Boolean determining if the Attribute can have multiple values",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean multiSelect = false;

    @Schema(
            description = "Protection level of the attribute content",
            defaultValue = "none",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private ProtectionLevel protectionLevel = ProtectionLevel.NONE;

    @Schema(description = "Resource of the attribute, relevant if the attribute has Resource content type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AttributeResource resource;

    /**
     * Boolean determining if a list Attribute can have values other than predefined options
     **/
    @Schema(
            description = "Boolean determining if a list Attribute can have values other than predefined options",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean extensibleList = false;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("required", required)
                .append("readOnly", readOnly)
                .append("list", list)
                .append("multi", multiSelect)
                .append("label", getLabel())
                .append("group", getGroup())
                .append("visible", isVisible())
                .append("protectionLevel", protectionLevel)
                .append("resource", resource)
                .append("extensibleList", extensibleList)
                .toString();
    }
}
