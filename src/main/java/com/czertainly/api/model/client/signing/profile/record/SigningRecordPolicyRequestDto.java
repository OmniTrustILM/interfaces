package com.czertainly.api.model.client.signing.profile.record;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(name = "SigningRecordPolicyRequestDto",
        description = "Per-profile policy for what to record about each signing operation")
public class SigningRecordPolicyRequestDto {

    @Schema(description = "Content policy: capture inbound request parameters (algorithm, policy IDs, claimed signer)")
    private boolean recordRequestMetadata;

    @Schema(description = "Content policy: capture raw signature value")
    private boolean recordSignature;

    @Schema(description = "Content policy: capture assembled signed document (only valid for CONTENT_SIGNING and TIMESTAMPING workflows)")
    private boolean recordSignedDocument;

    @Schema(description = "Content policy: capture data-to-be-signed bytes")
    private boolean recordDtbs;

    @Min(1)
    @Schema(description = "Retention in days. null = retain indefinitely. Minimum is 1 day.")
    private Integer retentionDays;

    @Schema(description = "Delete the signed document after CSC API async retrieval succeeds")
    private boolean deleteAfterRetrieval;

    @Schema(description = "Persistence-mode trade-off between latency and durability", defaultValue = "deferred_durable")
    private SigningRecordPersistenceMode persistenceMode = SigningRecordPersistenceMode.DEFERRED_DURABLE;
}
