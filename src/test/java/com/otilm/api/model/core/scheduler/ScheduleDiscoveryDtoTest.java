package com.otilm.api.model.core.scheduler;

import com.otilm.api.model.client.discovery.DiscoveryDto;
import com.otilm.api.model.client.discovery.DiscoveryWindowDto;
import com.otilm.api.model.client.discovery.DiscoveryWindowMode;
import com.otilm.api.testsupport.ValidatorFixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.Duration;
import java.util.Set;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleDiscoveryDtoTest {

    @AutoClose
    private static final ValidatorFixture VALIDATORS = new ValidatorFixture();
    private static final Validator VALIDATOR = VALIDATORS.validator();

    @Test
    void validatesTheDiscoveryWindowOfTheScheduledRequest() {
        Set<ConstraintViolation<ScheduleDiscoveryDto>> violations = VALIDATOR.validate(scheduled(lookbackWindow(null)));

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().startsWith("request.window")),
                violations.toString());
    }

    @Test
    void acceptsAScheduledRequestWithAValidWindow() {
        assertTrue(VALIDATOR.validate(scheduled(lookbackWindow(Duration.ofDays(1)))).isEmpty());
    }

    private static ScheduleDiscoveryDto scheduled(DiscoveryWindowDto window) {
        DiscoveryDto request = new DiscoveryDto();
        request.setName("nightly-scan");
        request.setConnectorUuid("c2f685d4-6a3e-11ec-90d6-0242ac120003");
        request.setKind("IP-HostName");
        request.setWindow(window);
        ScheduleDiscoveryDto dto = new ScheduleDiscoveryDto();
        dto.setJobName("nightly");
        dto.setCronExpression("0 0 * * * ?");
        dto.setRequest(request);
        return dto;
    }

    private static DiscoveryWindowDto lookbackWindow(Duration lookback) {
        DiscoveryWindowDto window = new DiscoveryWindowDto();
        window.setMode(DiscoveryWindowMode.LOOKBACK);
        window.setLookback(lookback);
        return window;
    }
}
