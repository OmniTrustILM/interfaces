package com.otilm.api.model.core.raprofile;

import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

/**
 * Governs how the RA Profile static request-attribute set combines with a connector-supplied set.
 * Merge key is the attribute UUID, with the attribute name as fallback.
 */
@Schema(enumAsRef = true)
public enum AttributeSetMergeMode implements IPlatformEnum {

    STATIC_ONLY("staticOnly", "Static only", "Use only the RA Profile static set; ignore the connector supplied set (default)"),
    CONNECTOR_ONLY("connectorOnly", "Connector only", "Use only the connector supplied set; ignore the static set"),
    MERGE("merge", "Merge", "Union of both sets; on a key conflict the connector definition wins and the static set contributes only what the connector did not supply");

    private static final AttributeSetMergeMode[] VALUES = values();

    private final String code;
    private final String label;
    private final String description;

    AttributeSetMergeMode(String code, String label, String description) {
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
    public static AttributeSetMergeMode fromCode(final String code) {
        return Arrays.stream(VALUES)
                .filter(e -> e.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unsupported request attribute set merge mode %s.", code)));
    }
}
