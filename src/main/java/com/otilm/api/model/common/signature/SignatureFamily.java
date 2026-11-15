package com.otilm.api.model.common.signature;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.regex.Pattern;

/** Open code of a family a formatting connector declares, such as {@code pades} or {@code mrtd}. */
@Schema(name = "SignatureFamily", type = "string",
        description = "Code of a signature family, naming the standard and document format a signature follows",
        pattern = SignatureFamily.CODE_PATTERN, example = "pades")
public record SignatureFamily(String code) implements Serializable {

    public static final String CODE_PATTERN = "^[a-z][a-z0-9-]*$";

    private static final Pattern WELL_FORMED_CODE = Pattern.compile(CODE_PATTERN);

    /** The code appears in URLs and in stored rows. */
    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public SignatureFamily {
        if (code == null || !WELL_FORMED_CODE.matcher(code).matches()) {
            throw new ValidationException(
                    ValidationError.create("Signature family code {} must match {}", code, CODE_PATTERN));
        }
    }

    @Override
    @JsonValue
    public String code() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}
