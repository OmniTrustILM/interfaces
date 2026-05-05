package com.czertainly.api.model.connector.v2;

import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Response from the Authority Provider status-check endpoints. The same DTO is used for
 * issuance and revocation status — for revocation the {@code certificateData} field is
 * unused (revocation completion carries no payload).
 *
 * <p>When {@link CertificateOperationStatus#COMPLETED} is reported for an issuance or
 * renewal, the Authority Provider populates {@code certificateData} (Base64-encoded). When
 * {@link CertificateOperationStatus#FAILED}, the {@code reason} field carries the failure
 * detail. The optional {@code meta} field lets the Authority Provider report updated
 * technology-specific state on each call.</p>
 */
@Data
public class CertificateOperationStatusResponseDto {

    @Schema(
            description = "Operation status as known to the Authority Provider",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private CertificateOperationStatus status;

    @Schema(
            description = "Base64-encoded certificate content. Present when status=COMPLETED for issue/renew. "
                    + "Unused for revoke status responses.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String certificateData;

    @Schema(
            description = "Updated technology-specific metadata. The Authority Provider may report a refreshed "
                    + "value on each call.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<MetadataAttribute> meta;

    @Schema(
            description = "Failure detail when `status` is `FAILED`.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String reason;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("status", status)
                .append("certificateData", certificateData != null ? "<present>" : null)
                .append("reason", reason)
                .toString();
    }
}
