package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.validation.NullableNotBlankValidator;
import com.otilm.api.model.connector.cryptography.v2.PlatformReservedAttribute;
import com.otilm.api.model.connector.cryptography.v2.ReservedAttributeStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The create-key attribute through which the platform states, on a rekey, the signer of the key pair being replaced. A
 * connector declaring {@link com.otilm.api.model.client.connector.v2.FeatureFlag#SIGNER_BOUND_AT_CREATION} publishes it
 * and binds the new key pair to that signer.
 */
public final class BoundSignerAttribute implements PlatformReservedAttribute {

    public static final String NAME = "boundSigner";

    /** Reserved attribute UUID, under which {@link #definition()} publishes and {@link #request} states the signer. */
    public static final UUID ATTRIBUTE_UUID = UUID.fromString("ab6ce1fe-abd3-47d4-8e6b-a33da08770ae");

    private BoundSignerAttribute() {
    }

    /**
     * The definition a connector publishes in its create-key attribute schema. It is hidden, optional and without a
     * default, because only the platform states the value, and only on a rekey.
     *
     * @return the reserved string attribute definition
     */
    public static DataAttributeV3 definition() {
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel("Bound signer");
        properties.setRequired(false);
        properties.setVisible(false);

        DataAttributeV3 definition = new DataAttributeV3();
        definition.setUuid(ATTRIBUTE_UUID.toString());
        definition.setName(NAME);
        definition.setDescription("Signer of the key pair that a rekey replaces.");
        definition.setContentType(AttributeContentType.STRING);
        definition.setProperties(properties);
        return definition;
    }

    /**
     * The attribute that states the bound signer on a rekey.
     *
     * @param signer the canonical signer the connector returned for the replaced key
     * @return the reserved attribute carrying the signer
     * @throws IllegalArgumentException if the signer is blank
     */
    public static RequestAttributeV3 request(String signer) {
        return new RequestAttributeV3(ATTRIBUTE_UUID, NAME, AttributeContentType.STRING,
                List.of(new StringAttributeContentV3(requireNonBlank(signer))));
    }

    /**
     * Reads the bound signer from the attributes of a create-key request.
     *
     * @param createKeyAttributes the create-key attributes supplied with the request, may be {@code null}
     * @return the stated signer, or empty for a request that creates a new key pair
     * @throws ValidationException if the attribute is supplied more than once, under only one of its reserved
     * identifiers, as a v2 attribute, or with content other than a single non-blank string
     */
    public static Optional<String> statedSigner(List<? extends RequestAttribute> createKeyAttributes) {
        return ReservedAttributeStatement
                .find(createKeyAttributes, ATTRIBUTE_UUID, NAME, BoundSignerAttribute::violation)
                .map(BoundSignerAttribute::signerOf);
    }

    private static String signerOf(ReservedAttributeStatement statement) {
        if (!(statement.item() instanceof StringAttributeContentV3 item)
                || statement.contentType() != AttributeContentType.STRING) {
            throw violation("must carry a string value.");
        }
        if (NullableNotBlankValidator.readsAsNoValue(item.getData())) {
            throw violation("must carry a non-blank signer.");
        }
        return item.getData();
    }

    private static ValidationException violation(String rule) {
        return new ValidationException(ValidationError
                .create("Create-key attribute with name '{}' and UUID '{}' " + rule, NAME, ATTRIBUTE_UUID));
    }

    private static String requireNonBlank(String signer) {
        if (NullableNotBlankValidator.readsAsNoValue(signer)) {
            throw new IllegalArgumentException(NAME + " must not be blank");
        }
        return signer;
    }
}
