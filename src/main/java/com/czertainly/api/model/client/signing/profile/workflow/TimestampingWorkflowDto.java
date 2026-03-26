package com.czertainly.api.model.client.signing.profile.workflow;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.czertainly.api.model.client.signing.timequality.TimeQualityConfigurationDto;
import com.czertainly.api.model.common.enums.cryptography.DigestAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Timestamping workflow configuration embedded in a {@code SigningProfileDto}.
 *
 * <p>Fields are grouped into two logical categories:</p>
 * <ul>
 *     <li>Workflow validation and DTBS formatting properties: managed signing only — null for delegated</li>
 *     <li>Workflow validation properties: both managed and delegated signing</li>
 * </ul>
 *
 * <p><b>.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(title = "TimestampingWorkflowDto", description = "Timestamping workflow configuration")
@ToString(callSuper = true)
public class TimestampingWorkflowDto extends WorkflowDto {

    // --------------------------------------------------------------------------------
    // Workflow validation and DTBS formatting properties — ILM-managed signing only
    // --------------------------------------------------------------------------------

    @Schema(
            description = "Signature Formatter Connector that constructs the data-to-be-signed (DTBS) for Timestamping. " +
                    "Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private NameAndUuidDto signatureFormatterConnector;

    @Schema(
            description = "Attributes configured on the Signature Formatter Connector that control DTBS construction " +
                    "(e.g. serial number generation strategy, whether to include signing time attribute). " +
                    "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> signatureFormatterConnectorAttributes = new ArrayList<>();

    @Schema(
            description = "ETSI qualified electronic timestamp. " +
                    "Present only when ILM-managed signing is used; null for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private Boolean qualifiedTimestamp;

    @Schema(
            description = "Time Quality Configuration that validates clock accuracy at signing time. " +
                    "Required when qualifiedTimestamp is true (ETSI EN 319 421). " +
                    "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TimeQualityConfigurationDto timeQualityConfiguration;

    // -------------------------------------------------------------------------
    // Workflow validation properties — both managed and delegated signing
    // -------------------------------------------------------------------------

    @Schema(
            description = "Default TSA Policy ID under which the timestamp token is issued (OID format). " +
                    "Used for validation of both managed and delegated Timestamping.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "1.2.3.4.5")
    private String defaultPolicyId;

    @Schema(
            description = "Set of TSA Policy IDs accepted as valid for incoming timestamping requests (OID format). " +
                    "Used for validation of both managed and delegated Timestamping.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "[\"1.2.3.4.5\", \"1.2.3.4.6\"]")
    private List<String> allowedPolicyIds = new ArrayList<>();

    @Schema(
            description = "List of digest algorithms that are accepted for timestamping. An empty list means that all digest algorithms are accepted.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "[\"SHA-256\", \"SHA-384\"]")
    private List<DigestAlgorithm> allowedDigestAlgorithms = new ArrayList<>();

    public TimestampingWorkflowDto() {
        super(SigningWorkflowType.TIMESTAMPING);
    }
}
