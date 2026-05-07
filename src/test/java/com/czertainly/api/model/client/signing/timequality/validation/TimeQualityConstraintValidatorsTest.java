package com.czertainly.api.model.client.signing.timequality.validation;

import com.czertainly.api.model.client.signing.timequality.TimeQualityConfigurationRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TimeQualityConstraintValidatorsTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setup() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void teardown() {
        factory.close();
    }

    private TimeQualityConfigurationRequestDto validRequest() {
        TimeQualityConfigurationRequestDto dto = new TimeQualityConfigurationRequestDto();
        dto.setName("test-config");
        dto.setAccuracy(Duration.ofSeconds(1));
        dto.setNtpServers(List.of("pool.ntp.org"));
        dto.setNtpCheckInterval(Duration.ofSeconds(30));
        dto.setNtpCheckTimeout(Duration.ofSeconds(5));
        dto.setNtpServersMinReachable(1);
        dto.setMaxClockDrift(Duration.ofMillis(500));
        return dto;
    }

    private boolean hasViolationOn(Set<ConstraintViolation<TimeQualityConfigurationRequestDto>> violations, String path) {
        return violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals(path));
    }

    @Test
    void validRequestPassesAllConstraints() {
        assertTrue(validator.validate(validRequest()).isEmpty());
    }

    // --- MaxClockDriftValidator ---

    static Stream<Duration> invalidMaxClockDriftValues() {
        return Stream.of(
                Duration.ofSeconds(1),  // equal to accuracy
                Duration.ofSeconds(2)   // greater than accuracy
        );
    }

    @ParameterizedTest
    @MethodSource("invalidMaxClockDriftValues")
    void maxClockDriftAtOrAboveAccuracyIsInvalid(Duration maxClockDrift) {
        TimeQualityConfigurationRequestDto dto = validRequest();
        dto.setMaxClockDrift(maxClockDrift);
        assertTrue(hasViolationOn(validator.validate(dto), "maxClockDrift"));
    }

    // --- NtpCheckTimeoutValidator ---

    static Stream<Duration> invalidNtpCheckTimeoutValues() {
        return Stream.of(
                Duration.ofSeconds(30), // equal to interval
                Duration.ofSeconds(60)  // greater than interval
        );
    }

    @ParameterizedTest
    @MethodSource("invalidNtpCheckTimeoutValues")
    void ntpCheckTimeoutAtOrAboveIntervalIsInvalid(Duration ntpCheckTimeout) {
        TimeQualityConfigurationRequestDto dto = validRequest();
        dto.setNtpCheckTimeout(ntpCheckTimeout);
        assertTrue(hasViolationOn(validator.validate(dto), "ntpCheckTimeout"));
    }

    // --- NtpMinReachableValidator ---

    static Stream<Arguments> ntpServersMinReachableCases() {
        return Stream.of(
                Arguments.of(1, false), // equal to server count -> valid
                Arguments.of(2, true)   // exceeds server count -> invalid
        );
    }

    @ParameterizedTest
    @MethodSource("ntpServersMinReachableCases")
    void ntpServersMinReachableValidation(int minReachable, boolean expectViolation) {
        TimeQualityConfigurationRequestDto dto = validRequest();
        dto.setNtpServersMinReachable(minReachable);
        assertEquals(expectViolation, hasViolationOn(validator.validate(dto), "ntpServersMinReachable"));
    }

    @Test
    void nullNtpServersSkipsMinReachableCheck() {
        TimeQualityConfigurationRequestDto dto = validRequest();
        dto.setNtpServers(null);
        assertFalse(hasViolationOn(validator.validate(dto), "ntpServersMinReachable"));
    }
}
