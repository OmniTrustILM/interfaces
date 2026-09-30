package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.IntegerAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.EncryptionAlgorithm;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EncryptionAlgorithmAttributeTest {
    @Test
    void definition_publishesOnlyTheProvidersOfferedProfiles() {
        // given
        List<EncryptionAlgorithm> offered = List
                .of(EncryptionAlgorithm.RSA_PKCS1_V1_5, EncryptionAlgorithm.RSA_OAEP_SHA256);

        // when
        DataAttributeV3 definition = EncryptionAlgorithmAttribute.definition(offered);

        // then
        assertEquals(EncryptionAlgorithmAttribute.ATTRIBUTE_UUID.toString(), definition.getUuid());
        assertEquals(EncryptionAlgorithmAttribute.NAME, definition.getName());
        assertEquals(offered.stream().map(EncryptionAlgorithm::getCode).toList(),
                definition.getContent().stream().map(AttributeContent::getData).toList());
        assertTrue(definition.getProperties().isRequired());
        assertTrue(definition.getProperties().isList());
        assertTrue(definition.getProperties().isVisible());
        assertFalse(definition.getProperties().isMultiSelect());
        assertFalse(definition.getProperties().isReadOnly());
        assertEquals(AttributeContentType.STRING, definition.getContentType());
        assertEquals(AttributeVersion.V3, definition.getSchemaVersion());
        assertEquals(3, definition.getVersion());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSupportedProfiles")
    void definition_rejectsNullProfiles(List<EncryptionAlgorithm> supported, String expectedMessage) {
        // given
        // when
        Executable define = () -> EncryptionAlgorithmAttribute.definition(supported);

        // then
        assertEquals(expectedMessage, assertThrows(NullPointerException.class, define).getMessage());
    }

    static Stream<Arguments> invalidSupportedProfiles() {
        return Stream
                .of(arguments(named("null collection", null), "supported must not be null"), arguments(
                        named("null profile", Collections.singletonList(null)), "algorithm must not be null"));
    }

    @Test
    void request_rejectsANullAlgorithm() {
        // given
        EncryptionAlgorithm missingAlgorithm = null;
        String expectedMessage = "algorithm must not be null";

        // when
        Executable select = () -> EncryptionAlgorithmAttribute.request(missingAlgorithm);

        // then
        assertEquals(expectedMessage, assertThrows(NullPointerException.class, select).getMessage());
    }

    @ParameterizedTest
    @EnumSource(EncryptionAlgorithm.class)
    void request_serializesAsV3_andRoundTripsThroughTheReader(EncryptionAlgorithm algorithm) throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        RequestAttributeV3 request = EncryptionAlgorithmAttribute.request(algorithm);

        // when
        String json = mapper.writeValueAsString(request);
        JsonNode tree = mapper.readTree(json);
        RequestAttribute decoded = mapper.readValue(json, RequestAttribute.class);

        // then
        assertEquals("v3", tree.get("version").asText());
        assertEquals("string", tree.get("contentType").asText());
        assertEquals("string", tree.get("content").get(0).get("contentType").asText());
        assertEquals(EncryptionAlgorithmAttribute.ATTRIBUTE_UUID.toString(), tree.get("uuid").asText());
        assertEquals(algorithm, EncryptionAlgorithmAttribute.selectedAlgorithm(List.of(decoded)));
    }

    @ParameterizedTest
    @MethodSource("malformedSelections")
    void selectedAlgorithm_reportsTheSelectionError(List<RequestAttribute> attributes, String expectedMessage) {
        // given
        // when
        Executable read = () -> EncryptionAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(expectedMessage, assertThrows(ValidationException.class, read).getMessage());
    }

    static Stream<Arguments> malformedSelections() {
        RequestAttributeV3 unknown = selection();
        unknown.setContent(List.of(new StringAttributeContentV3("AES/GCM/NoPadding")));
        RequestAttributeV3 wrongName = selection();
        wrongName.setName("otherSelector");
        RequestAttributeV3 wrongId = selection();
        wrongId.setUuid(UUID.randomUUID());
        RequestAttributeV3 wrongType = selection();
        wrongType.setContentType(AttributeContentType.INTEGER);
        RequestAttributeV3 wrongVersion = selection();
        wrongVersion.setVersion(AttributeVersion.V2);
        RequestAttributeV3 missingContent = selection();
        missingContent.setContent(null);
        RequestAttributeV3 emptyContent = selection();
        emptyContent.setContent(List.of());
        RequestAttributeV3 nullValue = selection();
        nullValue.setContent(Collections.singletonList(null));
        RequestAttributeV3 wrongValueType = selection();
        wrongValueType.getContent().get(0).setContentType(AttributeContentType.INTEGER);
        RequestAttributeV3 multipleValues = selection();
        multipleValues
                .setContent(List
                        .of(new StringAttributeContentV3(EncryptionAlgorithm.RSA_PKCS1_V1_5.getCode()),
                                new StringAttributeContentV3(EncryptionAlgorithm.RSA_OAEP_SHA256.getCode())));
        RequestAttributeV3 nonString = selection();
        nonString.setContent(List.of(new IntegerAttributeContentV3(1)));
        RequestAttributeV3 emptyValue = selection();
        emptyValue.setContent(List.of(new StringAttributeContentV3()));
        RequestAttributeV2 legacyEnvelope = new RequestAttributeV2();
        legacyEnvelope.setUuid(EncryptionAlgorithmAttribute.ATTRIBUTE_UUID);
        legacyEnvelope.setName(EncryptionAlgorithmAttribute.NAME);
        legacyEnvelope.setContentType(AttributeContentType.STRING);
        legacyEnvelope.setContent(List.of(new StringAttributeContentV2(EncryptionAlgorithm.RSA_PKCS1_V1_5.getCode())));
        String identity = "attribute with name 'encryptionAlgorithm' and UUID '"
                + EncryptionAlgorithmAttribute.ATTRIBUTE_UUID + "'";
        String noSelection = "Cipher attributes must select one value of the " + identity + ".";
        String duplicate = "Cipher " + identity + " must be supplied once.";
        String wrongEnvelope = "Cipher " + identity + " must be a v3 attribute.";
        String wrongContent = "Cipher " + identity + " must carry a string value.";
        return Stream
                .of(arguments(named("null list", null), noSelection),
                        arguments(named("empty list", List.of()), noSelection),
                        arguments(named("unknown algorithm", List.of(unknown)), "Unknown encryption algorithm code."),
                        arguments(named("wrong UUID", List.of(wrongId)), noSelection),
                        arguments(named("wrong name", List.of(wrongName)), noSelection),
                        arguments(named("reserved UUID duplicated under another name", List.of(selection(), wrongName)),
                                duplicate),
                        arguments(named("wrong content type", List.of(wrongType)), wrongContent),
                        arguments(named("wrong version", List.of(wrongVersion)), wrongEnvelope),
                        arguments(named("missing content", List.of(missingContent)), noSelection),
                        arguments(named("empty content", List.of(emptyContent)), noSelection),
                        arguments(named("null value", List.of(nullValue)), noSelection),
                        arguments(named("multiple values", List.of(multipleValues)), noSelection),
                        arguments(named("non-string", List.of(nonString)), wrongContent),
                        arguments(named("wrong value content type", List.of(wrongValueType)), wrongContent),
                        arguments(named("empty value", List.of(emptyValue)), noSelection),
                        arguments(named("v2 envelope", List.of(legacyEnvelope)), wrongEnvelope),
                        arguments(named("duplicate selector", List.of(selection(), selection())), duplicate));
    }

    @Test
    void selectedAlgorithm_rejectsTheReservedUuidAndNameOnDifferentAttributes() {
        // given
        String providerName = "providerEncryptionAlgorithm";
        EncryptionAlgorithm expected = EncryptionAlgorithm.RSA_OAEP_SHA256;
        RequestAttributeV3 selection = EncryptionAlgorithmAttribute.request(expected);
        selection.setName(providerName);
        RequestAttributeV3 unrelated = EncryptionAlgorithmAttribute.request(EncryptionAlgorithm.RSA_PKCS1_V1_5);
        unrelated.setUuid(UUID.randomUUID());
        List<RequestAttribute> attributes = List.of(unrelated, selection);

        // when
        Executable read = () -> EncryptionAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertThrows(ValidationException.class, read);
    }

    @Test
    void selectedAlgorithm_ignoresUnrelatedAttributes() {
        // given
        RequestAttributeV3 unrelated = new RequestAttributeV3();
        unrelated.setName("providerOption");
        List<RequestAttribute> attributes = Arrays.asList(null, unrelated, selection());

        // when
        EncryptionAlgorithm selected = EncryptionAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(EncryptionAlgorithm.RSA_OAEP_SHA256, selected);
    }

    private static RequestAttributeV3 selection() {
        return EncryptionAlgorithmAttribute.request(EncryptionAlgorithm.RSA_OAEP_SHA256);
    }
}
