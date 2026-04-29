package com.otilm.api.model.client.discovery;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.metadata.MetadataResponseDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.discovery.DiscoveryStatus;
import com.otilm.api.model.core.workflows.TriggerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiscoveryHistoryDetailDto extends NameAndUuidDto {

    @Schema(
            description = "Discovery Kind",
            examples = {"IP-HostName"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String kind;

    @Schema(
            description = "Status of Discovery",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private DiscoveryStatus status;

    @Schema(
            description = "Status of Discovery returned by connector",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private DiscoveryStatus connectorStatus;

    @Schema(
            description = "Failure/Success Messages",
            examples = {"Failed due to network connectivity issues"}
    )
    private String message;

    @Schema(
            description = "Date and time when Discovery started",
            nullable = true
    )
    private Date startTime;

    @Schema(
            description = "Date and time when Discovery finished",
            nullable = true
    )
    private Date endTime;

    @Schema(
            description = "Number of certificates that are discovered",
            defaultValue = "0"
    )
    private Integer totalCertificatesDiscovered;

    @Schema(
            description = "Number of certificates that were discovered by connector",
            defaultValue = "0"
    )
    private Integer connectorTotalCertificatesDiscovered;

    @Schema(
            description = "UUID of the Discovery Provider",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String connectorUuid;

    @Schema(
            description = "Name of the Discovery Provider",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String connectorName;

    @Schema(
            description = "List of Discovery Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Schema(
            description = "Metadata of the Discovery"
    )
    private List<MetadataResponseDto> metadata;

    @Schema(
            description = "List of associated triggers",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<TriggerDto> triggers = new ArrayList<>();
}
