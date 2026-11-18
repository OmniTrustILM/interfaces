package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.connector.cryptography.v2.key.KeyEntryStatus;
import com.otilm.api.model.connector.cryptography.v2.key.KeyEntryV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyEntryV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyPairV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedSecretKeyV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
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

class ListKeysResponseValidationTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final OperationResponseValidator VALIDATOR = new OperationResponseValidator(VALIDATORS.validator());
    private static final String KEY_CONSISTENCY_MESSAGE = "Connector response validation failed: "
            + "items[0].keyConsistentWithStatus: key must be present exactly when status is available or refused";

    private static List<MetadataAttribute> handle(String keyId) {
        MetadataAttributeV3 attribute = new MetadataAttributeV3();
        attribute.setUuid("6b1c9c5e-2f8a-4a7e-9f2d-5b8e1c3a7d40");
        attribute.setName("keyId");
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setProperties(new MetadataAttributeProperties());
        attribute.setContent(List.of(new StringAttributeContentV3(keyId)));
        return List.of(attribute);
    }

    private static List<MetadataAttribute> referenceOnlyHandle(String reference) {
        List<MetadataAttribute> handle = handle("unused");
        handle.get(0).setContent(List.of(new StringAttributeContentV3(reference, null)));
        return handle;
    }

    private static ListedKeyEntryV2Dto unusable(KeyEntryStatus status) {
        ListedKeyEntryV2Dto entry = new ListedKeyEntryV2Dto();
        entry.setStatus(status);
        entry.setStorageName("shared");
        entry.setKeyData(validPrivateKeyDataResponse().getKeyData());
        return entry;
    }

    private static ListedKeyEntryV2Dto unusableSecretKey() {
        ListedKeyEntryV2Dto entry = unusable(KeyEntryStatus.UNADDRESSABLE);
        entry.setKeyData(validSecretKeyData());
        return entry;
    }

    private static ListedKeyEntryV2Dto available(ListedKeyV2Dto key) {
        ListedKeyEntryV2Dto entry = new ListedKeyEntryV2Dto();
        entry.setStatus(KeyEntryStatus.AVAILABLE);
        entry.setStorageName("signer");
        entry.setKey(key);
        return entry;
    }

    private static ListedKeyEntryV2Dto available(String keyId) {
        return available(listed(keyId));
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

    private static ListKeysRequestV2Dto request(int limit) {
        return request(KeyRequestType.KEY_PAIR, limit);
    }

    private static ListKeysRequestV2Dto request(KeyRequestType keyRequestType, int limit) {
        ListKeysRequestV2Dto request = withValidTokenProfileScope(new ListKeysRequestV2Dto());
        request.setKeyRequestType(keyRequestType);
        request.setOffset(0);
        request.setLimit(limit);
        return request;
    }

    private static ListKeysResponseV2Dto page(ListedKeyEntryV2Dto... entries) {
        ListKeysResponseV2Dto response = new ListKeysResponseV2Dto();
        response.setItems(List.of(entries));
        response.setTotalItems((long) entries.length);
        return response;
    }

    @Test
    void validateListKeys_acceptsAPrivateOnlyKeyPair() {
        // given
        ListedKeyPairV2Dto privateOnly = listed("0b");
        privateOnly.setPublicKeyData(null);

        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(10), page(available("0a"), available(privateOnly)));

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_requiresTheRequest() {
        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(null, page(available("0a")));

        // then
        assertInvalid(result, "Key listing request is required");
    }

    @Test
    void validateListKeys_requiresAResponse() {
        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), null);

        // then
        assertInvalid(result, "Connector response body is required");
    }

    @Test
    void validateListKeys_refusesAKeyPairWithoutPrivateKeyData() {
        // given
        ListedKeyPairV2Dto incomplete = listed("0a");
        incomplete.setPrivateKeyData(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(available(incomplete)));

        // then
        assertInvalid(result,
                "Connector response validation failed: items[0].key.privateKeyData: privateKeyData is required");
    }

    @Test
    void validateListKeys_refusesMoreKeyPairsThanTheLimit() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(1), page(available("0a"), available("0b")));

        // then
        assertInvalid(result, "Connector returned 2 keys on a page that holds 1");
    }

    @Test
    void validateListKeys_refusesAPageReachingBeyondItsTotal() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setOffset(5);
        ListKeysResponseV2Dto page = page(available("0a"), available("0b"));
        page.setTotalItems(6L);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page);

        // then
        assertInvalid(result, "Connector returned 2 keys on a page that holds 1");
    }

    @Test
    void validateListKeys_countsUnusableEntriesTowardTheTotal() {
        // given
        ListKeysResponseV2Dto page = page(available("0a"), unusable(KeyEntryStatus.UNADDRESSABLE));
        page.setTotalItems(1L);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertInvalid(result, "Connector returned 2 keys on a page that holds 1");
    }

    @Test
    void validateListKeys_acceptsAnEmptyPageBeyondTheEndOfTheListing() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setOffset(20);
        ListKeysResponseV2Dto page = page();
        page.setTotalItems(3L);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_requiresTheOffset() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setOffset(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(available("0a")));

        // then
        assertInvalid(result, "Key listing offset is required");
    }

    @Test
    void validateListKeys_countsUnusableEntriesTowardTheLimit() {
        // given
        ListKeysResponseV2Dto page = page(available("0a"), unusable(KeyEntryStatus.UNADDRESSABLE));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(1), page);

        // then
        assertInvalid(result, "Connector returned 2 keys on a page that holds 1");
    }

    @Test
    void validateListKeys_refusesAKeyPairWhoseMetadataCannotServeAsAHandle() {
        // given
        ListedKeyPairV2Dto referenceOnly = listed("0a");
        referenceOnly.setKeyPairMeta(referenceOnlyHandle("signer-a"));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(available(referenceOnly)));

        // then
        assertInvalid(result, "Connector returned a key whose metadata cannot serve as a handle");
    }

    @Test
    void validateListKeys_refusesAShortPageBeforeTheEnd() {
        // given
        ListKeysRequestV2Dto request = request(2);
        ListKeysResponseV2Dto page = page(available("0a"));
        page.setTotalItems(5L);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page);

        // then
        assertInvalid(result, "Connector returned 1 keys on a page that holds 2");
    }

    @Test
    void validateListKeys_acceptsAShortLastPage() {
        // given
        ListKeysRequestV2Dto request = request(2);
        request.setOffset(4);
        ListKeysResponseV2Dto page = page(available("0a"));
        page.setTotalItems(5L);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesAKeyOnAPageBeyondTheEnd() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setOffset(5);
        ListKeysResponseV2Dto page = page(available("0a"));
        page.setTotalItems(2L);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page);

        // then
        assertInvalid(result, "Connector returned 1 keys on a page that holds 0");
    }

    @Test
    void validateListKeys_expectsAFullPageAtTheLargestOffset() {
        // given
        ListKeysRequestV2Dto request = request(1000);
        request.setOffset(Integer.MAX_VALUE);
        ListKeysResponseV2Dto full = page(available("0a"), available("0b"), available("0c"), available("0d"),
                available("0e"));
        full.setTotalItems(Integer.MAX_VALUE + 5L);
        ListKeysResponseV2Dto short4 = page(available("0a"), available("0b"), available("0c"), available("0d"));
        short4.setTotalItems(Integer.MAX_VALUE + 5L);

        // when
        OperationValidationResult accepted = VALIDATOR.validateListKeys(request, full);
        OperationValidationResult refused = VALIDATOR.validateListKeys(request, short4);

        // then
        assertTrue(accepted.isValid());
        assertInvalid(refused, "Connector returned 4 keys on a page that holds 5");
    }

    @Test
    void validateListKeys_acceptsAnUnreadableEntryWithoutKeyData() {
        // given
        ListedKeyEntryV2Dto unreadable = unusable(KeyEntryStatus.UNREADABLE);
        unreadable.setKeyData(null);
        ListKeysResponseV2Dto page = page(available("0a"), unreadable);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesAnEntryWithoutAStatus() {
        // given
        ListedKeyEntryV2Dto incomplete = unusable(KeyEntryStatus.UNADDRESSABLE);
        incomplete.setStatus(null);
        ListKeysResponseV2Dto page = page(available("0a"), incomplete);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertInvalid(result, "Connector response validation failed: items[1].status: status is required");
    }

    @Test
    void validateListKeys_requiresTheLimit() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setLimit(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(available("0a")));

        // then
        assertInvalid(result, "Key listing limit is required");
    }

    @Test
    void validateListKeys_requiresTheKeyRequestType() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setKeyRequestType(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(available("0a")));

        // then
        assertInvalid(result, "Key listing key request type is required");
    }

    @Test
    void validateListKeys_acceptsSecretKeysInASecretKeyListing() {
        // given
        ListKeysResponseV2Dto page = page(available(listedSecretKey("0a")), unusableSecretKey());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(KeyRequestType.SECRET, 10), page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesASecretKeyInAKeyPairListing() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(10), page(available("0a"), available(listedSecretKey("0b"))));

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateListKeys_refusesAKeyPairInASecretKeyListing() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(KeyRequestType.SECRET, 10), page(available("0a")));

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateListKeys_refusesAnUnusableSecretKeyInAKeyPairListing() {
        // given
        ListKeysResponseV2Dto page = page(available("0a"), unusableSecretKey());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateListKeys_refusesAnUnusablePrivateKeyInASecretKeyListing() {
        // given
        ListKeysResponseV2Dto page = page(available(listedSecretKey("0a")), unusable(KeyEntryStatus.UNADDRESSABLE));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(KeyRequestType.SECRET, 10), page);

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateListKeys_refusesASecretKeyWithoutKeyData() {
        // given
        ListedSecretKeyV2Dto incomplete = listedSecretKey("0a");
        incomplete.setKeyData(null);

        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(KeyRequestType.SECRET, 10), page(available(incomplete)));

        // then
        assertInvalid(result, "Connector response validation failed: items[0].key.keyData: keyData is required");
    }

    @Test
    void validateListKeys_refusesAnAvailableEntryWithoutAKey() {
        // given
        ListedKeyEntryV2Dto withoutKey = available("0a");
        withoutKey.setKey(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(withoutKey));

        // then
        assertInvalid(result, KEY_CONSISTENCY_MESSAGE);
    }

    @Test
    void validateListKeys_refusesAKeyOnAnUnaddressableEntry() {
        // given
        ListedKeyEntryV2Dto withKey = unusable(KeyEntryStatus.UNADDRESSABLE);
        withKey.setKeyData(null);
        withKey.setKey(listed("0a"));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(withKey));

        // then
        assertInvalid(result, KEY_CONSISTENCY_MESSAGE);
    }

    @Test
    void validateListKeys_acceptsARefusedEntryInItsPlace() {
        // given
        ListedKeyEntryV2Dto refused = available("0b");
        refused.setStatus(KeyEntryStatus.REFUSED);
        refused.setDiagnostic("The signature activation module holds this key.");

        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(10), page(available("0a"), refused, unusable(KeyEntryStatus.UNADDRESSABLE)));

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesKeyDataBesideTheKey() {
        // given
        ListedKeyEntryV2Dto both = available("0a");
        both.setKeyData(validPrivateKeyDataResponse().getKeyData());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(both));

        // then
        assertInvalid(result, "Connector response validation failed: "
                + "items[0].keyDataOnlyWithoutKey: keyData must be absent when key is present");
    }

    @Test
    void validateListKeys_refusesAnInvalidHandleEntry() {
        // given
        ListedKeyEntryV2Dto invalidHandle = unusable(KeyEntryStatus.INVALID_HANDLE);
        invalidHandle.setKeyData(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(invalidHandle));

        // then
        assertInvalid(result, "Connector response validation failed: items[0].statusOfAListedKey: "
                + "status must not be invalidHandle. invalidHandle is reported only by resolution");
    }

    @Test
    void validateListKeys_refusesKeyDataOnANotFoundEntry() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(10), page(unusable(KeyEntryStatus.NOT_FOUND)));

        // then
        assertInvalid(result, "Connector response validation failed: items[0].keyDataOnlyForAMatchedKey: "
                + "keyData must be absent when status is notFound or invalidHandle");
    }

    @Test
    void validateListKeys_refusesADiagnosticOverTheCap() {
        // given
        ListedKeyEntryV2Dto verbose = unusable(KeyEntryStatus.UNREADABLE);
        verbose.setDiagnostic("x".repeat(KeyEntryV2Dto.MAX_DIAGNOSTIC_LENGTH + 1));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(verbose));

        // then
        assertInvalid(result, "Connector response validation failed: items[0].diagnosticWithinLength: "
                + "diagnostic must not exceed " + KeyEntryV2Dto.MAX_DIAGNOSTIC_LENGTH + " characters");
    }

    @Test
    void validateListKeys_countsTheDiagnosticInCodePoints() {
        // given
        ListedKeyEntryV2Dto supplementary = unusable(KeyEntryStatus.UNREADABLE);
        supplementary.setDiagnostic("\uD83D\uDD11".repeat(KeyEntryV2Dto.MAX_DIAGNOSTIC_LENGTH));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(supplementary));

        // then
        assertTrue(result.isValid());
    }

    private static void assertInvalid(OperationValidationResult result, String expectedMessage) {
        assertFalse(result.isValid());
        assertEquals(expectedMessage, result.getCause().getMessage());
    }
}
