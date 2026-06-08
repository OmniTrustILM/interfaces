package com.otilm.api.model.client.signing.profile.record;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "SigningRecordPolicyDto",
        description = "Effective Signing Record policy on a Signing Profile")
public class SigningRecordPolicyDto {
    @Schema(description = "Content policy: capture base record metadata (signer identity, success/failure)")
    private boolean recordMetadata;

    @Schema(description = "Content policy: capture inbound request parameters (algorithm, policy IDs, claimed signer)")
    private boolean recordRequestMetadata;

    @Schema(description = "Content policy: capture raw signature value")
    private boolean recordSignature;

    @Schema(description = "Content policy: capture assembled signed document (only valid for CONTENT_SIGNING and TIMESTAMPING workflows)")
    private boolean recordSignedDocument;

    @Schema(description = "Content policy: capture data-to-be-signed bytes")
    private boolean recordDtbs;

    @Schema(description = "Retention in days. null = retain indefinitely.")
    private Integer retentionDays;

    @Schema(description = "Delete the signed document after CSC API async retrieval succeeds")
    private boolean deleteAfterRetrieval;

    @Schema(description = "Persistence-mode trade-off between latency and durability")
    private SigningRecordPersistenceMode persistenceMode;
}
