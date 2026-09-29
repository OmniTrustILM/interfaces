package com.otilm.api.model.client.certificate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * What became of one entry's certificate or key.
 */
@Schema(enumAsRef = true,
        description = "What became of one entry's certificate or key: made by this call, already in the inventory, "
                + "or a private key added to an existing public-key record.")
public enum ImportOutcome implements IPlatformEnum {

    CREATED("created", "Created", "Made by this call"),
    EXISTING("existing", "Existing", "Already in the inventory; nothing changed"),
    ADOPTED("adopted", "Adopted", "A private key added to an existing public-key record");

    private static final ImportOutcome[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    ImportOutcome(String code, String label, String description) {
        this.code = code;
        this.label = label;
        this.description = description;
    }

    @JsonCreator
    public static ImportOutcome findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(outcome -> outcome.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown import outcome " + code));
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
}
