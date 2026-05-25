package com.czertainly.api.model.core.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Per-operation support flags for an authority or RA profile.
 *
 * <p>An instance of this class is returned for each operation kind
 * (ISSUE, RENEW, REVOKE, REGISTER) inside {@link AvailableOperationsDto}.
 * Operators use these flags to validate flows and drive UI affordances
 * before invoking them.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "OperationSupport",
        description = "Per-operation support flags for an authority/RA profile."
)
public class OperationSupport {

    @Schema(
            description = "Operation kind (e.g. ISSUE, RENEW, REVOKE, REGISTER).",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String operation;

    @Schema(
            description = "Whether the operation is supported by this authority."
    )
    private boolean supported;

    @Schema(
            description = "Whether the operation may complete asynchronously (HTTP 202 + status polling)."
    )
    private boolean asyncSupported;

    @Schema(
            description = "Whether an in-flight asynchronous execution of this operation can be cancelled."
    )
    private boolean cancelSupported;

}
