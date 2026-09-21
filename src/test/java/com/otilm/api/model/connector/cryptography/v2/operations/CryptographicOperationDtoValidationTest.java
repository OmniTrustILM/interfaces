package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import com.otilm.api.model.connector.common.v2.validation.AsynchronousResponse;
import com.otilm.api.model.connector.common.v2.validation.SynchronousResponse;
import com.otilm.api.model.connector.cryptography.v2.KeyScopedRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.OperationTrackingRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.CipherDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureResultItemV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.data.VerificationResponseItemV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadataAttribute;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class CryptographicOperationDtoValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void cipherRequest_validateAcceptsEmptyAttributesAndValidUniqueItems() {
        // given
        CipherDataRequestV2Dto request = validCipherRequest();

        // when
        Set<ConstraintViolation<CipherDataRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCipherRequests")
    void cipherRequest_validateRejectsInvalidAttributesItemsAndIdentifiers(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidCipherRequests() {
        CipherDataRequestV2Dto missingAttributes = validCipherRequest();
        missingAttributes.setCipherAttributes(null);
        CipherDataRequestV2Dto nullAttribute = validCipherRequest();
        nullAttribute.setCipherAttributes(Collections.singletonList(null));
        CipherDataRequestV2Dto missingItems = validCipherRequest();
        missingItems.setCipherData(null);
        CipherDataRequestV2Dto emptyItems = validCipherRequest();
        emptyItems.setCipherData(List.of());
        CipherDataRequestV2Dto nullItem = validCipherRequest();
        nullItem.setCipherData(Collections.singletonList(null));
        CipherDataRequestV2Dto invalidItem = validCipherRequest();
        invalidItem.setCipherData(List.of(new CipherDataV2Dto(new byte[0], "item-1")));
        CipherDataRequestV2Dto duplicateIdentifiers = validCipherRequest();
        duplicateIdentifiers.setCipherData(List.of(cipherItem("duplicate"), cipherItem("duplicate")));

        return Stream
                .of(invalid("missing attributes", missingAttributes, "cipherAttributes",
                        "cipherAttributes is required (may be empty list, but must be present)"),
                        invalid("null attribute", nullAttribute, "cipherAttributes[0].<list element>",
                                "cipherAttributes must not contain null items"),
                        invalid("missing items", missingItems, "cipherData",
                                "cipherData must contain at least one item"),
                        invalid("empty items", emptyItems, "cipherData", "cipherData must contain at least one item"),
                        invalid("null item", nullItem, "cipherData[0].<list element>",
                                "cipherData must not contain null items"),
                        invalid("invalid nested item", invalidItem, "cipherData[0].data",
                                "data is required and must not be empty"),
                        invalid("duplicate identifier", duplicateIdentifiers, "cipherData[1].identifier",
                                "identifiers must be unique within the batch; duplicates item at index 0"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validCipherResponses")
    void cipherResponse_validateAcceptsValidUniqueItems(Object response) {
        // given
        Object validResponse = response;

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(validResponse);

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<Object>> validCipherResponses() {
        return Stream.of(named("encrypt", validEncryptResponse()), named("decrypt", validDecryptResponse()));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidCipherResponses")
    void cipherResponse_validateRejectsInvalidItemsAndIdentifiers(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidCipherResponses() {
        EncryptDataResponseV2Dto emptyEncrypt = validEncryptResponse();
        emptyEncrypt.setEncryptedData(List.of());
        EncryptDataResponseV2Dto nullEncryptItem = validEncryptResponse();
        nullEncryptItem.setEncryptedData(Collections.singletonList(null));
        EncryptDataResponseV2Dto invalidEncryptItem = validEncryptResponse();
        invalidEncryptItem.setEncryptedData(List.of(new CipherDataV2Dto(null, "item-1")));
        EncryptDataResponseV2Dto duplicateEncrypt = validEncryptResponse();
        duplicateEncrypt.setEncryptedData(List.of(cipherItem("duplicate"), cipherItem("duplicate")));
        DecryptDataResponseV2Dto emptyDecrypt = validDecryptResponse();
        emptyDecrypt.setDecryptedData(List.of());
        DecryptDataResponseV2Dto nullDecryptItem = validDecryptResponse();
        nullDecryptItem.setDecryptedData(Collections.singletonList(null));
        DecryptDataResponseV2Dto invalidDecryptItem = validDecryptResponse();
        invalidDecryptItem.setDecryptedData(List.of(new CipherDataV2Dto(null, "item-1")));
        DecryptDataResponseV2Dto duplicateDecrypt = validDecryptResponse();
        duplicateDecrypt.setDecryptedData(List.of(cipherItem("duplicate"), cipherItem("duplicate")));

        return Stream
                .of(invalid("encrypt empty", emptyEncrypt, "encryptedData",
                        "encryptedData must contain at least one item"),
                        invalid("encrypt null item", nullEncryptItem, "encryptedData[0].<list element>",
                                "encryptedData must not contain null items"),
                        invalid("encrypt invalid item", invalidEncryptItem, "encryptedData[0].data",
                                "data is required and must not be empty"),
                        invalid("encrypt duplicate", duplicateEncrypt, "encryptedData[1].identifier",
                                "identifiers must be unique within the batch; duplicates item at index 0"),
                        invalid("decrypt empty", emptyDecrypt, "decryptedData",
                                "decryptedData must contain at least one item"),
                        invalid("decrypt null item", nullDecryptItem, "decryptedData[0].<list element>",
                                "decryptedData must not contain null items"),
                        invalid("decrypt invalid item", invalidDecryptItem, "decryptedData[0].data",
                                "data is required and must not be empty"),
                        invalid("decrypt duplicate", duplicateDecrypt, "decryptedData[1].identifier",
                                "identifiers must be unique within the batch; duplicates item at index 0"));
    }

    @ParameterizedTest(name = "length {0}")
    @MethodSource("validRandomLengths")
    void randomRequest_validateAcceptsBoundaryLength(int validLength) {
        // given
        RandomDataRequestV2Dto request = validRandomRequest();
        request.setLength(validLength);

        // when
        Set<ConstraintViolation<RandomDataRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Integer> validRandomLengths() {
        return Stream.of(1, RandomDataRequestV2Dto.MAX_LENGTH);
    }

    @ParameterizedTest(name = "length {0}")
    @MethodSource("invalidRandomLengths")
    void randomRequest_validateRejectsOutOfRangeLength(int invalidLength) {
        // given
        RandomDataRequestV2Dto request = validRandomRequest();
        request.setLength(invalidLength);

        // when
        Set<ConstraintViolation<RandomDataRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.stream().anyMatch(violation -> violation.getPropertyPath().toString().equals("length")));
    }

    static Stream<Integer> invalidRandomLengths() {
        return Stream.of(-1, 0, RandomDataRequestV2Dto.MAX_LENGTH + 1);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRandomAttributes")
    void randomRequest_validateRejectsInvalidAttributeCollection(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidRandomAttributes() {
        RandomDataRequestV2Dto missingAttributes = validRandomRequest();
        missingAttributes.setOperationAttributes(null);
        RandomDataRequestV2Dto nullAttribute = validRandomRequest();
        nullAttribute.setOperationAttributes(Collections.singletonList(null));
        return Stream
                .of(invalid("missing attributes", missingAttributes, "operationAttributes",
                        "operationAttributes is required (may be empty list, but must be present)"),
                        invalid("null attribute", nullAttribute, "operationAttributes[0].<list element>",
                                "operationAttributes must not contain null entries"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSignRequests")
    void signRequest_validateRejectsInvalidModeAttributesItemsAndIdentifiers(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidSignRequests() {
        SignDataRequestV2Dto missingMode = validSignRequest();
        missingMode.setExecutionMode(null);
        SignDataRequestV2Dto missingAttributes = validSignRequest();
        missingAttributes.setSignatureAttributes(null);
        SignDataRequestV2Dto nullAttribute = validSignRequest();
        nullAttribute.setSignatureAttributes(Collections.singletonList(null));
        SignDataRequestV2Dto emptyData = validSignRequest();
        emptyData.setData(List.of());
        SignDataRequestV2Dto nullItem = validSignRequest();
        nullItem.setData(Collections.singletonList(null));
        SignDataRequestV2Dto invalidItem = validSignRequest();
        invalidItem.setData(List.of(new SignatureDataV2Dto(new byte[0], "item-1")));
        SignDataRequestV2Dto duplicate = validSignRequest();
        duplicate.setData(List.of(signatureItem("duplicate"), signatureItem("duplicate")));
        return Stream
                .of(invalid("missing mode", missingMode, "executionMode", "executionMode is required"),
                        invalid("missing attributes", missingAttributes, "signatureAttributes",
                                "signatureAttributes is required (may be empty list, but must be present)"),
                        invalid("null attribute", nullAttribute, "signatureAttributes[0].<list element>",
                                "signatureAttributes must not contain null items"),
                        invalid("empty data", emptyData, "data", "data must contain at least one item"),
                        invalid("null item", nullItem, "data[0].<list element>", "data must not contain null items"),
                        invalid("invalid nested item", invalidItem, "data[0].data",
                                "data is required and must not be empty"),
                        invalid("duplicate identifier", duplicate, "data[1].identifier",
                                "identifiers must be unique within the batch; duplicates item at index 0"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validSignResponses")
    void signResponse_validateAcceptsExecutionModeShape(GroupedDto grouped) {
        // given
        SignDataResponseV2Dto response = (SignDataResponseV2Dto) grouped.dto();

        // when
        Set<ConstraintViolation<SignDataResponseV2Dto>> violations = VALIDATOR.validate(response, grouped.group());

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<GroupedDto>> validSignResponses() {
        return Stream
                .of(named("synchronous", new GroupedDto(validSynchronousSignResponse(), SynchronousResponse.class)),
                        named("asynchronous",
                                new GroupedDto(validAsynchronousSignResponse(), AsynchronousResponse.class)));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSignResponses")
    void signResponse_validateRejectsContradictoryOrInvalidExecutionModeShape(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidSignResponses() {
        SignDataResponseV2Dto synchronousWithoutSignatures = validSynchronousSignResponse();
        synchronousWithoutSignatures.setSignatures(null);
        SignDataResponseV2Dto synchronousWithTrackingMeta = validSynchronousSignResponse();
        synchronousWithTrackingMeta.setOperationMeta(validMetadata());
        SignDataResponseV2Dto synchronousNullItem = validSynchronousSignResponse();
        synchronousNullItem.setSignatures(Collections.singletonList(null));
        SignDataResponseV2Dto synchronousDuplicate = validSynchronousSignResponse();
        synchronousDuplicate.setSignatures(List.of(signatureItem("duplicate"), signatureItem("duplicate")));
        SignDataResponseV2Dto asynchronousWithSignatures = validAsynchronousSignResponse();
        asynchronousWithSignatures.setSignatures(List.of(signatureItem("item-1")));
        SignDataResponseV2Dto asynchronousWithoutTrackingMeta = validAsynchronousSignResponse();
        asynchronousWithoutTrackingMeta.setOperationMeta(null);
        SignDataResponseV2Dto asynchronousNullMeta = validAsynchronousSignResponse();
        asynchronousNullMeta.setOperationMeta(Collections.singletonList(null));
        SignDataResponseV2Dto asynchronousInvalidMeta = validAsynchronousSignResponse();
        MetadataAttributeV2 metadataWithoutName = validMetadataAttribute();
        metadataWithoutName.setName(null);
        asynchronousInvalidMeta.setOperationMeta(List.of(metadataWithoutName));
        return Stream
                .of(invalid("sync missing signatures", synchronousWithoutSignatures, SynchronousResponse.class,
                        "signatures", "signatures must contain at least one item for synchronous execution"),
                        invalid("sync has tracking metadata", synchronousWithTrackingMeta, SynchronousResponse.class,
                                "operationMeta", "operationMeta must be absent for synchronous execution"),
                        invalid("sync null item", synchronousNullItem, SynchronousResponse.class,
                                "signatures[0].<list element>", "signatures must not contain null items"),
                        invalid("sync duplicate", synchronousDuplicate, SynchronousResponse.class,
                                "signatures[1].identifier",
                                "identifiers must be unique within the batch; duplicates item at index 0"),
                        invalid("async has signatures", asynchronousWithSignatures, AsynchronousResponse.class,
                                "signatures", "signatures must be absent for asynchronous execution"),
                        invalid("async missing tracking metadata", asynchronousWithoutTrackingMeta,
                                AsynchronousResponse.class, "operationMeta",
                                "operationMeta must contain at least one item for asynchronous execution"),
                        invalid("async null metadata", asynchronousNullMeta, AsynchronousResponse.class,
                                "operationMeta[0].<list element>", "operationMeta must not contain null items"),
                        invalid("async invalid metadata", asynchronousInvalidMeta, AsynchronousResponse.class,
                                "operationMeta[0].<list element>.name", "name must not be blank"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSignOperationRequests")
    void signOperationRequest_validateRejectsInvalidTrackingHandle(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidSignOperationRequests() {
        OperationTrackingRequestV2Dto emptyMeta = validSignOperationRequest();
        emptyMeta.setOperationMeta(List.of());
        OperationTrackingRequestV2Dto nullItem = validSignOperationRequest();
        nullItem.setOperationMeta(Collections.singletonList(null));
        OperationTrackingRequestV2Dto invalidMeta = validSignOperationRequest();
        MetadataAttributeV2 metadataWithoutName = validMetadataAttribute();
        metadataWithoutName.setName(null);
        invalidMeta.setOperationMeta(List.of(metadataWithoutName));
        return Stream
                .of(invalid("empty metadata", emptyMeta, "operationMeta",
                        "operationMeta is required and must not be empty"),
                        invalid("null metadata item", nullItem, "operationMeta[0].<list element>",
                                "operationMeta must not contain null items"),
                        invalid("invalid metadata item", invalidMeta, "operationMeta[0].<list element>.name",
                                "name must not be blank"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSignStatusResponses")
    void signStatusResponse_validateRejectsInvalidItemBatch(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidSignStatusResponses() {
        SignOperationStatusResponseV2Dto empty = validSignStatusResponse();
        empty.setItems(List.of());
        SignOperationStatusResponseV2Dto nullItem = validSignStatusResponse();
        nullItem.setItems(Collections.singletonList(null));
        SignOperationStatusResponseV2Dto invalidItem = validSignStatusResponse();
        invalidItem.setItems(List.of(new SignatureResultItemV2Dto("item-1", OperationStatus.COMPLETED, null, null)));
        SignOperationStatusResponseV2Dto duplicate = validSignStatusResponse();
        duplicate.setItems(List.of(signResult("duplicate"), signResult("duplicate")));
        return Stream
                .of(invalid("empty", empty, "items", "items must contain at least one item"),
                        invalid("null item", nullItem, "items[0].<list element>",
                                "items must not contain null entries"),
                        invalid("invalid nested item", invalidItem, "items[0].resultConsistentWithStatus",
                                "signature and reason must be consistent with status"),
                        invalid("duplicate", duplicate, "items[1].identifier",
                                "identifiers must be unique within the batch; duplicates item at index 0"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validItemBatchResponses")
    void itemBatchResponse_validateAcceptsValidUniqueItems(Object response) {
        // given
        Object validResponse = response;

        // when
        Set<ConstraintViolation<Object>> violations = VALIDATOR.validate(validResponse);

        // then
        assertTrue(violations.isEmpty());
    }

    static Stream<Named<Object>> validItemBatchResponses() {
        return Stream.of(named("sign status", validSignStatusResponse()), named("verification", validVerifyResponse()));
    }

    @Test
    void verifyRequest_validateAcceptsMatchingIdentifierSetsRegardlessOfOrder() {
        // given
        VerifyDataRequestV2Dto request = validVerifyRequest();
        request.setData(List.of(signatureItem("first"), signatureItem("second")));
        request.setSignatures(List.of(signatureItem("second"), signatureItem("first")));

        // when
        Set<ConstraintViolation<VerifyDataRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("mismatchedVerificationIdentifiers")
    void verifyRequest_validateRejectsMismatchedIdentifierSets(VerifyDataRequestV2Dto request) {
        // given
        VerifyDataRequestV2Dto mismatchedRequest = request;

        // when
        Set<ConstraintViolation<VerifyDataRequestV2Dto>> violations = VALIDATOR.validate(mismatchedRequest);

        // then
        assertHasViolation(violations, "verificationIdentifiersMatching",
                "signature identifiers must exactly match signed-data identifiers");
    }

    static Stream<Named<VerifyDataRequestV2Dto>> mismatchedVerificationIdentifiers() {
        VerifyDataRequestV2Dto missing = validVerifyRequest();
        missing.setData(List.of(signatureItem("first"), signatureItem("second")));
        missing.setSignatures(List.of(signatureItem("first")));
        VerifyDataRequestV2Dto extra = validVerifyRequest();
        extra.setData(List.of(signatureItem("first")));
        extra.setSignatures(List.of(signatureItem("first"), signatureItem("second")));
        VerifyDataRequestV2Dto mismatch = validVerifyRequest();
        mismatch.setData(List.of(signatureItem("first")));
        mismatch.setSignatures(List.of(signatureItem("second")));
        return Stream
                .of(named("missing signature", missing), named("extra signature", extra),
                        named("different identifier", mismatch));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("directVerificationIdentifierErrors")
    void verifyRequest_validateDefersSetMismatchForDirectIdentifierError(DirectIdentifierError error) {
        // given
        VerifyDataRequestV2Dto request = error.request();

        // when
        Set<ConstraintViolation<VerifyDataRequestV2Dto>> violations = VALIDATOR.validate(request);

        // then
        assertHasViolation(violations, error.directPath(), error.directMessage());
        assertFalse(violations
                .stream()
                .anyMatch(
                        violation -> violation.getPropertyPath().toString().equals("verificationIdentifiersMatching")));
    }

    static Stream<Named<DirectIdentifierError>> directVerificationIdentifierErrors() {
        VerifyDataRequestV2Dto nullDataList = validVerifyRequest();
        nullDataList.setData(null);
        VerifyDataRequestV2Dto nullDataItem = validVerifyRequest();
        nullDataItem.setData(Collections.singletonList(null));
        VerifyDataRequestV2Dto blankDataIdentifier = validVerifyRequest();
        blankDataIdentifier.setData(List.of(new SignatureDataV2Dto(new byte[]{1}, "   ")));
        return Stream
                .of(named("null list",
                        new DirectIdentifierError(nullDataList, "data", "data must contain at least one item")),
                        named("null item",
                                new DirectIdentifierError(nullDataItem, "data[0].<list element>",
                                        "data must not contain null items")),
                        named("blank identifier", new DirectIdentifierError(blankDataIdentifier, "data[0].identifier",
                                "identifier is required and must be unique within the batch")));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidVerifyResponses")
    void verifyResponse_validateRejectsInvalidItemsAndIdentifiers(InvalidDto invalid) {
        // given

        // when
        Set<ConstraintViolation<Object>> violations = validate(invalid);

        // then
        assertHasViolation(violations, invalid.path(), invalid.message());
    }

    static Stream<Named<InvalidDto>> invalidVerifyResponses() {
        VerifyDataResponseV2Dto empty = validVerifyResponse();
        empty.setVerifications(List.of());
        VerifyDataResponseV2Dto nullItem = validVerifyResponse();
        nullItem.setVerifications(Collections.singletonList(null));
        VerifyDataResponseV2Dto invalidItem = validVerifyResponse();
        invalidItem.setVerifications(List.of(new VerificationResponseItemV2Dto(null, "item-1", null)));
        VerifyDataResponseV2Dto duplicate = validVerifyResponse();
        duplicate.setVerifications(List.of(verificationItem("duplicate"), verificationItem("duplicate")));
        return Stream
                .of(invalid("empty", empty, "verifications", "verifications must contain at least one item"),
                        invalid("null item", nullItem, "verifications[0].<list element>",
                                "verifications must not contain null items"),
                        invalid("invalid nested item", invalidItem, "verifications[0].result", "result is required"),
                        invalid("duplicate", duplicate, "verifications[1].identifier",
                                "identifiers must be unique within the batch; duplicates item at index 0"));
    }

    @Test
    void randomResponse_validateRequiresDataAndRedactsIt() {
        // given
        String byteArrayMarker = "[101, 102, 103]";
        RandomDataResponseV2Dto validResponse = new RandomDataResponseV2Dto();
        validResponse.setData(new byte[]{101, 102, 103});
        RandomDataResponseV2Dto responseWithoutData = new RandomDataResponseV2Dto();

        // when
        Set<ConstraintViolation<RandomDataResponseV2Dto>> validViolations = VALIDATOR.validate(validResponse);
        Set<ConstraintViolation<RandomDataResponseV2Dto>> missingDataViolations = VALIDATOR
                .validate(responseWithoutData);
        String representation = validResponse.toString();

        // then
        assertTrue(validViolations.isEmpty());
        assertHasViolation(missingDataViolations, "data", "data is required and must not be empty");
        assertFalse(representation.contains(byteArrayMarker));
    }

    private static CipherDataRequestV2Dto validCipherRequest() {
        CipherDataRequestV2Dto request = withValidKeyScope(new CipherDataRequestV2Dto());
        request.setCipherAttributes(List.of());
        request.setCipherData(List.of(cipherItem("item-1")));
        return request;
    }

    private static EncryptDataResponseV2Dto validEncryptResponse() {
        EncryptDataResponseV2Dto response = new EncryptDataResponseV2Dto();
        response.setEncryptedData(List.of(cipherItem("item-1")));
        return response;
    }

    private static DecryptDataResponseV2Dto validDecryptResponse() {
        DecryptDataResponseV2Dto response = new DecryptDataResponseV2Dto();
        response.setDecryptedData(List.of(cipherItem("item-1")));
        return response;
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
        request.setData(List.of(signatureItem("item-1")));
        return request;
    }

    private static SignDataResponseV2Dto validSynchronousSignResponse() {
        SignDataResponseV2Dto response = new SignDataResponseV2Dto();
        response.setSignatures(List.of(signatureItem("item-1")));
        return response;
    }

    private static SignDataResponseV2Dto validAsynchronousSignResponse() {
        SignDataResponseV2Dto response = new SignDataResponseV2Dto();
        response.setOperationMeta(validMetadata());
        return response;
    }

    private static OperationTrackingRequestV2Dto validSignOperationRequest() {
        OperationTrackingRequestV2Dto request = new OperationTrackingRequestV2Dto();
        request.setOperationMeta(validMetadata());
        return request;
    }

    private static SignOperationStatusResponseV2Dto validSignStatusResponse() {
        SignOperationStatusResponseV2Dto response = new SignOperationStatusResponseV2Dto();
        response.setItems(List.of(signResult("item-1")));
        return response;
    }

    private static VerifyDataRequestV2Dto validVerifyRequest() {
        String identifier = "item-1";
        VerifyDataRequestV2Dto request = withValidKeyScope(new VerifyDataRequestV2Dto());
        request.setSignatureAttributes(List.of());
        request.setData(List.of(signatureItem(identifier)));
        request.setSignatures(List.of(signatureItem(identifier)));
        return request;
    }

    private static VerifyDataResponseV2Dto validVerifyResponse() {
        VerifyDataResponseV2Dto response = new VerifyDataResponseV2Dto();
        response.setVerifications(List.of(verificationItem("item-1")));
        return response;
    }

    private static CipherDataV2Dto cipherItem(String identifier) {
        return new CipherDataV2Dto(new byte[]{1}, identifier);
    }

    private static SignatureDataV2Dto signatureItem(String identifier) {
        return new SignatureDataV2Dto(new byte[]{1}, identifier);
    }

    private static SignatureResultItemV2Dto signResult(String identifier) {
        return new SignatureResultItemV2Dto(identifier, OperationStatus.COMPLETED, new byte[]{1}, null);
    }

    private static VerificationResponseItemV2Dto verificationItem(String identifier) {
        return new VerificationResponseItemV2Dto(true, identifier, null);
    }

    private static <T extends KeyScopedRequestV2Dto> T withValidKeyScope(T request) {
        withValidTokenProfileScope(request);
        request.setKeyMeta(validMetadata());
        return request;
    }

    private static Named<InvalidDto> invalid(String name, Object dto, String path, String message) {
        return named(name, new InvalidDto(dto, new Class<?>[0], path, message));
    }

    private static Named<InvalidDto> invalid(String name, Object dto, Class<?> group, String path, String message) {
        return named(name, new InvalidDto(dto, new Class<?>[]{group}, path, message));
    }

    private static Set<ConstraintViolation<Object>> validate(InvalidDto invalid) {
        return VALIDATOR.validate(invalid.dto(), invalid.groups());
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

    private record InvalidDto(Object dto, Class<?>[] groups, String path, String message) {
    }

    private record GroupedDto(Object dto, Class<?> group) {
    }

    private record DirectIdentifierError(VerifyDataRequestV2Dto request, String directPath, String directMessage) {
    }
}
