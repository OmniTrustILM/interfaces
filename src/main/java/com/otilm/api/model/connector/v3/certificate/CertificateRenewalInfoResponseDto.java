package com.otilm.api.model.connector.v3.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response of v3 /renewalInfo: one result per request item, in any order.
 */
@Getter
@Setter
@ToString
@Schema(name = "CertificateRenewalInfoResponseDtoV3")
public class CertificateRenewalInfoResponseDto {

    @Schema(description = "Default seconds until Core should ask again, for results without their own retryAfter",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "0")
    @Min(0)
    private Integer retryAfter;

    @Schema(description = "One result per request item; an item missing here is treated as retry",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<CertificateRenewalInfoResultDtoV3> results;
}
