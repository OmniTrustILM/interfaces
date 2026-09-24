package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
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
    public static DataAttributeV3 definition(Collection<SignatureAlgorithm> supported) {
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel("Signature Algorithm");
        properties.setRequired(true);
        properties.setVisible(true);
        properties.setList(true);
        properties.setMultiSelect(false);
        properties.setReadOnly(false);

        DataAttributeV3 attribute = new DataAttributeV3();
        attribute.setUuid(ATTRIBUTE_UUID.toString());
        attribute.setName(NAME);
        attribute.setDescription("Signature algorithm the signature is produced with");
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setProperties(properties);
        attribute.setContent(supported.stream().map(SignatureAlgorithmAttribute::content).toList());
        return attribute;
    }

    public static RequestAttributeV3 request(SignatureAlgorithm algorithm) {
        return new RequestAttributeV3(ATTRIBUTE_UUID, NAME, AttributeContentType.STRING, List.of(content(algorithm)));
    }

    /**
     * The signature attributes must select exactly one platform signature algorithm, through a single v3
     * {@code signatureAlgorithm} attribute.
     *
     * @throws ValidationException when the signature attributes break that rule
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
        if (selections.isEmpty()) {
            throw noSelection();
        }
        if (!(selections.get(0) instanceof RequestAttributeV3 selection)) {
            throw new ValidationException(
                    ValidationError.create("Signature attribute {} must be a v3 attribute.", NAME));
        }
        List<BaseAttributeContentV3<?>> values = selection.getContent();
        if (values == null || values.size() != 1 || values.get(0) == null || values.get(0).getData() == null) {
            throw noSelection();
        }
        if (!(values.get(0) instanceof StringAttributeContentV3 value)) {
            throw new ValidationException(
                    ValidationError.create("Signature attribute {} must carry a string value.", NAME));
        }
        return SignatureAlgorithm.findByCode(value.getData());
    }

    private static ValidationException noSelection() {
        return new ValidationException(
                ValidationError.create("Signature attributes must select one value of {}.", NAME));
    }

    private static StringAttributeContentV3 content(SignatureAlgorithm algorithm) {
        return new StringAttributeContentV3(algorithm.getLabel(), algorithm.getCode());
    }
}
