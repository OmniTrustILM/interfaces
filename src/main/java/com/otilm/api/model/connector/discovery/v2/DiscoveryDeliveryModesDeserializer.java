package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.otilm.api.exception.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the delivery modes Core offers, skipping codes this library does not know, so an offer from a newer Core still
 * parses. The single mode a connector chooses is read strictly.
 */
public class DiscoveryDeliveryModesDeserializer extends JsonDeserializer<List<DiscoveryDeliveryMode>> {

    @Override
    public List<DiscoveryDeliveryMode> deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        if (parser.currentToken() != JsonToken.START_ARRAY) {
            return context.reportInputMismatch(this, "deliveryModes must be an array of strings");
        }
        List<DiscoveryDeliveryMode> modes = new ArrayList<>();
        for (JsonToken token = parser.nextToken(); token != JsonToken.END_ARRAY; token = parser.nextToken()) {
            if (token == JsonToken.VALUE_STRING) {
                addKnown(modes, parser.getText());
            } else {
                parser.skipChildren();
            }
        }
        return List.copyOf(modes);
    }

    @Override
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
