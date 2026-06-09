package com.otilm.api.model.common.attribute.common;

import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "DataAttribute",
        description = "Data Attribute",
        type = "object",
        oneOf = {
                DataAttributeV2.class,
                DataAttributeV3.class
        }

)
public interface DataAttributeDto {

}
