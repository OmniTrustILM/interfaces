package com.otilm.api.model.client.signing.timequality.validation;

import java.time.Duration;

public interface NtpCheckIntervalConfiguration {
    Duration getNtpCheckTimeout();
    Duration getNtpCheckInterval();
}
