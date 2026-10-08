package com.otilm.api.config.jackson3;

import com.otilm.api.model.core.secret.Passphrase;
import com.otilm.api.model.core.secret.PassphraseFormatException;
import java.util.Arrays;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

/**
 * Jackson 3 twin of {@link com.otilm.api.model.core.secret.PassphraseDeserializer}. Jackson 3 handlers throw only
 * unchecked exceptions, so the {@link PassphraseFormatException} travels as the cause.
 */
final class PassphraseDeserializer extends ValueDeserializer<Passphrase> {

    private static final String WRONG_TYPE = "passphrase must be a string";

    @Override
    public Passphrase deserialize(JsonParser parser, DeserializationContext context) {
        if (parser.currentToken() != JsonToken.VALUE_STRING) {
            throw DatabindException.from(parser, WRONG_TYPE, new PassphraseFormatException(WRONG_TYPE));
        }
        char[] characters = new char[parser.getStringLength()];
        System.arraycopy(parser.getStringCharacters(), parser.getStringOffset(), characters, 0, characters.length);
        Passphrase passphrase = new Passphrase(characters);
        Arrays.fill(characters, '\0');
        return passphrase;
    }

    @Override
    public Passphrase getNullValue(DeserializationContext context) {
        return null;
    }
}
