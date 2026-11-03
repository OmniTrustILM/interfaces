package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.common.validation.OidFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Raw signing workflow configuration request embedded in a Signing Profile create/update request.
 *
 * <p>
 * Raw signing requires no Signature Formatting Provider and carries a digest cap per request and the
 * certificate-purpose constraints.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "RawSigningWorkflowRequestDto", description = "Raw signing workflow configuration request."
        + " The digest algorithm is not configured here: the signature algorithm of the signing scheme fixes it, "
        + "and a signHash request must name that digest.")
@ToString(callSuper = true)
public class RawSigningWorkflowRequestDto extends WorkflowRequestDto {

    @Min(1)
    @Schema(minimum = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            description = "Maximum digests one signHash request may carry. Absent means the platform default "
                    + "applies. Applies to ILM-managed signing only; must be omitted for delegated signing.")
    private Integer maxDigestsPerRequest;

    @Schema(description = "Demand the nonRepudiation key-usage bit specifically, for commitment-grade profiles. "
            + "Applies to ILM-managed signing only. For delegated signing, omit it or send false; an echoed "
            + "false from a response is accepted, true is rejected.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Boolean requireNonRepudiation;

    @Schema(description = "Extended key usage OIDs the signing certificate must all carry, in dot notation "
            + "(1.3.6.1.5.5.7.3.36 is RFC 9336 id-kp-documentSigning). An empty set accepts any extended key "
            + "usage. Applies to ILM-managed signing only; for delegated signing send nothing or an empty set.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    // @Pattern publishes into the generated item schema, and reports the violation on the offending element.
    private Set<@NotBlank @Pattern(regexp = OidFormat.REGEX,
            message = OidFormat.MESSAGE) String> requiredExtendedKeyUsageOids = new LinkedHashSet<>();

    public RawSigningWorkflowRequestDto() {
        super(SigningWorkflowType.RAW_SIGNING);
    }
}
