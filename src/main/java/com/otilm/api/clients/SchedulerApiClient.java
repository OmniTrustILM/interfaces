package com.otilm.api.clients;

import com.otilm.api.exception.SchedulerException;
import com.otilm.api.model.scheduler.SchedulerRequestDto;
import com.otilm.api.model.scheduler.SchedulerResponseDto;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@NoArgsConstructor
public class SchedulerApiClient extends CzertainlyBaseApiClient {

    @Value("${scheduler.base-url}")
    private String schedulerBaseUrl;

    private static final String SCHEDULER_CREATE = "/v1/scheduler/create";

    private static final String SCHEDULER_DELETE = "/v1/scheduler/{jobName}";

    private static final String SCHEDULER_ENABLE = "/v1/scheduler/{jobName}/enable";

    private static final String SCHEDULER_DISABLE = "/v1/scheduler/{jobName}/disable";

    private static final String SCHEDULER_UPDATE = "/v1/scheduler/update";

    public void schedulerCreate(final SchedulerRequestDto schedulerDto) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.POST);
        processRequest(r -> r
                        .uri(schedulerBaseUrl + SCHEDULER_CREATE)
                        .body(Mono.just(schedulerDto), SchedulerRequestDto.class)
                        .retrieve()
                        .toEntity(SchedulerResponseDto.class)
                        .block().getBody(),
                request);
    }

    public void deleteScheduledJob(final String jobName) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.DELETE);
        processRequest(r -> r
                        .uri(schedulerBaseUrl + SCHEDULER_DELETE, jobName)
                        .retrieve()
                        .toEntity(Void.class)
                        .block().getBody(),
                request);
    }

    public void enableScheduledJob(final String jobName) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET);
        processRequest(r -> r
                        .uri(schedulerBaseUrl + SCHEDULER_ENABLE, jobName)
                        .retrieve()
                        .toEntity(Void.class)
                        .block().getBody(),
                request);
    }

    public void disableScheduledJob(final String jobName) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET);
        processRequest(r -> r
                        .uri(schedulerBaseUrl + SCHEDULER_DISABLE, jobName)
                        .retrieve()
                        .toEntity(Void.class)
                        .block().getBody(),
                request);
    }

    public void updateScheduledJob(SchedulerRequestDto schedulerRequestDto) throws SchedulerException {
        final WebClient.RequestBodyUriSpec request = prepareRequest(HttpMethod.GET);
        processRequest(r -> r
                        .uri(schedulerBaseUrl + SCHEDULER_UPDATE)
                        .body(Mono.just(schedulerRequestDto), SchedulerRequestDto.class)
                        .retrieve()
                        .toEntity(SchedulerResponseDto.class)
                        .block().getBody(),
                request);
    }

    @Override
    protected String getServiceUrl() {
        return schedulerBaseUrl;
    }
}
