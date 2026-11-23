package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiscoveredItemDtoTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void resourceKeyAppearsExactlyOnce() throws Exception {
        String json = mapper.writeValueAsString(certificateItem());
        assertEquals(1, StringUtils.countMatches(json, "\"resource\""));
    }

    @Test
    void getResourceIsDerivedFromPayloadAndNotSerialized() throws Exception {
        DiscoveredItemDto dto = certificateItem();

        assertEquals(Resource.CERTIFICATE, dto.getResource(), "getResource must delegate to the payload");

        String json = mapper.writeValueAsString(dto);
        // The sibling test covering single occurrence already shows the payload carries the only
        // "resource" key. What matters here is different: the container's derived accessor must
        // not contribute a second, top-level one. Asserted on the parsed tree rather than the raw
        // string, so nesting is taken into account.
        JsonNode root = mapper.readTree(json);
        assertFalse(root.has("resource"),
                "resource must not appear as a sibling of payload; it must live inside payload only");
        assertTrue(root.path("payload").has("resource"), "resource must live inside payload");

        DiscoveredItemDto withoutPayload = new DiscoveredItemDto();
        assertNull(withoutPayload.getResource(), "getResource must be null-safe when payload is absent");
    }

    @Test
    void certificateItemRoundTrips() throws Exception {
        DiscoveredItemDto dto = certificateItem();

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"meta\""), "metadata field must serialize as meta, not attributes");
        assertFalse(json.contains("\"attributes\""), "metadata field must never serialize as attributes");
        assertTrue(json.contains("\"resource\":\"certificates\""), "resource must serialize using the wire code");

        DiscoveredItemDto back = mapper.readValue(json, DiscoveredItemDto.class);
        assertEquals(1L, back.getSequence());
        assertEquals(Resource.CERTIFICATE, back.getResource());
        assertEquals("cert-ref-1", back.getUniqueRef());

        DiscoveredCertificateDto payload = assertInstanceOf(DiscoveredCertificateDto.class, back.getPayload());
        assertEquals("Y2VydC1kYXRh", payload.getCertificateData());
        assertEquals(Resource.CERTIFICATE, payload.getResource());

        assertEquals(1, back.getMeta().size());
        MetadataAttributeV3 backMeta = assertInstanceOf(MetadataAttributeV3.class, back.getMeta().get(0));
        assertEquals("source", backMeta.getName());
        assertEquals("scanner-1", ((StringAttributeContentV3) backMeta.getContent().get(0)).getData());

        assertEquals(OffsetDateTime.parse("2026-08-01T00:00:00Z"), back.getDiscoveredAt());
    }

    @Test
    void publicKeyItemRoundTripsAndResolvesKeyPayload() throws Exception {
        DiscoveredItemDto dto = new DiscoveredItemDto();
        dto.setSequence(2L);
        dto.setUniqueRef("key-ref-1");

        DiscoveredKeyDto key = new DiscoveredKeyDto();
        key.setType(KeyType.PUBLIC_KEY);
        key.setAlgorithm(KeyAlgorithm.RSA);
        key.setLength(2048);
        key.setFingerprint("3b8f1c2a9d4e5f607182930415263748596a7b8c9d0e1f2a3b4c5d6e7f8a9b0");
        key.setPublicKeyFormat(KeyFormat.SPKI);
        key.setPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A");
        dto.setPayload(key);

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"resource\":\"keys\""), "resource must serialize using the wire code");
        assertEquals(1, StringUtils.countMatches(json, "\"resource\""));
        // Platform wire codes are not camelCase — pin the real codes, not the enum's own name().
        assertTrue(json.contains("\"type\":\"Public\""),
                "KeyType must serialize using its platform code, not PUBLIC_KEY");
        assertTrue(json.contains("\"algorithm\":\"RSA\""));
        assertTrue(json.contains("\"publicKeyFormat\":\"SubjectPublicKeyInfo\""),
                "KeyFormat must serialize using its platform code, not SPKI");

        DiscoveredItemDto back = mapper.readValue(json, DiscoveredItemDto.class);
        assertEquals(Resource.CRYPTOGRAPHIC_KEY, back.getResource());

        DiscoveredKeyDto backPayload = assertInstanceOf(DiscoveredKeyDto.class, back.getPayload());
        assertEquals(KeyType.PUBLIC_KEY, backPayload.getType());
        assertEquals(KeyAlgorithm.RSA, backPayload.getAlgorithm());
        assertEquals(2048, backPayload.getLength());
        assertEquals("3b8f1c2a9d4e5f607182930415263748596a7b8c9d0e1f2a3b4c5d6e7f8a9b0", backPayload.getFingerprint());
        assertEquals(KeyFormat.SPKI, backPayload.getPublicKeyFormat());
        assertEquals("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A", backPayload.getPublicKey());
    }

    @Test
    void privateKeyExistenceOnlyItemRoundTripsAndValidates() throws Exception {
        // A private key's bytes never traverse discovery: only its existence and its intrinsic,
        // non-secret metadata are reported.
        DiscoveredItemDto dto = new DiscoveredItemDto();
        dto.setSequence(3L);
        dto.setUniqueRef("key-ref-2");

        DiscoveredKeyDto key = new DiscoveredKeyDto();
        key.setType(KeyType.PRIVATE_KEY);
        key.setAlgorithm(KeyAlgorithm.ECDSA);
        key.setFingerprint("9c1a2b3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcde");
        // publicKey, publicKeyFormat, length intentionally left unset.
        dto.setPayload(key);

        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"resource\":\"keys\""));
        assertTrue(json.contains("\"type\":\"Private\""));
        assertFalse(json.contains("\"publicKey\""), "no public key material may appear when absent");
        assertFalse(json.contains("\"publicKeyFormat\""), "no public key format may appear when publicKey is absent");
        assertFalse(json.contains("\"length\""), "length is genuinely unknown here and must be omitted, not nulled");

        DiscoveredItemDto back = mapper.readValue(json, DiscoveredItemDto.class);
        DiscoveredKeyDto backPayload = assertInstanceOf(DiscoveredKeyDto.class, back.getPayload());
        assertEquals(KeyType.PRIVATE_KEY, backPayload.getType());
        assertEquals(KeyAlgorithm.ECDSA, backPayload.getAlgorithm());
        assertEquals("9c1a2b3d4e5f60718293a4b5c6d7e8f90123456789abcdef0123456789abcde", backPayload.getFingerprint());
        assertNull(backPayload.getPublicKey());
        assertNull(backPayload.getPublicKeyFormat());
        assertNull(backPayload.getLength());

        Set<ConstraintViolation<DiscoveredItemDto>> violations = VALIDATOR.validate(back);
        assertTrue(violations.isEmpty(), "an existence-only private-key report must pass validation cleanly");
    }

    @Test
    void handWrittenLiteralWithRfc3339TimestampRoundTrips() throws Exception {
        // A hand-written literal, not this class's own serializer output: Go and Python connectors
        // emit discoveredAt as an RFC-3339 string, not the numeric timestamp form Jackson writes.
        String json = "{\"sequence\":1,\"uniqueRef\":\"r\","
                + "\"payload\":{\"resource\":\"certificates\",\"certificateData\":\"Y2VydC1kYXRh\"},"
                + "\"discoveredAt\":\"2026-08-01T00:00:00Z\"}";

        DiscoveredItemDto dto = mapper.readValue(json, DiscoveredItemDto.class);

        assertEquals(1L, dto.getSequence());
        assertEquals(Resource.CERTIFICATE, dto.getResource());
        assertEquals("r", dto.getUniqueRef());
        DiscoveredCertificateDto payload = assertInstanceOf(DiscoveredCertificateDto.class, dto.getPayload());
        assertEquals("Y2VydC1kYXRh", payload.getCertificateData());
        assertEquals(OffsetDateTime.parse("2026-08-01T00:00:00Z"), dto.getDiscoveredAt());
    }

    @Test
    void unregisteredOrUnknownResourceCodeFailsTypeResolution() {
        // Jackson matches the wire string against the @JsonSubTypes names registered on
        // DiscoveredItemPayloadDto, never against Resource's own codes. So "groups" (a real
        // Resource, but not a registered payload) and "widgets" (not a Resource at all) fail
        // identically — both are simply absent from the registered subtype names.
        String groupsJson = "{\"sequence\":1,\"uniqueRef\":\"r\",\"payload\":{\"resource\":\"groups\"}}";
        String widgetsJson = "{\"sequence\":1,\"uniqueRef\":\"r\",\"payload\":{\"resource\":\"widgets\"}}";

        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(groupsJson, DiscoveredItemDto.class));
        assertThrows(InvalidTypeIdException.class, () -> mapper.readValue(widgetsJson, DiscoveredItemDto.class));
    }

    @Test
    void payloadMissingResourceDiscriminatorFailsTypeResolution() {
        // The payload object is present, but carries no "resource" key at all: Jackson has no
        // discriminator to resolve a concrete payload subtype from.
        String json = "{\"sequence\":1,\"uniqueRef\":\"r\",\"payload\":{\"certificateData\":\"x\"}}";

        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, DiscoveredItemDto.class));
    }

    @Test
    void payloadNullResourceDiscriminatorFailsTypeResolution() {
        String json = "{\"sequence\":1,\"uniqueRef\":\"r\",\"payload\":{\"resource\":null,\"certificateData\":\"x\"}}";

        assertThrows(MismatchedInputException.class, () -> mapper.readValue(json, DiscoveredItemDto.class));
    }

    @Test
    void mismatchedPayloadShapeFailsValidationNotDeserialization() throws Exception {
        // resource says "keys" but the body carries the certificate shape. Because
        // DiscoveredKeyDto tolerates unknown properties — so connectors can add a field without a
        // lock-step release — Jackson cannot distinguish this from a new field on the right shape
        // and deserializes it rather than throwing. The @NotNull constraints catch it instead,
        // which makes a shape mismatch a validation failure, not a deserialization one.
        String json = "{\"sequence\":1,\"uniqueRef\":\"r\","
                + "\"payload\":{\"resource\":\"keys\",\"certificateData\":\"x\"}}";

        DiscoveredItemDto dto = mapper.readValue(json, DiscoveredItemDto.class);
        DiscoveredKeyDto payload = assertInstanceOf(DiscoveredKeyDto.class, dto.getPayload());
        assertEquals(Resource.CRYPTOGRAPHIC_KEY, payload.getResource());
        assertNull(payload.getType());
        assertNull(payload.getAlgorithm());

        Set<ConstraintViolation<DiscoveredItemDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("payload.type")),
                "a cert-shaped payload under resource: keys must fail validation on the key's required type");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("payload.algorithm")),
                "a cert-shaped payload under resource: keys must fail validation on the key's required algorithm");
    }

    @Test
    void unknownAdditionalPropertyOnCorrectlyShapedPayloadIsTolerated() throws Exception {
        // This is the case @JsonIgnoreProperties(ignoreUnknown = true) exists for: a connector
        // adds a genuinely new field to an otherwise-correctly-shaped payload, and Core must not
        // choke on it (that is what keeps Go/Java/Python connectors free of a lock-step release).
        String json = "{\"sequence\":1,\"uniqueRef\":\"r\","
                + "\"payload\":{\"resource\":\"certificates\",\"certificateData\":\"Y2VydC1kYXRh\",\"issuer\":\"CA-1\"}}";

        DiscoveredItemDto dto = mapper.readValue(json, DiscoveredItemDto.class);
        DiscoveredCertificateDto payload = assertInstanceOf(DiscoveredCertificateDto.class, dto.getPayload());
        assertEquals("Y2VydC1kYXRh", payload.getCertificateData());

        Set<ConstraintViolation<DiscoveredItemDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.isEmpty(),
                "an unknown but additional field on an otherwise-correct payload must not fail validation");
    }

    @Test
    void nonObjectTopLevelInputIsRejected() {
        assertThrows(MismatchedInputException.class,
                () -> mapper.readValue("\"just-a-string\"", DiscoveredItemDto.class));
    }

    @Test
    void missingPayloadDeserializesToNullAndFailsNotNullValidation() throws Exception {
        String json = "{\"sequence\":1,\"uniqueRef\":\"r\"}";

        DiscoveredItemDto dto = mapper.readValue(json, DiscoveredItemDto.class);
        assertNull(dto.getPayload());
        assertNull(dto.getResource(), "getResource must be null-safe when payload is absent");

        Set<ConstraintViolation<DiscoveredItemDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("payload")),
                "a null payload must fail the @NotNull constraint");
    }

    @Test
    void missingSequenceIsRejectedRatherThanDefaultedToZero() throws Exception {
        // A boxed sequence is what makes this catchable: a primitive long would serialize a
        // forgotten cursor as 0, which contradicts the published minimum of 1.
        DiscoveredItemDto fresh = new DiscoveredItemDto();
        assertNull(fresh.getSequence(), "sequence must not be defaulted to 0");

        DiscoveredItemDto dto = mapper
                .readValue(
                        "{\"uniqueRef\":\"r\",\"payload\":{\"resource\":\"certificates\",\"certificateData\":\"Y2VydC1kYXRh\"}}",
                        DiscoveredItemDto.class);
        assertNull(dto.getSequence(), "an omitted sequence must deserialize to null, not to 0");

        Set<ConstraintViolation<DiscoveredItemDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("sequence")),
                "an omitted sequence must fail the @NotNull constraint");
    }

    @Test
    void zeroAndNegativeSequenceAreRejected() {
        DiscoveredItemDto zero = new DiscoveredItemDto();
        zero.setSequence(0L);
        zero.setUniqueRef("r");
        zero.setPayload(certificatePayload());

        DiscoveredItemDto negative = new DiscoveredItemDto();
        negative.setSequence(-1L);
        negative.setUniqueRef("r");
        negative.setPayload(certificatePayload());

        assertTrue(VALIDATOR.validate(zero).stream().anyMatch(v -> v.getPropertyPath().toString().equals("sequence")),
                "sequence 0 must be rejected: the published minimum is 1");
        assertTrue(
                VALIDATOR.validate(negative).stream().anyMatch(v -> v.getPropertyPath().toString().equals("sequence")),
                "a negative sequence must be rejected");
    }

    @Test
    void emptyPayloadCascadesIntoPayloadConstraintsViaValid() {
        DiscoveredItemDto dto = new DiscoveredItemDto();
        dto.setSequence(1L);
        dto.setUniqueRef("r");
        dto.setPayload(new DiscoveredCertificateDto()); // certificateData left null (resource keeps its default)

        Set<ConstraintViolation<DiscoveredItemDto>> violations = VALIDATOR.validate(dto);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("payload.certificateData")),
                "@Valid must cascade into payload so certificateData's @NotBlank is actually evaluated");
    }

    private DiscoveredCertificateDto certificatePayload() {
        DiscoveredCertificateDto cert = new DiscoveredCertificateDto();
        cert.setCertificateData("Y2VydC1kYXRh");
        return cert;
    }

    private DiscoveredItemDto certificateItem() {
        DiscoveredItemDto dto = new DiscoveredItemDto();
        dto.setSequence(1L);
        dto.setUniqueRef("cert-ref-1");
        dto.setPayload(certificatePayload());

        MetadataAttributeV3 metaAttribute = new MetadataAttributeV3();
        metaAttribute.setUuid("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        metaAttribute.setName("source");
        metaAttribute.setContentType(AttributeContentType.STRING);
        MetadataAttributeProperties properties = new MetadataAttributeProperties();
        properties.setLabel("Source");
        metaAttribute.setProperties(properties);
        metaAttribute.setContent(List.of(new StringAttributeContentV3("scanner-1")));
        dto.setMeta(List.of(metaAttribute));

        dto.setDiscoveredAt(OffsetDateTime.parse("2026-08-01T00:00:00Z"));
        return dto;
    }
}
