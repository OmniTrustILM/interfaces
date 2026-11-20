package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "SignDocumentResult", description = "Signing output of one request item")
public class SignDocumentResultDto implements IdentifiedDataV2Dto {

    @Schema(description = "Identifier of the corresponding request item", examples = {"contract-2026-17"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String identifier;

    @Schema(description = "UUID of this item's signing record; absent when the Signing Profile's record policy "
            + "disables recording, or when a `best_effort` record could not be staged. A `best_effort` record may "
            + "still be lost after it is staged.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UUID signingRecordUuid;

    @Sensitive
    @ToString.Exclude
    @Schema(description = "Signing output, base64-encoded in JSON: the signed document, the detached signature or "
            + "the container, as the Signing Profile's signature format produces", format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] output;

    @Schema(description = "Media type of `output`, as the formatting connector names it",
            examples = {"application/pdf"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String mediaType;
}
