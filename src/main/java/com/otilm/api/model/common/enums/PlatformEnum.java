package com.otilm.api.model.common.enums;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.approval.ApprovalStatusEnum;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.client.connector.v2.ConnectorVersion;
import com.otilm.api.model.client.connector.v2.FeatureFlag;
import com.otilm.api.model.client.cryptography.key.KeyCompromiseReason;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.HealthStatus;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.callback.AttributeValueTarget;
import com.otilm.api.model.common.attribute.common.constraint.AttributeConstraintType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.ProgrammingLanguageEnum;
import com.otilm.api.model.common.attribute.common.content.data.ProtectionLevel;
import com.otilm.api.model.common.enums.cryptography.*;
import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import com.otilm.api.model.connector.secrets.SecretType;
import com.otilm.api.model.connector.secrets.content.KeyStoreType;
import com.otilm.api.model.core.acme.AccountStatus;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.authority.CertificateRevocationReason;
import com.otilm.api.model.core.certificate.*;
import com.otilm.api.model.core.cmp.CmpProfileVariant;
import com.otilm.api.model.core.cmp.ProtectionMethod;
import com.otilm.api.model.core.compliance.ComplianceRuleStatus;
import com.otilm.api.model.core.compliance.ComplianceStatus;
import com.otilm.api.model.core.connector.AuthType;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.model.core.connector.FunctionGroupCode;
import com.otilm.api.model.core.cryptography.key.KeyState;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import com.otilm.api.model.core.discovery.DiscoveryStatus;
import com.otilm.api.model.core.enums.CertificateRequestFormat;
import com.otilm.api.model.core.enums.CertificateProtocol;
import com.otilm.api.model.core.logging.enums.*;
import com.otilm.api.model.core.logging.enums.Module;
import com.otilm.api.model.core.notification.RecipientType;
import com.otilm.api.model.core.oid.OidCategory;
import com.otilm.api.model.core.other.ResourceEvent;
import com.otilm.api.model.core.secret.SecretState;
import com.otilm.api.model.core.workflows.ConditionType;
import com.otilm.api.model.core.workflows.ExecutionType;
import com.otilm.api.model.core.workflows.TriggerType;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.model.core.search.FilterConditionOperator;
import com.otilm.api.model.core.search.FilterFieldType;
import com.otilm.api.model.core.settings.SettingsSection;
import com.otilm.api.model.scheduler.SchedulerJobExecutionStatus;
import com.otilm.core.model.auth.ResourceAction;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum PlatformEnum implements IPlatformEnum {
    // general
    RESOURCE(Resource.class, "Platform resource"),
    RESOURCE_ACTION(ResourceAction.class, "Actions for a Resource"),
    FILTER_CONDITION_OPERATOR(FilterConditionOperator.class, "Filter condition operator"),
    FILTER_FIELD_TYPE(FilterFieldType.class, "Filter field type"),
    FILTER_FIELD_SOURCE(FilterFieldSource.class, "Filter field source"),
    SETTINGS_SECTION(SettingsSection.class, "Settings section"),

    // connectors
    AUTH_TYPE(AuthType.class, "Authentication type"),
    HEALTH_STATUS(HealthStatus.class, "Health status"),
    CONNECTOR_STATUS(ConnectorStatus.class, "Connector status"),
    CONNECTOR_FUNCTION_GROUP(FunctionGroupCode.class, "Connector function group"),
    CONNECTOR_VERSION(ConnectorVersion.class, "Connector version"),
    CONNECTOR_INTERFACE(ConnectorInterface.class, "Connector interface"),
    CONNECTOR_INTERFACE_FEATURE_FLAG(FeatureFlag.class, "Connector interface feature flag"),

    // certificates
    CERTIFICATE_TYPE(CertificateType.class, "Certificate type"),
    CERTIFICATE_STATE(CertificateState.class, "Certificate state"),
    CERTIFICATE_VALIDATION_STATUS(CertificateValidationStatus.class, "Certificate validation status"),
    CERTIFICATE_FORMAT(CertificateFormat.class, "Certificate format"),
    CERTIFICATE_FORMAT_ENCODING(CertificateFormatEncoding.class, "Certificate format encoding"),
    CERTIFICATE_VALIDATION_CHECK(CertificateValidationCheck.class, "Certificate validation check type"),
    CERTIFICATE_REVOCATION_REASON(CertificateRevocationReason.class, "Certificate revocation reason"),
    CERTIFICATE_REQUEST_FORMAT(CertificateRequestFormat.class, "Certificate request format"),
    DISCOVERY_STATUS(DiscoveryStatus.class, "Discovery status"),
    PROTOCOL(CertificateProtocol.class, "Protocol used to issue certificate"),
    CERTIFICATE_SUBJECT_TYPE(CertificateSubjectType.class, "Subject Type of certificate"),
    CERTIFICATE_KEY_USAGE(CertificateKeyUsage.class, "Key Usage of Certificate"),
    CERTIFICATE_RELATION_TYPE(CertificateRelationType.class, "Type of relation between successor and predecessor certificate"),

    // oid
    OID_CATEGORY(OidCategory.class, "OID Category"),

    // keys & tokens
    KEY_ALGORITHM(KeyAlgorithm.class, "Cryptographic key algorithm"),
    KEY_FORMAT(KeyFormat.class, "Key format type"),
    KEY_STATE(KeyState.class, "Key state"),
    KEY_TYPE(KeyType.class, "Key type"),
    KEY_USAGE(KeyUsage.class, "Key usage"),
    KEY_REQUEST_TYPE(KeyRequestType.class, "Key request type"),
    KEY_COMPROMISE_REASON(KeyCompromiseReason.class, "Key compromise reason"),
    TOKEN_STATUS(TokenInstanceStatus.class, "Token instance status"),
    DIGEST_ALGORITHM(DigestAlgorithm.class, "Digest algorithm"),
    RSA_SIGNATURE_SCHEME(RsaSignatureScheme.class, "RSA signature scheme"),
    RSA_ENCRYPTION_SCHEME(RsaEncryptionScheme.class, "RSA encryption scheme"),

    // compliance
    COMPLIANCE_STATUS(ComplianceStatus.class, "Compliance status"),
    COMPLIANCE_RULE_STATUS(ComplianceRuleStatus.class, "Compliance rule status"),

    // ACME
    ACME_ACCOUNT_STATUS(AccountStatus.class, "ACME Account status"), // TODO: rename to AcmeAccountStatus

    // CMP
    CMP_PROTECTION_METHOD(ProtectionMethod.class, "CMP protection method"),
    CMP_PROFILE_VARIANT(CmpProfileVariant.class, "CMP protocol variant"),

    // Attributes
    ATTRIBUTE_TYPE(AttributeType.class, "Attribute Type"),
    ATTRIBUTE_CONTENT_TYPE(AttributeContentType.class, "Attribute content type"),
    ATTRIBUTE_CONSTRAINT_TYPE(AttributeConstraintType.class, "Attribute constraint type"),
    ATTRIBUTE_CALLBACK_VALUE_TARGET(AttributeValueTarget.class, "Attribute callback mapping value target"), // TODO: rename to AttributeCallbackValueTarget
    PROGRAMMING_LANGUAGE(ProgrammingLanguageEnum.class, "Programming language for code block attribute"),
    ATTRIBUTE_VERSION(AttributeVersion.class, "Version of attribute"),
    PROTECTION_LEVEL(ProtectionLevel.class, "Protection level of attribute content"),

    // Scheduler
    SCHEDULER_JOB_EXECUTION_STATUS(SchedulerJobExecutionStatus.class, "Scheduled job execution status"),

    // notifications
    RECIPIENT_TYPE(RecipientType.class, "Recipient type"),

    // workflows
    TRIGGER_TYPE(TriggerType.class, "Trigger Type"),
    CONDITION_TYPE(ConditionType.class, "Condition Type"),
    EXECUTION_TYPE(ExecutionType.class, "Execution Type"),
    RESOURCE_EVENT(ResourceEvent.class, "Events available for a resource"),

    // logging
    MODULE(Module.class, "CZERTAINLY platform module"),
    ACTOR_TYPE(ActorType.class, "Actor type"),
    AUTH_METHOD(AuthMethod.class, "Actor auth method"),
    OPERATION(Operation.class, "Log operation"),
    OPERATION_RESULT(OperationResult.class, "Log operation result"),

    // Approval
    APPROVAL_STATUS(ApprovalStatusEnum.class, "Approval Status"),

    // Secrets
    SECRET_TYPE(SecretType.class, "Secret Type"),
    KEY_STORE_TYPE(KeyStoreType.class, "Key Store Type"),
    SECRET_STATE(SecretState.class, "Secret State"),

    ;

    private static final PlatformEnum[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;

    private final Class<? extends IPlatformEnum> enumClass;

    PlatformEnum(Class<? extends IPlatformEnum> enumClass, String label) {
        this(enumClass, label, null);
    }

    PlatformEnum(Class<? extends IPlatformEnum> enumClass, String label, String description) {
        this.enumClass = enumClass;
        this.code = enumClass.getSimpleName();
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

    public Class<? extends IPlatformEnum> getEnumClass() {
        return enumClass;
    }

    @JsonCreator
    public static PlatformEnum findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown platform enum {}", code)));
    }

    public static PlatformEnum findByClass(Class clazz) {
        return Arrays.stream(VALUES)
                .filter(e -> e.enumClass.equals(clazz))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Unknown platform enum for class %s.", clazz)));
    }
}
