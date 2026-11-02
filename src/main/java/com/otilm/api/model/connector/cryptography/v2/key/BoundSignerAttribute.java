package com.otilm.api.model.connector.cryptography.v2.key;

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
import com.otilm.api.model.connector.cryptography.v2.PlatformReservedAttribute;
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
        if (createKeyAttributes == null) {
            return Optional.empty();
        }

        List<? extends RequestAttribute> declarations = createKeyAttributes
                .stream()
                .filter(attribute -> attribute != null
                        && (ATTRIBUTE_UUID.equals(attribute.getUuid()) || NAME.equals(attribute.getName())))
                .toList();
        if (declarations.isEmpty()) {
            return Optional.empty();
        }
        if (declarations.size() > 1) {
            throw violation("must be supplied at most once.");
        }

        RequestAttribute declaration = declarations.get(0);
        if (!ATTRIBUTE_UUID.equals(declaration.getUuid()) || !NAME.equals(declaration.getName())) {
            throw violation("must be supplied under both its name and its UUID.");
        }
        if (!(declaration instanceof RequestAttributeV3 selection) || selection.getVersion() != AttributeVersion.V3) {
            throw violation("must be a v3 attribute.");
        }
        List<BaseAttributeContentV3<?>> content = selection.getContent();
        if (content == null || content.size() != 1) {
            throw violation("must carry exactly one content item.");
        }
        if (!(content.get(0) instanceof StringAttributeContentV3 item)
                || selection.getContentType() != AttributeContentType.STRING) {
            throw violation("must carry a string value.");
        }
        if (item.getData() == null || item.getData().isBlank()) {
            throw violation("must carry a non-blank signer.");
        }
        return Optional.of(item.getData());
    }

    private static ValidationException violation(String rule) {
        return new ValidationException(ValidationError
                .create("Create-key attribute with name '{}' and UUID '{}' " + rule, NAME, ATTRIBUTE_UUID));
    }

    private static String requireNonBlank(String signer) {
        if (signer == null || signer.isBlank()) {
            throw new IllegalArgumentException(NAME + " must not be blank");
        }
        return signer;
    }
}
