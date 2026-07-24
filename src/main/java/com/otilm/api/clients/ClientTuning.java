package com.otilm.api.clients;

import java.time.Duration;

/**
 * Tuning for the shared connector {@link org.springframework.web.reactive.function.client.WebClient}.
 *
 * <p>Only the load-bearing knobs are exposed here; connection-pool hygiene (idle eviction,
 * lifetime, background disposal, LIFO leasing) is fixed inside {@link BaseApiClient} because it is
 * not deployment-specific. Deployment code supplies these values from configuration;
 * {@link #defaults()} backs tests and any caller that does not tune.
 *
 * <p>{@code maxConnections} is <em>per remote host</em> (Reactor-Netty pools per destination), not a
 * global cap, so it is sized against the per-connector concurrent load rather than a single queue's
 * listener concurrency.
 */
public record ClientTuning(
        Duration connectTimeout,
        Duration responseTimeout,
        int maxConnections,
        Duration pendingAcquireTimeout) {

    public ClientTuning {
        requirePositive(connectTimeout, "connectTimeout");
        requirePositive(responseTimeout, "responseTimeout");
        requirePositive(pendingAcquireTimeout, "pendingAcquireTimeout");
        if (maxConnections <= 0) {
            throw new IllegalArgumentException("maxConnections must be positive, was " + maxConnections);
        }
    }

    private static void requirePositive(Duration value, String name) {
        if (value == null || value.isNegative() || value.isZero()) {
            throw new IllegalArgumentException(name + " must be a positive duration, was " + value);
        }
    }

    public static ClientTuning defaults() {
        // 35s response ~ authority connector per-call budget (30s) + margin; 3s connect fails fast on unreachable hosts.
        return new ClientTuning(Duration.ofSeconds(3), Duration.ofSeconds(35), 20, Duration.ofSeconds(10));
    }
}
