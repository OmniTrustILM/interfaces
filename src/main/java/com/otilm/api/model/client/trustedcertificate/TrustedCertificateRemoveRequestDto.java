package com.otilm.api.model.client.trustedcertificate;

import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Trusted Certificates to remove trust from; the certificates stay in the inventory")
public class TrustedCertificateRemoveRequestDto implements Loggable {

    @Schema(description = "UUIDs of the Trusted Certificates to remove", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private List<@NotNull String> uuids;

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
