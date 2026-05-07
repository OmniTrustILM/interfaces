package com.czertainly.api.model.client.signing.timequality.validation;

import java.time.Duration;

public interface NtpCheckIntervalConfiguration {
    Duration getNtpCheckTimeout();
    Duration getNtpCheckInterval();
}
