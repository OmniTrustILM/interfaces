package com.czertainly.core.model.auth;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum ResourceAction implements IPlatformEnum {
    NONE("NONE", "None"),
    ANY("ANY", "Any"), // Action that is evaluated as any action
    MEMBERS("members", "Members"), // action that is evaluated to allow action for resource lower in hierarchy, e.g. access to certificates through RA profile members action

    // Default (CRUD) Actions
    LIST("list", "List"),
    DETAIL("detail", "Detail"),
    CREATE("create", "Create"),
    UPDATE("update", "Update"),
    DELETE("delete", "Delete"),

    // Default change state actions that allows also reverse action (disable/deactivate)
    ENABLE("enable", "Enable"),
    ACTIVATE("activate", "Activate"),

    // Connector actions
    APPROVE("approve", "Approve"),
    CONNECT("connect", "Connect"), // allows also reconnect action

    // Certificate actions
    ISSUE("issue", "Issue"),
    RENEW("renew", "Renew"),
    REKEY("rekey", "Rekey"),
    REVOKE("revoke", "Revoke"),
    ARCHIVE("archive", "Archive"),

    // Audit Log export
    EXPORT("export", "Export"),

    // Certificate, RA Profile and Compliance Profile
    CHECK_COMPLIANCE("checkCompliance", "Check compliance"),

    // Cryptography operation
    ENCRYPT("encrypt", "Encrypt"),
    DECRYPT("decrypt", "Decrypt"),
    VERIFY("verify", "Verify"),
    SIGN("sign", "Sign"),

    // Secret
    GET_SECRET_CONTENT("getSecretContent", "Get secret content"),
    UPDATE_SOURCE_VAULT_PROFILE("updateSourceVaultProfile", "Update source vault profile")
    ;

    @Schema(description = "Resource Action Name",
            example = "create",
            requiredMode = Schema.RequiredMode.REQUIRED)

    private final String code;

    @Schema(description = "Resource Action label",
            example = "Create",
            requiredMode = Schema.RequiredMode.REQUIRED)

    private final String label;

    ResourceAction(String code, String label) {
        this.code = code;
        this.label = label;
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

    @JsonCreator
    public static ResourceAction findByCode(String code) {
        return Arrays.stream(ResourceAction.values())
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Resource Action Name {}", code)));
    }

}
