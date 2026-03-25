package com.czertainly.api.model.core.signing.signatureprofile.workflow;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum SigningWorkflowType implements IPlatformEnum {

    CODE_BINARY_SIGNING(Codes.CODE_BINARY_SIGNING, "Code & Binary Signing", "Signing of code and binary files"),
    DOCUMENT_SIGNING(Codes.DOCUMENT_SIGNING, "Document Signing", "Signing of documents"),
    RAW_SIGNING(Codes.RAW_SIGNING, "Raw Signing", "Signing of raw data"),
    TIMESTAMPING(Codes.TIMESTAMPING, "Timestamping", "Timestamping of data"),
    ;

    private static final SigningWorkflowType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    SigningWorkflowType(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static SigningWorkflowType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signing workflow type {}", code)));
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

    public static class Codes {
        public static final String CODE_BINARY_SIGNING = "code_binary_signing";
        public static final String DOCUMENT_SIGNING = "document_signing";
        public static final String RAW_SIGNING = "raw_signing";
        public static final String TIMESTAMPING = "timestamping";

        private Codes() {
        }
    }
}
