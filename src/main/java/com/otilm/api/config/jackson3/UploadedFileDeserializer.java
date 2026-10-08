package com.otilm.api.config.jackson3;

import com.otilm.api.model.core.secret.UploadedFile;
import com.otilm.api.model.core.secret.UploadedFileFormatException;
import java.util.Arrays;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Jackson 3 twin of {@link com.otilm.api.model.core.secret.UploadedFileDeserializer}. Jackson 3 handlers throw only
 * unchecked exceptions, so the {@link UploadedFileFormatException} travels as the cause.
 */
final class UploadedFileDeserializer extends ValueDeserializer<UploadedFile> {

    private static final String WRONG_FORMAT = "file must be a base64-encoded string";

    @Override
    public UploadedFile deserialize(JsonParser parser, DeserializationContext context) {
        if (parser.currentToken() != JsonToken.VALUE_STRING) {
            throw wrongFormat(parser);
        }
        byte[] content;
        try {
            content = parser.getBinaryValue();
        } catch (JacksonException | IllegalArgumentException e) {
            throw wrongFormat(parser);
        }
        UploadedFile file = new UploadedFile(content);
        Arrays.fill(content, (byte) 0);
        return file;
    }

    @Override
    public UploadedFile getNullValue(DeserializationContext context) {
        return null;
    }

    private static DatabindException wrongFormat(JsonParser parser) {
        return DatabindException.from(parser, WRONG_FORMAT, new UploadedFileFormatException(WRONG_FORMAT));
    }
}
