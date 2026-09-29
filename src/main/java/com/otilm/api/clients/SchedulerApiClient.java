package com.otilm.api.clients;

import com.otilm.api.exception.SchedulerException;
import com.otilm.api.model.scheduler.SchedulerRequestDto;
import com.otilm.api.model.scheduler.SchedulerResponseDto;
import java.time.Duration;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@NoArgsConstructor
public class SchedulerApiClient extends PlatformBaseApiClient {

    @Value("${scheduler.base-url}")
    private String schedulerBaseUrl;

    private static final String SCHEDULER_CREATE = "/v1/scheduler/create";

    private static final String SCHEDULER_DELETE = "/v1/scheduler/{jobName}";

    private static final String SCHEDULER_ENABLE = "/v1/scheduler/{jobName}/enable";

    private static final String SCHEDULER_DISABLE = "/v1/scheduler/{jobName}/disable";

    private static final String SCHEDULER_UPDATE = "/v1/scheduler/update";

    private static final String SCHEDULER_LIST = "/v1/scheduler/list";

    /**
     * How long {@link #listScheduledJobs()} waits for the scheduler. The read decorates a listing rather than being
     * what the listing depends on, so a scheduler that accepts the connection and never answers must not hold the
     * listing open.
     */
    public static final Duration LIST_TIMEOUT = Duration.ofSeconds(5);

    public void schedulerCreate(final SchedulerRequestDto schedulerDto) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST);
        processRequest(r -> r
                .uri(schedulerBaseUrl + SCHEDULER_CREATE)
                .body(Mono.just(schedulerDto), SchedulerRequestDto.class)
                .retrieve()
                .toEntity(SchedulerResponseDto.class)
                .block()
                .getBody(), request);
    }

    public void deleteScheduledJob(final String jobName) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE);
        processRequest(r -> r
                .uri(schedulerBaseUrl + SCHEDULER_DELETE, jobName)
                .retrieve()
                .toEntity(Void.class)
                .block()
                .getBody(), request);
    }

    public void enableScheduledJob(final String jobName) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET);
        processRequest(r -> r
                .uri(schedulerBaseUrl + SCHEDULER_ENABLE, jobName)
                .retrieve()
                .toEntity(Void.class)
                .block()
                .getBody(), request);
    }

    public void disableScheduledJob(final String jobName) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET);
        processRequest(r -> r
                .uri(schedulerBaseUrl + SCHEDULER_DISABLE, jobName)
                .retrieve()
                .toEntity(Void.class)
                .block()
                .getBody(), request);
    }

    public void updateScheduledJob(SchedulerRequestDto schedulerRequestDto) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET);
        processRequest(r -> r
                .uri(schedulerBaseUrl + SCHEDULER_UPDATE)
                .body(Mono.just(schedulerRequestDto), SchedulerRequestDto.class)
                .retrieve()
                .toEntity(SchedulerResponseDto.class)
                .block()
                .getBody(), request);
    }

    /**
     * Every job the scheduler holds, with the trigger facts it observes for each.
     *
     * <p>
     * Not wrapped in {@link #processRequest}: this read fails soft in its caller, which decides how loudly, so nothing
     * is logged here. Failures are unchecked -- {@code WebClientRequestException} when the scheduler cannot be reached,
     * {@code IllegalStateException} once {@link #LIST_TIMEOUT} passes, and a reactor-wrapped
     * {@code ConnectionServiceException} on an error status ({@code reactor.core.Exceptions.unwrap} recovers it). An
     * empty body answers {@code null}.
     */
    public SchedulerResponseDto listScheduledJobs() {
        return listScheduledJobs(LIST_TIMEOUT);
    }

    /** {@link #listScheduledJobs()} with the caller's own budget. */
    public SchedulerResponseDto listScheduledJobs(final Duration timeout) {
        // Relative to the client's base URL, which getServiceUrl() supplies; the absolute form the other methods
        // build resolves to the same address.
        return prepareRequest(HttpMethod.GET)
                .uri(SCHEDULER_LIST)
                .retrieve()
                .bodyToMono(SchedulerResponseDto.class)
                .block(timeout);
    }

    @Override
    protected String getServiceUrl() {
        return schedulerBaseUrl;
    }
}
