package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Content of the {@link SignatureActivationAttribute}: the platform's decision on a signature and the signer it
 * authorized. The connector checks that signer against the signer bound to the key.
 *
 * @param decision who authorized the signature
 * @param signer the canonical signer the platform bound to the key
 */
public record SignatureActivation(@JsonProperty(DECISION_FIELD) SignatureActivationDecision decision,
        @JsonProperty(SIGNER_FIELD) String signer) implements Serializable {

    static final String DECISION_FIELD = "decision";
    static final String SIGNER_FIELD = "signer";
}
