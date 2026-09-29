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
     * is logged here. Every failure is unchecked, and a caller degrades on any {@code RuntimeException} rather than on
     * a list of types. The shapes known today:
     * <ul>
     * <li>{@code WebClientRequestException}: the scheduler cannot be reached.</li>
     * <li>{@code IllegalStateException} with a message starting "Timeout on blocking read": the timeout passed --
     * {@link #LIST_TIMEOUT} here, the caller's own in {@link #listScheduledJobs(Duration)}.</li>
     * <li>A reactor-wrapped {@code ConnectionServiceException} carrying the status: an error status with a body
     * ({@code reactor.core.Exceptions.unwrap} recovers it).</li>
     * <li>{@code IllegalStateException} with no status, the type the timeout has: an error status with an empty body,
     * which the error handler this client shares turns into no exception, so the exchange ends empty.</li>
     * <li>{@code WebClientResponseException}: a success status whose body is not JSON.</li>
     * <li>{@code DecodingException}: JSON that does not bind, such as a trigger state this artifact does not know.</li>
     * </ul>
     * A success status with an empty body, or 204 No Content, answers {@code null}.
     */
    public SchedulerResponseDto listScheduledJobs() {
        return listScheduledJobs(LIST_TIMEOUT);
    }

    /** {@link #listScheduledJobs()}, giving up once the caller's own {@code timeout} passes. */
    public SchedulerResponseDto listScheduledJobs(final Duration timeout) {
        return prepareRequest(HttpMethod.GET)
                .uri(schedulerBaseUrl + SCHEDULER_LIST)
                .retrieve()
                .bodyToMono(SchedulerResponseDto.class)
                .block(timeout);
    }

    @Override
    protected String getServiceUrl() {
        return schedulerBaseUrl;
    }
}
