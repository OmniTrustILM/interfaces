package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * The unit an operator allow-lists on a Signing Profile to let signing requests supply signature parameters.
 *
 * <p>
 * Group membership is the only permission mechanism there is: whether a request may certify a document is exactly
 * whether {@code SIGNATURE_SCOPE} is allowed. The override unit stays the field, so a request replaces only the values
 * it sends.
 * </p>
 */
@Schema(enumAsRef = true,
        description = "Group of signature parameters a Signing Profile may allow signing requests to supply. "
                + "Membership in the profile's allow-list is the only permission mechanism. A request carrying a "
                + "parameter from a group the profile does not allow is rejected.")
public enum SignatureParameterGroup implements IPlatformEnum {

    SIGNATURE_CONTEXT(Codes.SIGNATURE_CONTEXT, "Signature context",
            "The reason, location and contact information recorded in the signature dictionary",
            Collections.unmodifiableSet(EnumSet.of(SignatureFamily.PADES))),
    SIGNER_IDENTITY(Codes.SIGNER_IDENTITY, "Signer identity",
            "The signer name shown in the signature, which a request may otherwise not contradict",
            Collections.unmodifiableSet(EnumSet.of(SignatureFamily.PADES))),
    SIGNED_ATTRIBUTES(Codes.SIGNED_ATTRIBUTES, "Signed attributes",
            "Commitment type and claimed roles, cryptographically bound into the signature",
            Collections.unmodifiableSet(EnumSet.of(SignatureFamily.PADES))),
    SIGNATURE_SCOPE(Codes.SIGNATURE_SCOPE, "Signature scope",
            "Whether the signature certifies the document or approves a revision of it",
            Collections.unmodifiableSet(EnumSet.of(SignatureFamily.PADES))),
    VISIBLE_SIGNATURE_PLACEMENT(Codes.VISIBLE_SIGNATURE_PLACEMENT, "Visible signature placement",
            "Where the visible signature lands: a named field, coordinates, or an anchor",
            Collections.unmodifiableSet(EnumSet.of(SignatureFamily.PADES))),
    VISIBLE_SIGNATURE_CONTENT(Codes.VISIBLE_SIGNATURE_CONTENT, "Visible signature content",
            "What the visible signature shows: whether it is drawn at all, its caption, and its image",
            Collections.unmodifiableSet(EnumSet.of(SignatureFamily.PADES))),
    VISIBLE_SIGNATURE_STYLE(Codes.VISIBLE_SIGNATURE_STYLE, "Visible signature style",
            "How the visible signature is drawn: colours, padding, caption layout, image scaling and resolution",
            Collections.unmodifiableSet(EnumSet.of(SignatureFamily.PADES)));

    public static class Codes {

        private Codes() {
        }

        public static final String SIGNATURE_CONTEXT = "signature_context";
        public static final String SIGNER_IDENTITY = "signer_identity";
        public static final String SIGNED_ATTRIBUTES = "signed_attributes";
        public static final String SIGNATURE_SCOPE = "signature_scope";
        public static final String VISIBLE_SIGNATURE_PLACEMENT = "visible_signature_placement";
        public static final String VISIBLE_SIGNATURE_CONTENT = "visible_signature_content";
        public static final String VISIBLE_SIGNATURE_STYLE = "visible_signature_style";
    }

    private static final SignatureParameterGroup[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final Set<SignatureFamily> families;

    SignatureParameterGroup(String code, String label, String description, Set<SignatureFamily> families) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.families = families;
    }

    /**
     * Whether a family's parameters actually carry this group today. This declares what is implemented, not what the
     * standards permit, so it widens when another family's parameters ship.
     */
    public boolean appliesTo(SignatureFamily family) {
        return this.families.contains(family);
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @JsonCreator
    public static SignatureParameterGroup findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown signature parameter group {}", code)));
    }
}
