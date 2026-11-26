package com.otilm.api.model.core.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BulkEarlyRenewalRequestDto extends EarlyRenewalRequestDto {

    @Schema(description = "UUIDs of the certificates to renew early", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty
    private List<UUID> uuids;
}
