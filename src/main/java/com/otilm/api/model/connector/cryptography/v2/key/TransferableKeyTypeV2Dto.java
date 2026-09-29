package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * One key type a connector can move in or out of a token context, with the algorithms it accepts for that type.
 *
 * <p>
 * The algorithms are declared per type because technologies differ at that granularity, so the platform can present an
 * entry as unsupported before a user commits to it rather than failing the operation later. Import and export declare
 * their own schema so either direction can gain a rule without changing the other.
 * </p>
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class TransferableKeyTypeV2Dto {

    @Schema(description = "Key type the declaration applies to", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "keyRequestType is required")
    private KeyRequestType keyRequestType;

    @Schema(description = "Algorithms accepted for this key type. `Unknown` cannot be declared: the platform decides "
            + "before it sends anything, which it cannot do from an algorithm the connector could not name.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "algorithms must contain at least one algorithm")
    private Set<@NotNull(message = "algorithms must not contain null entries") KeyAlgorithm> algorithms;

    /**
     * A declaration exists so the platform can decide before it sends anything, which it cannot do from an algorithm
     * the connector itself could not name.
     *
     * @return whether every declared algorithm is a named one
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "algorithms must name the algorithms the connector accepts and must not contain Unknown")
    public boolean isNamedAlgorithms() {
        return algorithms == null || !algorithms.contains(KeyAlgorithm.UNKNOWN);
    }

    /**
     * A declaration is what the platform decides from, so it must not pair a key type with an algorithm that cannot
     * produce it: a secret key type is declared with secret-key algorithms, a key pair type with key-pair algorithms.
     *
     * @return whether every declared algorithm can produce the declared key type
     */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "algorithms must be able to produce the declared keyRequestType")
    public boolean isAlgorithmsMatchingKeyType() {
        if (keyRequestType == null || algorithms == null) {
            return true;
        }
        boolean keyPairWanted = keyRequestType == KeyRequestType.KEY_PAIR;
        return algorithms
                .stream()
                .filter(algorithm -> algorithm != KeyAlgorithm.UNKNOWN)
                .allMatch(algorithm -> algorithm.isKeyPairAlgorithm() == keyPairWanted);
    }

    /**
     * Reject unknown key-type properties during deserialization.
     */
    @JsonAnySetter
    @Schema(hidden = true)
    public final void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported v2 key type property: " + property);
    }
}
