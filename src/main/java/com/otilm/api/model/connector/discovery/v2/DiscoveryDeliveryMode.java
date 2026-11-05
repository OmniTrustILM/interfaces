package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true, description = "How a run's discovered items and position travel between connector and Core.")
public enum DiscoveryDeliveryMode implements IPlatformEnum {

    BUFFERED("buffered", "Buffered"),
    CHECKPOINTED("checkpointed", "Checkpointed");

    private static final DiscoveryDeliveryMode[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    DiscoveryDeliveryMode(String code, String label) {
        this(code, label, null);
    }

    DiscoveryDeliveryMode(String code, String label, String description) {
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
    public static DiscoveryDeliveryMode findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown Discovery delivery mode {}", code)));
    }
}
