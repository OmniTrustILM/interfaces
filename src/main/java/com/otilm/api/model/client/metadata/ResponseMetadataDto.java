package com.otilm.api.model.client.metadata;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Schema(description = "Request attribute to send attribute content for object",
        type = "object",
        discriminatorProperty = "version",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "v2", schema = ResponseMetadataV2.class),
                @DiscriminatorMapping(value = "v3", schema = ResponseMetadataV3.class),
        },
        oneOf = {
                ResponseMetadataV3.class,
                ResponseMetadataV2.class
        })
public interface ResponseMetadataDto extends Serializable {

    @Schema(description = "Source Objects", requiredMode = Schema.RequiredMode.REQUIRED)
    List<NameAndUuidDto> getSourceObjects();

    /**
     * UUID of the Attribute
     **/
    @Schema(
            description = "UUID of the Attribute",
            example = "b11c9be1-b619-4ef5-be1b-a1cd9ef265b7"
    )
    @JsonInclude(JsonInclude.Include.NON_NULL)
    UUID getUuid();

    /**
     * Name of the Attribute, can be used as key for form field label text
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
            examples = {"Attribute"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    AttributeVersion getVersion();


}
