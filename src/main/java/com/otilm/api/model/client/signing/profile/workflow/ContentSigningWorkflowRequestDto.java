package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.TimestampSourceRequestDto;
import com.otilm.api.model.common.signature.SignatureLevel;
import com.otilm.api.model.common.validation.OidFormat;
import com.otilm.api.model.common.validation.SignatureFamilyFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    private static final String NO_BLANK_ALLOWED_NAMES = "allowedRequestAttributes must not contain blank names";

    @Schema(description = "UUID of the Signature Formatting Provider that constructs the data-to-be-signed (DTBS) for "
            + "Content signing. Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID signatureFormattingConnectorUuid;

    @Schema(description = "Values of the Signature Formatting Provider's profile attributes and request attributes, "
            + "which control DTBS construction for the content-signing workflow; a request attribute's value here is "
            + "the one a signing request inherits. Must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> signatureFormattingConnectorAttributes = new ArrayList<>();

    @Schema(description = "Signature family this profile produces: a code the selected formatting connector "
            + "declares. Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "pades")
    @Pattern(regexp = SignatureFamilyFormat.REGEX, message = SignatureFamilyFormat.MESSAGE)
    private String family;

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

    @Schema(description = "Names of the request-attribute groups and ungrouped request attributes a signing request "
            + "may set, each declared by the formatting connector; a request setting any other request attribute is "
            + "refused, even when its value matches the profile value. Empty allows none; must be omitted for "
            + "delegated signing.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<@NotBlank(message = NO_BLANK_ALLOWED_NAMES) String> allowedRequestAttributes = new LinkedHashSet<>();

    public ContentSigningWorkflowRequestDto() {
        super(SigningWorkflowType.CONTENT_SIGNING);
    }
}
