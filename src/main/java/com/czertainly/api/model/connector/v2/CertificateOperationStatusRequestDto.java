package com.czertainly.api.model.connector.v2;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Body for the Authority Provider status-check endpoints. The operation type is
 * determined by which status endpoint is called; this body returns the technology-specific
 * {@link MetadataAttribute} entries that the Authority Provider returned in the original
 * {@code 202 Accepted} response so it can resolve and report on the in-flight operation.
 */
@Setter
@Getter
public class CertificateOperationStatusRequestDto {

    @Schema(
            description = "List of RA Profile attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute> raProfileAttributes;

    @Schema(
            description = "Technology-specific metadata that was returned by the Authority Provider in the "
                    + "original `202 Accepted` response, sent back here so the Authority Provider can resolve "
                    + "the in-flight operation and report its status.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private List<MetadataAttribute> meta;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("raProfileAttributes", raProfileAttributes)
                .append("meta", meta)
                .toString();
    }
}
