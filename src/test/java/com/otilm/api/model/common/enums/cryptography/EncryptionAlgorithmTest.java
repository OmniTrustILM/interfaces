package com.otilm.api.model.common.enums.cryptography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otilm.api.exception.ValidationException;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;
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
        Stream<Arguments> profileNames = profiles().flatMap(profile -> {
            EncryptionAlgorithm algorithm = (EncryptionAlgorithm) profile.get()[0];
            String code = (String) profile.get()[1];
            return Stream
                    .of(code, code.replace("/ECB/", "/NONE/"), code.replace("SHA-", "SHA"),
                            code.replace("/ECB/", "/NONE/").replace("SHA-", "SHA").toLowerCase(Locale.ROOT))
                    .distinct()
                    .map(name -> arguments(named(name, name), algorithm));
        });
        Stream<Arguments> compatibilityNames = Stream
                .of("RSA", "rsa")
                .map(name -> arguments(named(name, name), EncryptionAlgorithm.RSA_PKCS1_V1_5));
        return Stream.concat(profileNames, compatibilityNames);
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
