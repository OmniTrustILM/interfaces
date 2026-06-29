package com.otilm.api.model.client.connector.v2;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.List;


/**
 * Capability advertisements emitted by connectors beyond the version contract.
 *
 * <p><b>Opt-in semantic for ENFORCED flags:</b> a feature is supported only if the connector
 * explicitly lists it. Absent = NOT supported. Core enforces this gate before invoking
 * capability-dependent operations.</p>
 *
 * <p><b>INFORMATIONAL flags</b> are advertisement-only metadata. Core handles all behaviors
 * regardless of whether they are advertised.</p>
 */
@Schema(enumAsRef = true)
public enum FeatureFlag implements IPlatformEnum {

    STATELESS("stateless", "Stateless", "A stateless connector does not require persistence layer (e.g. database)", FeatureFlagBehavior.INFORMATIONAL, null),
    OPEN_METRICS("openMetrics", "OpenMetrics", "Metrics are exposed in OpenMetrics format", FeatureFlagBehavior.INFORMATIONAL, List.of(ConnectorInterface.METRICS)),
    SECRET_VERSIONING("secretVersioning", "Secret Versioning", "Supports versioning of secrets, allowing to keep track of history of secrets.", FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.SECRET)),
    SECRET_ROTATION("secretRotation", "Secret Rotation", "Supports triggering rotation of secrets", FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.SECRET)),
    CONTENT_SIGNING("contentSigning", "Content Signing", "Supports content signing workflows", FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.SIGNING, ConnectorInterface.SIGNATURE_FORMATTING)),
    TIMESTAMPING("timestamping", "Timestamping", "Supports timestamping of signatures", FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.SIGNING, ConnectorInterface.SIGNATURE_FORMATTING)),
    CERTIFICATE_REGISTRATION("certificateRegistration", "Certificate Registration", "Supports pre-registering a certificate's identity (Subject DN, SAN, extensions) at the upstream CA before a CSR exists", FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.AUTHORITY)),
    CERTIFICATE_STATUS_POLLING("certificateStatusPolling", "Certificate Status Polling", "Supports being polled for asynchronous operation completion; the platform polls the status endpoint rather than relying on out-of-band/manual completion", FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.AUTHORITY));

    public enum FeatureFlagBehavior {
        ENFORCED,
        INFORMATIONAL
    }

    private static final FeatureFlag[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final FeatureFlagBehavior behavior;
    private final List<ConnectorInterface> applicableInterfaces;

    FeatureFlag(String code, String label, String description, FeatureFlagBehavior behavior, List<ConnectorInterface> applicableInterfaces) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.behavior = behavior;
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

    public FeatureFlagBehavior getBehavior() {
        return behavior;
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
