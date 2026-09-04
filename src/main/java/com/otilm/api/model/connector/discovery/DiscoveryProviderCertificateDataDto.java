package com.otilm.api.model.connector.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
@Schema(description = "One certificate a Discovery Provider reports, with the metadata it was found under.")
public class DiscoveryProviderCertificateDataDto {

    @Schema(description = "Certificate UUID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Base64 encoded Certificate content", requiredMode = Schema.RequiredMode.REQUIRED)
    private String base64Content;

    @Schema(description = "Metadata for the Certificate", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataAttribute> meta;

    @Schema(description = "Run-wide item number the Connector assigned, shared across the run's resources. "
            + "Absent for a v1 Connector, whose provider numbers nothing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long sequence;

    @Schema(description = "When the Connector observed the certificate, which is not when Core staged it. "
            + "Absent for a v1 Connector.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private OffsetDateTime discoveredAt;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("base64Content", base64Content)
                .append("meta", meta)
                .append("sequence", sequence)
                .append("discoveredAt", discoveredAt)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DiscoveryProviderCertificateDataDto)) {
            return false;
        }
        DiscoveryProviderCertificateDataDto that = (DiscoveryProviderCertificateDataDto) o;
        return Objects.equals(base64Content, that.base64Content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base64Content);
    }
}
