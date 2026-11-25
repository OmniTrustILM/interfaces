package com.otilm.api.model.core.discovery;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Derived by the same rules as the key inventory. */
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Schema(name = "KeyDescriptionDto",
        description = "Algorithm, parameter set and size of a discovered key, as the platform derives them from the "
                + "key's payload")
public class KeyDescriptionDto {

    @Schema(description = "Algorithm of the key", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyAlgorithm algorithm;

    @Schema(description = "Name of the key's standardized parameters: the curve of an ECDSA key or the parameter set "
            + "of a post-quantum key, such as P-256 or ML-DSA-65; absent for RSA and AES keys, whose `length` gives "
            + "their size, and for a key the platform cannot name.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String parameterSet;

    @Schema(description = "Key length in bits; absent when the platform cannot size the key.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, minimum = "1")
    private Integer length;
}
