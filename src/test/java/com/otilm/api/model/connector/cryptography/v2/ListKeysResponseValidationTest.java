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
import com.otilm.api.model.connector.cryptography.v2.key.UnaddressableKeyV2Dto;
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

    private static UnaddressableKeyV2Dto unaddressable() {
        UnaddressableKeyV2Dto key = new UnaddressableKeyV2Dto();
        key.setLabel("shared");
        key.setKeyData(validPrivateKeyDataResponse().getKeyData());
        return key;
    }

    private static UnaddressableKeyV2Dto unaddressableSecretKey() {
        UnaddressableKeyV2Dto key = new UnaddressableKeyV2Dto();
        key.setLabel("shared");
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

    private static ListKeysResponseV2Dto withUnaddressable(ListKeysResponseV2Dto page, UnaddressableKeyV2Dto... keys) {
        page.setUnaddressableKeys(List.of(keys));
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
        assertInvalid(result, "Connector returned 2 keys; the request allows at most 1");
    }

    @Test
    void validateListKeys_acceptsTheKeyPairsTheFilterNames() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setKeyMetaFilter(List.of(handle("0a"), handle("0b")));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(listed("0b"), listed("0a")));

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesAKeyPairOutsideTheFilter() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setKeyMetaFilter(List.of(handle("0a")));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page(listed("0a"), listed("0c")));

        // then
        assertInvalid(result, "Connector returned a key the filter does not name");
    }

    @Test
    void validateListKeys_countsUnaddressableKeysTowardTheLimit() {
        // given
        ListKeysResponseV2Dto page = withUnaddressable(page(listed("0a")), unaddressable());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(1), page);

        // then
        assertInvalid(result, "Connector returned 2 keys; the request allows at most 1");
    }

    @Test
    void validateListKeys_acceptsUnaddressableKeysInAnUnfilteredPage() {
        // given
        ListKeysResponseV2Dto page = withUnaddressable(page(listed("0a")), unaddressable());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(2), page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeys_refusesUnaddressableKeysInAFilteredPage() {
        // given
        ListKeysRequestV2Dto request = request(10);
        request.setKeyMetaFilter(List.of(handle("0a")));
        ListKeysResponseV2Dto page = withUnaddressable(page(listed("0a")), unaddressable());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request, page);

        // then
        assertInvalid(result, "Connector returned unaddressable keys to a filtered listing");
    }

    @Test
    void validateListKeys_refusesAKeyPairNoFilterCanName() {
        // given
        ListedKeyPairV2Dto referenceOnly = listed("0a");
        referenceOnly.setKeyPairMeta(referenceOnlyHandle("signer-a"));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page(referenceOnly));

        // then
        assertInvalid(result, "Connector returned a key whose metadata no filter can name");
    }

    @Test
    void validateListKeys_refusesAnUnaddressableKeyWithoutKeyData() {
        // given
        UnaddressableKeyV2Dto incomplete = unaddressable();
        incomplete.setKeyData(null);
        ListKeysResponseV2Dto page = withUnaddressable(page(listed("0a")), incomplete);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertInvalid(result,
                "Connector response validation failed: unaddressableKeys[0].keyData: keyData is required");
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
        ListKeysResponseV2Dto page = withUnaddressable(page(listedSecretKey("0a")), unaddressableSecretKey());

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
        assertInvalid(result, "Connector returned a key of another type than the request lists");
    }

    @Test
    void validateListKeys_refusesAKeyPairInASecretKeyListing() {
        // when
        OperationValidationResult result = VALIDATOR
                .validateListKeys(request(KeyRequestType.SECRET, 10), page(listed("0a")));

        // then
        assertInvalid(result, "Connector returned a key of another type than the request lists");
    }

    @Test
    void validateListKeys_refusesAnUnaddressableSecretKeyInAKeyPairListing() {
        // given
        ListKeysResponseV2Dto page = withUnaddressable(page(listed("0a")), unaddressableSecretKey());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(10), page);

        // then
        assertInvalid(result, "Connector returned a key of another type than the request lists");
    }

    @Test
    void validateListKeys_refusesAnUnaddressablePrivateKeyInASecretKeyListing() {
        // given
        ListKeysResponseV2Dto page = withUnaddressable(page(listedSecretKey("0a")), unaddressable());

        // when
        OperationValidationResult result = VALIDATOR.validateListKeys(request(KeyRequestType.SECRET, 10), page);

        // then
        assertInvalid(result, "Connector returned a key of another type than the request lists");
    }

    @Test
    void validateListKeys_matchesTheFilterAgainstTheKeyMetaOfASecretKey() {
        // given
        ListKeysRequestV2Dto request = request(KeyRequestType.SECRET, 10);
        request.setKeyMetaFilter(List.of(handle("0a")));

        // when
        OperationValidationResult named = VALIDATOR.validateListKeys(request, page(listedSecretKey("0a")));
        OperationValidationResult unnamed = VALIDATOR.validateListKeys(request, page(listedSecretKey("0c")));

        // then
        assertTrue(named.isValid());
        assertInvalid(unnamed, "Connector returned a key the filter does not name");
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
