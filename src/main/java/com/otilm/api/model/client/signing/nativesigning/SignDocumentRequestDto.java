package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.signing.nativesigning.validation.ValidDocumentTransfer;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.signature.DocumentTransferMode;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import com.otilm.api.model.core.settings.SigningSettingsDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ValidDocumentTransfer
@Schema(name = "SignDocumentRequest",
        description = "Documents to sign at the Signing Profile's signature level, inline or as digests")
public class SignDocumentRequestDto {

    @Schema(description = "How every item of the request carries its document",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "transferMode is required")
    private DocumentTransferMode transferMode;

    @Schema(description = "Algorithm that produced every item's `documentDigest`. Required when `transferMode` is "
            + "`digestOnly` and must be the digest the Signing Profile's signature algorithm commits to; absent "
            + "otherwise.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private DigestAlgorithm digestAlgorithm;

    @ArraySchema(arraySchema = @Schema(description = "Documents to sign, each with an identifier unique within the "
            + "request. The platform's `maxItemsPerRequest` signing setting bounds how many a request may carry, "
            + "with a default of " + SigningSettingsDto.DEFAULT_MAX_ITEMS_PER_REQUEST + ".",
            requiredMode = Schema.RequiredMode.REQUIRED), minItems = 1)
    @NotEmpty(message = "items must contain at least one item")
    @UniqueIdentifiers(message = "identifiers must be unique within the request")
    private List<@NotNull(message = "items must not contain null items") @Valid SignDocumentItemDto> items;

    @ToString.Exclude
    @Schema(description = "Values the request sets for request attributes, each allowed by the Signing Profile; the "
            + "formatting connector merges them over the profile's values. Each value takes the version of its "
            + "attribute definition. Absent means none.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<@NotNull(
            message = "requestAttributes must not contain null items") RequestAttribute> requestAttributes;
}
