package com.otilm.api.model.core.search;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum FilterConditionOperator implements IPlatformEnum {
    EQUALS("EQUALS", "equals", "eq"),
    NOT_EQUALS("NOT_EQUALS", "not equals", "ne"),
    GREATER("GREATER", "greater than", "gt"),
    GREATER_OR_EQUAL("GREATER_OR_EQUAL", "greater than or equal", "ge"),
    LESSER("LESSER", "lesser than", "lt"),
    LESSER_OR_EQUAL("LESSER_OR_EQUAL", "lesser than or equal", "le"),
    CONTAINS("CONTAINS", "contains", "like"),
    NOT_CONTAINS("NOT_CONTAINS", "not contains", "nlike"),
    STARTS_WITH("STARTS_WITH", "starts with", "start"),
    ENDS_WITH("ENDS_WITH", "ends with", "end"),
    EMPTY("EMPTY", "empty", "em"),
    NOT_EMPTY("NOT_EMPTY", "not empty", "nem"),
    IN_NEXT("IN_NEXT", "in next", "in"),
    IN_PAST("IN_PAST", "in past", "ip"),
    MATCHES("MATCHES", "matches regex", "ma"),
    NOT_MATCHES("NOT_MATCHES", "not matches regex", "nma"),
    COUNT_EQUAL("COUNT_EQUAL", "count equals", "ce"),
    COUNT_NOT_EQUAL("COUNT_NOT_EQUAL", "count not equals", "cne"),
    COUNT_GREATER_THAN("COUNT_GREATER_THAN", "count greater than", "cge"),
    COUNT_LESS_THAN("COUNT_LESS_THAN", "count less than", "cle");

    private static final FilterConditionOperator[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final String queryCode;

    FilterConditionOperator(String code, String label, String queryCode) {
        this(code, label, null, queryCode);
    }

    FilterConditionOperator(String code, String label, String description, String queryCode) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.queryCode = queryCode;
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

    public String getQueryCode() {
        return queryCode;
    }

    @JsonCreator
    public static FilterConditionOperator fromCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported search condition %s.", code)));
    }
}
