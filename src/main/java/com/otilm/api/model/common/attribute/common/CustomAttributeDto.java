package com.otilm.api.model.common.attribute.common;

import com.otilm.api.model.common.attribute.v2.CustomAttributeV2;
import com.otilm.api.model.common.attribute.v3.CustomAttributeV3;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "CustomAttribute",
        description = "Custom Attribute",
        type = "object",
        oneOf = {
                CustomAttributeV2.class,
                CustomAttributeV3.class
        }
)

public interface CustomAttributeDto {
}
