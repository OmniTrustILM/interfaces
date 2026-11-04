package com.otilm.api.model.common.signature.parameters;

import java.io.Serializable;

/** A node of the signature-parameters model. */
public interface SignatureParameterContainer extends Serializable {

    /**
     * Reports every field this object declares to {@code sink}, in declaration order, together with the group that
     * governs it — the sole declaration of group membership, so a field left out here ships unenforced.
     */
    void describe(SignatureParameterSink sink);
}
