package com.otilm.api.model.common.attribute.v3.content.data;

import com.otilm.api.model.common.attribute.common.content.data.AttributeContentData;
import com.otilm.api.model.core.auth.AttributeResource;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "ResourceObjectContentData",
        description = "",
        type = "object",
        discriminatorProperty = "resource",
        discriminatorMapping = {
                @DiscriminatorMapping(value = Resource.Codes.AUTHORITY, schema = ResourceSimpleContentData.class),
                @DiscriminatorMapping(value = Resource.Codes.ENTITY, schema = ResourceSimpleContentData.class),
                @DiscriminatorMapping(value = Resource.Codes.LOCATION, schema = ResourceSimpleContentData.class),
                @DiscriminatorMapping(value = Resource.Codes.CREDENTIAL, schema = ResourceSimpleContentData.class),
                @DiscriminatorMapping(value = Resource.Codes.CERTIFICATE, schema = ResourceCertificateContentData.class),
                @DiscriminatorMapping(value = Resource.Codes.SECRET, schema = ResourceSecretContentData.class),
        },
        oneOf = {
                ResourceSimpleContentData.class,
                ResourceCertificateContentData.class,
                ResourceSecretContentData.class,
        })
public interface ResourceObjectContentDataDto extends AttributeContentData {

    @Schema(description = "Resource identifier",
            examples = {"7b55ge1c-844f-11dc-a8a3-0242ac120002"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    String getUuid();

    @Schema(description = "Resource name",
            examples = {"Main authority"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    String getName();

    @Schema(description = "Resource contained in data", example = Resource.Codes.AUTHORITY, requiredMode = Schema.RequiredMode.REQUIRED)
    AttributeResource getResource();
}
