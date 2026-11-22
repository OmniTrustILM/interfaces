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
import com.otilm.api.model.core.cryptography.key.TokenKeyOnboardingOutcome;
import com.otilm.api.model.core.cryptography.key.TokenKeyOnboardingResultDto;
import com.otilm.api.model.core.cryptography.key.TokenKeyStatus;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

import static com.otilm.api.model.connector.cryptography.v2.utils.CryptographyDtoFixtures.validMetadata;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        result.setOutcome(TokenKeyOnboardingOutcome.JOINED_EXISTING_KEY);

        // when
        JsonNode json = mapper.valueToTree(result);

        // then
        assertEquals("joinedExistingKey", json.path("outcome").asText());
    }

    @Test
    void theOutcomesOfOnboardingHaveCodesOfTheirOwn() {
        // when
        List<TokenKeyOnboardingOutcome> outcomes = Stream
                .of("onboarded", "joinedExistingKey", "nameTaken", "blocked", "failed")
                .map(TokenKeyOnboardingOutcome::findByCode)
                .toList();

        // then
        assertEquals(List.of(TokenKeyOnboardingOutcome.values()), outcomes);
    }

    @Test
    void aBlockedResultCarriesItsStatusAndTheConnectorsDiagnostic() {
        // given
        TokenKeyOnboardingResultDto result = new TokenKeyOnboardingResultDto();
        result.setOutcome(TokenKeyOnboardingOutcome.BLOCKED);
        result.setStatus(TokenKeyStatus.REFUSED);
        result.setDiagnostic("The signature activation module holds this key.");

        // when
        JsonNode json = mapper.valueToTree(result);

        // then
        assertEquals("blocked", json.path("outcome").asText());
        assertEquals("refused", json.path("status").asText());
        assertEquals("The signature activation module holds this key.", json.path("diagnostic").asText());
    }

    @Test
    void anEntryCarriesTheConnectorsDiagnostic() throws Exception {
        // given
        String json = "{\"status\":\"unreadable\",\"diagnostic\":\"CKR_ATTRIBUTE_SENSITIVE\"}";

        // when
        TokenKeyDto entry = mapper.readValue(json, TokenKeyDto.class);

        // then
        assertEquals("CKR_ATTRIBUTE_SENSITIVE", entry.getDiagnostic());
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
                .of("unaddressable", "unsupportedKeyType", "unreadable", "gone", "refused", "publicKeyUnavailable")
                .map(TokenKeyStatus::findByCode)
                .toList();

        // then
        assertEquals(List
                .of(TokenKeyStatus.UNADDRESSABLE, TokenKeyStatus.UNSUPPORTED_KEY_TYPE, TokenKeyStatus.UNREADABLE,
                        TokenKeyStatus.GONE, TokenKeyStatus.REFUSED, TokenKeyStatus.PUBLIC_KEY_UNAVAILABLE),
                statuses);
    }

    @Test
    void anEntryNamesItsKeyByTheStorageName() throws Exception {
        // given
        String json = "{\"storageName\":\"signer\",\"status\":\"onboardable\"}";

        // when
        TokenKeyDto entry = mapper.readValue(json, TokenKeyDto.class);

        // then
        assertEquals("signer", mapper.valueToTree(entry).path("storageName").asText());
    }

    @Test
    void theConnectorsDiagnosticStaysOutOfTheTextOfAKeyAndOfAResult() {
        // given
        String diagnostic = "CKR_PIN_INCORRECT";
        TokenKeyDto entry = TokenKeyDto.builder().diagnostic(diagnostic).build();
        TokenKeyOnboardingResultDto result = TokenKeyOnboardingResultDto.builder().diagnostic(diagnostic).build();

        // when
        String rendered = entry + " " + result;

        // then
        assertFalse(rendered.contains(diagnostic), rendered);
    }

    private static List<Object> dataOf(MetadataAttribute attribute) {
        List<? extends AttributeContent> content = attribute.getContent();
        return content.stream().<Object>map(AttributeContent::getData).toList();
    }
}
