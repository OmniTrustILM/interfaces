package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * How a document reaches the formatting connector: inline, or as a digest only. The schema it publishes is
 * {@link DocumentTransferInterface}.
 *
 * <p>
 * Enveloped formats (PAdES, enveloped XAdES, ASiC) send the document itself. Detached formats send only its digest, so
 * the customer document never enters the platform at all.
 * </p>
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "transferMode")
@JsonSubTypes({
        @Type(value = InlineDocumentTransferDto.class, name = DocumentTransferMode.Codes.INLINE),
        @Type(value = DigestOnlyDocumentTransferDto.class, name = DocumentTransferMode.Codes.DIGEST_ONLY)})
@Schema(implementation = DocumentTransferInterface.class)
public sealed interface DocumentTransferDto extends DocumentTransferInterface
        permits InlineDocumentTransferDto, DigestOnlyDocumentTransferDto {
}
