package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(name = "ContentSigningFormattingAttributes",
        description = "Attribute definitions of content-signing formatting, split by whether a signing request may "
                + "set them")
public class ContentSigningFormattingAttributesDto {

    @NotNull
    @Schema(description = "Definitions of the attributes the operator alone sets on the Signing Profile",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull BaseAttribute> profileAttributes = new ArrayList<>();

    @NotNull
    @Schema(description = "Definitions of the data attributes a signing request may override where the Signing "
            + "Profile allows it; `properties.group` names each attribute's permission group",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotNull BaseAttribute> requestAttributes = new ArrayList<>();

    /** Other repositories call this positionally. A generated constructor would change with every added field. */
    public ContentSigningFormattingAttributesDto(List<BaseAttribute> profileAttributes,
            List<BaseAttribute> requestAttributes) {
        this.profileAttributes = profileAttributes;
        this.requestAttributes = requestAttributes;
    }
}
