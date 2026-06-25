package com.otilm.api.model.client.connector.v2.attribute;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Connector response to a callback. Exactly one arm is set: {@code content} (resolved dropdown options
 * for a DATA attribute) or {@code attributes} (runtime-injected GROUP children definitions).
 *
 * <p><b>Why {@code content} is {@code BaseAttributeContentV3} (schema v3), not v2:</b> the version axes
 * are independent — this DTO belongs to the Attributes <i>v2</i> API (the common NG <i>interface</i>
 * version), but NG callback DATA content is always <i>attribute schema</i> v3, even when the triggering
 * definition is schema v2. See {@code com.otilm.api.model.client.connector.v2.attribute} for the full
 * axis explanation. Do not "correct" this to a v2 content type.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeCallbackResponseDto {

    @Schema(
            description = "Resolved attribute content (v3 content model). Set this arm for DATA-attribute dropdown options. "
                    + "Exactly one of content or attributes must be set; leave the other unset.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<BaseAttributeContentV3<?>> content;

    @Schema(
            description = "Runtime-injected attribute definitions. Set this arm to return GROUP children. "
                    + "Exactly one of content or attributes must be set; leave the other unset.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<BaseAttribute> attributes;

    @Schema(
            description = "Total number of items available, for paginated content responses.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Long totalItems;

    /**
     * The exactly-one-arm invariant, as a bean-validation rule (XOR on which arm is non-null).
     *
     * <p><b>Caveat for reviewers/consumers:</b> Spring does NOT run JSR-380 validation on response
     * bodies it produces, so this {@code @AssertTrue} does not auto-reject a malformed connector
     * response at this layer. It documents the contract and fires for any consumer that explicitly
     * validates; the binding enforcement is on the connector (which must set exactly one arm) and on
     * Core, which validates the response on receipt. An empty-but-non-null list counts as "set"
     * (e.g. {@code content = []} legitimately means "resolved to zero options").
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "Exactly one of content or attributes must be set")
    public boolean isExactlyOneArmSet() {
        return (content == null) ^ (attributes == null);
    }
}
