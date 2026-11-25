package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.client.signing.nativesigning.validation.ValidSignRawItems;
import com.otilm.api.model.common.enums.cryptography.DigestAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureInputMode;
import com.otilm.api.model.connector.cryptography.v2.operations.validation.UniqueIdentifiers;
import com.otilm.api.model.core.settings.SigningSettingsDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@ValidSignRawItems
@Schema(name = "SignRawRequest", description = "Digests the caller computed, to sign with the Signing Profile's key")
public class SignRawRequestDto {

    @Schema(description = "What every item's `data` is. Only `dataDigest` is accepted; `data` answers 422.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "signatureInputMode is required")
    private SignatureInputMode signatureInputMode;

    @Schema(description = "Algorithm the caller computed every digest with. It must be the digest the Signing "
            + "Profile's signature algorithm commits to; any other answers 422.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "digestAlgorithm is required")
    private DigestAlgorithm digestAlgorithm;

    @ArraySchema(arraySchema = @Schema(description = "Items to sign, each with an identifier unique within the "
            + "request. The platform's `maxItemsPerRequest` signing setting bounds how many a request may carry, "
            + "with a default of " + SigningSettingsDto.DEFAULT_MAX_ITEMS_PER_REQUEST + ".",
            requiredMode = Schema.RequiredMode.REQUIRED), minItems = 1)
    @NotEmpty(message = "items must contain at least one item")
    @UniqueIdentifiers(message = "identifiers must be unique within the request")
    private List<@NotNull(message = "items must not contain null items") @Valid SignRawItemDto> items;

    @Schema(description = "SHA-256 fingerprint of the signing certificate the caller expects, which is the first entry "
            + "of `signerCertificateChain`, as the platform reports certificate fingerprints: 64 hexadecimal "
            + "characters in either case, over the DER encoding. If the Signing Profile's current signing certificate "
            + "differs, the request answers 422 and nothing is signed. Absent means no check.",
            examples = {"3f9a1c0e5b7d2e4f6a8c0b1d3e5f7a9c1b3d5e7f9a0c2e4b6d8f0a1c3e5b7d9f"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Pattern(regexp = "^[0-9a-fA-F]{64}$", message = "certificateFingerprint must be 64 hexadecimal characters")
    private String certificateFingerprint;
}
