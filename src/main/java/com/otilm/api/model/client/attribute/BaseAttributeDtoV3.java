package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.v3.*;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Base Attribute definition",
        type = "object",
        discriminatorProperty = "type",
        discriminatorMapping = {
                @DiscriminatorMapping(value = AttributeType.Codes.DATA, schema = DataAttributeV3.class),
                @DiscriminatorMapping(value = AttributeType.Codes.INFO, schema = InfoAttributeV3.class),
                @DiscriminatorMapping(value = AttributeType.Codes.GROUP, schema = GroupAttributeV3.class),
                @DiscriminatorMapping(value = AttributeType.Codes.META, schema = MetadataAttributeV3.class),
                @DiscriminatorMapping(value = AttributeType.Codes.CUSTOM, schema = CustomAttributeV3.class)
        },
        oneOf = {
                DataAttributeV3.class,
                InfoAttributeV3.class,
                GroupAttributeV3.class,
                MetadataAttributeV3.class,
                CustomAttributeV3.class
        }
)
public interface BaseAttributeDtoV3 {


        /**
         * Version of the Attribute
         **/
        @Schema(
                description = "Version of the Attribute",
                example = "3",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        AttributeVersion getSchemaVersion();
}