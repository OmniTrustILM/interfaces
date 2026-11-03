package com.otilm.api.model.client.signing.profile.workflow;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Raw signing workflow configuration embedded in a {@code SigningProfileDto}.
 *
 * <p>
 * Raw signing requires no Signature Formatting Provider and carries a digest cap per request and the
 * certificate-purpose constraints. The digest algorithm is not configurable: the scheme's signature algorithm fixes it.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "RawSigningWorkflowDto", description = "Raw signing workflow configuration.")
@ToString(callSuper = true)
public class RawSigningWorkflowDto extends WorkflowDto {

    @Schema(minimum = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            description = "Maximum digests one signHash request may carry. Null when the platform default applies. The digest "
                    + "algorithm is the one the scheme's signature algorithm fixes.")
    private Integer maxDigestsPerRequest;

    @Schema(description = "Demand the nonRepudiation key-usage bit specifically, for commitment-grade profiles. "
            + "Applies to ILM-managed signing only; delegated profile gets always false.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean requireNonRepudiation;

    @Schema(description = "Extended key usage OIDs the signing certificate must all carry, in dot notation "
            + "(1.3.6.1.5.5.7.3.36 is RFC 9336 id-kp-documentSigning). An empty set accepts any extended key "
            + "usage. Applies to ILM-managed signing only.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<String> requiredExtendedKeyUsageOids = new LinkedHashSet<>();

    public RawSigningWorkflowDto() {
        super(SigningWorkflowType.RAW_SIGNING);
    }
}
