package com.otilm.api.config.jackson3;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.client.signing.profile.scheme.ManagedSigningDto;
import com.otilm.api.model.client.signing.profile.scheme.SigningSchemeDto;
import com.otilm.api.model.client.signing.profile.scheme.SigningSchemeRequestDto;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v1.content.DateAttributeContent;
import com.otilm.api.model.common.attribute.v1.content.DateTimeAttributeContent;
import com.otilm.api.model.common.attribute.v1.content.TimeAttributeContent;
import com.otilm.api.model.common.attribute.v2.content.DateTimeAttributeContentV2;
import com.otilm.api.model.common.attribute.v3.content.DateTimeAttributeContentV3;
import com.otilm.api.model.common.events.data.CertificateExpiringEventData;
import com.otilm.api.model.core.secret.Passphrase;
import com.otilm.api.model.core.secret.PassphraseFormatException;
import com.otilm.api.model.core.secret.UploadedFile;
import com.otilm.api.model.core.secret.UploadedFileFormatException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.exc.InvalidTypeIdException;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A connector's REST API reads and writes these DTOs with Jackson 3 on Spring Boot 4.1, and must put the same JSON on
 * the wire as it did with Jackson 2 on Spring Boot 3.5.
 */
class Jackson3ParityTest {

    private static final String ATTRIBUTE_DEFINITIONS = """
            [
              {"uuid": "11111111-1111-1111-1111-111111111111", "name": "dataV2", "description": "d",
               "type": "data", "contentType": "string", "version": 2,
               "content": [{"reference": "r", "data": "x"}],
               "properties": {"label": "Data", "visible": true, "required": true, "readOnly": false,
                              "list": false, "multiSelect": false}},
              {"uuid": "22222222-2222-2222-2222-222222222222", "name": "dataV3", "type": "data",
               "contentType": "date", "version": 3,
               "content": [{"contentType": "date", "data": "2024-05-06"}]},
              {"uuid": "33333333-3333-3333-3333-333333333333", "name": "infoV2", "type": "info",
               "contentType": "text", "version": 2, "content": [{"data": "hello"}]},
              {"uuid": "44444444-4444-4444-4444-444444444444", "name": "groupV3", "type": "group",
               "version": 3},
              {"uuid": "55555555-5555-5555-5555-555555555555", "name": "metaV2", "type": "meta",
               "contentType": "datetime", "version": 2,
               "content": [{"data": "2024-05-06T07:08:09.123+02:00"}]},
              {"uuid": "66666666-6666-6666-6666-666666666666", "name": "customV3", "type": "custom",
               "contentType": "time", "version": 3,
               "content": [{"contentType": "time", "data": "07:08:09"}]},
              {"uuid": "77777777-7777-7777-7777-777777777777", "name": "noVersion", "type": "info",
               "contentType": "string", "content": [{"data": "v2 by default"}]}
            ]
            """;

    record Sample(String name, String json, Class<?> type, Class<?>... parameters) {

        @Override
        public String toString() {
            return name;
        }
    }

    static Stream<Sample> samples() {
        return Stream
                .of(new Sample("attribute definitions of every type and version", ATTRIBUTE_DEFINITIONS, List.class,
                        BaseAttribute.class), new Sample("request attributes of both versions", """
                                [
                                  {"uuid": "11111111-1111-1111-1111-111111111111", "name": "r2", "version": "v2",
                                   "contentType": "datetime",
                                   "content": [{"reference": "r", "data": "2024-05-06T07:08:09.123+02:00"}]},
                                  {"uuid": "22222222-2222-2222-2222-222222222222", "name": "r3", "version": "v3",
                                   "contentType": "time", "content": [{"contentType": "time", "data": "07:08:09"}]}
                                ]
                                """, List.class, RequestAttribute.class),
                        new Sample("attribute content of either version", """
                                [{"contentType": "string", "data": "v3"}, {"reference": "r", "data": "v2"}]
                                """, List.class, AttributeContent.class),
                        new Sample("a response attribute holding a secret",
                                """
                                        {"uuid": "11111111-1111-1111-1111-111111111111", "name": "s", "type": "data",
                                         "version": "v2", "contentType": "secret",
                                         "content": [{"reference": "r", "data": {"secret": "top", "protectionLevel": "encrypted"}}]}
                                        """,
                                ResponseAttribute.class),
                        new Sample("a response attribute holding strings", """
                                {"uuid": "11111111-1111-1111-1111-111111111111", "name": "s", "type": "data",
                                 "version": "v2", "contentType": "string", "content": [{"data": "a"}, {"data": "b"}]}
                                """, ResponseAttribute.class), new Sample("a v1 date-time", """
                                [{"value": "2024-05-06T07:08:09.123+02:00"}]
                                """, List.class, DateTimeAttributeContent.class),
                        new Sample("a v1 date", "{\"value\": \"2024-05-06\"}", DateAttributeContent.class),
                        new Sample("a v1 time", "{\"value\": \"07:08:09\"}", TimeAttributeContent.class),
                        new Sample("a v2 date-time with an offset", """
                                {"reference": "r", "data": "2024-05-06T07:08:09.123+02:00"}
                                """, DateTimeAttributeContentV2.class),
                        new Sample("a v2 date-time without an offset", """
                                {"reference": "r", "data": "2024-05-06T07:08:09.123"}
                                """, DateTimeAttributeContentV2.class),
                        new Sample("a v3 date-time keeps its offset", """
                                {"contentType": "datetime", "data": "2024-05-06T07:08:09.123+02:00"}
                                """, DateTimeAttributeContentV3.class), new Sample("event data with zoned dates", """
                                {"raProfileName": "ra", "notBefore": "2024-05-06T07:08:09+02:00",
                                 "expiresAt": "2025-05-06T07:08:09Z"}
                                """, CertificateExpiringEventData.class),
                        new Sample("a static-key managed signing scheme", """
                                {"signingScheme": "managed", "managedSigningType": "static_key",
                                 "certificate": {"uuid": "65418a34-360d-4b4c-ae2c-e716644d4120", "commonName": "c"}}
                                """, SigningSchemeDto.class), new Sample("a one-time-key managed signing scheme", """
                                {"signingScheme": "managed", "managedSigningType": "one_time_key",
                                 "raProfile": {"uuid": "65418a34-360d-4b4c-ae2c-e716644d4120", "name": "ra"}}
                                """, ManagedSigningDto.class), new Sample("a static-key managed signing request", """
                                {"signingScheme": "managed", "managedSigningType": "static_key",
                                 "certificateUuid": "65418a34-360d-4b4c-ae2c-e716644d4120",
                                 "signingOperationAttributes": [{"uuid": "11111111-1111-1111-1111-111111111111",
                                   "name": "a", "version": "v3", "contentType": "string",
                                   "content": [{"contentType": "string", "data": "x"}]}]}
                                """, SigningSchemeRequestDto.class));
    }

    @Test
    void connectorsFindTheModuleOnTheClasspath() {
        assertTrue(Jackson3Mvc.MAPPER.registeredModules().stream().anyMatch(InterfacesJacksonModule.class::isInstance));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("samples")
    void readsAndWritesLikeJackson2(Sample sample) throws IOException {
        Object readByJackson2 = Jackson2Mvc.read(sample.json(), sample.type(), sample.parameters());
        Object readByJackson3 = Jackson3Mvc.read(sample.json(), sample.type(), sample.parameters());
        String expected = Jackson2Mvc.write(readByJackson2, sample.type(), sample.parameters());

        assertAll(
                () -> assertEquals(Jackson2Mvc.tree(expected),
                        Jackson2Mvc.tree(Jackson2Mvc.write(readByJackson3, sample.type(), sample.parameters())),
                        "Jackson 3 read a different value"),
                () -> assertEquals(Jackson2Mvc.tree(expected),
                        Jackson2Mvc.tree(Jackson3Mvc.write(readByJackson2, sample.type(), sample.parameters())),
                        "Jackson 3 wrote different JSON"));
    }

    /**
     * Jackson writes a value declared as {@code BaseAttribute}, such as a controller's return value, with its own
     * serializer.
     */
    @Test
    void writesAnAttributeDeclaredAsBaseAttributeLikeJackson2() throws IOException {
        for (Object attribute : (List<?>) Jackson2Mvc.read(ATTRIBUTE_DEFINITIONS, List.class, BaseAttribute.class)) {
            assertEquals(Jackson2Mvc.tree(Jackson2Mvc.write(attribute, BaseAttribute.class)),
                    Jackson2Mvc.tree(Jackson3Mvc.write(attribute, BaseAttribute.class)));
        }
    }

    @Test
    void readsAPassphraseCharacterForCharacter() {
        Passphrase passphrase = (Passphrase) Jackson3Mvc.read("\"pä55 wörd\"", Passphrase.class);

        assertEquals("pä55 wörd", new String(passphrase.characters()));
    }

    @Test
    void refusesANonStringPassphraseWithoutQuotingIt() {
        DatabindException e = assertThrows(DatabindException.class, () -> Jackson3Mvc.read("4711", Passphrase.class));

        assertFalse(e.getMessage().contains("4711"), e.getMessage());
        assertInstanceOf(PassphraseFormatException.class, ExceptionUtils.getRootCause(e));
    }

    @Test
    void decodesAnUploadedFile() {
        UploadedFile file = (UploadedFile) Jackson3Mvc.read("\"a2V5IG1hdGVyaWFs\"", UploadedFile.class);

        assertArrayEquals("key material".getBytes(StandardCharsets.US_ASCII), file.content());
    }

    @Test
    void refusesAnUploadedFileThatIsNotBase64WithoutQuotingIt() {
        DatabindException e = assertThrows(DatabindException.class,
                () -> Jackson3Mvc.read("\"not*base64\"", UploadedFile.class));

        assertFalse(e.getMessage().contains("not*base64"), e.getMessage());
        assertInstanceOf(UploadedFileFormatException.class, ExceptionUtils.getRootCause(e));
    }

    @Test
    void namesAnUnknownManagedSigningTypeAsJackson2Does() {
        String json = "{\"signingScheme\": \"managed\", \"managedSigningType\": \"no_such_type\"}";

        InvalidTypeIdException e = assertThrows(InvalidTypeIdException.class,
                () -> Jackson3Mvc.read(json, ManagedSigningDto.class));

        assertEquals("no_such_type", e.getTypeId());
        assertTrue(e.getOriginalMessage().startsWith("Unknown managedSigningType: no_such_type"), e.getMessage());
    }
}
