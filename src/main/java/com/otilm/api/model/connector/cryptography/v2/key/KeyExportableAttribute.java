package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.BooleanAttributeContentV3;
import java.util.List;
import java.util.UUID;

/**
 * The create-key attribute that carries the exportable intent for a key.
 *
 * <p>
 * A connector that declares {@link com.otilm.api.model.client.connector.v2.FeatureFlag#KEY_EXPORT} publishes this
 * definition in its create-key attribute schema, and the platform states the intent to it with {@link #request}. The
 * obligation is stated in the contract, so this class is a convenience for Java. The attribute is a v3 attribute, as
 * every attribute on a v2 connector surface must be. The attribute belongs to key creation alone:
 * {@link ImportKeyRequestV2Dto} states the same intent as its required {@code exportable} field, so a connector must
 * not publish it in its import schema.
 * </p>
 */
public final class KeyExportableAttribute {

    /** Reserved attribute name. Connectors must publish the attribute under exactly this name. */
    public static final String NAME = "keyExportable";

    /** Reserved attribute UUID, under which {@link #definition()} publishes and {@link #request} states it. */
    public static final UUID ATTRIBUTE_UUID = UUID.fromString("9d3f1a26-5d4e-4b6c-8f0a-6c1f2d7e4b83");

    private KeyExportableAttribute() {
    }

    /**
     * The canonical definition connectors publish in their create-key attribute schema. Every call returns a fresh
     * instance with the same content, so a caller can adapt the copy it publishes without affecting anyone else.
     *
     * @return the reserved boolean attribute definition, defaulting to not exportable
     */
    public static DataAttributeV3 definition() {
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel("Exportable");
        properties.setRequired(true);
        properties.setVisible(true);

        DataAttributeV3 definition = new DataAttributeV3();
        definition.setUuid(ATTRIBUTE_UUID.toString());
        definition.setName(NAME);
        definition.setDescription("Whether the key may later be exported. It cannot be changed after the key exists.");
        definition.setContentType(AttributeContentType.BOOLEAN);
        definition.setContent(List.of(new BooleanAttributeContentV3(Boolean.FALSE)));
        definition.setProperties(properties);
        return definition;
    }

    /**
     * The attribute that states the exportable intent on a create-key request.
     *
     * @param exportable whether the key may later be exported
     * @return the reserved attribute carrying the intent
     */
    public static RequestAttributeV3 request(boolean exportable) {
        return new RequestAttributeV3(ATTRIBUTE_UUID, NAME, AttributeContentType.BOOLEAN,
                List.of(new BooleanAttributeContentV3(exportable)));
    }

    /**
     * Reads the exportable intent from the attributes of a create-key request.
     *
     * <p>
     * A missing attribute means not exportable, so a request that lost the attribute can never produce an exportable
     * key. Content that is present but unusable is refused instead, because it cannot be read as an intent either way.
     * The intent is read from the content's value rather than from its class, in either attribute version: a v2 request
     * that arrived as JSON carries generic content, since nothing in it names a content class, and a platform that has
     * not upgraded still states the intent as v2.
     * </p>
     *
     * @param attributes the create-key attributes supplied with the request, may be {@code null}
     * @return {@code true} only when the attribute is present and asks for an exportable key
     * @throws IllegalArgumentException if the attribute is supplied more than once, states a content type other than
     * boolean, or its content is not a single boolean value
     */
    public static boolean isRequested(List<RequestAttribute> attributes) {
        if (attributes == null) {
            return false;
        }

        List<RequestAttribute> declarations = attributes
                .stream()
                .filter(attribute -> attribute != null && NAME.equals(attribute.getName()))
                .toList();
        if (declarations.isEmpty()) {
            return false;
        }
        if (declarations.size() > 1) {
            throw new IllegalArgumentException(NAME + " must be supplied at most once");
        }

        RequestAttribute declaration = declarations.get(0);
        if (declaration.getContentType() != null && declaration.getContentType() != AttributeContentType.BOOLEAN) {
            throw new IllegalArgumentException(NAME + " must carry boolean content");
        }
        List<?> content = declaration.getContent();
        if (content == null || content.size() != 1) {
            throw new IllegalArgumentException(NAME + " must carry exactly one content item");
        }
        Object value = content.get(0) instanceof AttributeContent item ? item.getData() : null;
        if (!(value instanceof Boolean requested)) {
            throw new IllegalArgumentException(NAME + " must carry boolean content");
        }
        return requested;
    }
}
