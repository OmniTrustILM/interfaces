package com.otilm.api.config.jackson3;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/** Jackson 3 twin of {@link com.otilm.api.model.common.attribute.v1.content.ZonedDateTimeDeserializer}. */
final class OffsetZonedDateTimeDeserializer extends ValueDeserializer<ZonedDateTime> {

    @Override
    public ZonedDateTime deserialize(JsonParser parser, DeserializationContext context) {
        return ZonedDateTime.parse(parser.getString(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
