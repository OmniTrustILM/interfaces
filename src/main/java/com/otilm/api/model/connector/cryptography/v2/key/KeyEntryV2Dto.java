package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** The connector's report on one key, for a listing slot or a requested handle. */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class KeyEntryV2Dto {

    public static final int MAX_DIAGNOSTIC_LENGTH = 1000;

    @Schema(description = "Connector's report on the key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "status is required")
    private KeyEntryStatus status;

    @Schema(description = "Operator text naming the cause behind the status and how to fix it, such as a PKCS#11 "
            + "return code. Clients display it and never parse it, and the connector must keep secrets such as a "
            + "PIN out of it.", requiredMode = Schema.RequiredMode.NOT_REQUIRED, maxLength = MAX_DIAGNOSTIC_LENGTH)
    @ToString.Exclude
    private String diagnostic;

    @Schema(description = "Name the token shows for the key, such as a PKCS#11 label, a keystore alias or a KMS "
            + "key name; it may be absent or shared by several keys", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String storageName;

    @Schema(description = "Key of the requested key request type; present exactly when `status` is "
            + "`available` or `refused`", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private ListedKeyV2Dto key;

    @Schema(description = "Descriptor of a key the entry carries without `key`: a private key for a key pair "
            + "request, a secret key for a secret key request; absent when the connector cannot read it or the "
            + "token holds no matching key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private KeyDataV2Dto keyData;

    /** OpenAPI's {@code maxLength} counts code points, as connectors written in other languages do. */
    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "diagnostic must not exceed " + MAX_DIAGNOSTIC_LENGTH + " characters")
    public boolean isDiagnosticWithinLength() {
        return diagnostic == null || diagnostic.codePointCount(0, diagnostic.length()) <= MAX_DIAGNOSTIC_LENGTH;
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "key must be present exactly when status is available or refused")
    public boolean isKeyConsistentWithStatus() {
        return status == null || status.carriesListedKey() == (key != null);
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "keyData must be absent when key is present")
    public boolean isKeyDataOnlyWithoutKey() {
        return key == null || keyData == null;
    }

    @JsonIgnore
    @Schema(hidden = true)
    @AssertTrue(message = "keyData must be absent when status is notFound or invalidHandle")
    public boolean isKeyDataOnlyForAMatchedKey() {
        return status == null || status.matchesKey() || keyData == null;
    }

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key entry property: " + property);
    }
}
