package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.BooleanAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.BooleanAttributeContentV3;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyExportableAttributeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void definition_isTheReservedBooleanV3DataAttribute() {
        // given
        // when
        DataAttributeV3 definition = KeyExportableAttribute.definition();

        // then
        assertEquals(KeyExportableAttribute.ATTRIBUTE_UUID.toString(), definition.getUuid());
        assertEquals(KeyExportableAttribute.NAME, definition.getName());
        assertEquals(AttributeType.DATA, definition.getType());
        assertEquals(AttributeContentType.BOOLEAN, definition.getContentType());
        assertEquals(3, definition.getVersion());
    }

    @Test
    void definition_defaultsToNotExportableAndAcceptsExactlyOneValue() {
        // given
        // when
        DataAttributeV3 definition = KeyExportableAttribute.definition();

        // then
        assertEquals(List.of(Boolean.FALSE), definition.getContent().stream().map(AttributeContent::getData).toList());
        assertInstanceOf(BooleanAttributeContentV3.class, definition.getContent().get(0));
        assertEquals(AttributeContentType.BOOLEAN, definition.getContent().get(0).getContentType());
        assertTrue(definition.getProperties().isRequired());
        assertFalse(definition.getProperties().isList());
        assertFalse(definition.getProperties().isMultiSelect());
        assertFalse(definition.getProperties().isReadOnly());
    }

    @Test
    void definition_isANewInstanceEachTime() {
        // given
        DataAttributeV3 first = KeyExportableAttribute.definition();

        // when
        DataAttributeV3 second = KeyExportableAttribute.definition();

        // then
        assertNotSame(first, second);
        assertNotSame(first.getProperties(), second.getProperties());
        assertEquals(first.getUuid(), second.getUuid());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void request_statesTheIntentAsTheReservedAttribute(boolean exportable) {
        // given
        // when
        RequestAttributeV3 request = KeyExportableAttribute.request(exportable);

        // then
        assertEquals(KeyExportableAttribute.ATTRIBUTE_UUID, request.getUuid());
        assertEquals(KeyExportableAttribute.NAME, request.getName());
        assertEquals(AttributeContentType.BOOLEAN, request.getContentType());
        assertEquals(exportable, KeyExportableAttribute.isRequested(List.of(request)));
    }

    @Test
    void request_travelsAsAV3AttributeOnTheWire() throws Exception {
        // given
        String json = MAPPER.writeValueAsString(List.of(KeyExportableAttribute.request(true)));

        // when
        List<RequestAttribute> received = parse(json);

        // then
        assertTrue(json.contains("\"version\":\"v3\""), json);
        assertInstanceOf(RequestAttributeV3.class, received.get(0));
        assertTrue(KeyExportableAttribute.isRequested(received));
    }

    @Test
    void isRequested_failsClosed_whenTheAttributeIsAbsent() {
        // given
        List<RequestAttribute> attributes = List.of(otherAttribute());

        // when
        boolean exportable = KeyExportableAttribute.isRequested(attributes);

        // then
        assertFalse(exportable);
    }

    @Test
    void isRequested_failsClosed_whenNoAttributesAreSupplied() {
        // given
        // when
        boolean exportable = KeyExportableAttribute.isRequested(null);

        // then
        assertFalse(exportable);
    }

    @Test
    void isRequested_readsTheRequestedValue() {
        // given
        List<RequestAttribute> requested = List.of(keyExportable(new BooleanAttributeContentV2(Boolean.TRUE)));
        List<RequestAttribute> declined = List.of(keyExportable(new BooleanAttributeContentV2(Boolean.FALSE)));

        // when
        // then
        assertTrue(KeyExportableAttribute.isRequested(requested));
        assertFalse(KeyExportableAttribute.isRequested(declined));
    }

    @Test
    void isRequested_rejectsDuplicateDeclarations() {
        // given
        List<RequestAttribute> attributes = List
                .of(keyExportable(new BooleanAttributeContentV2(Boolean.TRUE)),
                        keyExportable(new BooleanAttributeContentV2(Boolean.FALSE)));

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> KeyExportableAttribute.isRequested(attributes));

        // then
        assertEquals("keyExportable must be supplied at most once", failure.getMessage());
    }

    @Test
    void isRequested_rejectsContentThatIsNotASingleItem() {
        // given
        List<RequestAttribute> empty = List.of(keyExportable());
        List<RequestAttribute> two = List
                .of(keyExportable(new BooleanAttributeContentV2(Boolean.TRUE),
                        new BooleanAttributeContentV2(Boolean.FALSE)));

        // when
        // then
        assertEquals("keyExportable must carry exactly one content item",
                assertThrows(IllegalArgumentException.class, () -> KeyExportableAttribute.isRequested(empty))
                        .getMessage());
        assertEquals("keyExportable must carry exactly one content item",
                assertThrows(IllegalArgumentException.class, () -> KeyExportableAttribute.isRequested(two))
                        .getMessage());
    }

    @Test
    void isRequested_rejectsContentThatIsNotBoolean() {
        // given
        List<RequestAttribute> attributes = List.of(keyExportable(new StringAttributeContentV2("true")));

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> KeyExportableAttribute.isRequested(attributes));

        // then
        assertEquals("keyExportable must carry boolean content", failure.getMessage());
    }

    /**
     * The intent is read from a request that arrived as JSON in either version: a Core that has not upgraded still
     * states it as v2, whose content arrives generic because nothing in the document names a content class.
     */
    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"v2", "v3"})
    void isRequested_readsContentThatArrivedAsJson(String version) throws Exception {
        // given
        String requested = wireRequest("true", version);
        String declined = wireRequest("false", version);

        // when
        // then
        assertTrue(KeyExportableAttribute.isRequested(parse(requested)));
        assertFalse(KeyExportableAttribute.isRequested(parse(declined)));
    }

    @Test
    void isRequested_rejectsJsonContentThatIsNotABoolean() throws Exception {
        // given
        List<RequestAttribute> attributes = parse(wireRequest("\"true\"", "v2"));

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> KeyExportableAttribute.isRequested(attributes));

        // then
        assertEquals("keyExportable must carry boolean content", failure.getMessage());
    }

    @Test
    void isRequested_rejectsAContentTypeOtherThanBoolean() {
        // given
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(KeyExportableAttribute.NAME);
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setContent(List.of(new BooleanAttributeContentV2(Boolean.TRUE)));
        List<RequestAttribute> attributes = List.of(attribute);

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> KeyExportableAttribute.isRequested(attributes));

        // then
        assertEquals("keyExportable must carry boolean content", failure.getMessage());
    }

    @Test
    void isRequested_rejectsBooleanContentWithoutAValue() {
        // given
        List<RequestAttribute> attributes = List.of(keyExportable(new BooleanAttributeContentV2()));

        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> KeyExportableAttribute.isRequested(attributes));

        // then
        assertEquals("keyExportable must carry boolean content", failure.getMessage());
    }

    /** A v3 content item names its content type; a v2 one does not, and a request naming no version is read as v2. */
    private static String wireRequest(String data, String version) {
        String item = "v3".equals(version)
                ? "{\"data\":" + data + ",\"contentType\":\"boolean\"}"
                : "{\"data\":" + data + "}";
        String stated = version == null ? "" : ",\"version\":\"" + version + "\"";
        return "[{\"uuid\":\"9d3f1a26-5d4e-4b6c-8f0a-6c1f2d7e4b83\",\"name\":\"keyExportable\","
                + "\"contentType\":\"boolean\",\"content\":[" + item + "]" + stated + "}]";
    }

    private static List<RequestAttribute> parse(String json) throws Exception {
        return MAPPER.readValue(json, new TypeReference<List<RequestAttribute>>() {
        });
    }

    private static RequestAttribute keyExportable(BaseAttributeContentV2<?>... content) {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(KeyExportableAttribute.NAME);
        attribute.setContentType(AttributeContentType.BOOLEAN);
        attribute.setContent(List.of(content));
        return attribute;
    }

    private static RequestAttribute otherAttribute() {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName("keyAlias");
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setContent(List.of(new StringAttributeContentV2("server-key")));
        return attribute;
    }
}
