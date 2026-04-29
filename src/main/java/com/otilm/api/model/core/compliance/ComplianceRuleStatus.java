package com.otilm.api.model.core.compliance;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/*
List of possible statuses for the individual rules on Compliance checks. This object will be
used only to define the status of individual rules. This object should not be used to define
the overall compliance status
 */

@Schema(enumAsRef = true)
public enum ComplianceRuleStatus implements IPlatformEnum {
    OK(Codes.OK, "Compliant"),
    NOK(Codes.NOK, "Not Compliant"),
    NA(Codes.NA, "Not Applicable"),
    NOT_AVAILABLE(Codes.NOT_AVAILABLE, "Not Available");

    private static final ComplianceRuleStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    ComplianceRuleStatus(String code, String label) {
        this(code, label, null);
    }

    ComplianceRuleStatus(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
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
    public static ComplianceRuleStatus findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Compliance rule status {}", code)));
    }

    public static class Codes {

        private Codes() {
        }

        public static final String OK = "ok";
        public static final String NOK = "nok";
        public static final String NA = "na";
        public static final String NOT_AVAILABLE = "not_available";
    }
}
