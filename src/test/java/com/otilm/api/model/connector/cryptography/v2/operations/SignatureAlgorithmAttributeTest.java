package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.ObjectAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SignatureAlgorithmAttributeTest {

    @Test
    void definition_offersEverySupportedAlgorithmByCode() {
        // when
        DataAttributeV2 definition = SignatureAlgorithmAttribute
                .definition(List.of(SignatureAlgorithm.SHA256_WITH_RSA, SignatureAlgorithm.SHA384_WITH_RSA_PSS));

        // then
        assertEquals(SignatureAlgorithmAttribute.NAME, definition.getName());
        assertEquals(AttributeContentType.STRING, definition.getContentType());
        assertTrue(definition.getProperties().isRequired());
        List<Object> offered = definition.getContent().stream().map(AttributeContent::getData).toList();
        assertEquals(List.of("SHA256withRSA", "SHA384withRSAandMGF1"), offered);
    }

    @ParameterizedTest
    @EnumSource(SignatureAlgorithm.class)
    void selectedAlgorithm_readsBackTheAlgorithmARequestSelects(SignatureAlgorithm algorithm) {
        // given
        List<RequestAttribute> attributes = List.of(otherAttribute(), SignatureAlgorithmAttribute.request(algorithm));

        // when
        SignatureAlgorithm selected = SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(algorithm, selected);
    }

    @Test
    void selectedAlgorithm_skipsANullElement() {
        // given
        List<RequestAttribute> attributes = Arrays
                .asList(null, SignatureAlgorithmAttribute.request(SignatureAlgorithm.ML_DSA_65));

        // when
        SignatureAlgorithm selected = SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(SignatureAlgorithm.ML_DSA_65, selected);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("selectionsOfNoPlatformAlgorithm")
    void selectedAlgorithm_refusesASelectionOfNoPlatformAlgorithm(List<RequestAttribute> attributes,
            String expectedMessage) {
        // when
        Executable read = () -> SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(expectedMessage, assertThrows(ValidationException.class, read).getMessage());
    }

    static Stream<Arguments> selectionsOfNoPlatformAlgorithm() {
        String noSelection = "Signature attributes must select one value of signatureAlgorithm.";
        return Stream
                .of(arguments(named("no attributes", null), noSelection),
                        arguments(named("no selection", List.of(otherAttribute())), noSelection),
                        arguments(
                                named("a code outside the enum",
                                        List.of(selection(new StringAttributeContentV2("SHA1withRSA")))),
                                "Unknown signature algorithm code SHA1withRSA"),
                        arguments(
                                named("an object value",
                                        List
                                                .of(selection(new ObjectAttributeContentV2(
                                                        new HashMap<>(Map.of("code", "SHA256withRSA")))))),
                                "Signature attribute signatureAlgorithm must carry a string value."),
                        arguments(named("two values",
                                List
                                        .of(selection(new StringAttributeContentV2("SHA256withRSA"),
                                                new StringAttributeContentV2("SHA384withRSA")))),
                                noSelection),
                        arguments(
                                named("two attributes", List
                                        .of(SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA),
                                                SignatureAlgorithmAttribute
                                                        .request(SignatureAlgorithm.SHA384_WITH_RSA))),
                                "Signature attribute signatureAlgorithm must be supplied once."),
                        arguments(named("an empty value", List.of(selection(new StringAttributeContentV2()))),
                                noSelection));
    }

    private static RequestAttribute selection(BaseAttributeContentV2<?>... values) {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setUuid(SignatureAlgorithmAttribute.ATTRIBUTE_UUID);
        attribute.setName(SignatureAlgorithmAttribute.NAME);
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setContent(List.of(values));
        return attribute;
    }

    private static RequestAttribute otherAttribute() {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setUuid(UUID.randomUUID());
        attribute.setName("keyLabel");
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setContent(List.of(new StringAttributeContentV2("tsa-key")));
        return attribute;
    }
}
