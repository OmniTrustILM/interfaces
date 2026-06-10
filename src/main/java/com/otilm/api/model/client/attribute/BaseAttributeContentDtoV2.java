package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.v2.content.*;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(
        description = "Base Attribute ContentV2 definition",
        type = "object",
        oneOf = {
                BooleanAttributeContentV2.class,
                CodeBlockAttributeContentV2.class,
                CredentialAttributeContentV2.class,
                DateAttributeContentV2.class,
                DateTimeAttributeContentV2.class,
                FileAttributeContentV2.class,
                FloatAttributeContentV2.class,
                IntegerAttributeContentV2.class,
                ObjectAttributeContentV2.class,
                SecretAttributeContentV2.class,
                StringAttributeContentV2.class,
                TextAttributeContentV2.class,
                TimeAttributeContentV2.class
        }
)
public interface BaseAttributeContentDtoV2 {

    @Schema(description = "ContentV2 Reference")
    String getReference();

}
