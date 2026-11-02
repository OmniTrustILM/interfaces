package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.BooleanAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoundSignerAttributeTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String SIGNER = "jane.doe|signers.example";

    @Test
    void definition_isTheReservedStringV3DataAttribute() {
        // given
        // when
        DataAttributeV3 definition = BoundSignerAttribute.definition();

        // then
        assertEquals(BoundSignerAttribute.ATTRIBUTE_UUID.toString(), definition.getUuid());
        assertEquals(BoundSignerAttribute.NAME, definition.getName());
        assertEquals(AttributeType.DATA, definition.getType());
        assertEquals(AttributeContentType.STRING, definition.getContentType());
        assertEquals(3, definition.getVersion());
    }

    @Test
    void definition_isAnOptionalHiddenValueWithoutADefault() {
        // given
        // when
        DataAttributeV3 definition = BoundSignerAttribute.definition();

        // then
        assertNull(definition.getContent());
        assertFalse(definition.getProperties().isRequired());
        assertFalse(definition.getProperties().isVisible());
        assertFalse(definition.getProperties().isList());
        assertFalse(definition.getProperties().isMultiSelect());
    }

    @Test
    void definition_isANewInstanceEachTime() {
        // given
        DataAttributeV3 first = BoundSignerAttribute.definition();

        // when
        DataAttributeV3 second = BoundSignerAttribute.definition();

        // then
        assertNotSame(first, second);
        assertNotSame(first.getProperties(), second.getProperties());
        assertEquals(first.getUuid(), second.getUuid());
    }

    @Test
    void request_statesTheSignerAsTheReservedStringAttribute() {
        // given
        // when
        RequestAttributeV3 request = BoundSignerAttribute.request(SIGNER);

        // then
        assertEquals(BoundSignerAttribute.ATTRIBUTE_UUID, request.getUuid());
        assertEquals(BoundSignerAttribute.NAME, request.getName());
        assertEquals(AttributeContentType.STRING, request.getContentType());
        assertEquals(Optional.of(SIGNER), BoundSignerAttribute.statedSigner(List.of(request)));
    }

    @Test
    void request_travelsAsAV3AttributeWithAContentTypeOnEveryItem() throws Exception {
        // given
        String json = MAPPER.writeValueAsString(List.of(BoundSignerAttribute.request(SIGNER)));

        // when
        JsonNode attribute = MAPPER.readTree(json).get(0);

        // then
        assertEquals("v3", attribute.get("version").textValue());
        assertEquals("string", attribute.get("content").get(0).get("contentType").textValue());
        assertEquals(Optional.of(SIGNER), BoundSignerAttribute.statedSigner(parse(json)));
    }

    @Test
    void request_rejectsABlankSigner() {
        // given
        // when
        IllegalArgumentException failure = assertThrows(IllegalArgumentException.class,
                () -> BoundSignerAttribute.request(" "));

        // then
        assertEquals("boundSigner must not be blank", failure.getMessage());
    }

    @Test
    void statedSigner_isEmptyWhenTheAttributeIsAbsent() {
        // given
        List<RequestAttribute> attributes = List.of(KeyExportableAttribute.request(false));

        // when
        Optional<String> signer = BoundSignerAttribute.statedSigner(attributes);

        // then
        assertTrue(signer.isEmpty());
    }

    @Test
    void statedSigner_isEmptyWhenNoAttributesAreSupplied() {
        // given
        // when
        Optional<String> signer = BoundSignerAttribute.statedSigner(null);

        // then
        assertTrue(signer.isEmpty());
    }

    @Test
    void statedSigner_rejectsDuplicateDeclarations() {
        // given
        List<RequestAttribute> attributes = List
                .of(BoundSignerAttribute.request(SIGNER), BoundSignerAttribute.request("john.roe|signers.example"));

        // when
        Executable read = () -> BoundSignerAttribute.statedSigner(attributes);

        // then
        assertEquals(violation("must be supplied at most once."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedSigner_rejectsTheReservedNameUnderAnotherUuid() {
        // given
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV3(UUID.randomUUID(), BoundSignerAttribute.NAME, AttributeContentType.STRING,
                        List.of(new StringAttributeContentV3(SIGNER))));

        // when
        Executable read = () -> BoundSignerAttribute.statedSigner(attributes);

        // then
        assertEquals(violation("must be supplied under both its name and its UUID."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedSigner_rejectsTheReservedUuidUnderAnotherName() {
        // given
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV3(BoundSignerAttribute.ATTRIBUTE_UUID, "signer", AttributeContentType.STRING,
                        List.of(new StringAttributeContentV3(SIGNER))));

        // when
        Executable read = () -> BoundSignerAttribute.statedSigner(attributes);

        // then
        assertEquals(violation("must be supplied under both its name and its UUID."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedSigner_rejectsAV2Attribute() {
        // given
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV2(BoundSignerAttribute.ATTRIBUTE_UUID, BoundSignerAttribute.NAME,
                        AttributeContentType.STRING, List.of(new StringAttributeContentV2(SIGNER))));

        // when
        Executable read = () -> BoundSignerAttribute.statedSigner(attributes);

        // then
        assertEquals(violation("must be a v3 attribute."), assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedSigner_rejectsContentThatIsNotASingleItem() {
        // given
        List<RequestAttribute> empty = List.of(boundSigner(AttributeContentType.STRING));
        List<RequestAttribute> two = List
                .of(boundSigner(AttributeContentType.STRING, new StringAttributeContentV3(SIGNER),
                        new StringAttributeContentV3(SIGNER)));

        // when
        Executable readEmpty = () -> BoundSignerAttribute.statedSigner(empty);
        Executable readTwo = () -> BoundSignerAttribute.statedSigner(two);

        // then
        assertEquals(violation("must carry exactly one content item."),
                assertThrows(ValidationException.class, readEmpty).getMessage());
        assertEquals(violation("must carry exactly one content item."),
                assertThrows(ValidationException.class, readTwo).getMessage());
    }

    @Test
    void statedSigner_rejectsAContentTypeOtherThanString() {
        // given
        List<RequestAttribute> attributes = List
                .of(boundSigner(AttributeContentType.BOOLEAN, new BooleanAttributeContentV3(Boolean.TRUE)));

        // when
        Executable read = () -> BoundSignerAttribute.statedSigner(attributes);

        // then
        assertEquals(violation("must carry a string value."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void statedSigner_rejectsABlankSigner() {
        // given
        List<RequestAttribute> attributes = List
                .of(boundSigner(AttributeContentType.STRING, new StringAttributeContentV3(" ")));

        // when
        Executable read = () -> BoundSignerAttribute.statedSigner(attributes);

        // then
        assertEquals(violation("must carry a non-blank signer."),
                assertThrows(ValidationException.class, read).getMessage());
    }

    private static String violation(String rule) {
        return "Create-key attribute with name 'boundSigner' and UUID '" + BoundSignerAttribute.ATTRIBUTE_UUID + "' "
                + rule;
    }

    private static List<RequestAttribute> parse(String json) throws Exception {
        return MAPPER.readValue(json, new TypeReference<List<RequestAttribute>>() {
        });
    }

    private static RequestAttribute boundSigner(AttributeContentType contentType,
            BaseAttributeContentV3<?>... content) {
        return new RequestAttributeV3(BoundSignerAttribute.ATTRIBUTE_UUID, BoundSignerAttribute.NAME, contentType,
                List.of(content));
    }
}
