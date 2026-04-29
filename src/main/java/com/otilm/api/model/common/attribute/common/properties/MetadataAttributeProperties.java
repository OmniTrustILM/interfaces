package com.otilm.api.model.common.attribute.common.properties;

import com.otilm.api.model.common.attribute.common.content.data.ProtectionLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
public class MetadataAttributeProperties extends BaseAttributeProperties {

    /**
     * Boolean determining if the Attribute is required. If true, the Attribute must be provided.
     **/
    @Schema(
            description = "Boolean determining if the Metadata is a global metadata.",
            defaultValue = "false"
    )
    private boolean global = false;

    @Schema(
            description = "Boolean determining if the new metadata content should overwrite (replace) existing content instead of appending.",
            defaultValue = "false"
    )
    private boolean overwrite = false;

    @Schema(
            description = "Protection level of the attribute content",
            defaultValue = "none",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private ProtectionLevel protectionLevel = ProtectionLevel.NONE;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("global", global)
                .append("overwrite", overwrite)
                .append("label", getLabel())
                .append("group", getGroup())
                .append("visible", isVisible())
                .toString();
    }
}
