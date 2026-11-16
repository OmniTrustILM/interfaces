package com.otilm.api.model.common.validation;

/** Format of a signature family code. A code appears in URLs and in stored rows. */
public final class SignatureFamilyFormat {

    public static final String REGEX = "^[a-z][a-z0-9-]*$";
    public static final String MESSAGE = "must be a signature family code: a lowercase letter, then lowercase letters, "
            + "digits or hyphens";

    private SignatureFamilyFormat() {
    }
}
