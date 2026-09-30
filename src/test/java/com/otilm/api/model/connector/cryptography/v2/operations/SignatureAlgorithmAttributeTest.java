package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.ObjectAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import java.util.Arrays;
import java.util.Collections;
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
import org.junit.jupiter.params.provider.NullSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class SignatureAlgorithmAttributeTest {

    @Test
    void definition_offersEverySupportedAlgorithmByCode() {
        // when
        DataAttributeV3 definition = SignatureAlgorithmAttribute
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

    @ParameterizedTest
    @NullSource
    @EnumSource(value = AttributeVersion.class, names = "V3", mode = EnumSource.Mode.EXCLUDE)
    void selectedAlgorithm_rejectsAMissingOrIncorrectVersion(AttributeVersion version) {
        // given
        RequestAttributeV3 selection = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA);
        selection.setVersion(version);
        List<RequestAttribute> attributes = List.of(selection);
        String expectedMessage = "Signature attribute with name 'signatureAlgorithm' and UUID '"
                + SignatureAlgorithmAttribute.ATTRIBUTE_UUID + "' must be a v3 attribute.";

        // when
        Executable read = () -> SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(expectedMessage, assertThrows(ValidationException.class, read).getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(value = AttributeContentType.class, names = "STRING", mode = EnumSource.Mode.EXCLUDE)
    void selectedAlgorithm_rejectsAMissingOrIncorrectEnvelopeContentType(AttributeContentType contentType) {
        // given
        RequestAttributeV3 selection = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA);
        selection.setContentType(contentType);
        List<RequestAttribute> attributes = List.of(selection);
        String expectedMessage = "Signature attribute with name 'signatureAlgorithm' and UUID '"
                + SignatureAlgorithmAttribute.ATTRIBUTE_UUID + "' must carry a string value.";

        // when
        Executable read = () -> SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(expectedMessage, assertThrows(ValidationException.class, read).getMessage());
    }

    @ParameterizedTest
    @NullSource
    @EnumSource(value = AttributeContentType.class, names = "STRING", mode = EnumSource.Mode.EXCLUDE)
    void selectedAlgorithm_rejectsAMissingOrIncorrectItemContentType(AttributeContentType contentType) {
        // given
        RequestAttributeV3 selection = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA);
        selection.getContent().get(0).setContentType(contentType);
        List<RequestAttribute> attributes = List.of(selection);
        String expectedMessage = "Signature attribute with name 'signatureAlgorithm' and UUID '"
                + SignatureAlgorithmAttribute.ATTRIBUTE_UUID + "' must carry a string value.";

        // when
        Executable read = () -> SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(expectedMessage, assertThrows(ValidationException.class, read).getMessage());
    }

    @Test
    void selectedAlgorithm_rejectsTheReservedUuidAndNameOnDifferentAttributes() {
        // given
        String providerName = "providerSignatureAlgorithm";
        SignatureAlgorithm expected = SignatureAlgorithm.SHA256_WITH_RSA;
        RequestAttributeV3 selection = SignatureAlgorithmAttribute.request(expected);
        selection.setName(providerName);
        RequestAttributeV3 unrelated = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA384_WITH_RSA);
        unrelated.setUuid(UUID.randomUUID());
        List<RequestAttribute> attributes = List.of(unrelated, selection);

        // when
        Executable read = () -> SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertThrows(ValidationException.class, read);
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
        // given
        // when
        Executable read = () -> SignatureAlgorithmAttribute.selectedAlgorithm(attributes);

        // then
        assertEquals(expectedMessage, assertThrows(ValidationException.class, read).getMessage());
    }

    static Stream<Arguments> selectionsOfNoPlatformAlgorithm() {
        String noSelection = "Signature attributes must select one value of the attribute with name 'signatureAlgorithm' and UUID '9180267f-c82f-4b7b-8160-d2363d813869'.";
        RequestAttributeV3 wrongUuid = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA);
        wrongUuid.setUuid(UUID.randomUUID());
        RequestAttributeV3 alias = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA384_WITH_RSA);
        alias.setName("providerSignatureAlgorithm");
        RequestAttributeV3 missingContent = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA);
        missingContent.setContent(null);
        RequestAttributeV3 nullContentItem = SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA);
        nullContentItem.setContent(Collections.singletonList(null));
        return Stream
                .of(arguments(named("no attributes", null), noSelection),
                        arguments(named("no selection", List.of(otherAttribute())), noSelection),
                        arguments(named("missing content", List.of(missingContent)), noSelection),
                        arguments(named("empty content", List.of(selection())), noSelection),
                        arguments(named("null content item", List.of(nullContentItem)), noSelection),
                        arguments(named("canonical name with wrong UUID", List.of(wrongUuid)), noSelection),
                        arguments(named("duplicate UUID under another name", List
                                .of(SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA), alias)),
                                "Signature attribute with name 'signatureAlgorithm' and UUID '9180267f-c82f-4b7b-8160-d2363d813869' must be supplied once."),
                        arguments(
                                named("a code outside the enum",
                                        List.of(selection(new StringAttributeContentV3("SHA1withRSA")))),
                                "Unknown signature algorithm code SHA1withRSA"),
                        arguments(
                                named("an object value",
                                        List
                                                .of(selection(new ObjectAttributeContentV3(
                                                        new HashMap<>(Map.of("code", "SHA256withRSA")))))),
                                "Signature attribute with name 'signatureAlgorithm' and UUID '9180267f-c82f-4b7b-8160-d2363d813869' must carry a string value."),
                        arguments(named("two values",
                                List
                                        .of(selection(new StringAttributeContentV3("SHA256withRSA"),
                                                new StringAttributeContentV3("SHA384withRSA")))),
                                noSelection),
                        arguments(
                                named("two attributes", List
                                        .of(SignatureAlgorithmAttribute.request(SignatureAlgorithm.SHA256_WITH_RSA),
                                                SignatureAlgorithmAttribute
                                                        .request(SignatureAlgorithm.SHA384_WITH_RSA))),
                                "Signature attribute with name 'signatureAlgorithm' and UUID '9180267f-c82f-4b7b-8160-d2363d813869' must be supplied once."),
                        arguments(named("an empty value", List.of(selection(new StringAttributeContentV3()))),
                                noSelection),
                        arguments(named("a v2 attribute", List.of(v2Selection())),
                                "Signature attribute with name 'signatureAlgorithm' and UUID '9180267f-c82f-4b7b-8160-d2363d813869' must be a v3 attribute."));
    }

    private static RequestAttribute selection(BaseAttributeContentV3<?>... values) {
        return new RequestAttributeV3(SignatureAlgorithmAttribute.ATTRIBUTE_UUID, SignatureAlgorithmAttribute.NAME,
                AttributeContentType.STRING, List.of(values));
    }

    private static RequestAttribute v2Selection() {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setUuid(SignatureAlgorithmAttribute.ATTRIBUTE_UUID);
        attribute.setName(SignatureAlgorithmAttribute.NAME);
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setContent(List.of(new StringAttributeContentV2(SignatureAlgorithm.SHA256_WITH_RSA.getCode())));
        return attribute;
    }

    private static RequestAttribute otherAttribute() {
        return new RequestAttributeV3(UUID.randomUUID(), "keyLabel", AttributeContentType.STRING,
                List.of(new StringAttributeContentV3("tsa-key")));
    }
}
