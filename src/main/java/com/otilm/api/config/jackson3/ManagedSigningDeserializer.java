package com.otilm.api.config.jackson3;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.signing.profile.scheme.ManagedSigningType;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.InvalidTypeIdException;
import tools.jackson.databind.exc.MismatchedInputException;

/**
 * Jackson 3 twin of {@code ManagedSigningDto.Deserializer} and {@code ManagedSigningRequestDto.Deserializer}: picks the
 * concrete class from {@code managedSigningType}.
 */
final class ManagedSigningDeserializer<T> extends StdDeserializer<T> {

    private final Class<? extends T> staticKey;
    private final Class<? extends T> oneTimeKey;

    ManagedSigningDeserializer(Class<T> type, Class<? extends T> staticKey, Class<? extends T> oneTimeKey) {
        super(type);
        this.staticKey = staticKey;
        this.oneTimeKey = oneTimeKey;
    }

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) {
        JsonNode node = context.readTree(parser);
        if (!node.isObject()) {
            throw MismatchedInputException
                    .from(parser, handledType(), "Expected JSON object for " + handledType().getSimpleName() + ", got: "
                            + node.getNodeType());
        }
        JsonNode typeNode = node.get("managedSigningType");
        String typeId = typeNode != null && !typeNode.isNull() ? typeNode.asString() : null;
        ManagedSigningType type;
        try {
            type = ManagedSigningType.findByCode(typeId);
        } catch (ValidationException e) {
            String message = typeId == null ? "Missing managedSigningType" : "Unknown managedSigningType: " + typeId;
            throw InvalidTypeIdException.from(parser, message, context.constructType(handledType()), typeId);
        }
        return switch (type) {
            case STATIC_KEY -> context.readTreeAsValue(node, staticKey);
            case ONE_TIME_KEY -> context.readTreeAsValue(node, oneTimeKey);
        };
    }
}
