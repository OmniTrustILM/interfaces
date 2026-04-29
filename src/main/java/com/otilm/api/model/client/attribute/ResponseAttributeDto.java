package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

@Schema(
        name = "ResponseAttribute",
        description = "Response attribute to send attribute content for object",
        type = "object",
        discriminatorProperty = "version",
        discriminatorMapping = {
                @DiscriminatorMapping(value = AttributeVersion.Codes.V2, schema = ResponseAttributeV2.class),
                @DiscriminatorMapping(value = AttributeVersion.Codes.V3, schema = ResponseAttributeV3.class),
        },
        oneOf = {
                ResponseAttributeV2.class,
                ResponseAttributeV3.class
        }
)
public interface ResponseAttributeDto extends Serializable {

        /**
         * UUID of the Attribute
         **/
        @Schema(
                description = "UUID of the Attribute",
                example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID getUuid();

        /**
         * Name of the Attribute
         **/
        @Schema(
                description = "Name of the Attribute",
                examples = {"Attribute"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String getName();

        /**
         * Label of the Attribute, Can be used to display the field name in the User Interface
         **/
        @Schema(
                description = "Label of the the Attribute",
                examples = {"Attribute Name"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String getLabel();

        /**
         * Type of the Attribute, base types are defined in {@link AttributeType}
         **/
        @Schema(
                description = "Type of the Attribute",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        AttributeType getType();

        /**
         * Content Type of the Attribute
         **/
        @Schema(
                description = "Content Type of the Attribute",
                examples = {"Attribute"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        AttributeContentType getContentType();

        @Schema(
                description = "Version of the Attribute",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        AttributeVersion getVersion();
}
