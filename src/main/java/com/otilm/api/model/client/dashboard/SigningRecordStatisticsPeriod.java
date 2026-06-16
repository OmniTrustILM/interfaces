package com.otilm.api.model.client.dashboard;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;
import java.util.Arrays;

/**
 * Time window for the Signing Record statistics endpoint. Only the volume-over-time series is
 * scoped to this window; the count badges and breakdown maps are window-independent.
 */
@Schema(enumAsRef = true)
public enum SigningRecordStatisticsPeriod implements IPlatformEnum {

    LAST_24H(Codes.LAST_24H, "Last 24 hours", "Signings over the last 24 hours, bucketed hourly", Duration.ofDays(1), Bucket.HOUR),
    LAST_7D(Codes.LAST_7D, "Last 7 days", "Signings over the last 7 days, bucketed daily", Duration.ofDays(7), Bucket.DAY),
    LAST_30D(Codes.LAST_30D, "Last 30 days", "Signings over the last 30 days, bucketed daily", Duration.ofDays(30), Bucket.DAY),
    LAST_90D(Codes.LAST_90D, "Last 90 days", "Signings over the last 90 days, bucketed daily", Duration.ofDays(90), Bucket.DAY),
    ;

    /** Bucketing granularity of the volume-over-time series for a period. */
    public enum Bucket {
        HOUR,
        DAY
    }

    private static final SigningRecordStatisticsPeriod[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final transient Duration window;
    private final transient Bucket bucket;

    SigningRecordStatisticsPeriod(String code, String label, String description, Duration window, Bucket bucket) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.window = window;
        this.bucket = bucket;
    }

    @JsonCreator
    public static SigningRecordStatisticsPeriod findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(k -> k.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown signing record statistics period {}", code)));
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public Duration getWindow() {
        return this.window;
    }

    public Bucket getBucket() {
        return this.bucket;
    }

    public static class Codes {
        public static final String LAST_24H = "24h";
        public static final String LAST_7D = "7d";
        public static final String LAST_30D = "30d";
        public static final String LAST_90D = "90d";

        private Codes() {
        }
    }
}
