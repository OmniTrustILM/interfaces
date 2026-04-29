package com.otilm.api.model.common.attribute.common;

import com.otilm.api.model.common.attribute.v2.BaseAttributeV2;
import com.otilm.api.model.common.attribute.v3.BaseAttributeV3;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        description = "Base Attribute definition",
        type = "object",
        oneOf = {
                BaseAttributeV2.class,
                BaseAttributeV3.class
        }
)
public interface BaseAttributeDto extends Serializable {

        /**
         * UUID of the Attribute
         **/
        @Schema(
                description = "UUID of the Attribute for unique identification",
                example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String getUuid();

        /**
         * Name of the Attribute for processing
         **/
        @Schema(
                description = "Name of the Attribute that is used for identification",
                examples = {"Attribute"},
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String getName();

        /**
         * Optional description of the Attribute, should contain helper text on what is expected
         **/
        @Schema(
                description = "Optional description of the Attribute, should contain helper text on what is expected"
        )
        String getDescription();

        /**
         * Type of the Attribute
         */
        @Schema(
                description = "Type of the Attribute",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        AttributeType getType();

}
