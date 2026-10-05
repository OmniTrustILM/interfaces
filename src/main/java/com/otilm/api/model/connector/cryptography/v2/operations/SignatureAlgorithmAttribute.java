package com.otilm.api.model.connector.cryptography.v2.operations;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.SignatureAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.PlatformReservedAttribute;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * The signature algorithm attribute through which a cryptography provider v2 lets the caller choose the signature
 * algorithm. Its UUID, name and values are part of the contract.
 */
public final class SignatureAlgorithmAttribute implements PlatformReservedAttribute {

    public static final String NAME = "signatureAlgorithm";
    public static final UUID ATTRIBUTE_UUID = UUID.fromString("9180267f-c82f-4b7b-8160-d2363d813869");

    private SignatureAlgorithmAttribute() {
    }

    /**
     * The definition a provider publishes from {@code /sign/attributes} or {@code /verify/attributes}, offering the
     * algorithms the key supports for that operation.
     */
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
        attribute.setDescription("Signature algorithm used to sign or verify the data");
        attribute.setContentType(AttributeContentType.STRING);
        attribute.setProperties(properties);
        attribute.setContent(supported.stream().map(SignatureAlgorithmAttribute::content).toList());
        return attribute;
    }

    public static RequestAttributeV3 request(SignatureAlgorithm algorithm) {
        return new RequestAttributeV3(ATTRIBUTE_UUID, NAME, AttributeContentType.STRING, List.of(content(algorithm)));
    }

    /**
     * The signature attributes must select exactly one platform signature algorithm, through a single v3 attribute with
     * {@link #ATTRIBUTE_UUID} and {@link #NAME}. No other attribute may use either reserved identifier.
     *
     * @throws ValidationException when the signature attributes break that rule
     */
    public static SignatureAlgorithm selectedAlgorithm(List<? extends RequestAttribute> signatureAttributes) {
        List<? extends RequestAttribute> selections = signatureAttributes == null
                ? List.of()
                : signatureAttributes
                        .stream()
                        .filter(attribute -> attribute != null
                                && (ATTRIBUTE_UUID.equals(attribute.getUuid()) || NAME.equals(attribute.getName())))
                        .toList();
        if (selections.size() > 1) {
            throw new ValidationException(ValidationError
                    .create("Signature attribute with name '{}' and UUID '{}' must be supplied once.", NAME,
                            ATTRIBUTE_UUID));
        }
        if (selections.isEmpty()) {
            throw noSelection();
        }
        if (!ATTRIBUTE_UUID.equals(selections.get(0).getUuid()) || !NAME.equals(selections.get(0).getName())) {
            throw noSelection();
        }
        if (!(selections.get(0) instanceof RequestAttributeV3 selection)
                || selection.getVersion() != AttributeVersion.V3) {
            throw new ValidationException(ValidationError
                    .create("Signature attribute with name '{}' and UUID '{}' must be a v3 attribute.", NAME,
                            ATTRIBUTE_UUID));
        }
        List<BaseAttributeContentV3<?>> values = selection.getContent();
        if (values == null || values.size() != 1 || values.get(0) == null || values.get(0).getData() == null) {
            throw noSelection();
        }
        if (!(values.get(0) instanceof StringAttributeContentV3 value)
                || selection.getContentType() != AttributeContentType.STRING
                || value.getContentType() != AttributeContentType.STRING) {
            throw new ValidationException(ValidationError
                    .create("Signature attribute with name '{}' and UUID '{}' must carry a string value.", NAME,
                            ATTRIBUTE_UUID));
        }
        return SignatureAlgorithm.findByCode(value.getData());
    }

    private static ValidationException noSelection() {
        return new ValidationException(ValidationError
                .create("Signature attributes must select one value of the attribute with name '{}' and UUID '{}'.",
                        NAME, ATTRIBUTE_UUID));
    }

    private static StringAttributeContentV3 content(SignatureAlgorithm algorithm) {
        return new StringAttributeContentV3(algorithm.getLabel(), algorithm.getCode());
    }
}
