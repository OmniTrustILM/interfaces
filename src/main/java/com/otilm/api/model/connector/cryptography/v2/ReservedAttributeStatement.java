package com.otilm.api.model.connector.cryptography.v2;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV3;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v3.content.BaseAttributeContentV3;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * A request's content for a platform reserved attribute: the declared content type and the one content item.
 *
 * @param contentType the declared content type
 * @param item the one content item
 */
public record ReservedAttributeStatement(AttributeContentType contentType, BaseAttributeContentV3<?> item) {

    /**
     * Checks the envelope of a reserved attribute among the request attributes. The attribute's own reader checks its
     * content.
     *
     * @param attributes the request attributes, may be {@code null}
     * @param uuid the reserved attribute UUID
     * @param name the reserved attribute name
     * @param violation builds the refusal from the rule the statement breaks
     * @return the statement, or empty when the attribute is absent
     * @throws ValidationException if the attribute is supplied more than once, under only one of its reserved
     * identifiers, as a v2 attribute, or with other than one content item
     */
    public static Optional<ReservedAttributeStatement> find(List<? extends RequestAttribute> attributes, UUID uuid,
            String name, Function<String, ValidationException> violation) {
        if (attributes == null) {
            return Optional.empty();
        }

        List<? extends RequestAttribute> declarations = attributes
                .stream()
                .filter(attribute -> attribute != null
                        && (uuid.equals(attribute.getUuid()) || name.equals(attribute.getName())))
                .toList();
        if (declarations.isEmpty()) {
            return Optional.empty();
        }
        if (declarations.size() > 1) {
            throw violation.apply("must be supplied at most once.");
        }

        RequestAttribute declaration = declarations.get(0);
        if (!uuid.equals(declaration.getUuid()) || !name.equals(declaration.getName())) {
            throw violation.apply("must be supplied under both its name and its UUID.");
        }
        if (!(declaration instanceof RequestAttributeV3 statement) || statement.getVersion() != AttributeVersion.V3) {
            throw violation.apply("must be a v3 attribute.");
        }
        List<BaseAttributeContentV3<?>> content = statement.getContent();
        if (content == null || content.size() != 1) {
            throw violation.apply("must carry exactly one content item.");
        }
        return Optional.of(new ReservedAttributeStatement(statement.getContentType(), content.get(0)));
    }
}
