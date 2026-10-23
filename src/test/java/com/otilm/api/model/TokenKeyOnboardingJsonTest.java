package com.otilm.api.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.client.cryptography.key.OnboardTokenKeysRequestDto;
import com.otilm.api.model.client.cryptography.key.TokenKeySelectionDto;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.connector.cryptography.v2.key.KeyHandles;
import com.otilm.api.model.core.cryptography.key.TokenKeyDto;
import com.otilm.api.model.core.cryptography.key.TokenKeyOnboardingResultDto;
import com.otilm.api.model.core.cryptography.key.TokenKeyStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenKeyOnboardingJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void aRequestCarriesTheCustomAttributesOfItsNewKeys() throws Exception {
        // given
        OnboardTokenKeysRequestDto request = new OnboardTokenKeysRequestDto(
                List.of(new TokenKeySelectionDto(validMetadata(), "name")),
                List
                        .of(new RequestAttributeV3(UUID.randomUUID(), "department", AttributeContentType.STRING,
                                List.of(new StringAttributeContentV3("signing")))));

        // when
        OnboardTokenKeysRequestDto read = mapper
                .readValue(mapper.writeValueAsString(request), OnboardTokenKeysRequestDto.class);

        // then
        assertEquals(1, read.getCustomAttributes().size());
        assertEquals("department", read.getCustomAttributes().get(0).getName());
    }

    @Test
    void aSelectionReadsBackTheMetadataOfItsKeyPair() throws Exception {
        // given
        List<MetadataAttribute> keyPairMeta = validMetadata();
        TokenKeySelectionDto selection = new TokenKeySelectionDto(keyPairMeta, null);

        // when
        TokenKeySelectionDto read = mapper.readValue(mapper.writeValueAsString(selection), TokenKeySelectionDto.class);

        // then
        assertEquals(keyPairMeta.get(0).getName(), read.getKeyPairMeta().get(0).getName());
        assertEquals(dataOf(keyPairMeta.get(0)), dataOf(read.getKeyPairMeta().get(0)));
        assertTrue(KeyHandles.isUnambiguous(read.getKeyPairMeta()));
    }

    @Test
    void aResultSaysWhenTheKeyPairJoinedAnExistingKey() {
        // given
        TokenKeyOnboardingResultDto result = new TokenKeyOnboardingResultDto();
        result.setOnboarded(true);
        result.setIntoExistingKey(true);

        // when
        JsonNode json = mapper.valueToTree(result);

        // then
        assertTrue(json.path("onboarded").asBoolean());
        assertTrue(json.path("intoExistingKey").asBoolean());
    }

    @Test
    void anEntryCarriesItsStatusAndTheReasonItCannotBeOnboarded() {
        // given
        TokenKeyDto entry = new TokenKeyDto();
        entry.setStatus(TokenKeyStatus.IN_INVENTORY_WITHOUT_TOKEN);
        entry.setReason("Key k holds the same public key, and you may not update it.");

        // when
        JsonNode json = mapper.valueToTree(entry);

        // then
        assertEquals("inInventoryWithoutToken", json.path("status").asText());
        assertEquals("Key k holds the same public key, and you may not update it.", json.path("reason").asText());
    }

    @Test
    void aKeyPairOnboardableIntoAnExistingKeyHasAStatusCodeOfItsOwn() {
        // when
        TokenKeyStatus status = TokenKeyStatus.findByCode("onboardableIntoKey");

        // then
        assertEquals(TokenKeyStatus.ONBOARDABLE_INTO_KEY, status);
    }

    @Test
    void anEntryWithoutAnAlgorithmRoundTrips() throws Exception {
        // given
        TokenKeyDto entry = new TokenKeyDto();
        entry.setStatus(TokenKeyStatus.UNREADABLE);

        // when
        TokenKeyDto read = mapper.readValue(mapper.writeValueAsString(entry), TokenKeyDto.class);

        // then
        assertNull(read.getKeyAlgorithm());
        assertEquals(TokenKeyStatus.UNREADABLE, read.getStatus());
    }

    @Test
    void theStatusesOfUnusableKeysHaveCodesOfTheirOwn() {
        // when
        List<TokenKeyStatus> statuses = Stream
                .of("noUsableId", "unsupportedKeyType", "unreadable", "gone")
                .map(TokenKeyStatus::findByCode)
                .toList();

        // then
        assertEquals(List
                .of(TokenKeyStatus.NO_USABLE_ID, TokenKeyStatus.UNSUPPORTED_KEY_TYPE, TokenKeyStatus.UNREADABLE,
                        TokenKeyStatus.GONE),
                statuses);
    }

    private static List<Object> dataOf(MetadataAttribute attribute) {
        List<? extends AttributeContent> content = attribute.getContent();
        return content.stream().<Object>map(AttributeContent::getData).toList();
    }
}
