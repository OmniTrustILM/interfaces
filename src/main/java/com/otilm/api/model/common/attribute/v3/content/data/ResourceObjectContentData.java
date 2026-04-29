package com.otilm.api.model.common.attribute.v3.content.data;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.AttributeResource;
import com.otilm.api.model.core.auth.Resource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Objects;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "resource", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ResourceSimpleContentData.class, name = Resource.Codes.AUTHORITY),
        @JsonSubTypes.Type(value = ResourceSimpleContentData.class, name = Resource.Codes.ENTITY),
        @JsonSubTypes.Type(value = ResourceSimpleContentData.class, name = Resource.Codes.LOCATION),
        @JsonSubTypes.Type(value = ResourceSimpleContentData.class, name = Resource.Codes.CREDENTIAL),
        @JsonSubTypes.Type(value = ResourceCertificateContentData.class, name = Resource.Codes.CERTIFICATE),
        @JsonSubTypes.Type(value = ResourceSecretContentData.class, name = Resource.Codes.SECRET),
})
@Schema(implementation = ResourceObjectContentDataDto.class)
public class ResourceObjectContentData extends NameAndUuidDto implements ResourceObjectContentDataDto {

    @Schema(description = "Resource contained in data", requiredMode = Schema.RequiredMode.REQUIRED)
    private final AttributeResource resource;

    public ResourceObjectContentData(AttributeResource resource) {
        this.resource = resource;
    }

    @Override
    public void validate() throws ValidationException {
        if (resource == null) {
            throw new ValidationException("Resource must be provided in Resource Object Content Data");
        }

        if (uuid == null || uuid.isEmpty()) {
            throw new ValidationException("UUID must be provided in Resource Object Content Data");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResourceObjectContentData that)) return false;
        return Objects.equals(resource, that.resource) && Objects.equals(getUuid(), that.getUuid()) && Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, getUuid(), getName());
    }
}
