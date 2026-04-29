package com.otilm.api.model.client.connector.v2;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.List;


@Schema(enumAsRef = true)
public enum FeatureFlag implements IPlatformEnum {

    STATELESS("stateless", "Stateless", "A stateless connector does not require persistence layer (e.g. database)"),
    OPEN_METRICS("openMetrics", "OpenMetrics", "Metrics are exposed in OpenMetrics format", List.of(ConnectorInterface.METRICS)),
    SECRET_VERSIONING("secretVersioning", "Secret Versioning", "Supports versioning of secrets, allowing to keep track of history of secrets.", List.of(ConnectorInterface.SECRET)),
    SECRET_ROTATION("secretRotation", "Secret Rotation", "Supports triggering rotation of secrets", List.of(ConnectorInterface.SECRET));

    private static final FeatureFlag[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final List<ConnectorInterface> applicableInterfaces;

    FeatureFlag(String code, String label, String description) {
        this(code, label, description, null);
    }

    FeatureFlag(String code, String label, String description, List<ConnectorInterface> applicableInterfaces) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.applicableInterfaces = applicableInterfaces;
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

    public List<ConnectorInterface> getApplicableInterfaces() {
        return applicableInterfaces;
    }

    @JsonCreator
    public static FeatureFlag findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown common feature flag code {}", code)));
    }
}
