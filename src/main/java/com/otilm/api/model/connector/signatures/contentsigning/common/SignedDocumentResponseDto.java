package com.otilm.api.model.connector.signatures.contentsigning.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Result of every embed operation: the signed document.
 */
@Getter
@Setter
@ToString
@Schema(name = "SignedDocumentResponse", description = "The signed document at the level this operation brought it to")
public class SignedDocumentResponseDto {

    @ToString.Exclude
    @NotNull(message = "signedDocument is required")
    @Schema(description = "The signed document, base64-encoded in JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    private byte[] signedDocument;

    @NotBlank(message = "mediaType is required")
    @Schema(description = "Media type of `signedDocument`", examples = {"application/pdf"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String mediaType;
}
