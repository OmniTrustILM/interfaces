package com.otilm.api.model.client.trustedcertificate;

import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Trusted Certificates to re-apply on the platform")
public class TrustedCertificateApplyRequestDto implements Loggable {

    @Schema(description = "UUIDs of the Trusted Certificates to re-apply; absent means every one that is pending, "
            + "failed, missing on the platform or whose removal failed",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> uuids;

    @Override
    public Serializable toLogData() {
        return null;
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of();
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return uuids == null ? List.of() : uuids.stream().map(UUID::fromString).toList();
    }
}
