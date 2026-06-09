package com.otilm.api.model.client.cryptography.tokenprofile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BulkTokenProfileKeyUsageRequestDto extends TokenProfileKeyUsageRequestDto {

    @Schema(
            description = "Token Profile UUIDs",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<UUID> uuids;
}
