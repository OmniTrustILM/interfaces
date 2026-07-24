package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.Introspector;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class KeyDataV2Test {

    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void deserializesEveryKeyRoleThroughDiscriminator() throws Exception {
        assertInstanceOf(SecretKeyDataV2Dto.class, read("Secret", "AES"));
        assertInstanceOf(PublicKeyDataV2Dto.class, read("Public", "RSA"));
        assertInstanceOf(PrivateKeyDataV2Dto.class, read("Private", "RSA"));
    }

    @Test
    void hierarchyIsClosedToUnadvertisedMaterialBearingSubtypes() {
        assertTrue(KeyDataV2Dto.class.isSealed());
        assertEquals(Set.of(
                        SecretKeyDataV2Dto.class,
                        PublicKeyDataV2Dto.class,
                        PrivateKeyDataV2Dto.class),
                Arrays.stream(KeyDataV2Dto.class.getPermittedSubclasses()).collect(Collectors.toSet()));
    }

    @Test
    void nonPublicRolesHaveNoRepresentationProperties() {
        List<KeyDataV2Dto> nonPublic = List.of(
                initialized(new SecretKeyDataV2Dto(), KeyAlgorithm.AES),
                initialized(new PrivateKeyDataV2Dto(), KeyAlgorithm.RSA));

        for (KeyDataV2Dto keyData : nonPublic) {
            JsonNode json = mapper.valueToTree(keyData);
            assertFalse(json.has("publicKeySpki"));
            assertFalse(json.has("format"));
            assertFalse(json.has("value"));
        }

        for (Class<? extends KeyDataV2Dto> type : List.of(
                SecretKeyDataV2Dto.class, PrivateKeyDataV2Dto.class)) {
            assertDoesNotThrow(() -> {
                Set<String> properties = Arrays.stream(Introspector.getBeanInfo(type).getPropertyDescriptors())
                        .map(descriptor -> descriptor.getName().toLowerCase())
                        .collect(Collectors.toSet());
                assertFalse(properties.stream().anyMatch(name -> name.contains("value")
                        || name.contains("spki") || name.contains("private") || name.contains("raw")));
            });
        }
    }

    @Test
    void publicRoleAcceptsOnlyCanonicalDerSubjectPublicKeyInfo() throws Exception {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        byte[] spki = keyPair.getPublic().getEncoded();
        PublicKeyDataV2Dto publicKey = initialized(new PublicKeyDataV2Dto(), KeyAlgorithm.RSA);
        publicKey.setPublicKeySpki(spki);

        String json = mapper.writeValueAsString(publicKey);
        KeyDataV2Dto roundTrip = mapper.readValue(json, KeyDataV2Dto.class);

        PublicKeyDataV2Dto decoded = assertInstanceOf(PublicKeyDataV2Dto.class, roundTrip);
        assertArrayEquals(spki, decoded.getPublicKeySpki());
        assertThrows(IllegalArgumentException.class, () -> publicKey.setPublicKeySpki(new byte[]{1, 2, 3}));

        SubjectPublicKeyInfo validSpki = SubjectPublicKeyInfo.getInstance(
                ASN1Primitive.fromByteArray(spki));
        SubjectPublicKeyInfo privateBytesDisguisedAsSpki = new SubjectPublicKeyInfo(
                validSpki.getAlgorithm(), keyPair.getPrivate().getEncoded());
        assertThrows(IllegalArgumentException.class,
                () -> publicKey.setPublicKeySpki(privateBytesDisguisedAsSpki.getEncoded()));
    }

    @Test
    void completedValidationRejectsSpkiAlgorithmMismatch() throws Exception {
        PublicKeyDataV2Dto publicKey = initialized(new PublicKeyDataV2Dto(), KeyAlgorithm.ECDSA);
        publicKey.setPublicKeySpki(KeyPairGenerator.getInstance("RSA").generateKeyPair().getPublic().getEncoded());

        assertThrows(IllegalArgumentException.class, publicKey::validate);
    }

    @Test
    void rejectsLegacyRawPrivateAndEncryptedKeyRepresentations() {
        for (String format : List.of("Raw", "PrivateKeyInfo", "EncryptedPrivateKeyInfo")) {
            String type = format.equals("Raw") ? "Secret" : "Private";
            String algorithm = format.equals("Raw") ? "AES" : "RSA";
            String json = """
                    {"type":"%s","algorithm":"%s","length":256,
                     "format":"%s","value":{"value":"c2VjcmV0"}}
                    """.formatted(type, algorithm, format);

            assertThrows(JsonProcessingException.class, () -> mapper.readValue(json, KeyDataV2Dto.class));
        }
    }

    @Test
    void rejectsSecretBearingAttributeShapeInDescriptorMetadata() {
        String json = """
                {"type":"Secret","algorithm":"AES","length":256,
                 "metadata":[{"name":"leak","secret":"c2VjcmV0LWtleQ=="}]}
                """;

        assertThrows(JsonProcessingException.class, () -> mapper.readValue(json, KeyDataV2Dto.class));
    }

    @Test
    void rejectsMissingUnknownAndMismatchedDiscriminators() {
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue("{\"algorithm\":\"RSA\",\"length\":2048}", KeyDataV2Dto.class));
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue("{\"type\":\"UnknownRole\",\"algorithm\":\"RSA\",\"length\":2048}",
                        KeyDataV2Dto.class));
        assertThrows(InvalidTypeIdException.class,
                () -> mapper.readValue("{\"type\":\"Split\",\"algorithm\":\"RSA\",\"length\":2048}",
                        KeyDataV2Dto.class));
        assertThrows(JsonProcessingException.class,
                () -> mapper.readValue("{\"type\":\"Public\",\"algorithm\":\"RSA\",\"length\":2048}",
                        SecretKeyDataV2Dto.class));
    }

    private KeyDataV2Dto read(String type, String algorithm) throws JsonProcessingException {
        return mapper.readValue("{\"type\":\"" + type + "\",\"algorithm\":\"" + algorithm
                + "\",\"length\":2048}", KeyDataV2Dto.class);
    }

    private static <T extends KeyDataV2Dto> T initialized(T keyData, KeyAlgorithm algorithm) {
        keyData.setAlgorithm(algorithm);
        keyData.setLength(2048);
        return keyData;
    }
}
