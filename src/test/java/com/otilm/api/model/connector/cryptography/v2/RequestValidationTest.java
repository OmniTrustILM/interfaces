package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyAttributesRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.DestroyKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.CipherDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.RandomDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.VerifyDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.CipherDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.token.TokenScopedRequestV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class RequestValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @ParameterizedTest(name = "{0}")
    @MethodSource("validRequests")
    void validate_hasNoViolations_forValidRequestContract(Object request) {
        // given
        Object validRequest = request;

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(validRequest);

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<Object>> validRequests() {
        return Stream
                .of(named("token scope", validTokenScope()), named("token-profile scope", validTokenProfileScope()),
                        named("key scope", validKeyScope()),
                        named("create-key attributes", validCreateKeyAttributesRequest()),
                        named("create key", validCreateKeyRequest()), named("destroy key", validDestroyKeyRequest()),
                        named("operation status/cancel", validOperationTrackingRequest()),
                        named("cipher operation", validCipherRequest()),
                        named("random operation", validRandomRequest()), named("sign operation", validSignRequest()),
                        named("verify operation", validVerifyRequest()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("missingRequiredFields")
    void validate_hasExpectedViolation_forMissingRequiredRequestField(InvalidRequest invalidRequest) {
        // given
        Object requestMissingRequiredField = invalidRequest.request();

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(requestMissingRequiredField);

        // then
        assertHasViolation(violations, invalidRequest.path(), invalidRequest.message());
    }

    static Stream<Named<InvalidRequest>> missingRequiredFields() {
        return Stream
                .of(invalidRequest("token attributes", validTokenScope(), TokenScopedRequestV2Dto::setTokenAttributes,
                        "tokenAttributes", "tokenAttributes is required (may be empty list, but must be present)"),
                        invalidRequest("token-profile attributes", validTokenProfileScope(),
                                TokenProfileScopedRequestV2Dto::setTokenProfileAttributes, "tokenProfileAttributes",
                                "tokenProfileAttributes is required (may be empty list, but must be present)"),
                        invalidRequest("key usages", validTokenProfileScope(),
                                TokenProfileScopedRequestV2Dto::setKeyUsages, "keyUsages",
                                "keyUsages must contain at least one usage"),
                        invalidRequest("attributes key request type", validCreateKeyAttributesRequest(),
                                CreateKeyAttributesRequestV2Dto::setKeyRequestType, "keyRequestType",
                                "keyRequestType is required"),
                        invalidRequest("key metadata", validKeyScope(), KeyScopedRequestV2Dto::setKeyMeta, "keyMeta",
                                "keyMeta is required and must not be empty"),
                        invalidRequest("key request type", validCreateKeyRequest(),
                                CreateKeyRequestV2Dto::setKeyRequestType, "keyRequestType",
                                "keyRequestType is required"),
                        invalidRequest("execution mode", validCreateKeyRequest(),
                                CreateKeyRequestV2Dto::setExecutionMode, "executionMode", "executionMode is required"),
                        invalidRequest("creation attributes", validCreateKeyRequest(),
                                CreateKeyRequestV2Dto::setCreateKeyAttributes, "createKeyAttributes",
                                "createKeyAttributes is required (may be empty list, but must be present)"),
                        invalidRequest("creation id", validCreateKeyRequest(), CreateKeyRequestV2Dto::setKeyCreationId,
                                "keyCreationId", "keyCreationId is required"),
                        invalidRequest("destroy execution mode", validDestroyKeyRequest(),
                                DestroyKeyRequestV2Dto::setExecutionMode, "executionMode", "executionMode is required"),
                        invalidRequest("operation metadata", validOperationTrackingRequest(),
                                OperationTrackingRequestV2Dto::setOperationMeta, "operationMeta",
                                "operationMeta is required and must not be empty"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRequestCollections")
    void validate_hasExpectedViolation_forInvalidRequestCollection(InvalidRequest invalidRequest) {
        // given
        Object requestWithInvalidCollection = invalidRequest.request();

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(requestWithInvalidCollection);

        // then
        assertHasViolation(violations, invalidRequest.path(), invalidRequest.message());
    }

    static Stream<Named<InvalidRequest>> invalidRequestCollections() {
        TokenScopedRequestV2Dto nullTokenAttribute = validTokenScope();
        nullTokenAttribute.setTokenAttributes(Collections.singletonList(null));
        TokenProfileScopedRequestV2Dto emptyKeyUsages = validTokenProfileScope();
        emptyKeyUsages.setKeyUsages(Set.of());
        TokenProfileScopedRequestV2Dto nullTokenProfileAttribute = validTokenProfileScope();
        nullTokenProfileAttribute.setTokenProfileAttributes(Collections.singletonList(null));
        TokenProfileScopedRequestV2Dto nullKeyUsage = validTokenProfileScope();
        nullKeyUsage.setKeyUsages(Collections.singleton(null));
        KeyScopedRequestV2Dto emptyKeyMetadata = validKeyScope();
        emptyKeyMetadata.setKeyMeta(List.of());
        KeyScopedRequestV2Dto nullKeyMetadata = validKeyScope();
        nullKeyMetadata.setKeyMeta(Collections.singletonList(null));
        OperationTrackingRequestV2Dto emptyOperationMetadata = validOperationTrackingRequest();
        emptyOperationMetadata.setOperationMeta(List.of());
        OperationTrackingRequestV2Dto nullOperationMetadata = validOperationTrackingRequest();
        nullOperationMetadata.setOperationMeta(Collections.singletonList(null));
        CreateKeyRequestV2Dto nullCreationAttribute = validCreateKeyRequest();
        nullCreationAttribute.setCreateKeyAttributes(Collections.singletonList(null));

        return Stream
                .of(named("null token attribute element",
                        new InvalidRequest(nullTokenAttribute, "tokenAttributes[0].<list element>",
                                "tokenAttributes must not contain null entries")),
                        named("empty key usages",
                                new InvalidRequest(emptyKeyUsages, "keyUsages",
                                        "keyUsages must contain at least one usage")),
                        named("null token-profile attribute element",
                                new InvalidRequest(nullTokenProfileAttribute,
                                        "tokenProfileAttributes[0].<list element>",
                                        "tokenProfileAttributes must not contain null entries")),
                        named("null key usage element",
                                new InvalidRequest(nullKeyUsage, "keyUsages[].<iterable element>",
                                        "keyUsages must not contain null entries")),
                        named("empty key metadata",
                                new InvalidRequest(emptyKeyMetadata, "keyMeta",
                                        "keyMeta is required and must not be empty")),
                        named("null key metadata element",
                                new InvalidRequest(nullKeyMetadata, "keyMeta[0].<list element>", "must not be null")),
                        named("empty operation metadata",
                                new InvalidRequest(emptyOperationMetadata, "operationMeta",
                                        "operationMeta is required and must not be empty")),
                        named("null operation metadata element",
                                new InvalidRequest(nullOperationMetadata, "operationMeta[0].<list element>",
                                        "operationMeta must not contain null items")),
                        named("null creation attribute element",
                                new InvalidRequest(nullCreationAttribute, "createKeyAttributes[0].<list element>",
                                        "createKeyAttributes must not contain null entries")));
    }

    @Test
    void validate_hasNoViolations_forCollectionsAllowedToBeEmpty() {
        // given
        CreateKeyRequestV2Dto requestWithEmptyAttributeLists = validCreateKeyRequest();
        requestWithEmptyAttributeLists.setTokenAttributes(List.of());
        requestWithEmptyAttributeLists.setTokenProfileAttributes(List.of());
        requestWithEmptyAttributeLists.setCreateKeyAttributes(List.of());

        // when
        Set<ConstraintViolation<CreateKeyRequestV2Dto>> violations = VALIDATOR.validate(requestWithEmptyAttributeLists);

        // then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCreationIds")
    void validate_hasExpectedViolations_forInvalidKeyCreationId(String invalidId, Set<String> expectedMessages) {
        // given
        String property = "keyCreationId";
        CreateKeyRequestV2Dto request = validCreateKeyRequest();
        request.setKeyCreationId(invalidId);

        // when
        Set<String> actualMessages = VALIDATOR
                .validate(request)
                .stream()
                .filter(violation -> property.equals(violation.getPropertyPath().toString()))
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toSet());

        // then
        assertEquals(expectedMessages, actualMessages);
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> invalidCreationIds() {
        return Stream
                .of(org.junit.jupiter.params.provider.Arguments
                        .arguments(named("empty", ""),
                                Set
                                        .of("keyCreationId is required",
                                                "keyCreationId must contain between 1 and 256 characters")),
                        org.junit.jupiter.params.provider.Arguments
                                .arguments(named("blank", "   "), Set.of("keyCreationId is required")),
                        org.junit.jupiter.params.provider.Arguments
                                .arguments(named("257 characters", "k".repeat(257)),
                                        Set.of("keyCreationId must contain between 1 and 256 characters")));
    }

    @Test
    void validate_hasNoViolations_forMaximumLengthKeyCreationId() {
        // given
        String maximumLengthCreationId = "k".repeat(256);
        CreateKeyRequestV2Dto request = validCreateKeyRequest();
        request.setKeyCreationId(maximumLengthCreationId);

        // when
        Set<ConstraintViolation<CreateKeyRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestsWithInvalidKeyMetadata")
    void validate_cascadesKeyMetadataThroughConcretePublicRequest(KeyScopedRequestV2Dto request) {
        // given
        MetadataAttributeV2 metadataWithoutName = validMetadataAttribute();
        metadataWithoutName.setName(null);
        request.setKeyMeta(List.of(metadataWithoutName));

        // when
        Set<ConstraintViolation<KeyScopedRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, "keyMeta[0].<list element>.name", "name must not be blank");
    }

    static Stream<Named<KeyScopedRequestV2Dto>> requestsWithInvalidKeyMetadata() {
        return Stream
                .of(named("destroy key", validDestroyKeyRequest()), named("cipher", validCipherRequest()),
                        named("sign", validSignRequest()), named("verify", validVerifyRequest()));
    }

    private static TokenScopedRequestV2Dto validTokenScope() {
        return withValidTokenScope(new TokenScopedRequestV2Dto());
    }

    private static TokenProfileScopedRequestV2Dto validTokenProfileScope() {
        return withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto());
    }

    private static KeyScopedRequestV2Dto validKeyScope() {
        KeyScopedRequestV2Dto request = withValidTokenProfileScope(new KeyScopedRequestV2Dto());
        request.setKeyMeta(validMetadata());
        return request;
    }

    private static CreateKeyAttributesRequestV2Dto validCreateKeyAttributesRequest() {
        CreateKeyAttributesRequestV2Dto request = withValidTokenProfileScope(new CreateKeyAttributesRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.SECRET);
        return request;
    }

    private static CreateKeyRequestV2Dto validCreateKeyRequest() {
        CreateKeyRequestV2Dto request = withValidTokenProfileScope(new CreateKeyRequestV2Dto());
        request.setKeyRequestType(KeyRequestType.SECRET);
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        request.setKeyCreationId("key-creation-1");
        request.setCreateKeyAttributes(List.of());
        return request;
    }

    private static DestroyKeyRequestV2Dto validDestroyKeyRequest() {
        DestroyKeyRequestV2Dto request = withValidTokenProfileScope(new DestroyKeyRequestV2Dto());
        request.setKeyMeta(validMetadata());
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        return request;
    }

    private static OperationTrackingRequestV2Dto validOperationTrackingRequest() {
        OperationTrackingRequestV2Dto request = new OperationTrackingRequestV2Dto();
        request.setOperationMeta(validMetadata());
        return request;
    }

    private static CipherDataRequestV2Dto validCipherRequest() {
        CipherDataRequestV2Dto request = withValidKeyScope(new CipherDataRequestV2Dto());
        request.setCipherAttributes(List.of());
        request.setCipherData(List.of(new CipherDataV2Dto(new byte[]{1}, "item-1")));
        return request;
    }

    private static RandomDataRequestV2Dto validRandomRequest() {
        RandomDataRequestV2Dto request = withValidTokenProfileScope(new RandomDataRequestV2Dto());
        request.setLength(1);
        request.setOperationAttributes(List.of());
        return request;
    }

    private static SignDataRequestV2Dto validSignRequest() {
        SignDataRequestV2Dto request = withValidKeyScope(new SignDataRequestV2Dto());
        request.setExecutionMode(OperationExecutionMode.SYNCHRONOUS);
        request.setSignatureAttributes(List.of());
        request.setData(List.of(new SignatureDataV2Dto(new byte[]{1}, "item-1")));
        return request;
    }

    private static VerifyDataRequestV2Dto validVerifyRequest() {
        String matchingIdentifier = "item-1";
        VerifyDataRequestV2Dto request = withValidKeyScope(new VerifyDataRequestV2Dto());
        request.setSignatureAttributes(List.of());
        request.setData(List.of(new SignatureDataV2Dto(new byte[]{1}, matchingIdentifier)));
        request.setSignatures(List.of(new SignatureDataV2Dto(new byte[]{2}, matchingIdentifier)));
        return request;
    }

    private static <T extends KeyScopedRequestV2Dto> T withValidKeyScope(T request) {
        withValidTokenProfileScope(request);
        request.setKeyMeta(validMetadata());
        return request;
    }

    private static <T, V> Named<InvalidRequest> invalidRequest(String name, T request, BiConsumer<T, V> clearField,
            String path, String message) {
        clearField.accept(request, null);
        return named(name, new InvalidRequest(request, path, message));
    }

    private static void assertHasViolation(Set<? extends ConstraintViolation<?>> violations, String path,
            String message) {
        assertTrue(
                violations
                        .stream()
                        .anyMatch(violation -> violation.getPropertyPath().toString().equals(path)
                                && violation.getMessage().equals(message)),
                () -> "Expected " + path + ": " + message + ", got "
                        + violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).toList());
    }

    private record InvalidRequest(Object request, String path, String message) {
    }
}
