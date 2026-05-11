package com.czertainly.api.model.client.signing.profile.record;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(description = "Persistence mode for Signing Records, ordered by descending durability")
public enum SigningRecordPersistenceMode implements IPlatformEnum {

    IMMEDIATE("immediate", "Immediate", "Record is written synchronously before the response is returned; highest durability, highest latency"),
    DEFERRED_DURABLE("deferred_durable", "Deferred Durable", "Record is written asynchronously but guaranteed to be persisted; balanced latency and durability"),
    BEST_EFFORT("best_effort", "Best Effort", "Record is written on a best-effort basis with no durability guarantee; lowest latency");

    private final String code;
    private final String label;
    private final String description;

    SigningRecordPersistenceMode(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    private static final SigningRecordPersistenceMode[] VALUES = values();

    @JsonCreator
    public static SigningRecordPersistenceMode findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signing record persistence mode {}", code)));
    }
}
