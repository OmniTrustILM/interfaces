package com.otilm.api.model.core.v2;

import com.otilm.api.model.common.UuidDto;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Response containing signed certificate data
 */
@Getter
@Setter
public class ClientCertificateDataResponseDto implements Loggable {

    @Schema(description = "Base64 encoded Certificate content",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String certificateData;

    @Schema(description = "UUID of Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("certificateData", certificateData)
                .append("certificateUuid", uuid)
                .toString();
    }

    @Override
    public Serializable toLogData() {
        return new UuidDto(uuid);
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of();
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of(UUID.fromString(uuid));
    }
}

