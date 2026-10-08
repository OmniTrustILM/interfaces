package com.otilm.api.config.jackson3;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Jackson 3 twin of {@link com.otilm.api.model.common.attribute.common.content.ZonedDateTimeDeserializer}: a value
 * without an offset is read in the JVM's zone.
 */
final class ZonedDateTimeDeserializer extends ValueDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) {
        String text = parser.getString();
        try {
            return ZonedDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException e) {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atZone(ZoneId.systemDefault());
        }
    }
}
