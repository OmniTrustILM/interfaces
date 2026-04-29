package com.otilm.api.model.client.attribute;

import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.UUID;

/**
 * This class contains set of properties to represent
 * an Attribute definition provided by the client
 */

@Schema(
        name = "RequestAttribute",
        description = "Request attribute to send attribute content for object",
        type = "object",
        discriminatorProperty = "version",
        discriminatorMapping = {
                @DiscriminatorMapping(value = AttributeVersion.Codes.V2, schema = RequestAttributeV2.class),
                @DiscriminatorMapping(value = AttributeVersion.Codes.V3, schema = RequestAttributeV3.class),
        },
        oneOf = {
                RequestAttributeV3.class,
                RequestAttributeV2.class
        })
public interface RequestAttributeDto extends Serializable {


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
