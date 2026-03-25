package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.czertainly.api.model.core.signing.signatureprofile.workflow.validation.ValidOid;
import com.czertainly.api.model.core.signing.signatureprofile.workflow.validation.ValidOidList;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Timestamping workflow configuration embedded in a Signature Profile create/update request.
 *
 * <p>Fields are grouped into two logical categories:</p>
 * <ul>
 *     <li>Signature Formatter Connector properties: managed signing only — null for delegated</li>
 *     <li>Workflow validation properties: both managed and delegated signing</li>
 * </ul>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "TimestampingWorkflowRequestDto", description = "Timestamping workflow configuration request")
@ToString(callSuper = true)
public class TimestampingWorkflowRequestDto extends WorkflowRequestDto {

    // -------------------------------------------------------------------------
    // Signature Formatter Connector properties — ILM-managed signing only
    // -------------------------------------------------------------------------

    @Schema(
            description = "UUID of the Signature Formatter Connector that constructs the data-to-be-signed (DTBS) for Timestamping. " +
                    "Required for ILM-managed signing; must be omitted for delegated signing.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID signatureFormatterConnectorUuid;

    @Schema(
            description = "Attributes for the Signature Formatter Connector that control DTBS construction " +
                    "(e.g. serial number generation strategy, qcStatement extension for ETSI EN 319 422 qualified timestamps). " +
                    "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> signatureFormatterConnectorAttributes = new ArrayList<>();

    @Schema(
            description = "UUID of the Time Quality Configuration that validates clock accuracy at signing time. " +
                    "Required when qualifiedTimestamp is true (ETSI EN 319 421). " +
                    "Applicable only when ILM-managed signing is used.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID timeQualityConfigurationUuid;

    // -------------------------------------------------------------------------
    // Workflow validation properties — both managed and delegated signing
    // -------------------------------------------------------------------------

    @ValidOid
    @Schema(
            description = "Default TSA Policy ID under which the timestamp token is issued (OID format). " +
                    "Used for validation of both managed and delegated Timestamping.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "1.2.3.4.5")
    private String defaultPolicyId;

    @ValidOidList
    @Schema(
            description = "Set of TSA Policy IDs accepted as valid for incoming timestamp requests (OID format). " +
                    "Used for validation of both managed and delegated Timestamping.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "[\"1.2.3.4.5\", \"1.2.3.4.6\"]")
    private List<String> allowedPolicyIds = new ArrayList<>();

    public TimestampingWorkflowRequestDto() {
        super(SigningWorkflowType.TIMESTAMPING);
    }
}
