package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class EncryptionAlgorithmTest {
    @ParameterizedTest
    @MethodSource("profiles")
    void algorithm_publishesTheContractCode_andReadsItCaseInsensitively(EncryptionAlgorithm algorithm,
            String expectedCode) throws Exception {
        // given
        ObjectMapper mapper = new ObjectMapper();
        String uppercaseCode = expectedCode.toUpperCase(Locale.ROOT);

        // when
        String encoded = mapper.writeValueAsString(algorithm);
        EncryptionAlgorithm decoded = mapper.readValue(encoded, EncryptionAlgorithm.class);

        // then
        assertEquals(expectedCode, algorithm.getCode());
        assertEquals(mapper.writeValueAsString(expectedCode), encoded);
        assertEquals(algorithm, decoded);
        assertEquals(algorithm, EncryptionAlgorithm.findByCode(uppercaseCode));
        assertTrue(algorithm.getLabel() != null && !algorithm.getLabel().isBlank());
        assertTrue(algorithm.getDescription() != null && !algorithm.getDescription().isBlank());
    }

    static Stream<Arguments> profiles() {
        return Stream
                .of(arguments(EncryptionAlgorithm.RSA_PKCS1_V1_5, "RSA/ECB/PKCS1Padding"),
                        arguments(EncryptionAlgorithm.RSA_OAEP_SHA1, "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"),
                        arguments(EncryptionAlgorithm.RSA_OAEP_SHA256, "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"),
                        arguments(EncryptionAlgorithm.RSA_OAEP_SHA384, "RSA/ECB/OAEPWithSHA-384AndMGF1Padding"),
                        arguments(EncryptionAlgorithm.RSA_OAEP_SHA512, "RSA/ECB/OAEPWithSHA-512AndMGF1Padding"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("jcaNames")
    void jcaName_resolvesProfilesAndCompatibilityAliases(String name, EncryptionAlgorithm expected) {
        // given
        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(name);

        // then
        assertEquals(expected, selected.orElseThrow());
    }

    static Stream<Arguments> jcaNames() {
        Stream<Arguments> profileNames = profiles()
                .filter(profile -> profile.get()[0] == EncryptionAlgorithm.RSA_PKCS1_V1_5)
                .flatMap(profile -> {
                    EncryptionAlgorithm algorithm = (EncryptionAlgorithm) profile.get()[0];
                    String code = (String) profile.get()[1];
                    return jcaAliases(code).map(name -> arguments(named(name, name), algorithm));
                });
        Stream<Arguments> compatibilityNames = Stream
                .of("RSA", "rsa")
                .map(name -> arguments(named(name, name), EncryptionAlgorithm.RSA_PKCS1_V1_5));
        return Stream.concat(profileNames, compatibilityNames);
    }

    @ParameterizedTest
    @MethodSource("oaepNames")
    void jcaName_returnsEmptyForOaepWithoutParameters(String name) {
        // given
        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(name);

        // then
        assertTrue(selected.isEmpty());
    }

    static Stream<String> oaepNames() {
        return profiles()
                .filter(profile -> profile.get()[0] != EncryptionAlgorithm.RSA_PKCS1_V1_5)
                .flatMap(profile -> jcaAliases((String) profile.get()[1]));
    }

    private static Stream<String> jcaAliases(String code) {
        return Stream
                .of(code, code.replace("/ECB/", "/NONE/"), code.replace("SHA-", "SHA"),
                        code.replace("/ECB/", "/NONE/").replace("SHA-", "SHA").toLowerCase(Locale.ROOT))
                .distinct();
    }

    @Test
    void jcaName_doesNotInferTheMgfDigestFromSunJceTransformation() throws Exception {
        // given
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        Cipher cipher = Cipher.getInstance(EncryptionAlgorithm.RSA_OAEP_SHA256.getCode(), "SunJCE");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        OAEPParameterSpec parameters = cipher.getParameters().getParameterSpec(OAEPParameterSpec.class);
        MGF1ParameterSpec mgf = (MGF1ParameterSpec) parameters.getMGFParameters();
        String defaultMgfDigest = "SHA-1";

        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(cipher.getAlgorithm(), parameters);

        // then
        assertEquals(defaultMgfDigest, mgf.getDigestAlgorithm());
        assertTrue(selected.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("oaepProfilesAndAliases")
    void jcaName_resolvesOaepWithMatchingDigestsAndAnEmptyLabel(String name, EncryptionAlgorithm expected,
            String digest) {
        // given
        OAEPParameterSpec parameters = oaepParameters(digest);

        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(name, parameters);

        // then
        assertEquals(expected, selected.orElseThrow());
    }

    static Stream<Arguments> oaepProfilesAndAliases() {
        return Stream
                .of(arguments(EncryptionAlgorithm.RSA_OAEP_SHA1, "SHA-1"),
                        arguments(EncryptionAlgorithm.RSA_OAEP_SHA256, "SHA-256"),
                        arguments(EncryptionAlgorithm.RSA_OAEP_SHA384, "SHA-384"),
                        arguments(EncryptionAlgorithm.RSA_OAEP_SHA512, "SHA-512"))
                .flatMap(profile -> {
                    EncryptionAlgorithm algorithm = (EncryptionAlgorithm) profile.get()[0];
                    String digest = (String) profile.get()[1];
                    return jcaAliases(algorithm.getCode()).map(name -> arguments(named(name, name), algorithm, digest));
                });
    }

    @Test
    void jcaName_resolvesTheExplicitParametersOfAnInitializedCipher() throws Exception {
        // given
        String digest = "SHA-256";
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        EncryptionAlgorithm expected = EncryptionAlgorithm.RSA_OAEP_SHA256;
        Cipher cipher = Cipher.getInstance(expected.getCode(), "SunJCE");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), oaepParameters(digest));
        OAEPParameterSpec parameters = cipher.getParameters().getParameterSpec(OAEPParameterSpec.class);

        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(cipher.getAlgorithm(), parameters);

        // then
        assertEquals(expected, selected.orElseThrow());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("unsupportedOaepParameters")
    void jcaName_returnsEmptyForParametersOutsideTheNamedProfile(OAEPParameterSpec parameters) {
        // given
        String name = EncryptionAlgorithm.RSA_OAEP_SHA256.getCode();

        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(name, parameters);

        // then
        assertTrue(selected.isEmpty());
    }

    static Stream<Named<OAEPParameterSpec>> unsupportedOaepParameters() {
        String digest = "SHA-256";
        String differentDigest = "SHA-384";
        String differentMgf = "MGF2";
        byte[] nonEmptyLabel = {1};
        PSource unsupportedLabelSource = new PSource("unsupported") {
        };
        return Stream
                .of(named("no parameters", null), named("different message digest", oaepParameters(differentDigest)),
                        named("different MGF digest",
                                new OAEPParameterSpec(digest, "MGF1", MGF1ParameterSpec.SHA1,
                                        PSource.PSpecified.DEFAULT)),
                        named("different MGF",
                                new OAEPParameterSpec(digest, differentMgf, MGF1ParameterSpec.SHA256,
                                        PSource.PSpecified.DEFAULT)),
                        named("missing MGF parameters",
                                new OAEPParameterSpec(digest, "MGF1", null, PSource.PSpecified.DEFAULT)),
                        named("wrong MGF parameter type",
                                new OAEPParameterSpec(digest, "MGF1", PSSParameterSpec.DEFAULT,
                                        PSource.PSpecified.DEFAULT)),
                        named("non-empty label",
                                new OAEPParameterSpec(digest, "MGF1", MGF1ParameterSpec.SHA256,
                                        new PSource.PSpecified(nonEmptyLabel))),
                        named("unsupported label source", new OAEPParameterSpec(digest, "MGF1",
                                MGF1ParameterSpec.SHA256, unsupportedLabelSource)));
    }

    @Test
    void jcaName_acceptsCaseInsensitiveUnhyphenatedParameterDigests() {
        // given
        String digestAlias = "sha256";
        EncryptionAlgorithm expected = EncryptionAlgorithm.RSA_OAEP_SHA256;
        OAEPParameterSpec parameters = new OAEPParameterSpec(digestAlias, "mgf1", new MGF1ParameterSpec(digestAlias),
                PSource.PSpecified.DEFAULT);

        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(expected.getCode(), parameters);

        // then
        assertEquals(expected, selected.orElseThrow());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"RSA", "RSA/ECB/PKCS1Padding", "AES/GCM/NoPadding", "RSA/ECB/OAEPPadding"})
    void jcaName_returnsEmptyForOaepParametersOnOtherOrUnspecifiedTransformations(String name) {
        // given
        String digest = "SHA-256";
        OAEPParameterSpec parameters = oaepParameters(digest);

        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(name, parameters);

        // then
        assertTrue(selected.isEmpty());
    }

    private static OAEPParameterSpec oaepParameters(String digest) {
        return new OAEPParameterSpec(digest, "MGF1", new MGF1ParameterSpec(digest), PSource.PSpecified.DEFAULT);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {
            "AES/GCM/NoPadding",
            "RSA/ECB/OAEPPadding",
            "RSA/ECB/NoPadding",
            "RSA/ECB/OAEPWithSHA-224AndMGF1Padding",
            " RSA "})
    void jcaName_returnsEmptyForUnsupportedOrUnspecifiedProfiles(String unsupportedName) {
        // given
        // when
        Optional<EncryptionAlgorithm> selected = EncryptionAlgorithm.lookupByJcaName(unsupportedName);

        // then
        assertTrue(selected.isEmpty());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {
            "AES/GCM/NoPadding",
            "RSA",
            "RSA/NONE/PKCS1Padding",
            "RSA/ECB/OAEPWithSHA256AndMGF1Padding",
            "RSA/ECB/OAEPPadding",
            "RSA/ECB/NoPadding"})
    void codeReader_rejectsUnknownCodesAndJcaAliasesWithASafeMessage(String unsupportedCode) {
        // given
        String expectedMessage = "Unknown encryption algorithm code.";

        // when
        Executable read = () -> EncryptionAlgorithm.findByCode(unsupportedCode);

        // then
        assertTrue(EncryptionAlgorithm.lookupByCode(unsupportedCode).isEmpty());
        assertEquals(expectedMessage, assertThrows(ValidationException.class, read).getMessage());
    }
}
