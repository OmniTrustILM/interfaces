package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.core.util.AttributeDefinitionUtils;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The sign attribute through which a cryptography provider v2 lets the caller choose the signature algorithm. Its name
 * and values are part of the contract, so a caller that must name the algorithm before the signature exists reads it
 * from the selection.
 */
public final class SignatureAlgorithmAttribute {

    public static final String NAME = "signatureAlgorithm";
    public static final UUID ATTRIBUTE_UUID = UUID.fromString("9180267f-c82f-4b7b-8160-d2363d813869");

    private SignatureAlgorithmAttribute() {
    }

    /** The definition a provider publishes from {@code /sign/attributes}, offering the algorithms the key supports. */
    public static DataAttributeV2 definition(Collection<SignatureAlgorithm> supported) {
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel("Signature Algorithm");
        properties.setRequired(true);
        properties.setVisible(true);
        properties.setList(true);
        properties.setMultiSelect(false);
        properties.setReadOnly(false);

        DataAttributeV2 attribute = new DataAttributeV2();
        attribute.setUuid(ATTRIBUTE_UUID.toString());
        attribute.setName(NAME);
        attribute.setDescription("Signature algorithm the signature is produced with");
        attribute.setType(AttributeType.DATA);
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setProperties(properties);
        attribute.setContent(supported.stream().map(SignatureAlgorithmAttribute::content).toList());
        return attribute;
    }

    public static RequestAttributeV2 request(SignatureAlgorithm algorithm) {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setUuid(ATTRIBUTE_UUID);
        attribute.setName(NAME);
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setContent(List.of(content(algorithm)));
        return attribute;
    }

    /**
     * The signature attributes must select exactly one platform signature algorithm, through a single
     * {@code signatureAlgorithm} attribute.
     *
     * @throws ValidationException when they select none, several, or a value that is not a platform signature algorithm
     */
    public static SignatureAlgorithm selectedAlgorithm(List<? extends RequestAttribute> signatureAttributes) {
        List<? extends RequestAttribute> selections = signatureAttributes == null
                ? List.of()
                : signatureAttributes
                        .stream()
                        .filter(attribute -> attribute != null && NAME.equals(attribute.getName()))
                        .toList();
        if (selections.size() > 1) {
            throw new ValidationException(
                    ValidationError.create("Signature attribute {} must be supplied once.", NAME));
        }
        List<StringAttributeContentV2> values;
        try {
            values = AttributeDefinitionUtils.getAttributeContent(NAME, selections, StringAttributeContentV2.class);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(
                    ValidationError.create("Signature attribute {} must carry a string value.", NAME));
        }
        StringAttributeContentV2 value = values == null || values.size() != 1 ? null : values.get(0);
        if (value == null || value.getData() == null) {
            throw new ValidationException(
                    ValidationError.create("Signature attributes must select one value of {}.", NAME));
        }
        return SignatureAlgorithm.findByCode(value.getData());
    }

    private static StringAttributeContentV2 content(SignatureAlgorithm algorithm) {
        return new StringAttributeContentV2(algorithm.getLabel(), algorithm.getCode());
    }
}
