package com.otilm.api.model.client.signing.nativesigning;

import com.otilm.api.model.client.signing.nativesigning.validation.ItemIdentifierFormat;
import com.otilm.api.model.connector.cryptography.v2.operations.data.IdentifiedDataV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(name = "SignRawItem", description = "Item to sign, identified by the caller")
public class SignRawItemDto implements IdentifiedDataV2Dto {

    @Schema(description = "Identifier used to correlate the item with its result; unique within the request",
            examples = {"installer-x64"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "identifier is required")
    @Size(max = ItemIdentifierFormat.MAX_LENGTH, message = ItemIdentifierFormat.LENGTH_MESSAGE)
    @Pattern(regexp = ItemIdentifierFormat.REGEX, message = ItemIdentifierFormat.MESSAGE)
    private String identifier;

    @ToString.Exclude
    @Schema(description = "Item to sign, base64-encoded in JSON. Under `dataDigest` it is the digest of the data, and "
            + "its decoded length must match the output size of `digestAlgorithm` in bytes.", format = "byte",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "data is required and must not be empty")
    private byte[] data;
}
