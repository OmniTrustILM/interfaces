package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.connector.cryptography.v2.key.KeyEntryStatus;
import com.otilm.api.model.connector.cryptography.v2.key.KeyHandleV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.KeyResolutionV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyPairV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedSecretKeyV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ResolveKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ResolveKeysResponseV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validSecretKeyData;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResolveKeysResponseValidationTest {

    private static final String IDENTIFIERS_MESSAGE = "Key resolution response identifiers must match request identifiers";
    private static final String KEY_CONSISTENCY_MESSAGE = "Connector response validation failed: "
            + "results[0].keyConsistentWithStatus: key must be present exactly when status is available or refused";

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final OperationResponseValidator VALIDATOR = new OperationResponseValidator(VALIDATORS.validator());

    private static List<MetadataAttribute> handle(String keyId) {
        MetadataAttributeV3 attribute = new MetadataAttributeV3();
        attribute.setUuid("6b1c9c5e-2f8a-4a7e-9f2d-5b8e1c3a7d40");
        attribute.setName("keyId");
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setProperties(new MetadataAttributeProperties());
        attribute.setContent(List.of(new StringAttributeContentV3(keyId)));
        return List.of(attribute);
    }

    private static ListedKeyPairV2Dto listed(String keyId) {
        ListedKeyPairV2Dto pair = new ListedKeyPairV2Dto();
        pair.setKeyPairMeta(handle(keyId));
        pair.setPrivateKeyData(validPrivateKeyDataResponse());
        pair.setPublicKeyData(validPublicKeyDataResponse());
        return pair;
    }

    private static ListedSecretKeyV2Dto listedSecretKey(String keyId) {
        ListedSecretKeyV2Dto key = new ListedSecretKeyV2Dto();
        key.setKeyMeta(handle(keyId));
        key.setKeyData(validSecretKeyData());
        return key;
    }

    private static ResolveKeysRequestV2Dto request(String... identifiers) {
        List<KeyHandleV2Dto> keys = Arrays
                .stream(identifiers)
                .map(identifier -> new KeyHandleV2Dto(identifier, handle("0a")))
                .toList();
        return ResolveKeysRequestV2Dto
                .builder()
                .scope(withValidTokenProfileScope(new TokenProfileScopedRequestV2Dto()))
                .keyRequestType(KeyRequestType.KEY_PAIR)
                .keys(keys)
                .build();
    }

    private static KeyResolutionV2Dto resolution(String identifier, KeyEntryStatus status, ListedKeyV2Dto key) {
        KeyResolutionV2Dto resolution = new KeyResolutionV2Dto();
        resolution.setIdentifier(identifier);
        resolution.setStatus(status);
        resolution.setKey(key);
        return resolution;
    }

    private static KeyResolutionV2Dto absent(String identifier, KeyEntryStatus status) {
        return resolution(identifier, status, null);
    }

    private static KeyResolutionV2Dto found(String identifier) {
        return resolution(identifier, KeyEntryStatus.AVAILABLE, listed("0a"));
    }

    private static ResolveKeysResponseV2Dto response(KeyResolutionV2Dto... results) {
        return new ResolveKeysResponseV2Dto(List.of(results));
    }

    @Test
    void validateResolveKeys_acceptsAResultPerHandle() {
        // given
        ResolveKeysResponseV2Dto response = response(found("0"), absent("1", KeyEntryStatus.NOT_FOUND),
                absent("2", KeyEntryStatus.UNADDRESSABLE), absent("3", KeyEntryStatus.UNREADABLE),
                absent("4", KeyEntryStatus.INVALID_HANDLE));

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0", "1", "2", "3", "4"), response);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateResolveKeys_acceptsResultsInAnotherOrder() {
        // given
        ResolveKeysResponseV2Dto response = response(absent("1", KeyEntryStatus.NOT_FOUND), found("0"));

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0", "1"), response);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateResolveKeys_refusesAMissingResult() {
        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0", "1"), response(found("0")));

        // then
        assertInvalid(result, IDENTIFIERS_MESSAGE);
    }

    @Test
    void validateResolveKeys_refusesAResultForAnUnknownIdentifier() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateResolveKeys(request("0"), response(found("0"), found("9")));

        // then
        assertInvalid(result, IDENTIFIERS_MESSAGE);
    }

    @Test
    void validateResolveKeys_refusesTwoResultsForOneHandle() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateResolveKeys(request("0", "1"), response(found("0"), found("0")));

        // then
        assertInvalid(result, IDENTIFIERS_MESSAGE);
    }

    @Test
    void validateResolveKeys_requiresTheKeyRequestType() {
        // given
        ResolveKeysRequestV2Dto withoutType = request("0");
        withoutType.setKeyRequestType(null);

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(withoutType, response(found("0")));

        // then
        assertInvalid(result, "Key resolution key request type is required");
    }

    @Test
    void validateResolveKeys_refusesAnAvailableResultWithoutAKey() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateResolveKeys(request("0"), response(absent("0", KeyEntryStatus.AVAILABLE)));

        // then
        assertInvalid(result, KEY_CONSISTENCY_MESSAGE);
    }

    @Test
    void validateResolveKeys_refusesAKeyOnANotFoundResult() {
        // given
        KeyResolutionV2Dto notFoundWithKey = resolution("0", KeyEntryStatus.NOT_FOUND, listed("0a"));

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0"), response(notFoundWithKey));

        // then
        assertInvalid(result, KEY_CONSISTENCY_MESSAGE);
    }

    @Test
    void validateResolveKeys_acceptsARefusedResultWithItsKey() {
        // given
        KeyResolutionV2Dto refused = resolution("0", KeyEntryStatus.REFUSED, listed("0a"));
        refused.setDiagnostic("The signature activation module holds this key.");

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0"), response(refused));

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateResolveKeys_refusesAFoundKeyOfAnotherType() {
        // given
        KeyResolutionV2Dto secretKey = resolution("0", KeyEntryStatus.AVAILABLE, listedSecretKey("0a"));

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0"), response(secretKey));

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateResolveKeys_refusesAFoundKeyWhoseMetadataCannotServeAsAHandle() {
        // given
        ListedKeyPairV2Dto referenceOnly = listed("0a");
        referenceOnly.getKeyPairMeta().get(0).setContent(List.of(new StringAttributeContentV3("signer-a", null)));
        KeyResolutionV2Dto found = resolution("0", KeyEntryStatus.AVAILABLE, referenceOnly);

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0"), response(found));

        // then
        assertInvalid(result, "Connector returned a key whose metadata cannot serve as a handle");
    }

    @Test
    void validateResolveKeys_refusesADescriptorOfAnotherType() {
        // given
        KeyResolutionV2Dto unaddressable = absent("0", KeyEntryStatus.UNADDRESSABLE);
        unaddressable.setKeyData(validSecretKeyData());

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0"), response(unaddressable));

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateResolveKeys_refusesKeyDataOnAnInvalidHandleResult() {
        // given
        KeyResolutionV2Dto invalidHandle = absent("0", KeyEntryStatus.INVALID_HANDLE);
        invalidHandle.setKeyData(validPrivateKeyDataResponse().getKeyData());

        // when
        OperationValidationResult result = VALIDATOR.validateResolveKeys(request("0"), response(invalidHandle));

        // then
        assertInvalid(result, "Connector response validation failed: results[0].keyDataOnlyForAMatchedKey: "
                + "keyData must be absent when status is notFound or invalidHandle");
    }

    private static void assertInvalid(OperationValidationResult result, String expectedMessage) {
        assertFalse(result.isValid());
        assertEquals(expectedMessage, result.getCause().getMessage());
    }
}
