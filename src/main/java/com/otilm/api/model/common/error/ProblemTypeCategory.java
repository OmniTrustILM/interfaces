package com.otilm.api.model.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(enumAsRef = true)
public enum ProblemTypeCategory {
    COMMON("common"),
    CONNECTOR("connector");

    private final String code;

    ProblemTypeCategory(String code) {
        this.code = code;
    }
}
