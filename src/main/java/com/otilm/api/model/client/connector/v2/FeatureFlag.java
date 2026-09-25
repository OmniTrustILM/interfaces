package com.otilm.api.model.client.connector.v2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;

/**
 * Capability advertisements emitted by connectors beyond the version contract.
 *
 * <p>
 * <b>Opt-in semantic for ENFORCED flags:</b> a feature is supported only if the connector explicitly lists it. Absent =
 * NOT supported. Core enforces this gate before invoking capability-dependent operations.
 * </p>
 *
 * <p>
 * <b>INFORMATIONAL flags</b> are advertisement-only metadata. Core handles all behaviors regardless of whether they are
 * advertised.
 * </p>
 */
@Schema(enumAsRef = true)
public enum FeatureFlag implements IPlatformEnum {

    STATELESS("stateless", "Stateless", "A stateless connector does not require persistence layer (e.g. database)",
            FeatureFlagBehavior.INFORMATIONAL, null),
    OPEN_METRICS("openMetrics", "OpenMetrics", "Metrics are exposed in OpenMetrics format",
            FeatureFlagBehavior.INFORMATIONAL, List.of(ConnectorInterface.METRICS)),
    ASYNCHRONOUS("asynchronous", "Asynchronous", "Supports asynchronous operations", FeatureFlagBehavior.ENFORCED,
            List.of(ConnectorInterface.CRYPTOGRAPHY)),
    KEY_IMPORT("keyImport", "Key Import",
            "Supports importing key material supplied as a protected PKCS#8 envelope into the technology. A request "
                    + "carrying the envelope and the passphrase that opens it is as sensitive as the key itself: the transport "
                    + "is trusted with it as it is with connector credentials, and the request must not outlive its operation, "
                    + "so over a message broker it expires with the operation timeout.",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.CRYPTOGRAPHY)),
    KEY_EXPORT("keyExport", "Key Export",
            "Supports exporting key material as a protected PKCS#8 envelope; only keys created or imported as "
                    + "exportable can be exported. A connector declaring this must publish the reserved keyExportable attribute "
                    + "in its create-key attribute schema: a v3 data attribute named keyExportable with boolean content, exactly "
                    + "one content item, required, defaulting to false. It carries the exportable intent at creation time, so "
                    + "key creation needs no field of its own, and it participates in keyCreationId replay equivalence like any "
                    + "other create-key attribute. The connector maps it to the technology's own extractability control, which "
                    + "is set once when the key is created and never raised afterwards. The export request carries the "
                    + "passphrase and the response the envelope it protects, so together they are as sensitive as the key "
                    + "itself: the transport is trusted with them as it is with connector credentials, and the request must not "
                    + "outlive its operation, so over a message broker it expires with the operation timeout.",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.CRYPTOGRAPHY)),
    SECRET_VERSIONING("secretVersioning", "Secret Versioning",
            "Supports versioning of secrets, allowing to keep track of history of secrets.",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.SECRET)),
    SECRET_ROTATION("secretRotation", "Secret Rotation", "Supports triggering rotation of secrets",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.SECRET)),
    CONTENT_SIGNING("contentSigning", "Content Signing", "Supports content signing workflows",
            FeatureFlagBehavior.ENFORCED,
            List
                    .of(ConnectorInterface.PADES_FORMATTING, ConnectorInterface.XADES_FORMATTING,
                            ConnectorInterface.CADES_FORMATTING, ConnectorInterface.JADES_FORMATTING)),
    LEVEL_TIMESTAMPED("levelTimestamped", "Level Timestamped",
            "Reaches the TIMESTAMPED level for the family interface it is declared on, by implementing the signature timestamp imprint and embed pair. The SIGNED level is implied by contentSigning itself",
            FeatureFlagBehavior.ENFORCED,
            List
                    .of(ConnectorInterface.PADES_FORMATTING, ConnectorInterface.XADES_FORMATTING,
                            ConnectorInterface.CADES_FORMATTING, ConnectorInterface.JADES_FORMATTING),
            CONTENT_SIGNING),
    LEVEL_LONG_TERM("levelLongTerm", "Level Long Term",
            "Reaches the LONG_TERM level for the family interface it is declared on, by implementing extendToLevel with its own fetching. A connector without it answers 422 on that operation. Requires levelTimestamped",
            FeatureFlagBehavior.ENFORCED,
            List
                    .of(ConnectorInterface.PADES_FORMATTING, ConnectorInterface.XADES_FORMATTING,
                            ConnectorInterface.CADES_FORMATTING, ConnectorInterface.JADES_FORMATTING),
            LEVEL_TIMESTAMPED),
    LEVEL_ARCHIVAL("levelArchival", "Level Archival",
            "Reaches the ARCHIVAL level for the family interface it is declared on, by implementing the archive timestamp imprint and embed pair. Requires levelLongTerm",
            FeatureFlagBehavior.ENFORCED,
            List
                    .of(ConnectorInterface.PADES_FORMATTING, ConnectorInterface.XADES_FORMATTING,
                            ConnectorInterface.CADES_FORMATTING, ConnectorInterface.JADES_FORMATTING),
            LEVEL_LONG_TERM),
    TIMESTAMPING("timestamping", "Timestamping", "Supports timestamping of signatures", FeatureFlagBehavior.ENFORCED,
            List.of(ConnectorInterface.SIGNATURE_FORMATTING)),
    CERTIFICATE_REGISTRATION("certificateRegistration", "Certificate Registration",
            "Supports pre-registering a certificate's identity (Subject DN, SAN, extensions) at the upstream CA before a CSR exists",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.AUTHORITY)),
    CERTIFICATE_STATUS_POLLING("certificateStatusPolling", "Certificate Status Polling",
            "Supports being polled for asynchronous operation completion; the platform polls the status endpoint rather than relying on out-of-band/manual completion",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.AUTHORITY)),
    CERTIFICATE_REQUEST_STRUCTURED("certificateRequestStructured", "Structured Certificate Request",
            "Accepts the structured requestContent model (typed RDNs, SANs, extensions) on register/issue/renew instead of only the flat subjectDn/subjectAltName/extensions fields",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.AUTHORITY)),
    CERTIFICATE_IDENTITY_OVERRIDE("certificateIdentityOverride", "Certificate Identity Override",
            "Applies an authoritative platform-supplied identity to a forwarded CSR per the CA technology (EJBCA End Entity override; CRMF raVerified), without the platform stripping or re-signing the CSR",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.AUTHORITY)),
    DISCOVERY_STREAMING("discoveryStreaming", "Discovery Streaming",
            "Supports streaming discovery events over a held-open NDJSON response; when absent the platform polls for status and drains results instead. Reached by direct HTTP only — this call never traverses the platform proxy. A connector reachable only through the proxy MUST NOT advertise this flag and should push discovery events over the proxy's AMQP discovery.event binding instead, letting the platform fall back to polling and draining",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.DISCOVERY)),
    DISCOVERY_STOP_RESUME("discoveryStopResume", "Discovery Stop and Resume",
            "Supports pausing a running discovery with a resumable checkpoint and resuming it later",
            FeatureFlagBehavior.ENFORCED, List.of(ConnectorInterface.DISCOVERY));

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
    private final FeatureFlag prerequisite;

    FeatureFlag(String code, String label, String description, FeatureFlagBehavior behavior,
            List<ConnectorInterface> applicableInterfaces) {
        this(code, label, description, behavior, applicableInterfaces, null);
    }

    FeatureFlag(String code, String label, String description, FeatureFlagBehavior behavior,
            List<ConnectorInterface> applicableInterfaces, FeatureFlag prerequisite) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.behavior = behavior;
        this.applicableInterfaces = applicableInterfaces;
        this.prerequisite = prerequisite;
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

    /**
     * The flag this one requires, or null where it stands alone.
     *
     * <p>
     * Signature levels form a prefix ladder: a connector that reaches a rung necessarily reaches every rung below it,
     * so a rung declared without its prerequisite describes a capability that cannot exist. The SIGNED rung has no flag
     * of its own — {@link #CONTENT_SIGNING} is what declares it.
     * </p>
     *
     * <p>
     * The chain is advisory today. Nothing rejects an advertisement that skips a rung, so a consumer that needs the
     * guarantee checks it here rather than assuming ingestion already did.
     * </p>
     */
    public FeatureFlag getPrerequisite() {
        return prerequisite;
    }

    @JsonCreator
    public static FeatureFlag findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new ValidationException(
                        ValidationError.create("Unknown common feature flag code {}", code)));
    }
}
