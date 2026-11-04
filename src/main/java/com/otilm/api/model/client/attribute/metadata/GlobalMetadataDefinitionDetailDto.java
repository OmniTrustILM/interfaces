package com.otilm.api.model.client.attribute.metadata;

import com.otilm.api.model.client.attribute.AttributeDefinitionDto;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
public class GlobalMetadataDefinitionDetailDto extends AttributeDefinitionDto implements ObjectActionsAware {

    /**
     * Type of the Attribute. For the custom attribute, the type will always be "custom"
     */
    @Schema(description = "Type of the Attribute", requiredMode = Schema.RequiredMode.REQUIRED, examples = {"custom"},
            defaultValue = "custom")
    private AttributeType type;

    /**
     * Boolean determining if the Attribute is visible and can be displayed, otherwise it should be hidden to the user.
     **/
    @Schema(description = "Boolean determining if the Attribute is visible and can be displayed, otherwise it should be hidden to the user.",
            defaultValue = "true")
    private boolean visible;

    /**
     * Group of the Attribute, used for the logical grouping of the Attribute
     **/
    @Schema(description = "Group of the Attribute, used for the logical grouping of the Attribute",
            examples = {"requiredAttributes"})
    private String group;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("label", getLabel())
                .append("visible", visible)
                .append("group", group)
                .append("allowedActions", allowedActions)
                .toString();
    }
}
