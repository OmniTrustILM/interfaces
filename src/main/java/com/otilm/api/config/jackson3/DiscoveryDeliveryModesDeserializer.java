package com.otilm.api.config.jackson3;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDeliveryMode;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Jackson 3 twin of {@link com.otilm.api.model.connector.discovery.v2.DiscoveryDeliveryModesDeserializer}: an offered
 * mode this library does not know is skipped.
 */
final class DiscoveryDeliveryModesDeserializer extends ValueDeserializer<List<DiscoveryDeliveryMode>> {

    @Override
    public List<DiscoveryDeliveryMode> deserialize(JsonParser parser, DeserializationContext context) {
        if (parser.currentToken() != JsonToken.START_ARRAY) {
            return context.reportInputMismatch(this, "deliveryModes must be an array of strings");
        }
        List<DiscoveryDeliveryMode> modes = new ArrayList<>();
        for (JsonToken token = parser.nextToken(); token != JsonToken.END_ARRAY; token = parser.nextToken()) {
            if (token == JsonToken.VALUE_STRING) {
                addKnown(modes, parser.getString());
            } else {
                parser.skipChildren();
            }
        }
        return List.copyOf(modes);
    }

    @Override
    @SuppressWarnings("java:S1168") // null means the offer was absent, which differs from an empty offer
    public List<DiscoveryDeliveryMode> getNullValue(DeserializationContext context) {
        return null;
    }

    private static void addKnown(List<DiscoveryDeliveryMode> modes, String code) {
        try {
            modes.add(DiscoveryDeliveryMode.findByCode(code));
        } catch (ValidationException unknown) {
            // An offered mode this library does not know is not one this connector can choose.
        }
    }
}
