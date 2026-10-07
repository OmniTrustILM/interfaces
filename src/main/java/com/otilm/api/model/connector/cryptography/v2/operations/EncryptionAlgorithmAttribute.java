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
import com.otilm.api.model.common.enums.cryptography.EncryptionAlgorithm;
import com.otilm.api.model.connector.cryptography.v2.PlatformReservedAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Reserved v2 encryption/decryption selector. Its UUID, name and algorithm codes are shared across connectors.
 */
public final class EncryptionAlgorithmAttribute implements PlatformReservedAttribute {
    public static final String NAME = "encryptionAlgorithm";
    public static final UUID ATTRIBUTE_UUID = UUID.fromString("5e364467-fa95-4253-907b-0c73cdfb2be7");

    private EncryptionAlgorithmAttribute() {
    }

    /** A key-scoped encrypt/decrypt schema offers only the profiles that the key and backend support. */
    public static DataAttributeV3 definition(Collection<EncryptionAlgorithm> supported) {
        Objects.requireNonNull(supported, "supported must not be null");
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel("Encryption Algorithm");
        properties.setRequired(true);
        properties.setVisible(true);
        properties.setList(true);
        properties.setMultiSelect(false);
        properties.setReadOnly(false);
        DataAttributeV3 definition = new DataAttributeV3();
        definition.setUuid(ATTRIBUTE_UUID.toString());
        definition.setName(NAME);
        definition.setDescription("Encryption algorithm used to encrypt or decrypt the data");
        definition.setContentType(AttributeContentType.STRING);
        definition.setProperties(properties);
        definition.setContent(supported.stream().map(EncryptionAlgorithmAttribute::content).toList());
        return definition;
    }

    public static RequestAttributeV3 request(EncryptionAlgorithm algorithm) {
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        return new RequestAttributeV3(ATTRIBUTE_UUID, NAME, AttributeContentType.STRING, List.of(content(algorithm)));
    }

    /**
     * Reads exactly one v3 string selection with {@link #ATTRIBUTE_UUID} and {@link #NAME}. No other attribute may use
     * either reserved identifier.
     */
    public static EncryptionAlgorithm selectedAlgorithm(List<? extends RequestAttribute> cipherAttributes) {
        List<? extends RequestAttribute> selections = cipherAttributes == null
                ? List.of()
                : cipherAttributes
                        .stream()
                        .filter(attribute -> attribute != null
                                && (ATTRIBUTE_UUID.equals(attribute.getUuid()) || NAME.equals(attribute.getName())))
                        .toList();
        if (selections.size() > 1) {
            throw new ValidationException(ValidationError
                    .create("Cipher attribute with name '{}' and UUID '{}' must be supplied once.", NAME,
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
                    .create("Cipher attribute with name '{}' and UUID '{}' must be a v3 attribute.", NAME,
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
                    .create("Cipher attribute with name '{}' and UUID '{}' must carry a string value.", NAME,
                            ATTRIBUTE_UUID));
        }
        return EncryptionAlgorithm.findByCode(value.getData());
    }

    private static ValidationException noSelection() {
        return new ValidationException(ValidationError
                .create("Cipher attributes must select one value of the attribute with name '{}' and UUID '{}'.", NAME,
                        ATTRIBUTE_UUID));
    }

    private static StringAttributeContentV3 content(EncryptionAlgorithm algorithm) {
        Objects.requireNonNull(algorithm, "algorithm must not be null");
        return new StringAttributeContentV3(algorithm.getLabel(), algorithm.getCode());
    }
}
