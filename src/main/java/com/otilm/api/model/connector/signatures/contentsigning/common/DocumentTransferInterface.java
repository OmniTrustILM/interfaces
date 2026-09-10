package com.otilm.api.model.connector.signatures.contentsigning.common;

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
@Schema(name = "DocumentTransfer", description = "Document transport for a formatting operation. The required "
        + "transferMode property is the discriminator selecting the transport: an enveloped format sends the "
        + "document inline, a detached format sends only its digest. An inline document is bounded by the size cap "
        + "configured on the Signing Profile, which always stays below the platform's 16 MiB request ceiling.",
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
