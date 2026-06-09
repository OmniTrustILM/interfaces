package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.v2.*;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Base Attribute definition",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = AttributeType.Codes.DATA, schema = DataAttributeV2.class),
                @DiscriminatorMapping(value = AttributeType.Codes.INFO, schema = InfoAttributeV2.class),
                @DiscriminatorMapping(value = AttributeType.Codes.GROUP, schema = GroupAttributeV2.class),
                @DiscriminatorMapping(value = AttributeType.Codes.META, schema = MetadataAttributeV2.class),
                @DiscriminatorMapping(value = AttributeType.Codes.CUSTOM, schema = CustomAttributeV2.class)
        },
        oneOf = {
                DataAttributeV2.class,
                InfoAttributeV2.class,
                GroupAttributeV2.class,
                MetadataAttributeV2.class,
                CustomAttributeV2.class
        }
)
public interface BaseAttributeDtoV2 {



}