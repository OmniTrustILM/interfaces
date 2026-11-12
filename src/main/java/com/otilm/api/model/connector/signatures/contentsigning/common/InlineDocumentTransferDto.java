package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;

/**
 * The inline arm of {@link DocumentTransferDto}: the document itself travels.
 */
@JsonIgnoreProperties(value = "transferMode", allowGetters = true)
@Schema(name = "InlineDocumentTransfer", description = "Document transport carrying the document itself")
public record InlineDocumentTransferDto(
        @NotNull(message = "document is required") @Schema(description = "The document itself. Base64-encoded in JSON.",
                requiredMode = Schema.RequiredMode.REQUIRED) byte[] document)
        implements
            DocumentTransferDto {

    @Override
    public DocumentTransferMode getTransferMode() {
        return DocumentTransferMode.INLINE;
    }

    /**
     * A record's generated members would defeat two properties this type needs: value equality over the document, and
     * keeping customer content out of a routine log line.
     */
    @Override
    public boolean equals(Object other) {
        return other instanceof InlineDocumentTransferDto transfer && Arrays.equals(document, transfer.document);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(document);
    }

    @Override
    public String toString() {
        return "InlineDocumentTransferDto[document=<" + (document == null ? 0 : document.length) + " bytes>]";
    }
}
