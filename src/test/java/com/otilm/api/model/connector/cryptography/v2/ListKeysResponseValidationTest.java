package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeysResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyPairV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedSecretKeyV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.UnusableKeyReason;
import com.otilm.api.model.connector.cryptography.v2.key.UnusableKeyV2Dto;
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

    private static UnusableKeyV2Dto unusable(UnusableKeyReason reason) {
        UnusableKeyV2Dto key = new UnusableKeyV2Dto();
        key.setLabel("shared");
        key.setReason(reason);
        key.setKeyData(validPrivateKeyDataResponse().getKeyData());
        return key;
    }

    private static UnusableKeyV2Dto unusableSecretKey() {
        UnusableKeyV2Dto key = unusable(UnusableKeyReason.UNADDRESSABLE);
        key.setKeyData(validSecretKeyData());
        return key;
    }

    private static ListedKeyPairV2Dto listed(String keyId) {
        ListedKeyPairV2Dto pair = new ListedKeyPairV2Dto();
        pair.setKeyPairMeta(handle(keyId));
        pair.setLabel("signer " + keyId);
        pair.setPrivateKeyData(validPrivateKeyDataResponse());
        pair.setPublicKeyData(validPublicKeyDataResponse());
        return pair;
    }

    private static ListedSecretKeyV2Dto listedSecretKey(String keyId) {
        ListedSecretKeyV2Dto key = new ListedSecretKeyV2Dto();
        key.setKeyMeta(handle(keyId));
        key.setLabel("wrapping " + keyId);
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

    private static ListKeysResponseV2Dto page(ListedKeyV2Dto... keys) {
        ListKeysResponseV2Dto response = new ListKeysResponseV2Dto();
        response.setItems(List.of(keys));
        response.setTotalItems((long) keys.length);
        return response;
    }

    private static ListKeysResponseV2Dto withUnusable(ListKeysResponseV2Dto page, UnusableKeyV2Dto... keys) {
        page.setUnusableKeys(List.of(keys));
        page.setTotalItems(page.getTotalItems() + keys.length);
        return page;
    }

    @Test
    void validateListKeys_acceptsAPrivateOnlyKeyPair() {
        // given
        ListedKeyPairV2Dto privateOnly = listed("0b");
        privateOnly.setPublicKeyData(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(listed("0a"), privateOnly));

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_requiresTheRequest() {
        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(null, page(listed("0a")));

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
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(incomplete));

        // then
        assertInvalid(result,
                "Connector response validation failed: items[0].privateKeyData: privateKeyData is required");
    }

    @Test
    void validateListKeys_refusesMoreKeyPairsThanTheLimit() {
        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(1), page(listed("0a"), listed("0b")));

        // then
        assertInvalid(result, "Connector returned 2 keys on a page that holds 1");
    }

    @Test
    void validateListKeys_refusesAPageReachingBeyondItsTotal() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setOffset(5);
        ListKeysResponseV2Dto page = page(listed("0a"), listed("0b"));
        page.setTotalItems(6L);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page);

        // then
        assertInvalid(result, "Connector returned 2 keys on a page that holds 1");
    }

    @Test
    void validateListKeys_countsUnusableKeysTowardTheTotal() {
        // given
        ListKeysResponseV2Dto page = withUnusable(page(listed("0a")), unusable(UnusableKeyReason.UNADDRESSABLE));
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
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(listed("0a")));

        // then
        assertInvalid(result, "Key listing offset is required");
    }

    @Test
    void validateListKeys_countsUnusableKeysTowardTheLimit() {
        // given
        ListKeysResponseV2Dto page = withUnusable(page(listed("0a")), unusable(UnusableKeyReason.UNADDRESSABLE));

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
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(referenceOnly));

        // then
        assertInvalid(result, "Connector returned a key whose metadata cannot serve as a handle");
    }

    @Test
    void validateListKeys_refusesAShortPageBeforeTheEnd() {
        // given
        ListKeysRequestV2Dto request = request(2);
        ListKeysResponseV2Dto page = page(listed("0a"));
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
        ListKeysResponseV2Dto page = page(listed("0a"));
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
        ListKeysResponseV2Dto page = page(listed("0a"));
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
        ListKeysResponseV2Dto full = page(listed("0a"), listed("0b"), listed("0c"), listed("0d"), listed("0e"));
        full.setTotalItems(Integer.MAX_VALUE + 5L);
        ListKeysResponseV2Dto short4 = page(listed("0a"), listed("0b"), listed("0c"), listed("0d"));
        short4.setTotalItems(Integer.MAX_VALUE + 5L);

        // when
        OperationValidationResult accepted = VALIDATOR.validateListKeys(request, full);
        OperationValidationResult refused = VALIDATOR.validateListKeys(request, short4);

        // then
        assertTrue(accepted.isValid());
        assertInvalid(refused, "Connector returned 4 keys on a page that holds 5");
    }

    @Test
    void validateListKeys_acceptsAnUnusableKeyWithoutKeyData() {
        // given
        UnusableKeyV2Dto unreadable = unusable(UnusableKeyReason.UNREADABLE);
        unreadable.setKeyData(null);
        ListKeysResponseV2Dto page = withUnusable(page(listed("0a")), unreadable);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesAnUnusableKeyWithoutAReason() {
        // given
        UnusableKeyV2Dto incomplete = unusable(UnusableKeyReason.UNADDRESSABLE);
        incomplete.setReason(null);
        ListKeysResponseV2Dto page = withUnusable(page(listed("0a")), incomplete);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertInvalid(result, "Connector response validation failed: unusableKeys[0].reason: reason is required");
    }

    @Test
    void validateListKeys_requiresTheLimit() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setLimit(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(listed("0a")));

        // then
        assertInvalid(result, "Key listing limit is required");
    }

    @Test
    void validateListKeys_requiresTheKeyRequestType() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setKeyRequestType(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(listed("0a")));

        // then
        assertInvalid(result, "Key listing key request type is required");
    }

    @Test
    void validateListKeys_acceptsSecretKeysInASecretKeyListing() {
        // given
        ListKeysResponseV2Dto page = withUnusable(page(listedSecretKey("0a")), unusableSecretKey());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(KeyRequestType.SECRET, 10), page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesASecretKeyInAKeyPairListing() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(10), page(listed("0a"), listedSecretKey("0b")));

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateListKeys_refusesAKeyPairInASecretKeyListing() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(KeyRequestType.SECRET, 10), page(listed("0a")));

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateListKeys_refusesAnUnusableSecretKeyInAKeyPairListing() {
        // given
        ListKeysResponseV2Dto page = withUnusable(page(listed("0a")), unusableSecretKey());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertInvalid(result, "Connector returned a key of another type than the request names");
    }

    @Test
    void validateListKeys_refusesAnUnusablePrivateKeyInASecretKeyListing() {
        // given
        ListKeysResponseV2Dto page = withUnusable(page(listedSecretKey("0a")),
                unusable(UnusableKeyReason.UNADDRESSABLE));

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
                .validateListKeys(request(KeyRequestType.SECRET, 10), page(incomplete));

        // then
        assertInvalid(result, "Connector response validation failed: items[0].keyData: keyData is required");
    }

    private static void assertInvalid(OperationValidationResult result, String expectedMessage) {
        assertFalse(result.isValid());
        assertEquals(expectedMessage, result.getCause().getMessage());
    }
}
