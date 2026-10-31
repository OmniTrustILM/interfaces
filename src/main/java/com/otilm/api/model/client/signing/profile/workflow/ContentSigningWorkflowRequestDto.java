package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.TimestampSourceRequestDto;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.SignatureParametersDto;
import com.otilm.api.model.common.validation.OidFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Content signing workflow configuration request embedded in a Signing Profile create/update request.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ContentSigningWorkflowRequestDto",
        description = "Content signing workflow configuration request. The validation material that LONG_TERM and "
                + "ARCHIVAL embed is fetched by the Signature Formatting Provider and needs no configuration here.")
@ToString(callSuper = true)
public class ContentSigningWorkflowRequestDto extends WorkflowRequestDto {

    /** Named so the element annotation fits a line; the formatter cannot break a type-use annotation. */
    private static final String NO_NULL_GROUPS = "allowedRequestParameterGroups must not contain null items";

    @Schema(description = "UUID of the Signature Formatting Provider that constructs the data-to-be-signed (DTBS) for "
            + "Content signing. Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID signatureFormattingConnectorUuid;

    @Schema(description = "Attributes for the Signature Formatting Provider that control DTBS construction "
            + "for the content signing workflow. Must be omitted for delegated signing. These attributes carry "
            + "operator-only knobs; caller-influenceable parameters live in defaultSignatureParameters.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> signatureFormattingConnectorAttributes = new ArrayList<>();

    @Schema(description = "Signature family this profile produces. "
            + "Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SignatureFamily family;

    @Schema(description = "Highest signature level a request may ask for. A request asking for more is rejected. "
            + "Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SignatureLevel maxLevel;

    @Valid
    @Schema(description = "Source of the timestamps embedded at level TIMESTAMPED and above. "
            + "Required when maxLevel is TIMESTAMPED or higher, and must be omitted when maxLevel is SIGNED, "
            + "which embeds no timestamp. Must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TimestampSourceRequestDto timestampSource;

    @Positive
    @Schema(description = "Largest document accepted for signing, in bytes. Enforced when an ILM-managed "
            + "signing request arrives. When omitted, no profile-level cap applies.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "5242880")
    private Long documentSizeCap;

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

    @Schema(description = "Signature parameter groups a signing request may supply; every group must apply to the "
            + "profile's family. Must be omitted for delegated signing. Membership is the only permission mechanism. "
            + "A request carrying any field of a group absent from this list is rejected, even when the value matches "
            + "the profile's own. Omitted or empty means no request-supplied group is accepted. Allowing a group and "
            + "defaulting it are separate decisions.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<@NotNull(
            message = NO_NULL_GROUPS) SignatureParameterGroup> allowedRequestParameterGroups = new LinkedHashSet<>();

    @Valid
    @Schema(description = "Signature parameters this profile applies when a request does not override them; their "
            + "family must match the profile's family. Must be omitted for delegated signing. Merged field by field "
            + "with the request winning; list-typed fields, and a placement in a different addressing mode, replace "
            + "the default wholesale.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SignatureParametersDto defaultSignatureParameters;

    public ContentSigningWorkflowRequestDto() {
        super(SigningWorkflowType.CONTENT_SIGNING);
    }
}
