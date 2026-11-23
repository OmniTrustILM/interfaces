package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.client.signing.nativesigning.validation.ItemIdentifierFormat;
import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@Schema(name = "SignDocumentItem", description = "Document to sign, identified by the caller")
public class SignDocumentItemDto implements IdentifiedDataV2Dto {

    @Schema(description = "Identifier used to correlate the item with its result; unique within the request",
            examples = {"contract-2026-17"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    @Size(max = ItemIdentifierFormat.MAX_LENGTH, message = ItemIdentifierFormat.LENGTH_MESSAGE)
    @Pattern(regexp = ItemIdentifierFormat.REGEX, message = ItemIdentifierFormat.MESSAGE)
    private String identifier;

    @Sensitive
    @ToString.Exclude
    @Schema(description = "Document to sign, base64-encoded in JSON. Required when `transferMode` is `inline`; "
            + "absent otherwise.", format = "byte", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private byte[] document;

    @Schema(description = "Digest of the document to sign, base64-encoded in JSON. Required when `transferMode` is "
            + "`digestOnly`, and its decoded length must match the output size of `digestAlgorithm` in bytes; absent "
            + "otherwise.", format = "byte", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private byte[] documentDigest;
}
