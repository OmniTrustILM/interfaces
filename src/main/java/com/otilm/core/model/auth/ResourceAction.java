package com.otilm.core.model.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ResourceAction implements IPlatformEnum {
    NONE("NONE", "None", AccessType.NOT_GRANTABLE),
    ANY("ANY", "Any", AccessType.NOT_GRANTABLE),
    // Action that is evaluated to allow action for resource lower in hierarchy, e.g. access to
    // certificates through RA profile members action.
    MEMBERS("members", "Members", AccessType.READ),

    // Default (CRUD) Actions
    LIST("list", "List", AccessType.READ),
    DETAIL("detail", "Detail", AccessType.READ),
    CREATE("create", "Create", AccessType.WRITE),
    UPDATE("update", "Update", AccessType.WRITE),
    DELETE("delete", "Delete", AccessType.WRITE),
    USE("use", "Use", AccessType.WRITE),
    APPROVE_DESTINATION("approveDestination", "Approve destination", AccessType.WRITE),

    // Posting comments on an object; a capability of its own, grantable to users who cannot update the object
    COMMENT("comment", "Comment", AccessType.WRITE),

    // Default change state actions that allows also reverse action (disable/deactivate)
    ENABLE("enable", "Enable", AccessType.WRITE),
    ACTIVATE("activate", "Activate", AccessType.WRITE),

    // Lifecycle control actions for long-running operations (e.g. discovery runs)
    STOP("stop", "Stop", AccessType.WRITE),
    RESUME("resume", "Resume", AccessType.WRITE),
    CANCEL("cancel", "Cancel", AccessType.WRITE),

    // Connector actions
    APPROVE("approve", "Approve", AccessType.WRITE),
    // Allows also the reconnect action.
    CONNECT("connect", "Connect", AccessType.WRITE),

    // Certificate actions
    // auth-opa-policies pairs this code with Resource.CONNECTOR in resourcesWithAnonymousAccess, so annotating
    // it on that resource would leave the endpoint reachable unauthenticated.
    REGISTER("register", "Register", AccessType.WRITE),
    ISSUE("issue", "Issue", AccessType.WRITE),
    RENEW("renew", "Renew", AccessType.WRITE),
    REKEY("rekey", "Rekey", AccessType.WRITE),
    REVOKE("revoke", "Revoke", AccessType.WRITE),
    ARCHIVE("archive", "Archive", AccessType.WRITE),

    // Audit Log export
    EXPORT("export", "Export", AccessType.READ),

    // Certificate, RA Profile and Compliance Profile
    CHECK_COMPLIANCE("checkCompliance", "Check compliance", AccessType.WRITE),

    // Cryptography operation
    ENCRYPT("encrypt", "Encrypt", AccessType.WRITE),
    DECRYPT("decrypt", "Decrypt", AccessType.WRITE),
    VERIFY("verify", "Verify", AccessType.WRITE),
    SIGN("sign", "Sign", AccessType.WRITE),

    // PROXY
    // Instructions embed a client secret, a broker SAS key and a non-expiring configuration token.
    GET_PROXY_INSTALLATION("getProxyInstallation", "Get proxy installation", AccessType.SENSITIVE_READ),

    // Cryptographic key material transfer
    // Import creates a key in the technology, so it is a write. Export discloses private or secret key material, so it
    // is a SENSITIVE_READ rather than the READ-typed EXPORT above, which exists for audit-log export and is granted to
    // the read-only role.
    IMPORT_KEY("importKey", "Import key", AccessType.WRITE),
    EXPORT_KEY("exportKey", "Export key", AccessType.SENSITIVE_READ),

    // Secret
    GET_SECRET_CONTENT("getSecretContent", "Get secret content", AccessType.SENSITIVE_READ),
    UPDATE_SOURCE_VAULT_PROFILE("updateSourceVaultProfile", "Update source vault profile", AccessType.WRITE),

    // Digital Signing
    TIMESTAMP("timestamp", "Timestamp", AccessType.WRITE), // RFC 3161 Timestamping

    // Settings
    // Branding is separated from UPDATE so that operators can delegate the platform's appearance without also handing
    // over the utils service URL, certificate validation defaults and every other platform setting.
    UPDATE_BRANDING("updateBranding", "Update branding", AccessType.WRITE);

    /**
     * Whether an action may be granted to a role that must not be able to change anything, so such a permission set can
     * be derived from the action catalogue rather than maintained by hand.
     * <p>
     * {@code WRITE} covers mutation, side effects in a called system, and use of platform key material even when
     * nothing is persisted ({@code ENCRYPT}, {@code VERIFY}, {@code SIGN}, {@code TIMESTAMP}); when uncertain, classify
     * {@code WRITE}, since a wrong {@code READ} grants a write silently. {@code SENSITIVE_READ} discloses stored secret
     * material. {@code NOT_GRANTABLE} marks {@code NONE} and {@code ANY}, which the auth service rejects as unknown
     * actions — omitting {@code ANY} denies nothing, as that gate is satisfied by holding any action on the resource.
     */
    public enum AccessType {
        READ,
        WRITE,
        SENSITIVE_READ,
        NOT_GRANTABLE
    }

    private static final ResourceAction[] VALUES;

    static {
        VALUES = values();
    }

    @Schema(description = "Resource Action Name", example = "create", requiredMode = Schema.RequiredMode.REQUIRED)

    private final String code;

    @Schema(description = "Resource Action label", example = "Create", requiredMode = Schema.RequiredMode.REQUIRED)

    private final String label;

    private final AccessType accessType;

    ResourceAction(String code, String label, AccessType accessType) {
        this.code = code;
        this.label = label;
        this.accessType = accessType;
    }

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
        return null;
    }

    public AccessType getAccessType() {
        return this.accessType;
    }

    /**
     * Prefer this over comparing {@link AccessType} directly: {@code != WRITE} admits both sensitive reads and the
     * sentinels that cannot be persisted at all.
     */
    public boolean isGrantableToReadOnlyRole() {
        return this.accessType == AccessType.READ;
    }

    @JsonCreator
    public static ResourceAction findByCode(String code) {
        return Arrays
                .stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(
                        () -> new ValidationException(ValidationError.create("Unknown Resource Action Name {}", code)));
    }

}
