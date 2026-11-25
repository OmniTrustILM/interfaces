package com.otilm.api.model.core.raprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RaProfileEarlyRenewalResponseDto {

    @Schema(description = "Number of certificates of the RA profile marked for early renewal",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private long certificatesAffected;
}
