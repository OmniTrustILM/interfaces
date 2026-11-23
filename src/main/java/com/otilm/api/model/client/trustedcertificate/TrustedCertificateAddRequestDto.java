package com.otilm.api.model.client.trustedcertificate;

import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "Certificates to trust and the purposes to trust them for. Exactly one of certificateUuids and "
        + "certificateContent is set; an empty certificateUuids counts as absent.")
public class TrustedCertificateAddRequestDto implements Loggable {

    @Schema(description = "UUIDs of inventory certificates to trust", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@NotNull String> certificateUuids;

    @Schema(description = "Base64 content of one certificate to trust, the same encoding as certificate upload",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String certificateContent;

    @Schema(description = "Purposes to trust the certificates for", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private Set<@NotNull TrustedCertificatePurpose> purposes;

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
        return certificateUuids == null ? List.of() : certificateUuids.stream().map(UUID::fromString).toList();
    }
}
