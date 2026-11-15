package com.otilm.api.model.client.signing.profile.workflow;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.signing.profile.workflow.timestamp.TimestampSourceDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.signature.SignatureFamily;
import com.otilm.api.model.common.signature.SignatureLevel;
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

    @Schema(description = "Values of the Signature Formatting Provider's profile attributes and request attributes, "
            + "which control DTBS construction for the content-signing workflow; a request attribute's value here is "
            + "the one a signing request inherits. Applicable only when ILM-managed signing is used.",
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

    @Schema(description = "Demand the nonRepudiation key-usage bit specifically, for commitment-grade profiles. "
            + "Applies to ILM-managed signing only; delegated profile gets always false.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, defaultValue = "false")
    private boolean requireNonRepudiation;

    @Schema(description = "Extended key usage OIDs the signing certificate must all carry, in dot notation "
            + "(1.3.6.1.5.5.7.3.36 is RFC 9336 id-kp-documentSigning). An empty set accepts any extended key "
            + "usage. Applies to ILM-managed signing only.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<String> requiredExtendedKeyUsageOids = new LinkedHashSet<>();

    @Schema(description = "Names of the request-attribute groups and ungrouped request attributes a signing request "
            + "may set; a request setting any other request attribute is refused, even when its value matches the "
            + "profile value. Empty allows none and is always empty for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<String> allowedRequestAttributes = new LinkedHashSet<>();

    public ContentSigningWorkflowDto() {
        super(SigningWorkflowType.CONTENT_SIGNING);
    }
}
