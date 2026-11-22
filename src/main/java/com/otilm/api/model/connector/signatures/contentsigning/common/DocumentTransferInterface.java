package com.otilm.api.model.connector.signatures.contentsigning.common;

import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.model.connector.discovery.v2.DiscoveredItemPayloadInterface;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * OpenAPI schema for the polymorphic {@link DocumentTransferDto} hierarchy.
 *
 * <p>
 * The Jackson subtype registry stays on {@link DocumentTransferDto}, for the reason spelled out on
 * {@link DiscoveredItemPayloadInterface}.
 * </p>
 */
@Schema(name = "DocumentTransfer",
        description = "Document transport for a formatting operation. The required "
                + "transferMode property is the discriminator selecting the transport: the document inline, or only "
                + "its digest for a signature family that can sign from one.",
        type = "object", discriminatorProperty = "transferMode",
        discriminatorMapping = {
                @DiscriminatorMapping(value = DocumentTransferMode.Codes.INLINE,
                        schema = InlineDocumentTransferDto.class),
                @DiscriminatorMapping(value = DocumentTransferMode.Codes.DIGEST_ONLY,
                        schema = DigestOnlyDocumentTransferDto.class)},
        oneOf = {InlineDocumentTransferDto.class, DigestOnlyDocumentTransferDto.class})
public interface DocumentTransferInterface {

    @Schema(description = "Transport this document uses, and the discriminator selecting the fields that accompany "
            + "it", requiredMode = Schema.RequiredMode.REQUIRED)
    DocumentTransferMode getTransferMode();
}
