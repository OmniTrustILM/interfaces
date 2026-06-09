package com.otilm.api.model.client.discovery;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.discovery.DiscoveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class DiscoveryHistoryDto extends NameAndUuidDto {

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
            description = "UUID of the Discovery Provider",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String connectorUuid;

    @Schema(
            description = "Name of the Discovery Provider",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String connectorName;
}
