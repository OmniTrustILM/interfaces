package com.czertainly.api.model.client.signing.profile.scheme;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

final class ManagedSigningDeserializerUtil {

    private ManagedSigningDeserializerUtil() {}

    /**
     * Reads the current JSON value as a tree and asserts it is a JSON object.
     * Throws {@link MismatchedInputException} (mapped to 4xx by Jackson) instead of
     * a {@link ClassCastException} when the payload is not an object node.
     */
    static ObjectNode readObjectNode(JsonParser p, Class<?> targetType) throws IOException {
        JsonNode node = p.readValueAsTree();
        if (!node.isObject()) {
            throw MismatchedInputException.from(p, targetType,
                    "Expected JSON object for " + targetType.getSimpleName() + ", got: " + node.getNodeType());
        }
        return (ObjectNode) node;
    }
}
