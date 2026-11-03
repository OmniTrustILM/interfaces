package com.otilm.api.model.connector.cryptography.v2.operations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.ObjectAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.ObjectAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SignatureActivationAttributeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SIGNER = "jane.doe|signers.example";
    private static final SignatureActivation ACTIVATION = new SignatureActivation(
            SignatureActivationDecision.PLATFORM_ASSERTED, SIGNER);

    @Test
    void definition_isTheReservedObjectV3DataAttribute() {
        // given
        // when
        DataAttributeV3 definition = SignatureActivationAttribute.definition();

        // then
        assertEquals(SignatureActivationAttribute.ATTRIBUTE_UUID.toString(), definition.getUuid());
        assertEquals(SignatureActivationAttribute.NAME, definition.getName());
        assertEquals(AttributeType.DATA, definition.getType());
        assertEquals(AttributeContentType.OBJECT, definition.getContentType());
        assertEquals(3, definition.getVersion());
    }

    @Test
    void definition_isARequiredHiddenValueWithoutADefault() {
        // given
        // when
        DataAttributeV3 definition = SignatureActivationAttribute.definition();

        // then
        assertNull(definition.getContent());
        assertTrue(definition.getProperties().isRequired());
        assertFalse(definition.getProperties().isVisible());
        assertFalse(definition.getProperties().isList());
        assertFalse(definition.getProperties().isMultiSelect());
    }

    @Test
    void definition_isANewInstanceEachTime() {
        // given
        DataAttributeV3 first = SignatureActivationAttribute.definition();

        // when
        DataAttributeV3 second = SignatureActivationAttribute.definition();

        // then
        assertNotSame(first, second);
        assertNotSame(first.getProperties(), second.getProperties());
        assertEquals(first.getUuid(), second.getUuid());
    }

    @Test
    void request_statesTheActivationAsOneObjectItem() {
        // given
        // when
        RequestAttributeV3 request = SignatureActivationAttribute.request(ACTIVATION);

        // then
        assertEquals(SignatureActivationAttribute.ATTRIBUTE_UUID, request.getUuid());
        assertEquals(SignatureActivationAttribute.NAME, request.getName());
        assertEquals(AttributeContentType.OBJECT, request.getContentType());
        assertEquals(1, request.getContent().size());
        assertEquals(ACTIVATION,
                assertInstanceOf(ObjectAttributeContentV3.class, request.getContent().get(0)).getData());
    }

    @Test
    void request_travelsAsAV3AttributeWithTheDecisionCode() throws Exception {
        // given
        String json = MAPPER.writeValueAsString(List.of(SignatureActivationAttribute.request(ACTIVATION)));

        // when
        JsonNode attribute = MAPPER.readTree(json).get(0);

        // then
        JsonNode item = attribute.get("content").get(0);
        assertEquals("v3", attribute.get("version").textValue());
        assertEquals("object", item.get("contentType").textValue());
        assertEquals("platformAsserted", item.get("data").get("decision").textValue());
        assertEquals(SIGNER, item.get("data").get("signer").textValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "\u0000", "\u2003"})
    void request_rejectsABlankSigner(String signer) {
        // given
        SignatureActivation blankSigner = new SignatureActivation(SignatureActivationDecision.PLATFORM_ASSERTED,
                signer);

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> SignatureActivationAttribute.request(blankSigner));

        // then
        assertEquals("signatureActivation signer must not be blank", failure.getMessage());
    }

    @Test
    void request_rejectsAMissingDecision() {
        // given
        SignatureActivation noDecision = new SignatureActivation(null, SIGNER);

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> SignatureActivationAttribute.request(noDecision));

        // then
        assertEquals("signatureActivation decision must not be null", failure.getMessage());
    }

    @Test
    void statedActivation_isEmptyWhenTheAttributeIsAbsent() {
        // given
        List<RequestAttribute> attributes = List
                .of(SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA));

        // when
        Optional<SignatureActivation> activation = SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertTrue(activation.isEmpty());
    }

    @Test
    void statedActivation_isEmptyWhenNoAttributesAreSupplied() {
        // given
        // when
        Optional<SignatureActivation> activation = SignatureActivationAttribute.statedActivation(null);

        // then
        assertTrue(activation.isEmpty());
    }

    @Test
    void statedActivation_readsTheActivationTheRequestStates() {
        // given
        List<RequestAttribute> attributes = List.of(SignatureActivationAttribute.request(ACTIVATION));

        // when
        Optional<SignatureActivation> activation = SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(Optional.of(ACTIVATION), activation);
    }

    @Test
    void statedActivation_readsTheActivationBackFromTheWire() throws Exception {
        // given
        String json = MAPPER.writeValueAsString(List.of(SignatureActivationAttribute.request(ACTIVATION)));

        // when
        Optional<SignatureActivation> activation = SignatureActivationAttribute.statedActivation(parse(json));

        // then
        assertEquals(Optional.of(ACTIVATION), activation);
    }

    @Test
    void statedActivation_rejectsDuplicateDeclarations() {
        // given
        List<RequestAttribute> attributes = List
                .of(SignatureActivationAttribute.request(ACTIVATION), SignatureActivationAttribute.request(ACTIVATION));

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must be supplied at most once."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedActivation_rejectsTheReservedNameUnderAnotherUuid() {
        // given
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV3(UUID.randomUUID(), SignatureActivationAttribute.NAME,
                        AttributeContentType.OBJECT, List.of(new ObjectAttributeContentV3(ACTIVATION))));

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must be supplied under both its name and its UUID."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedActivation_rejectsTheReservedUuidUnderAnotherName() {
        // given
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV3(SignatureActivationAttribute.ATTRIBUTE_UUID, "activation",
                        AttributeContentType.OBJECT, List.of(new ObjectAttributeContentV3(ACTIVATION))));

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must be supplied under both its name and its UUID."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedActivation_rejectsAV2Attribute() {
        // given
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV2(SignatureActivationAttribute.ATTRIBUTE_UUID,
                        SignatureActivationAttribute.NAME, AttributeContentType.OBJECT,
                        List.of(new ObjectAttributeContentV2(ACTIVATION))));

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must be a v3 attribute."), assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedActivation_rejectsContentThatIsNotASingleItem() {
        // given
        List<RequestAttribute> empty = List.of(signatureActivation(AttributeContentType.OBJECT));
        List<RequestAttribute> two = List
                .of(signatureActivation(AttributeContentType.OBJECT, new ObjectAttributeContentV3(ACTIVATION),
                        new ObjectAttributeContentV3(ACTIVATION)));

        // when
        Executable readEmpty = () -> SignatureActivationAttribute.statedActivation(empty);
        Executable readTwo = () -> SignatureActivationAttribute.statedActivation(two);

        // then
        assertEquals(violation("must carry exactly one content item."),
                assertThrows(ValidationException.class, readEmpty).getMessage());
        assertEquals(violation("must carry exactly one content item."),
                assertThrows(ValidationException.class, readTwo).getMessage());
    }

    @Test
    void statedActivation_rejectsAContentTypeOtherThanObject() {
        // given
        List<RequestAttribute> attributes = List
                .of(signatureActivation(AttributeContentType.STRING, new ObjectAttributeContentV3(ACTIVATION)));

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must carry an object value."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedActivation_rejectsAnItemOtherThanAnObject() {
        // given
        List<RequestAttribute> attributes = List
                .of(signatureActivation(AttributeContentType.OBJECT, new StringAttributeContentV3(SIGNER)));

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must carry an object value."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"\"platformAsserted\"", "null", "[]"})
    void statedActivation_rejectsAnObjectItemHoldingAnotherValue(String data) throws Exception {
        // given
        List<RequestAttribute> attributes = fromWire(data);

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must carry an object value."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedActivation_rejectsAFieldBesideTheDecisionAndTheSigner() throws Exception {
        // given
        List<RequestAttribute> attributes = fromWire(
                "{\"decision\":\"platformAsserted\",\"signer\":\"" + SIGNER + "\",\"evidence\":\"token\"}");

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must carry only the fields 'decision' and 'signer'."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"signer\":\"jane.doe|signers.example\"}",
            "{\"decision\":null,\"signer\":\"jane.doe|signers.example\"}",
            "{\"decision\":7,\"signer\":\"jane.doe|signers.example\"}",
            "{\"decision\":\"PLATFORM_ASSERTED\",\"signer\":\"jane.doe|signers.example\"}",
            "{\"decision\":\"remoteAsserted\",\"signer\":\"jane.doe|signers.example\"}"})
    void statedActivation_rejectsAnUnknownDecision(String data) throws Exception {
        // given
        List<RequestAttribute> attributes = fromWire(data);

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must carry a known decision."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"decision\":\"platformAsserted\"}",
            "{\"decision\":\"platformAsserted\",\"signer\":null}",
            "{\"decision\":\"platformAsserted\",\"signer\":7}",
            "{\"decision\":\"platformAsserted\",\"signer\":\" \"}",
            "{\"decision\":\"platformAsserted\",\"signer\":\"\\u0000\"}",
            "{\"decision\":\"platformAsserted\",\"signer\":\"\\u2003\"}"})
    void statedActivation_rejectsABlankSigner(String data) throws Exception {
        // given
        List<RequestAttribute> attributes = fromWire(data);

        // when
        Executable read = () -> SignatureActivationAttribute.statedActivation(attributes);

        // then
        assertEquals(violation("must carry a non-blank signer."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedActivation_rejectsAnIncompleteActivationStatedInProcess() {
        // given
        List<RequestAttribute> noDecision = List
                .of(signatureActivation(AttributeContentType.OBJECT,
                        new ObjectAttributeContentV3(new SignatureActivation(null, SIGNER))));
        List<RequestAttribute> blankSigner = List
                .of(signatureActivation(AttributeContentType.OBJECT, new ObjectAttributeContentV3(
                        new SignatureActivation(SignatureActivationDecision.PLATFORM_ASSERTED, "\u0000"))));

        // when
        Executable readNoDecision = () -> SignatureActivationAttribute.statedActivation(noDecision);
        Executable readBlankSigner = () -> SignatureActivationAttribute.statedActivation(blankSigner);

        // then
        assertEquals(violation("must carry a known decision."),
                assertThrows(ValidationException.class, readNoDecision).getMessage());
        assertEquals(violation("must carry a non-blank signer."),
                assertThrows(ValidationException.class, readBlankSigner).getMessage());
    }

    private static String violation(String rule) {
        return "Signature attribute with name 'signatureActivation' and UUID '"
                + SignatureActivationAttribute.ATTRIBUTE_UUID + "' " + rule;
    }

    private static List<RequestAttribute> parse(String json) throws Exception {
        return MAPPER.readValue(json, new TypeReference<List<RequestAttribute>>() {
        });
    }

    private static List<RequestAttribute> fromWire(String data) throws Exception {
        return parse("[{\"version\":\"v3\",\"uuid\":\"" + SignatureActivationAttribute.ATTRIBUTE_UUID
                + "\",\"name\":\"signatureActivation\",\"contentType\":\"object\","
                + "\"content\":[{\"contentType\":\"object\",\"data\":" + data + "}]}]");
    }

    private static RequestAttribute signatureActivation(AttributeContentType contentType,
            BaseAttributeContentV3<?>... content) {
        return new RequestAttributeV3(SignatureActivationAttribute.ATTRIBUTE_UUID, SignatureActivationAttribute.NAME,
                contentType, List.of(content));
    }
}
