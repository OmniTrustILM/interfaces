package com.otilm.api.model.client.attribute.custom;

import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.core.auth.ObjectActionsAware;
import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class CustomAttributeDefinitionDetailDto extends CustomAttributeDefinitionDto implements ObjectActionsAware {

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

    /**
     * Boolean determining if the Attribute is read only. If true, the Attribute content cannot be changed.
     **/
    @Schema(description = "Boolean determining if the Attribute is read only. If true, the Attribute content cannot be changed.",
            defaultValue = "false")
    private boolean readOnly;

    /**
     * Boolean determining if the Attribute contains list of values in the content
     **/
    @Schema(description = "Boolean determining if the Attribute contains list of values in the content",
            defaultValue = "false")
    private boolean list;

    /**
     * Boolean determining if the Attribute can have multiple values
     **/
    @Schema(description = "Boolean determining if the Attribute can have multiple values", defaultValue = "false")
    private boolean multiSelect;

    /**
     * Attribute Content
     */
    @Schema(description = "Predefined content for the attribute if needed. The content of the Attribute must satisfy the type")
    private List<AttributeContent> content;

    @Schema(description = "Boolean determining if a list Attribute can have values other than predefined options")
    private boolean extensibleList;

    @Schema(description = ObjectActionsAware.ALLOWED_ACTIONS_DESCRIPTION)
    private List<ResourceAction> allowedActions;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("label", getLabel())
                .append("visible", visible)
                .append("group", group)
                .append("readOnly", readOnly)
                .append("list", list)
                .append("multiSelect", multiSelect)
                .append("content", content)
                .append("resources", getResources())
                .append("required", isRequired())
                .append("extensibleList", extensibleList)
                .toString();
    }
}
