package com.czertainly.api.model.common.events.data;

import com.czertainly.api.model.core.certificate.CertificateState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CertificateActionPerformedEventData extends CertificateEventAuthorityData {

    @Schema(description = "Certificate action", requiredMode = Schema.RequiredMode.REQUIRED)
    private String action;

    @Schema(description = "Error message. Filled when action failed", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String errorMessage;

    @Schema(
            description = "Certificate state observed at the moment the event was emitted. "
                    + "Subscribers use this to distinguish a synchronous success "
                    + "(state=ISSUED / REVOKED) from an asynchronous-pending outcome "
                    + "(state=PENDING_ISSUE / PENDING_REVOKE) for the same action. "
                    + "Optional for backward compatibility with subscribers built before the field "
                    + "was introduced.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateState state;

}
