package com.otilm.api.model.core.location;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing Location
 */
@Setter
@Getter
public class LocationDto extends NameAndUuidDto {

    @Schema(description = "Description of the Location")
    private String description;

    @Schema(description = "UUID of Entity instance",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String entityInstanceUuid;

    @Schema(description = "Name of Entity instance",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String entityInstanceName;

    @Schema(description = "List of Location attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean enabled;

    @Schema(description = "If the location supports multiple Certificates",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean supportMultipleEntries;

    @Schema(description = "If the location supports key management operations",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean supportKeyManagement;

    @Schema(description = "List of Certificates in Location",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CertificateInLocationDto> certificates;

    @Schema(description = "Location metadata")
    private List<MetadataResponseDto> metadata;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("description", description)
                .append("entityInstanceUuid", entityInstanceUuid)
                .append("attributes", attributes)
                .append("customAttributes", customAttributes)
                .append("enabled", enabled)
                .append("entityInstanceName", entityInstanceName)
                .append("supportMultipleEntries", supportMultipleEntries)
                .append("supportKeyManagement", supportKeyManagement)
                .append("metadata", metadata)
                .toString();
    }
}
