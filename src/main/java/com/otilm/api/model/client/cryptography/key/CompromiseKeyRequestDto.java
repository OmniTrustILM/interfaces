package com.otilm.api.model.client.cryptography.key;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CompromiseKeyRequestDto {

    @Schema(description = "Reason for marking the key items as compromised",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyCompromiseReason reason;

    @Schema(description = "UUIDs of the key items to mark as compromised. If omitted or empty, all items in the key"
            + " are marked as compromised.")
    private List<UUID> uuids;
}
