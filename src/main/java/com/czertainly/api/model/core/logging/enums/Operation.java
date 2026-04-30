package com.czertainly.api.model.core.logging.enums;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum Operation implements IPlatformEnum {
    UNKNOWN("unknown", "Unknown"),
    LIST("list", "List"),
    DETAIL("detail", "Detail"),
    CREATE("create", "Create"),
    UPDATE("update", "Update"),
    DELETE("delete", "Delete"),
    FORCE_DELETE("forceDelete", "Force delete"),
    ADD("add", "Add"),
    REMOVE("remove", "Remove"),
    REQUEST("request", "Request"),
    REGISTER("register", "Register"),
    ENABLE("enable", "Enable"),
    DISABLE("disable", "Disable"),
    ACTIVATE("activate", "Activate"),
    DEACTIVATE("deactivate", "Deactivate"),
    LIST_ASSOCIATIONS("listAssociations", "List associations"),
    ASSOCIATE("associate", "Associate"),
    DISASSOCIATE("disassociate", "Disassociate"),
    HISTORY("history", "History"),
    SUMMARY("summary", "Summary"),
    LIST_RULES("listRules", "List rules"),
    GET_COMPLIANCE_RESULT("getComplianceResult", "Get compliance result"),
    CHECK_COMPLIANCE("checkCompliance", "Check compliance"),
    CHECK_VALIDATION("checkValidation", "Check validation"),
    ISSUE("issue", "Issue"),
    RENEW("renew", "Renew"),
    REKEY("rekey", "Rekey"),
    REVOKE("revoke", "Revoke"),
    EXPORT("export", "Export"),
    GET_STATUS("getStatus", "Get status"),
    GET_CONTENT("getContent", "Get content"),
    GET_CHAIN("getChain", "Get chain"),
    DOWNLOAD("download", "Download"),
    DOWNLOAD_CHAIN("downloadChain", "Download chain"),
    GET_PROTOCOL_INFO("getProtocolInfo", "Get protocol info"),
    LIST_PROTOCOL_CERTIFICATES("listProtocolCertificates", "List protocol certificates"),
    UPDATE_PROTOCOL_ISSUE_PROFILE("updateProtocolIssueProfile", "Update protocol issue profile"),
    ACTIVATE_PROTOCOL("activateProtocol", "Activate protocol"),
    DEACTIVATE_PROTOCOL("deactivateProtocol", "Deactivate protocol"),
    ACME_DIRECTORY("acmeDirectory", "ACME directory"),
    ACME_NONCE("acmeNonce", "ACME nonce"),
    ACME_KEY_ROLLOVER("acmeKeyRollover", "ACME key rollover"),
    ACME_VALIDATE("acmeValidate", "ACME validate"),
    ACME_FINALIZE("acmeFinalize", "ACME finalize"),
    SCEP_CA_CAPABILITIES("scepCaCapabilities", "SCEP CA Capabilities"),
    SCEP_CERTIFICATE_POLL("scepCertificatePoll", "SCEP Certificate Poll"),
    SCEP_TRANSACTION_CHECK("scepTransactionCheck", "SCEP Transaction check"),
    CMP_CONFIRM("cmpConfirm", "CMP confirm"),
    UPLOAD("upload", "Upload"),
    SYNC("sync", "Sync"),
    COMPROMISE("compromise", "Compromise"),
    DESTROY("destroy", "Destroy"),
    UPDATE_KEY_USAGE("updateKeyUsage", "Update key usage"),
    ENCRYPT("encrypt", "Encrypt"),
    DECRYPT("decrypt", "Decrypt"),
    SIGN("sign", "Sign"),
    VERIFY("verify", "Verify"),
    RANDOM_DATA("randomData", "Generate random data"),
    PUSH_TO_LOCATION("pushToLocation", "Push to location"),
    REMOVE_FROM_LOCATION("removeFromLocation", "Remove from location"),
    ISSUE_IN_LOCATION("issueInLocation", "Issue in location"),
    RENEW_IN_LOCATION("renewInLocation", "Renew in location"),
    CONNECT("connect", "Connect"),
    RECONNECT("reconnect", "Reconnect"),
    CHECK_HEALTH("checkHealth", "Check health"),
    GET_CONNECTOR_INFO("getConnectorInfo", "Get Connector info"),
    STATISTICS("statistics", "Statistics"),
    APPROVE("approve", "Approve"),
    APPROVE_OVERRIDE("approveOverride", "Approve override"),
    REJECT("reject", "Reject"),
    REJECT_OVERRIDE("rejectOverride", "Reject override"),
    LOGIN("login", "Login"),
    LOGOUT("logout", "Logout"),
    AUTHENTICATION("authentication", "Authentication"),
    GET_USER_PROFILE("getUserProfile", "Get user profile"),
    UPDATE_USER_PROFILE("updateUserProfile", "Update user profile"),
    IDENTIFY("identify", "Identify"),
    MARK_AS_READ("markAsRead", "Mark as read"),
    GET_PERMISSIONS("getPermissions", "Get permissions"),
    GET_OBJECT_PERMISSIONS("getObjectPermissions", "Get object permissions"),
    UPDATE_PERMISSIONS("updatePermissions", "Update permissions"),
    UPDATE_OBJECT_PERMISSIONS("updateObjectPermissions", "Update object permissions"),
    SCHEDULE("schedule", "Schedule"),
    LIST_ATTRIBUTES("listAttributes", "List attributes"),
    VALIDATE_ATTRIBUTES("validateAttributes", "Validate attributes"),
    ATTRIBUTE_CALLBACK("attributeCallback", "Attribute callback"),
    UPDATE_ATTRIBUTE_RESOURCES("updateAttributeResources", "Update attribute resources"),
    UPDATE_ATTRIBUTE_CONTENT("updateAttributeContent", "Update attribute content"),
    DELETE_ATTRIBUTE_CONTENT("deleteAttributeContent", "Delete attribute content"),
    PROMOTE_METADATA("promoteMetadata", "Promote metadata"),
    ARCHIVE("archive", "Archive certificate"),
    UNARCHIVE("unarchive", "Unarchive certificate"),
    GET_ASSOCIATIONS("getAssociations", "Get associations"),
    LIST_VERSIONS("listVersions", "List versions"),
    GET_PROXY_INSTALLATION("getProxyInstallation", "Get proxy installation instructions"),

    // legacy
    LIST_CERTIFICATE_PROFILES("listCertificateProfiles", "List Certificate profiles"),
    LIST_CAS("listCas", "List CAs");

    private static final Operation[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    Operation(String code, String label) {
        this(code, label,null);
    }

    Operation(String code, String label, String description) {
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
    public static Operation findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(o -> o.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Operation code {}", code)));
    }
}
