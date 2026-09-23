package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.common.v2.OperationExecutionMode;
import com.otilm.api.model.connector.cryptography.v2.key.CreateKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportKeyResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ExportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportKeyRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ImportableKeyTypeV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyCreationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyOperationResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyPairDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.PublicKeyDataV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.SecretKeyOperationStatusResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignDataResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.operations.SignatureAlgorithmAttribute;
import com.otilm.api.model.connector.cryptography.v2.operations.data.SignatureDataV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import java.security.KeyPairGenerator;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validExportKeyRequest;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validExportKeyResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

class OperationResponseValidatorTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final OperationResponseValidator VALIDATOR = new OperationResponseValidator(VALIDATORS.validator());

    @ParameterizedTest(name = "{0}")
    @MethodSource("validCreateKeyResponses")
    void validateCreateKey_acceptsValidResponse_forExecutionMode(CreateKeyCase testCase) {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(KeyRequestType.SECRET, testCase.mode());
        ResponseEntity<? extends KeyCreationResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, response);

        // then
        assertValid(result);
    }

    @Test
    void validateCreateKey_rejectsMissingRequest() {
        // given
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(null, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateCreateKey_rejectsMissingExecutionMode() {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(KeyRequestType.SECRET, null);
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateCreateKey_rejectsMissingKeyRequestType() {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(null, OperationExecutionMode.SYNCHRONOUS);
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateCreateKey_rejectsMissingResponse() {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(KeyRequestType.SECRET, OperationExecutionMode.SYNCHRONOUS);

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, null);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createKeyResponsesWithContradictingStatus")
    void validateCreateKey_rejectsStatusContradictingExecutionMode(CreateKeyCase testCase) {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(KeyRequestType.SECRET, testCase.mode());
        ResponseEntity<? extends KeyCreationResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, response);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createKeyResponsesWithoutBody")
    void validateCreateKey_rejectsMissingBody_forExecutionMode(CreateKeyCase testCase) {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(KeyRequestType.SECRET, testCase.mode());
        ResponseEntity<? extends KeyCreationResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, response);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createKeyBodiesContradictingExecutionMode")
    void validateCreateKey_rejectsBodyContradictingExecutionMode(CreateKeyCase testCase) {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(KeyRequestType.SECRET, testCase.mode());
        ResponseEntity<? extends KeyCreationResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, response);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createKeyResponsesWithMismatchedKeyRequestType")
    void validateCreateKey_rejectsMismatchedKeyRequestType(CreateKeyTypeMismatchCase testCase) {
        // given
        CreateKeyRequestV2Dto request = createKeyRequest(testCase.requestType(), testCase.mode());
        ResponseEntity<? extends KeyCreationResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateCreateKey(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateImportKey_acceptsValidSynchronousResponse() {
        // given
        ImportKeyRequestV2Dto request = importKeyRequest(KeyRequestType.SECRET, OperationExecutionMode.SYNCHRONOUS);
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportKey(request, response);

        // then
        assertValid(result);
    }

    @Test
    void validateImportKey_acceptsValidAsynchronousResponse() {
        // given
        ImportKeyRequestV2Dto request = importKeyRequest(KeyRequestType.SECRET, OperationExecutionMode.ASYNCHRONOUS);
        ResponseEntity<SecretKeyDataResponseV2Dto> response = asynchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportKey(request, response);

        // then
        assertValid(result);
    }

    @Test
    void validateImportKey_rejectsMissingRequest() {
        // given
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportKey(null, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateImportKey_rejectsMissingKeyRequestType() {
        // given
        ImportKeyRequestV2Dto request = importKeyRequest(null, OperationExecutionMode.SYNCHRONOUS);
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportKey(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateImportKey_rejectsStatusContradictingExecutionMode() {
        // given
        ImportKeyRequestV2Dto request = importKeyRequest(KeyRequestType.SECRET, OperationExecutionMode.ASYNCHRONOUS);
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportKey(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateImportKey_rejectsMismatchedKeyRequestType() {
        // given
        ImportKeyRequestV2Dto request = importKeyRequest(KeyRequestType.KEY_PAIR, OperationExecutionMode.SYNCHRONOUS);
        ResponseEntity<SecretKeyDataResponseV2Dto> response = synchronousCreateKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportKey(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateImportKeyStatus_rejectsMissingStatus() {
        // given
        KeyCreationStatusResponseV2Dto response = new SecretKeyOperationStatusResponseV2Dto();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportKeyStatus(response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateImportableKeyTypes_acceptsAdvertisedTypes() {
        // given
        List<ImportableKeyTypeV2Dto> response = List.of(importableKeyType());

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportableKeyTypes(response);

        // then
        assertValid(result);
    }

    @Test
    void validateImportableKeyTypes_rejectsNullItem() {
        // given
        List<ImportableKeyTypeV2Dto> response = Collections.singletonList(null);

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportableKeyTypes(response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateImportableKeyTypes_rejectsTypeWithoutAlgorithms() {
        // given
        ImportableKeyTypeV2Dto withoutAlgorithms = new ImportableKeyTypeV2Dto();
        withoutAlgorithms.setKeyRequestType(KeyRequestType.KEY_PAIR);

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateImportableKeyTypes(List.of(withoutAlgorithms));

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validDestroyKeyResponses")
    void validateDestroy_acceptsValidResponse_forExecutionMode(DestroyKeyCase testCase) {
        // given
        OperationExecutionMode mode = testCase.mode();
        ResponseEntity<KeyOperationResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(mode, response);

        // then
        assertValid(result);
    }

    @Test
    void validateDestroy_rejectsMissingExecutionMode() {
        // given
        ResponseEntity<KeyOperationResponseV2Dto> response = ResponseEntity.ok().build();

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(null, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateDestroy_rejectsMissingResponse() {
        // given
        OperationExecutionMode mode = OperationExecutionMode.SYNCHRONOUS;

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(mode, null);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("destroyKeyResponsesWithContradictingStatus")
    void validateDestroy_rejectsStatusContradictingExecutionMode(DestroyKeyCase testCase) {
        // given
        OperationExecutionMode mode = testCase.mode();
        ResponseEntity<KeyOperationResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(mode, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateDestroy_rejectsMissingBody_forSynchronousExecution() {
        // given
        OperationExecutionMode mode = OperationExecutionMode.SYNCHRONOUS;
        ResponseEntity<KeyOperationResponseV2Dto> response = ResponseEntity.ok().build();

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(mode, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateDestroy_rejectsOperationMetadata_forSynchronousExecution() {
        // given
        OperationExecutionMode mode = OperationExecutionMode.SYNCHRONOUS;
        KeyOperationResponseV2Dto body = new KeyOperationResponseV2Dto();
        body.setOperationMeta(validMetadata());
        ResponseEntity<KeyOperationResponseV2Dto> response = ResponseEntity.ok(body);

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(mode, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateDestroy_rejectsMissingBody_forAsynchronousExecution() {
        // given
        OperationExecutionMode mode = OperationExecutionMode.ASYNCHRONOUS;
        ResponseEntity<KeyOperationResponseV2Dto> response = ResponseEntity.accepted().build();

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(mode, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateDestroy_rejectsInvalidAsynchronousOperationBody() {
        // given
        OperationExecutionMode mode = OperationExecutionMode.ASYNCHRONOUS;
        KeyOperationResponseV2Dto bodyWithoutOperationMetadata = new KeyOperationResponseV2Dto();
        ResponseEntity<KeyOperationResponseV2Dto> response = ResponseEntity
                .accepted()
                .body(bodyWithoutOperationMetadata);

        // when
        OperationValidationResult result = VALIDATOR.validateDestroy(mode, response);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("validSignResponses")
    void validateSign_acceptsValidResponse_forExecutionMode(SignCase testCase) {
        // given
        SignDataRequestV2Dto request = testCase.request();
        ResponseEntity<SignDataResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertValid(result);
    }

    @Test
    void validateSign_rejectsMissingRequest() {
        // given
        ResponseEntity<SignDataResponseV2Dto> response = synchronousSignResponse("item-1");

        // when
        OperationValidationResult result = VALIDATOR.validateSign(null, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateSign_rejectsMissingExecutionMode() {
        // given
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.SYNCHRONOUS, "item-1");
        request.setExecutionMode(null);
        ResponseEntity<SignDataResponseV2Dto> response = synchronousSignResponse("item-1");

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateSign_rejectsMissingResponse() {
        // given
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.SYNCHRONOUS, "item-1");

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, null);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("signResponsesWithContradictingStatus")
    void validateSign_rejectsStatusContradictingExecutionMode(SignCase testCase) {
        // given
        SignDataRequestV2Dto request = testCase.request();
        ResponseEntity<SignDataResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("signResponsesWithoutBody")
    void validateSign_rejectsMissingBody_forExecutionMode(SignCase testCase) {
        // given
        SignDataRequestV2Dto request = testCase.request();
        ResponseEntity<SignDataResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("signBodiesContradictingExecutionMode")
    void validateSign_rejectsBodyContradictingExecutionMode(SignCase testCase) {
        // given
        SignDataRequestV2Dto request = testCase.request();
        ResponseEntity<SignDataResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateSign_acceptsMatchingIdentifiers_inDifferentOrder() {
        // given
        String firstIdentifier = "item-1";
        String secondIdentifier = "item-2";
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.SYNCHRONOUS, firstIdentifier,
                secondIdentifier);
        ResponseEntity<SignDataResponseV2Dto> response = synchronousSignResponse(secondIdentifier, firstIdentifier);

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertValid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("signResponsesWithDifferentIdentifierSets")
    void validateSign_rejectsDifferentIdentifierSets(SignCase testCase) {
        // given
        SignDataRequestV2Dto request = testCase.request();
        ResponseEntity<SignDataResponseV2Dto> response = testCase.response();

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateSign_rejectsMissingRequestData_forSynchronousExecution() {
        // given
        String identifier = "item-1";
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.SYNCHRONOUS, identifier);
        request.setData(null);
        ResponseEntity<SignDataResponseV2Dto> response = synchronousSignResponse(identifier);

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateSign_rejectsNullRequestItem_forSynchronousExecution() {
        // given
        String identifier = "item-1";
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.SYNCHRONOUS, identifier);
        request.setData(Collections.singletonList(null));
        ResponseEntity<SignDataResponseV2Dto> response = synchronousSignResponse(identifier);

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateSign_rejectsNullResponseItem_forSynchronousExecution() {
        // given
        String identifier = "item-1";
        SignDataRequestV2Dto request = signRequest(OperationExecutionMode.SYNCHRONOUS, identifier);
        SignDataResponseV2Dto body = new SignDataResponseV2Dto();
        body.setSignatures(Collections.singletonList(null));
        ResponseEntity<SignDataResponseV2Dto> response = ResponseEntity.ok(body);

        // when
        OperationValidationResult result = VALIDATOR.validateSign(request, response);

        // then
        assertInvalid(result);
    }

    @Test
    void validateSignAttributeList_acceptsASchemaThatOffersTheSignatureAlgorithm() {
        // given
        List<BaseAttribute> schema = List
                .of(SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA)));

        // when
        OperationValidationResult result = VALIDATOR.validateSignAttributeList(schema);

        // then
        assertValid(result);
    }

    @Test
    void validateSignAttributeList_findsTheSignatureAlgorithmAfterOtherAttributes() {
        // given
        DataAttributeV3 other = SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        other.setName("signatureScheme");
        List<BaseAttribute> schema = List
                .of(other, SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA)));

        // when
        OperationValidationResult result = VALIDATOR.validateSignAttributeList(schema);

        // then
        assertValid(result);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("signatureAlgorithmOffersOfAnotherAttributeKind")
    void validateSignAttributeList_rejectsAnOfferThatIsNotAV3DataAttribute(BaseAttribute definition) {
        // when
        OperationValidationResult result = VALIDATOR.validateSignAttributeList(List.of(definition));

        // then
        assertInvalid(result, "Sign attributes must declare signatureAlgorithm as a v3 data attribute");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("signatureAlgorithmOffersOfTheWrongShape")
    void validateSignAttributeList_rejectsAnOfferThatIsNotARequiredSingleSelect(DataAttributeV3 definition) {
        // when
        OperationValidationResult result = VALIDATOR.validateSignAttributeList(List.of(definition));

        // then
        assertInvalid(result, "signatureAlgorithm must be a required single-select attribute");
    }

    @Test
    void validateSignAttributeList_rejectsASchemaWithoutTheSignatureAlgorithm() {
        // given
        DataAttributeV3 unrelated = SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        unrelated.setName("signatureScheme");

        // when
        OperationValidationResult result = VALIDATOR.validateSignAttributeList(List.of(unrelated));

        // then
        assertInvalid(result, "Sign attributes must declare signatureAlgorithm as a v3 data attribute");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("malformedSignatureAlgorithmOffers")
    void validateSignAttributeList_rejectsAnOfferThatNamesNoSignatureAlgorithm(DataAttributeV3 definition) {
        // when
        OperationValidationResult result = VALIDATOR.validateSignAttributeList(List.of(definition));

        // then
        assertInvalid(result, "signatureAlgorithm must offer at least one value, and only signature algorithm codes");
    }

    @Test
    void validateSignAttributeList_rejectsANullDefinition() {
        // when
        OperationValidationResult result = VALIDATOR.validateSignAttributeList(Collections.singletonList(null));

        // then
        assertInvalid(result);
    }

    static Stream<Named<DataAttributeV3>> malformedSignatureAlgorithmOffers() {
        DataAttributeV3 outsideTheEnum = SignatureAlgorithmAttribute.definition(List.of());
        outsideTheEnum.setContent(List.of(new StringAttributeContentV3("SHA1withRSA")));
        DataAttributeV3 nothingOffered = SignatureAlgorithmAttribute.definition(List.of());
        DataAttributeV3 notAString = SignatureAlgorithmAttribute
                .definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        notAString.setContentType(AttributeContentType.INTEGER);
        DataAttributeV3 noContent = SignatureAlgorithmAttribute.definition(List.of());
        noContent.setContent(null);
        DataAttributeV3 nullOption = SignatureAlgorithmAttribute.definition(List.of());
        nullOption.setContent(Collections.singletonList(null));
        DataAttributeV3 optionWithoutData = SignatureAlgorithmAttribute.definition(List.of());
        optionWithoutData.setContent(List.of(new StringAttributeContentV3()));
        return Stream
                .of(named("a code outside the enum", outsideTheEnum), named("no value", nothingOffered),
                        named("a non-string attribute", notAString), named("no content", noContent),
                        named("a null option", nullOption), named("an option without data", optionWithoutData));
    }

    static Stream<Named<DataAttributeV3>> signatureAlgorithmOffersOfTheWrongShape() {
        DataAttributeV3 optional = SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        optional.getProperties().setRequired(false);
        DataAttributeV3 multiSelect = SignatureAlgorithmAttribute
                .definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        multiSelect.getProperties().setMultiSelect(true);
        DataAttributeV3 noProperties = SignatureAlgorithmAttribute
                .definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        noProperties.setProperties(null);
        return Stream
                .of(named("optional", optional), named("multi-select", multiSelect),
                        named("no properties", noProperties));
    }

    static Stream<Named<BaseAttribute>> signatureAlgorithmOffersOfAnotherAttributeKind() {
        InfoAttributeV3 info = new InfoAttributeV3();
        info.setName(SignatureAlgorithmAttribute.NAME);
        DataAttributeV3 offer = SignatureAlgorithmAttribute.definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA));
        DataAttributeV2 v2Offer = new DataAttributeV2();
        v2Offer.setUuid(offer.getUuid());
        v2Offer.setName(offer.getName());
        v2Offer.setContentType(offer.getContentType());
        v2Offer.setProperties(offer.getProperties());
        v2Offer.setContent(List.of(new StringAttributeContentV2(SignatureAlgorithm.SHA256_WITH_RSA.getCode())));
        return Stream.of(named("an info attribute", info), named("a v2 data attribute", v2Offer));
    }

    static Stream<Named<CreateKeyCase>> validCreateKeyResponses() {
        return Stream
                .of(named("synchronous",
                        new CreateKeyCase(OperationExecutionMode.SYNCHRONOUS, synchronousCreateKeyResponse())),
                        named("asynchronous", new CreateKeyCase(OperationExecutionMode.ASYNCHRONOUS,
                                asynchronousCreateKeyResponse())));
    }

    static Stream<Named<CreateKeyCase>> createKeyResponsesWithContradictingStatus() {
        SecretKeyDataResponseV2Dto asynchronousBody = asynchronousCreateKeyBody();
        return Stream
                .of(named("synchronous receives 202",
                        new CreateKeyCase(OperationExecutionMode.SYNCHRONOUS,
                                ResponseEntity.accepted().body(validSecretKeyDataResponse()))),
                        named("asynchronous receives 200", new CreateKeyCase(OperationExecutionMode.ASYNCHRONOUS,
                                ResponseEntity.ok(asynchronousBody))));
    }

    static Stream<Named<CreateKeyCase>> createKeyResponsesWithoutBody() {
        return Stream
                .of(named("synchronous",
                        new CreateKeyCase(OperationExecutionMode.SYNCHRONOUS,
                                ResponseEntity.status(HttpStatus.OK).build())),
                        named("asynchronous", new CreateKeyCase(OperationExecutionMode.ASYNCHRONOUS,
                                ResponseEntity.status(HttpStatus.ACCEPTED).build())));
    }

    static Stream<Named<CreateKeyCase>> createKeyBodiesContradictingExecutionMode() {
        SecretKeyDataResponseV2Dto synchronousBodyWithOperationMetadata = validSecretKeyDataResponse();
        synchronousBodyWithOperationMetadata.setOperationMeta(validMetadata());
        SecretKeyDataResponseV2Dto asynchronousBodyWithKeyData = validSecretKeyDataResponse();
        asynchronousBodyWithKeyData.setOperationMeta(validMetadata());
        return Stream
                .of(named("synchronous body contains operation metadata",
                        new CreateKeyCase(OperationExecutionMode.SYNCHRONOUS,
                                ResponseEntity.ok(synchronousBodyWithOperationMetadata))),
                        named("asynchronous body contains synchronous key data",
                                new CreateKeyCase(OperationExecutionMode.ASYNCHRONOUS,
                                        ResponseEntity.accepted().body(asynchronousBodyWithKeyData))));
    }

    static Stream<Named<CreateKeyTypeMismatchCase>> createKeyResponsesWithMismatchedKeyRequestType() {
        return Stream
                .of(named("synchronous secret request receives key pair",
                        new CreateKeyTypeMismatchCase(KeyRequestType.SECRET, OperationExecutionMode.SYNCHRONOUS,
                                ResponseEntity.ok(synchronousCreateKeyPairBody()))),
                        named("asynchronous secret request receives key pair",
                                new CreateKeyTypeMismatchCase(KeyRequestType.SECRET,
                                        OperationExecutionMode.ASYNCHRONOUS,
                                        ResponseEntity.accepted().body(asynchronousCreateKeyPairBody()))),
                        named("synchronous key-pair request receives secret key",
                                new CreateKeyTypeMismatchCase(KeyRequestType.KEY_PAIR,
                                        OperationExecutionMode.SYNCHRONOUS, synchronousCreateKeyResponse())),
                        named("asynchronous key-pair request receives secret key",
                                new CreateKeyTypeMismatchCase(KeyRequestType.KEY_PAIR,
                                        OperationExecutionMode.ASYNCHRONOUS, asynchronousCreateKeyResponse())));
    }

    static Stream<Named<DestroyKeyCase>> validDestroyKeyResponses() {
        return Stream
                .of(named("synchronous",
                        new DestroyKeyCase(OperationExecutionMode.SYNCHRONOUS,
                                ResponseEntity.ok(new KeyOperationResponseV2Dto()))),
                        named("asynchronous", new DestroyKeyCase(OperationExecutionMode.ASYNCHRONOUS,
                                asynchronousDestroyKeyResponse())));
    }

    static Stream<Named<DestroyKeyCase>> destroyKeyResponsesWithContradictingStatus() {
        return Stream
                .of(named("synchronous receives 202",
                        new DestroyKeyCase(OperationExecutionMode.SYNCHRONOUS, ResponseEntity.accepted().build())),
                        named("asynchronous receives 200", new DestroyKeyCase(OperationExecutionMode.ASYNCHRONOUS,
                                ResponseEntity.ok(asynchronousDestroyKeyBody()))));
    }

    static Stream<Named<SignCase>> validSignResponses() {
        String identifier = "item-1";
        return Stream
                .of(named("synchronous",
                        new SignCase(signRequest(OperationExecutionMode.SYNCHRONOUS, identifier),
                                synchronousSignResponse(identifier))),
                        named("asynchronous", new SignCase(signRequest(OperationExecutionMode.ASYNCHRONOUS, identifier),
                                asynchronousSignResponse())));
    }

    static Stream<Named<SignCase>> signResponsesWithContradictingStatus() {
        String identifier = "item-1";
        SignDataResponseV2Dto synchronousBody = synchronousSignBody(identifier);
        SignDataResponseV2Dto asynchronousBody = asynchronousSignBody();
        return Stream
                .of(named("synchronous receives 202",
                        new SignCase(signRequest(OperationExecutionMode.SYNCHRONOUS, identifier),
                                ResponseEntity.accepted().body(synchronousBody))),
                        named("asynchronous receives 200",
                                new SignCase(signRequest(OperationExecutionMode.ASYNCHRONOUS, identifier),
                                        ResponseEntity.ok(asynchronousBody))));
    }

    static Stream<Named<SignCase>> signResponsesWithoutBody() {
        String identifier = "item-1";
        return Stream
                .of(named("synchronous",
                        new SignCase(signRequest(OperationExecutionMode.SYNCHRONOUS, identifier),
                                ResponseEntity.status(HttpStatus.OK).build())),
                        named("asynchronous", new SignCase(signRequest(OperationExecutionMode.ASYNCHRONOUS, identifier),
                                ResponseEntity.status(HttpStatus.ACCEPTED).build())));
    }

    static Stream<Named<SignCase>> signBodiesContradictingExecutionMode() {
        String identifier = "item-1";
        SignDataResponseV2Dto synchronousBodyWithOperationMetadata = synchronousSignBody(identifier);
        synchronousBodyWithOperationMetadata.setOperationMeta(validMetadata());
        SignDataResponseV2Dto asynchronousBodyWithSignatures = asynchronousSignBody();
        asynchronousBodyWithSignatures.setSignatures(List.of(signatureItem(identifier)));
        return Stream
                .of(named("synchronous body contains operation metadata",
                        new SignCase(signRequest(OperationExecutionMode.SYNCHRONOUS, identifier),
                                ResponseEntity.ok(synchronousBodyWithOperationMetadata))),
                        named("asynchronous body contains signatures",
                                new SignCase(signRequest(OperationExecutionMode.ASYNCHRONOUS, identifier),
                                        ResponseEntity.accepted().body(asynchronousBodyWithSignatures))));
    }

    static Stream<Named<SignCase>> signResponsesWithDifferentIdentifierSets() {
        String firstIdentifier = "item-1";
        String secondIdentifier = "item-2";
        String replacementIdentifier = "item-3";
        return Stream
                .of(named("response is missing an identifier",
                        new SignCase(signRequest(OperationExecutionMode.SYNCHRONOUS, firstIdentifier, secondIdentifier),
                                synchronousSignResponse(firstIdentifier))),
                        named("response has an additional identifier",
                                new SignCase(signRequest(OperationExecutionMode.SYNCHRONOUS, firstIdentifier),
                                        synchronousSignResponse(firstIdentifier, secondIdentifier))),
                        named("response substitutes an identifier",
                                new SignCase(
                                        signRequest(OperationExecutionMode.SYNCHRONOUS, firstIdentifier,
                                                secondIdentifier),
                                        synchronousSignResponse(firstIdentifier, replacementIdentifier))));
    }

    private static ResponseEntity<SecretKeyDataResponseV2Dto> synchronousCreateKeyResponse() {
        return ResponseEntity.ok(validSecretKeyDataResponse());
    }

    private static ResponseEntity<SecretKeyDataResponseV2Dto> asynchronousCreateKeyResponse() {
        return ResponseEntity.accepted().body(asynchronousCreateKeyBody());
    }

    private static SecretKeyDataResponseV2Dto asynchronousCreateKeyBody() {
        SecretKeyDataResponseV2Dto body = new SecretKeyDataResponseV2Dto();
        body.setOperationMeta(validMetadata());
        return body;
    }

    private static KeyPairDataResponseV2Dto synchronousCreateKeyPairBody() {
        KeyPairDataResponseV2Dto body = new KeyPairDataResponseV2Dto();
        body.setPublicKeyData(validPublicKeyDataResponse());
        body.setPrivateKeyData(validPrivateKeyDataResponse());
        body.setKeyPairMeta(validMetadata());
        return body;
    }

    private static KeyPairDataResponseV2Dto asynchronousCreateKeyPairBody() {
        KeyPairDataResponseV2Dto body = new KeyPairDataResponseV2Dto();
        body.setOperationMeta(validMetadata());
        return body;
    }

    @Test
    void validateExportKey_acceptsAKeyPairExportThatEchoesTheReferenceAndCarriesThePublicKey() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertValid(result);
    }

    @Test
    void validateExportKey_requiresTheRequest() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateExportKey(null, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Key export request is required");
    }

    @Test
    void validateExportKey_requiresTheRequestedKeyRequestType() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyRequestType(null);
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Key request type is required");
    }

    @Test
    void validateExportKey_requiresAResponse() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateExportKey(request, null);

        // then
        assertInvalid(result, "Connector returned no response");
    }

    @Test
    void validateExportKey_requiresAResponseBody() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok().build());

        // then
        assertInvalid(result, "Connector response body is required");
    }

    @Test
    void validateExportKey_rejectsAnAsynchronousStatus() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.accepted().body(response));

        // then
        assertInvalid(result, "Connector returned HTTP 202; expected HTTP 200");
    }

    @Test
    void validateExportKey_rejectsAResponseWithoutMaterial() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setMaterial(null);

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Connector response validation failed: material: material is required");
    }

    @Test
    void validateExportKey_rejectsAKeyReferenceTheRequestDidNotAskFor() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyReference(null);
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Connector returned a key reference for a key that carries none");
    }

    @Test
    void validateExportKey_rejectsAMissingKeyReferenceEcho() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyReference(null);

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Connector did not echo the requested key reference");
    }

    @Test
    void validateExportKey_rejectsADifferentKeyReference() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyReference("2f3d4e5a-6b7c-48d9-90e1-a2b3c4d5e6f7");

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Connector echoed a different key reference than requested");
    }

    @Test
    void validateExportKey_requiresThePublicKeyDescriptorForAKeyPair() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyData(validSecretKeyData());

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Connector described an exported key pair as Secret; expected its public key");
    }

    @Test
    void validateExportKey_requiresTheSecretKeyDescriptorForASecretKey() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyRequestType(KeyRequestType.SECRET);
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertInvalid(result, "Connector described an exported secret key as Public; expected Secret");
    }

    @Test
    void validateExportKey_acceptsASecretKeyExportDescribedAsSecret() {
        // given
        ExportKeyRequestV2Dto request = validExportKeyRequest();
        request.setKeyRequestType(KeyRequestType.SECRET);
        ExportKeyResponseV2Dto response = validExportKeyResponse();
        response.setKeyData(validSecretKeyData());

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportKey(request, ResponseEntity.ok(response));

        // then
        assertValid(result);
    }

    @Test
    void validateExportedKeyDescriptor_acceptsADescriptorMatchingTheRecord() {
        // given
        ExportKeyResponseV2Dto response = validExportKeyResponse();

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportedKeyDescriptor(validPublicKeyData(), response);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateExportedKeyDescriptor_rejectsAPublicKeyOtherThanTheRecord() throws Exception {
        // given
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        PublicKeyDataV2Dto expected = validPublicKeyData();
        expected.setPublicKeySpki(generator.generateKeyPair().getPublic().getEncoded());

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportedKeyDescriptor(expected, validExportKeyResponse());

        // then
        assertInvalid(result, "Connector exported a public key that differs from the platform's record");
    }

    @Test
    void validateExportedKeyDescriptor_rejectsAnotherAlgorithm() {
        // given
        PublicKeyDataV2Dto expected = validPublicKeyData();
        expected.setAlgorithm(KeyAlgorithm.ECDSA);

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportedKeyDescriptor(expected, validExportKeyResponse());

        // then
        assertInvalid(result, "Connector exported a key with algorithm RSA; expected ECDSA");
    }

    @Test
    void validateExportedKeyDescriptor_rejectsAnotherLength() {
        // given
        PublicKeyDataV2Dto expected = validPublicKeyData();
        expected.setLength(3072);

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportedKeyDescriptor(expected, validExportKeyResponse());

        // then
        assertInvalid(result, "Connector exported a key of length 2048; expected 3072");
    }

    @Test
    void validateExportedKeyDescriptor_rejectsAnotherKindOfKey() {
        // given
        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportedKeyDescriptor(validSecretKeyData(), validExportKeyResponse());

        // then
        assertInvalid(result, "Connector described the exported key as Public; expected Secret");
    }

    @Test
    void validateExportedKeyDescriptor_requiresTheRecordAndADescribedKey() {
        // given
        ExportKeyResponseV2Dto undescribed = validExportKeyResponse();
        undescribed.setKeyData(null);

        // when
        OperationValidationResult withoutRecord = VALIDATOR
                .keyTransfer()
                .validateExportedKeyDescriptor(null, validExportKeyResponse());
        OperationValidationResult withoutDescriptor = VALIDATOR
                .keyTransfer()
                .validateExportedKeyDescriptor(validPublicKeyData(), undescribed);

        // then
        assertInvalid(withoutRecord, "Expected key descriptor is required");
        assertInvalid(withoutDescriptor, "Connector did not describe the exported key");
    }

    @Test
    void validateExportableKeyTypes_acceptsADeclaredType() {
        // given
        List<ExportableKeyTypeV2Dto> response = List.of(exportableKeyType());

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateExportableKeyTypes(response);

        // then
        assertValid(result);
    }

    @Test
    void validateExportableKeyTypes_rejectsATypeWithoutAlgorithms() {
        // given
        ExportableKeyTypeV2Dto exportableKeyType = exportableKeyType();
        exportableKeyType.setAlgorithms(Set.of());

        // when
        OperationValidationResult result = VALIDATOR
                .keyTransfer()
                .validateExportableKeyTypes(List.of(exportableKeyType));

        // then
        assertInvalid(result,
                "Connector response validation failed: algorithms: algorithms must contain at least one algorithm");
    }

    @Test
    void validateExportableKeyTypes_rejectsANullEntry() {
        // given
        List<ExportableKeyTypeV2Dto> response = Collections.singletonList(null);

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateExportableKeyTypes(response);

        // then
        assertInvalid(result, "Connector response must not contain a null exportable key type");
    }

    @Test
    void validateExportableKeyTypes_rejectsTheSameKeyTypeTwice() {
        // given
        List<ExportableKeyTypeV2Dto> response = List.of(exportableKeyType(), exportableKeyType());

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateExportableKeyTypes(response);

        // then
        assertInvalid(result, "Connector declared key type keyPair more than once");
    }

    @Test
    void validateImportableKeyTypes_rejectsTheSameKeyTypeTwice() {
        // given
        List<ImportableKeyTypeV2Dto> response = List.of(importableKeyType(), importableKeyType());

        // when
        OperationValidationResult result = VALIDATOR.keyTransfer().validateImportableKeyTypes(response);

        // then
        assertInvalid(result, "Connector declared key type keyPair more than once");
    }

    private static CreateKeyRequestV2Dto createKeyRequest(KeyRequestType keyRequestType,
            OperationExecutionMode executionMode) {
        CreateKeyRequestV2Dto request = new CreateKeyRequestV2Dto();
        request.setKeyRequestType(keyRequestType);
        request.setExecutionMode(executionMode);
        return request;
    }

    private static ImportKeyRequestV2Dto importKeyRequest(KeyRequestType keyRequestType,
            OperationExecutionMode executionMode) {
        ImportKeyRequestV2Dto request = new ImportKeyRequestV2Dto();
        request.setKeyRequestType(keyRequestType);
        request.setExecutionMode(executionMode);
        return request;
    }

    private static ExportableKeyTypeV2Dto exportableKeyType() {
        ExportableKeyTypeV2Dto exportableKeyType = new ExportableKeyTypeV2Dto();
        exportableKeyType.setKeyRequestType(KeyRequestType.KEY_PAIR);
        exportableKeyType.setAlgorithms(Set.of(KeyAlgorithm.RSA));
        return exportableKeyType;
    }

    private static ImportableKeyTypeV2Dto importableKeyType() {
        ImportableKeyTypeV2Dto importableKeyType = new ImportableKeyTypeV2Dto();
        importableKeyType.setKeyRequestType(KeyRequestType.KEY_PAIR);
        importableKeyType.setAlgorithms(Set.of(KeyAlgorithm.RSA));
        return importableKeyType;
    }

    private static ResponseEntity<KeyOperationResponseV2Dto> asynchronousDestroyKeyResponse() {
        return ResponseEntity.accepted().body(asynchronousDestroyKeyBody());
    }

    private static KeyOperationResponseV2Dto asynchronousDestroyKeyBody() {
        KeyOperationResponseV2Dto body = new KeyOperationResponseV2Dto();
        body.setOperationMeta(validMetadata());
        return body;
    }

    private static SignDataRequestV2Dto signRequest(OperationExecutionMode executionMode, String... identifiers) {
        SignDataRequestV2Dto request = withValidTokenProfileScope(new SignDataRequestV2Dto());
        request.setKeyMeta(validMetadata());
        request.setExecutionMode(executionMode);
        request.setSignatureAttributes(List.of());
        request.setData(Stream.of(identifiers).map(OperationResponseValidatorTest::signatureItem).toList());
        return request;
    }

    private static ResponseEntity<SignDataResponseV2Dto> synchronousSignResponse(String... identifiers) {
        return ResponseEntity.ok(synchronousSignBody(identifiers));
    }

    private static SignDataResponseV2Dto synchronousSignBody(String... identifiers) {
        SignDataResponseV2Dto response = new SignDataResponseV2Dto();
        response.setSignatures(Stream.of(identifiers).map(OperationResponseValidatorTest::signatureItem).toList());
        return response;
    }

    private static ResponseEntity<SignDataResponseV2Dto> asynchronousSignResponse() {
        return ResponseEntity.accepted().body(asynchronousSignBody());
    }

    private static SignDataResponseV2Dto asynchronousSignBody() {
        SignDataResponseV2Dto response = new SignDataResponseV2Dto();
        response.setOperationMeta(validMetadata());
        return response;
    }

    private static SignatureDataV2Dto signatureItem(String identifier) {
        byte[] data = {1};
        return new SignatureDataV2Dto(data, identifier);
    }

    private static void assertValid(OperationValidationResult result) {
        assertTrue(result.isValid());
    }

    private static void assertInvalid(OperationValidationResult result) {
        assertFalse(result.isValid());
        assertNotNull(result.getCause());
    }

    private static void assertInvalid(OperationValidationResult result, String expectedMessage) {
        assertInvalid(result);
        assertEquals(expectedMessage, result.getCause().getMessage());
    }

    private record CreateKeyCase(OperationExecutionMode mode,
            ResponseEntity<? extends KeyCreationResponseV2Dto> response) {
    }

    private record CreateKeyTypeMismatchCase(KeyRequestType requestType, OperationExecutionMode mode,
            ResponseEntity<? extends KeyCreationResponseV2Dto> response) {
    }

    private record DestroyKeyCase(OperationExecutionMode mode, ResponseEntity<KeyOperationResponseV2Dto> response) {
    }

    private record SignCase(SignDataRequestV2Dto request, ResponseEntity<SignDataResponseV2Dto> response) {
    }
}
