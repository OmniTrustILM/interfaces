package com.czertainly.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Request body for the cancel endpoint that aborts an in-flight asynchronous
 * certificate issuance or revocation.
 *
 * <p>Cancel transitions:</p>
 * <ul>
 *   <li>{@code PENDING_ISSUE} &rarr; {@code FAILED} — the certificate was never issued and never will be.</li>
 *   <li>{@code PENDING_REVOKE} &rarr; {@code ISSUED} — the certificate remains valid; the revocation was abandoned.</li>
 * </ul>
 */
@Data
public class CancelPendingCertificateRequestDto {

    @Schema(
            description = "Optional free-text reason from the user explaining why the pending operation "
                    + "is being cancelled. Recorded in the certificate event history.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            maxLength = 1000
    )
    private String reason;
}
