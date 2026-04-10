package com.czertainly.api.model.core.auth;

import com.czertainly.api.exception.ValidationError;
import com.czertainly.api.exception.ValidationException;
import com.czertainly.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@Schema(enumAsRef = true)
public enum Resource implements IPlatformEnum {
    NONE("NONE", "None"),
    ANY("ANY", "Any"),

    // GENERAL
    DASHBOARD("dashboard", "Dashboard"),
    SETTINGS("settings", "Settings"),
    AUDIT_LOG("auditLogs", "Audit logs"),
    CREDENTIAL(Codes.CREDENTIAL, "Credential", true, true),
    CONNECTOR(Codes.CONNECTOR, "Connector", true, true),
    ATTRIBUTE(Codes.ATTRIBUTE, "Attribute", true),
    SCHEDULED_JOB("jobs", "Scheduled job"),

    // AUTH
    USER(Codes.USER, "User", false, true, true, false),
    ROLE(Codes.ROLE, "Role", false, true),

    // ACME
    ACME_ACCOUNT(Codes.ACME_ACCOUNT, "ACME Account"),
    ACME_PROFILE(Codes.ACME_PROFILE, "ACME Profile", true, true),

    CBOM(Codes.CBOM, "CBOM"),

    // SCEP
    SCEP_PROFILE(Codes.SCEP_PROFILE, "SCEP Profile", true, true),

    // CMP
    CMP_PROFILE(Codes.CMP_PROFILE, "CMP Profile", true, true),

    // CERTIFICATES
    AUTHORITY(Codes.AUTHORITY, "Authority", true, true),
    RA_PROFILE(Codes.RA_PROFILE, "RA Profile", true, true),
    CERTIFICATE(Codes.CERTIFICATE, "Certificate", false, true, true, true),
    CERTIFICATE_REQUEST(Codes.CERTIFICATE_REQUEST, "Certificate Request", false, false),
    GROUP(Codes.GROUP, "Group", true, true),
    COMPLIANCE_PROFILE(Codes.COMPLIANCE_PROFILE, "Compliance Profile", true, true),
    DISCOVERY(Codes.DISCOVERY, "Discovery", false, true),

    // OID
    OID(Codes.OID, "OID", true, false),

    // ENTITIES
    ENTITY(Codes.ENTITY, "Entity", true, true),
    LOCATION(Codes.LOCATION, "Location", true, true),

    // CRYPTOGRAPHY
    TOKEN_PROFILE(Codes.TOKEN_PROFILE, "Token Profile", true, true),
    TOKEN(Codes.TOKEN, "Token", true, true),
    CRYPTOGRAPHIC_KEY(Codes.CRYPTOGRAPHIC_KEY, "Key", false, true, true, true),

    // APPROVALS
    APPROVAL_PROFILE("approvalProfiles", "Approval profile"),
    APPROVAL("approvals", "Approval"),

    // NOTIFICATIONS
    NOTIFICATION_PROFILE("notificationProfiles", "Notification profile"),
    NOTIFICATION_INSTANCE("notificationInstances", "Notification instance"),

    // WORKFLOWS
    RULE("rules", "Rule"),
    ACTION("actions", "Action"),
    TRIGGER("triggers", "Trigger"),

    // ADDED FOR LOGS PURPOSES
    RESOURCE("resources", "Resource"),
    RESOURCE_EVENT("resourceEvents", "Resource Event"),
    SEARCH_FILTER("searchFilters", "Search Filter"),
    CRYPTOGRAPHIC_KEY_ITEM("keyItems", "Key item"),
    PLATFORM_ENUM("platformEnums", "Platform enumerator"),
    NOTIFICATION("notifications", "Notification"),
    CONDITION("conditions", "Condition"),
    EXECUTION("executions", "Execution"),
    COMPLIANCE_RULE("complianceRules", "Compliance Rule"),
    COMPLIANCE_GROUP("complianceGroups", "Compliance Group"),
    CUSTOM_ATTRIBUTE("customAttributes", "Custom Attribute"),
    GLOBAL_METADATA("globalMetadata", "Global Metadata"),
    ACME_ORDER("acmeOrders", "ACME Order"),
    ACME_AUTHORIZATION("acmeAuthorizations", "ACME Authorization"),
    ACME_CHALLENGE("acmeChallenges", "ACME Challenge"),
    CMP_TRANSACTION("cmpTransactions", "CMP Transaction"),
    END_ENTITY_PROFILE("endEntityProfiles", "End entity profile"),
    AUTHENTICATION_PROVIDER("authenticationProviders", "Authentication Provider"),

    // Secrets
    VAULT(Codes.VAULT, "Vault", true, true),
    VAULT_PROFILE(Codes.VAULT_PROFILE, "Vault Profile", true, true),
    SECRET(Codes.SECRET, "Secret", false, true, true, true),

    // Signing
    DIGITAL_SIGNATURE(Codes.DIGITAL_SIGNATURE, "Digital Signature", false, true, true, true),
    SIGNING_PROFILE(Codes.SIGNING_PROFILE, "Signing Profile", true, true),
    TIME_QUALITY_CONFIGURATION(Codes.TIME_QUALITY_CONFIGURATION, "Time Quality Configuration", true, true),
    TSP_PROFILE(Codes.TSP_PROFILE, "Timestamping Protocol Profile", true, true),
    ;

    private static final Resource[] VALUES;
    private static final EnumSet<Resource> complianceSubjects = EnumSet.of(Resource.CERTIFICATE, Resource.CERTIFICATE_REQUEST, Resource.CRYPTOGRAPHIC_KEY, Resource.SECRET);
    private static final EnumSet<Resource> complianceProfilesAssignable = EnumSet.of(Resource.RA_PROFILE, Resource.TOKEN_PROFILE, Resource.VAULT_PROFILE);
    private static final EnumSet<Resource> approvalProfilesAssignable = EnumSet.of(Resource.RA_PROFILE, Resource.SIGNING_PROFILE, Resource.VAULT_PROFILE);

    static {
        VALUES = values();
    }

    @Schema(description = "Resource Name",
            examples = { "certificates" },
            requiredMode = Schema.RequiredMode.REQUIRED)
    private final String code;
    private final String label;
    private final String description;

    private final boolean hasObjectAccess;
    private final boolean hasCustomAttributes;
    private final boolean hasGroups;
    private final boolean hasOwner;

    Resource(String code, String label) {
        this(code, label, null, false, false, false, false);
    }

    Resource(String code, String label, boolean objectAccess) {
        this(code, label, null, objectAccess, false, false, false);
    }

    Resource(String code, String label, boolean objectAccess, boolean hasCustomAttributes) {
        this(code, label, null, objectAccess, hasCustomAttributes, false, false);

    }

    Resource(String code, String label, boolean objectAccess, boolean hasCustomAttributes, boolean hasGroups, boolean hasOwner) {
        this(code, label, null, objectAccess, hasCustomAttributes, hasGroups, hasOwner);
    }

    Resource(String code, String label, String description, boolean objectAccess, boolean hasCustomAttributes, boolean hasGroups, boolean hasOwner) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.hasObjectAccess = objectAccess;
        this.hasCustomAttributes = hasCustomAttributes;
        this.hasGroups = hasGroups;
        this.hasOwner = hasOwner;
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

    public boolean hasObjectAccess() {
        return hasObjectAccess;
    }

    public boolean hasCustomAttributes() {
        return hasCustomAttributes;
    }

    public boolean hasGroups() {
        return hasGroups;
    }

    public boolean hasOwner() {
        return hasOwner;
    }

    public boolean complianceSubject() {
        return complianceSubjects.contains(this);
    }

    public boolean hasComplianceProfiles() {
        return complianceProfilesAssignable.contains(this);
    }

    public boolean hasApprovalProfiles() {
        return approvalProfilesAssignable.contains(this);
    }

    @JsonCreator
    public static Resource findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown Resource Name {}", code)));
    }

    public static List<Resource> getCustomAttributesResources() {
        return Arrays.stream(VALUES).filter(k -> k.hasCustomAttributes).toList();
    }

    public static class Codes {
        public static final String AUTHORITY = "authorities";
        public static final String RA_PROFILE = "raProfiles";
        public static final String CERTIFICATE = "certificates";
        public static final String CERTIFICATE_REQUEST = "certificateRequests";
        public static final String GROUP = "groups";
        public static final String DISCOVERY = "discoveries";
        public static final String ACME_PROFILE = "acmeProfiles";
        public static final String ATTRIBUTE = "attributes";
        public static final String CMP_PROFILE = "cmpProfiles";
        public static final String COMPLIANCE_PROFILE = "complianceProfiles";
        public static final String CONNECTOR = "connectors";
        public static final String CREDENTIAL = "credentials";
        public static final String CRYPTOGRAPHIC_KEY = "keys";
        public static final String ENTITY = "entities";
        public static final String LOCATION = "locations";
        public static final String ROLE = "roles";
        public static final String SCEP_PROFILE = "scepProfiles";
        public static final String TOKEN = "tokens";
        public static final String TOKEN_PROFILE = "tokenProfiles";
        public static final String USER = "users";
        public static final String OID = "oids";
        public static final String ACME_ACCOUNT = "acmeAccounts";
        public static final String VAULT = "vaults";
        public static final String VAULT_PROFILE = "vaultProfiles";
        public static final String SECRET = "secrets";
        public static final String CBOM = "cboms";
        public static final String DIGITAL_SIGNATURE = "digitalSignatures";
        public static final String SIGNING_PROFILE = "signingProfiles";
        public static final String TIME_QUALITY_CONFIGURATION = "timeQualityConfigurations";
        public static final String TSP_PROFILE = "tspProfiles";

        private Codes() {

        }
    }
}
