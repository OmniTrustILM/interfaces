package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.TimestampSourceDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.common.signature.SignatureParameterGroup;
import com.otilm.api.model.common.signature.parameters.SignatureParametersDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Content signing workflow configuration embedded in a {@code SigningProfileDto}.
 *
 * <p>
 * The Signature Formatting Provider and signature fields are null when delegated signing is used.
 * </p>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "ContentSigningWorkflowDto",
        description = "Content signing workflow configuration. The validation material that LONG_TERM and ARCHIVAL "
                + "embed is fetched by the Signature Formatting Provider and is not configured here.")
@ToString(callSuper = true)
public class ContentSigningWorkflowDto extends WorkflowDto {

    @Schema(description = "Signature Formatting Provider that constructs the data-to-be-signed (DTBS) for Content "
            + "signing. Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NameAndUuidDto signatureFormattingConnector;

    @Schema(description = "Attributes configured on the Signature Formatting Provider that control DTBS construction "
            + "for the content signing workflow. Applicable only when ILM-managed signing is used. These attributes "
            + "carry operator-only knobs; caller-influenceable parameters live in defaultSignatureParameters.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> signatureFormattingConnectorAttributes = new ArrayList<>();

    @Schema(description = "Signature family this profile produces. "
            + "Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SignatureFamily family;

    @Schema(description = "Highest signature level a request may ask for. A request asking for more is rejected. "
            + "Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SignatureLevel maxLevel;

    @Schema(description = "Source of the timestamps embedded at level TIMESTAMPED and above. "
            + "Set when maxLevel is TIMESTAMPED or higher.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TimestampSourceDto timestampSource;

    @Schema(description = "Largest document accepted for signing, in bytes. Enforced when an ILM-managed "
            + "signing request arrives. Null when no profile-level cap applies.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "5242880")
    private Long documentSizeCap;

    @Schema(description = "Demand the nonRepudiation key-usage bit specifically, for commitment-grade profiles. "
            + "Applies to ILM-managed signing only; delegated profile gets always false.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean requireNonRepudiation;

    @Schema(description = "Extended key usage OIDs the signing certificate must all carry, in dot notation "
            + "(1.3.6.1.5.5.7.3.36 is RFC 9336 id-kp-documentSigning). An empty set accepts any extended key "
            + "usage. Applies to ILM-managed signing only.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<String> requiredExtendedKeyUsageOids = new LinkedHashSet<>();

    @Schema(description = "Signature parameter groups which a signing request may supply. Empty for delegated signing. "
            + "Membership is the only permission mechanism. A request carrying any field of a group absent from this "
            + "list is rejected, even when the value matches the profile's own. An empty list accepts no "
            + "request-supplied group. Independent of defaultSignatureParameters: a group may be allowed with no "
            + "default (pure pass-through), or carry a default without being allowed (operator-fixed).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<SignatureParameterGroup> allowedRequestParameterGroups = new LinkedHashSet<>();

    @Schema(description = "Signature parameters this profile applies when a request does not override them. Null for "
            + "delegated signing. Merged field by field with the request winning; list-typed fields, and a placement "
            + "in a different addressing mode, replace the default wholesale.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SignatureParametersDto defaultSignatureParameters;

    public ContentSigningWorkflowDto() {
        super(SigningWorkflowType.CONTENT_SIGNING);
    }
}
