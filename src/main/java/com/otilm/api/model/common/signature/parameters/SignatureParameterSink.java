package com.otilm.api.model.common.signature.parameters;

import com.otilm.api.model.common.signature.SignatureParameterGroup;

/** Receives the fields a {@link SignatureParameterContainer} declares. */
public interface SignatureParameterSink {

    /**
     * A parameter carrying a value of its own, together with the group that decides whether a request may supply it.
     *
     * @param name the field's own name, unqualified
     */
    void leaf(String name, SignatureParameterGroup group, Object value);

    /** A nested parameters object, which may be {@code null}. */
    void container(String name, SignatureParameterContainer value);
}
