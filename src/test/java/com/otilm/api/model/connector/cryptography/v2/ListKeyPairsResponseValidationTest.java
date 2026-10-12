package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeyPairsRequestV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListKeyPairsResponseV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.ListedKeyPairV2Dto;
import com.otilm.api.model.connector.cryptography.v2.key.UnaddressableKeyV2Dto;
import com.otilm.api.testsupport.ValidatorFixture;
import java.util.List;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPrivateKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validPublicKeyDataResponse;
import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.withValidTokenProfileScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListKeyPairsResponseValidationTest {

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

    private static ListedKeyPairV2Dto listed(String keyId) {
        ListedKeyPairV2Dto pair = new ListedKeyPairV2Dto();
        pair.setKeyPairMeta(handle(keyId));
        pair.setLabel("signer " + keyId);
        pair.setPrivateKeyData(validPrivateKeyDataResponse());
        pair.setPublicKeyData(validPublicKeyDataResponse());
        return pair;
    }

    private static ListKeyPairsRequestV2Dto request(int limit) {
        ListKeyPairsRequestV2Dto request = withValidTokenProfileScope(new ListKeyPairsRequestV2Dto());
        request.setOffset(0);
        request.setLimit(limit);
        return request;
    }

    private static ListKeyPairsResponseV2Dto page(ListedKeyPairV2Dto... pairs) {
        ListKeyPairsResponseV2Dto response = new ListKeyPairsResponseV2Dto();
        response.setItems(List.of(pairs));
        response.setTotalItems((long) pairs.length);
        return response;
    }

    @Test
    void validateListKeyPairs_acceptsAPrivateOnlyKeyPair() {
        // given
        ListedKeyPairV2Dto privateOnly = listed("0b");
        privateOnly.setPublicKeyData(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(10), page(listed("0a"), privateOnly));

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeyPairs_requiresTheRequest() {
        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(null, page(listed("0a")));

        // then
        assertInvalid(result, "Key pair listing request is required");
    }

    @Test
    void validateListKeyPairs_requiresAResponse() {
        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(10), null);

        // then
        assertInvalid(result, "Connector response body is required");
    }

    @Test
    void validateListKeyPairs_refusesAKeyPairWithoutPrivateKeyData() {
        // given
        ListedKeyPairV2Dto incomplete = listed("0a");
        incomplete.setPrivateKeyData(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(10), page(incomplete));

        // then
        assertInvalid(result,
                "Connector response validation failed: items[0].privateKeyData: privateKeyData is required");
    }

    @Test
    void validateListKeyPairs_refusesMoreKeyPairsThanTheLimit() {
        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(1), page(listed("0a"), listed("0b")));

        // then
        assertInvalid(result, "Connector returned 2 key pairs; the request allows at most 1");
    }

    @Test
    void validateListKeyPairs_acceptsTheKeyPairsTheFilterNames() {
        // given
        ListKeyPairsRequestV2Dto request = request(10);
        request.setKeyPairMetaFilter(List.of(handle("0a"), handle("0b")));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request, page(listed("0b"), listed("0a")));

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeyPairs_refusesAKeyPairOutsideTheFilter() {
        // given
        ListKeyPairsRequestV2Dto request = request(10);
        request.setKeyPairMetaFilter(List.of(handle("0a")));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request, page(listed("0a"), listed("0c")));

        // then
        assertInvalid(result, "Connector returned a key pair the filter does not name");
    }

    @Test
    void validateListKeyPairs_countsUnaddressableKeysTowardTheLimit() {
        // given
        ListKeyPairsResponseV2Dto page = page(listed("0a"));
        page.setUnaddressableKeys(List.of(unaddressable()));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(1), page);

        // then
        assertInvalid(result, "Connector returned 2 key pairs; the request allows at most 1");
    }

    @Test
    void validateListKeyPairs_acceptsUnaddressableKeysInAnUnfilteredPage() {
        // given
        ListKeyPairsResponseV2Dto page = page(listed("0a"));
        page.setUnaddressableKeys(List.of(unaddressable()));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(2), page);

        // then
        assertTrue(result.isValid());
    }

    @Test
    void validateListKeyPairs_refusesUnaddressableKeysInAFilteredPage() {
        // given
        ListKeyPairsRequestV2Dto request = request(10);
        request.setKeyPairMetaFilter(List.of(handle("0a")));
        ListKeyPairsResponseV2Dto page = page(listed("0a"));
        page.setUnaddressableKeys(List.of(unaddressable()));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request, page);

        // then
        assertInvalid(result, "Connector returned unaddressable keys to a filtered listing");
    }

    @Test
    void validateListKeyPairs_refusesAKeyPairNoFilterCanName() {
        // given
        ListedKeyPairV2Dto referenceOnly = listed("0a");
        referenceOnly.setKeyPairMeta(referenceOnlyHandle("signer-a"));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(10), page(referenceOnly));

        // then
        assertInvalid(result, "Connector returned a key pair whose keyPairMeta no filter can name");
    }

    @Test
    void validateListKeyPairs_refusesAnUnaddressableKeyWithoutKeyData() {
        // given
        ListKeyPairsResponseV2Dto page = page(listed("0a"));
        UnaddressableKeyV2Dto incomplete = unaddressable();
        incomplete.setKeyData(null);
        page.setUnaddressableKeys(List.of(incomplete));

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request(10), page);

        // then
        assertInvalid(result,
                "Connector response validation failed: unaddressableKeys[0].keyData: keyData is required");
    }

    @Test
    void validateListKeyPairs_requiresTheLimit() {
        // given
        ListKeyPairsRequestV2Dto request = request(10);
        request.setLimit(null);

        // when
        OperationValidationResult result = VALIDATOR.validateListKeyPairs(request, page(listed("0a")));

        // then
        assertInvalid(result, "Key pair listing limit is required");
    }

    private static void assertInvalid(OperationValidationResult result, String expectedMessage) {
        assertFalse(result.isValid());
        assertEquals(expectedMessage, result.getCause().getMessage());
    }
}
