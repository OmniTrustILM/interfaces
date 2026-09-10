package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A payload's {@code resource} is its own discriminator, so it must not be independently writable: a settable
 * discriminator lets Java code serialize an object whose declared shape and declared resource contradict each other,
 * and the receiver then only notices by accident, when the subtype the wire value resolved to turns out to be missing
 * its own required fields.
 *
 * <p>
 * Fixing the field to its subtype's constant only works if Jackson can still resolve and emit it without a mutator, so
 * both directions are pinned here rather than assumed.
 */
class DiscoveredItemPayloadDiscriminatorTest {

    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void neitherPayloadSubtypeExposesAResourceSetter() {
        assertThrows(NoSuchMethodException.class,
                () -> DiscoveredCertificateDto.class.getMethod("setResource", Resource.class),
                "a certificate payload must not be able to relabel itself as another resource");
        assertThrows(NoSuchMethodException.class, () -> DiscoveredKeyDto.class.getMethod("setResource", Resource.class),
                "a key payload must not be able to relabel itself as another resource");
    }

    @Test
    void eachSubtypeReportsItsOwnConstantOnAFreshInstance() {
        assertEquals(Resource.CERTIFICATE, new DiscoveredCertificateDto().getResource());
        assertEquals(Resource.CRYPTOGRAPHIC_KEY, new DiscoveredKeyDto().getResource());
    }

    @Test
    void certificatePayloadStillSerializesItsDiscriminator() throws Exception {
        DiscoveredCertificateDto payload = new DiscoveredCertificateDto();
        payload.setCertificateData("Y2VydC1kYXRh");

        String json = mapper.writeValueAsString(payload);

        assertTrue(json.contains("\"resource\":\"certificates\""),
                "a non-settable discriminator must still be written to the wire: " + json);
    }

    @Test
    void keyPayloadStillSerializesItsDiscriminator() throws Exception {
        DiscoveredKeyDto payload = new DiscoveredKeyDto();
        payload.setType(KeyType.PUBLIC_KEY);
        payload.setAlgorithm(KeyAlgorithm.RSA);

        String json = mapper.writeValueAsString(payload);

        assertTrue(json.contains("\"resource\":\"keys\""),
                "a non-settable discriminator must still be written to the wire: " + json);
    }

    @Test
    void polymorphicDeserializationStillResolvesBothSubtypesFromTheWireValue() throws Exception {
        DiscoveredItemPayloadDto cert = mapper
                .readValue("{\"resource\":\"certificates\",\"certificateData\":\"Y2VydC1kYXRh\"}",
                        DiscoveredItemPayloadDto.class);
        DiscoveredCertificateDto certPayload = assertInstanceOf(DiscoveredCertificateDto.class, cert);
        assertEquals(Resource.CERTIFICATE, certPayload.getResource());
        assertEquals("Y2VydC1kYXRh", certPayload.getCertificateData());

        DiscoveredItemPayloadDto key = mapper
                .readValue("{\"resource\":\"keys\",\"type\":\"Public\",\"algorithm\":\"RSA\"}",
                        DiscoveredItemPayloadDto.class);
        DiscoveredKeyDto keyPayload = assertInstanceOf(DiscoveredKeyDto.class, key);
        assertEquals(Resource.CRYPTOGRAPHIC_KEY, keyPayload.getResource());
        assertEquals(KeyType.PUBLIC_KEY, keyPayload.getType());
    }

    @Test
    void bothSubtypesRoundTripThroughTheBaseType() throws Exception {
        DiscoveredCertificateDto cert = new DiscoveredCertificateDto();
        cert.setCertificateData("Y2VydC1kYXRh");

        DiscoveredItemPayloadDto certBack = mapper
                .readValue(mapper.writeValueAsString(cert), DiscoveredItemPayloadDto.class);
        assertEquals(Resource.CERTIFICATE, assertInstanceOf(DiscoveredCertificateDto.class, certBack).getResource());

        DiscoveredKeyDto key = new DiscoveredKeyDto();
        key.setType(KeyType.PUBLIC_KEY);
        key.setAlgorithm(KeyAlgorithm.RSA);

        DiscoveredItemPayloadDto keyBack = mapper
                .readValue(mapper.writeValueAsString(key), DiscoveredItemPayloadDto.class);
        assertEquals(Resource.CRYPTOGRAPHIC_KEY, assertInstanceOf(DiscoveredKeyDto.class, keyBack).getResource());
    }

    /**
     * A property with a getter and no setter is the shape swagger-core publishes as {@code readOnly}, which would tell
     * every generator the discriminator must not be sent in a request body. It does not here - but that is a fact about
     * the generator, not about this contract, so it is pinned rather than trusted. Each subtype publishes the
     * discriminator itself, so it is checked there rather than on the union.
     */
    @Test
    void theDiscriminatorStaysRequiredAndWritableInTheGeneratedSchema() {
        Map<String, Schema> schemas = ModelConverters.getInstance().readAll(DiscoveredItemPayloadDto.class);

        for (String subtype : List.of("DiscoveredCertificateDto", "DiscoveredKeyDto")) {
            Schema<?> sub = schemas.get(subtype);
            assertNotNull(sub, "expected a generated schema named " + subtype + "; found " + schemas.keySet());
            assertTrue(sub.getRequired().contains("resource"),
                    subtype + " must still publish resource as required; was " + sub.getRequired());
            assertNotEquals(Boolean.TRUE, sub.getProperties().get("resource").getReadOnly(),
                    "the discriminator must not become readOnly: connectors send it in request bodies");
        }
    }

    /**
     * Reading a concrete subtype still goes through the discriminator the interface declares, so a wire value naming
     * the other subtype is rejected outright rather than quietly relabeling the object. The matching value binds
     * normally and leaves the constant intact.
     */
    @Test
    void aConcreteSubtypeRejectsAContradictingWireValueAndAcceptsItsOwn() throws Exception {
        assertThrows(InvalidTypeIdException.class,
                () -> mapper
                        .readValue("{\"resource\":\"keys\",\"certificateData\":\"Y2VydC1kYXRh\"}",
                                DiscoveredCertificateDto.class),
                "resource: keys cannot resolve to a certificate payload");
        assertThrows(InvalidTypeIdException.class,
                () -> mapper
                        .readValue("{\"resource\":\"certificates\",\"type\":\"Public\",\"algorithm\":\"RSA\"}",
                                DiscoveredKeyDto.class),
                "resource: certificates cannot resolve to a key payload");

        DiscoveredCertificateDto cert = mapper
                .readValue("{\"resource\":\"certificates\",\"certificateData\":\"Y2VydC1kYXRh\"}",
                        DiscoveredCertificateDto.class);
        assertEquals(Resource.CERTIFICATE, cert.getResource());
        assertEquals("Y2VydC1kYXRh", cert.getCertificateData());

        DiscoveredKeyDto key = mapper
                .readValue("{\"resource\":\"keys\",\"type\":\"Public\",\"algorithm\":\"RSA\"}", DiscoveredKeyDto.class);
        assertEquals(Resource.CRYPTOGRAPHIC_KEY, key.getResource());
        assertEquals(KeyType.PUBLIC_KEY, key.getType());
    }
}
