package com.otilm.api.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.otilm.api.exception.ConnectionServiceException;
import com.otilm.api.model.scheduler.SchedulerJobDto;
import com.otilm.api.model.scheduler.SchedulerResponseDto;
import com.otilm.api.model.scheduler.SchedulerStatus;
import com.otilm.api.model.scheduler.SchedulerTriggerState;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.core.Exceptions;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The scheduler list as core reads it: the whole wire shape, the old shape, and the two ways the read can fail. */
class SchedulerApiClientTest {

    private static final String LIST_PATH = "/v1/scheduler/list";

    private static final String LIST_JSON = """
            {
              "schedulerStatus": "OK",
              "schedulerName": "ilm-scheduler",
              "schedulerJobList": [{
                "uuidJob": null,
                "jobName": "CryptoAssetPqcSweepTask",
                "cronExpression": "0 30 * ? * *",
                "classNameToBeExecuted": "com.otilm.core.tasks.CryptoAssetPqcSweepTask",
                "nextFireTime": "2026-09-29T12:30:00Z",
                "previousFireTime": "2026-09-29T11:30:00Z",
                "triggerState": "NORMAL"
              }]
            }
            """;

    /** What a scheduler that predates the trigger facts answers. */
    private static final String LEGACY_LIST_JSON = """
            {
              "schedulerStatus": "OK",
              "schedulerJobList": [{
                "jobName": "CryptoAssetPqcSweepTask",
                "cronExpression": "0 30 * ? * *",
                "classNameToBeExecuted": "com.otilm.core.tasks.CryptoAssetPqcSweepTask"
              }]
            }
            """;

    private static WireMockServer mockServer;

    private SchedulerApiClient client;

    @BeforeAll
    static void startServer() {
        mockServer = new WireMockServer(options().dynamicPort());
        mockServer.start();
    }

    @AfterAll
    static void stopServer() {
        mockServer.stop();
    }

    @BeforeEach
    void setUp() {
        mockServer.resetAll();
        client = new SchedulerApiClient() {
            @Override
            protected String getServiceUrl() {
                return mockServer.baseUrl();
            }
        };
    }

    @Test
    void listScheduledJobs_readsEachJobsTriggerFacts() {
        mockServer.stubFor(get(urlPathEqualTo(LIST_PATH)).willReturn(okJson(LIST_JSON)));

        SchedulerResponseDto response = client.listScheduledJobs();

        assertEquals(SchedulerStatus.OK, response.getSchedulerStatus());
        assertEquals(1, response.getSchedulerJobList().size());
        SchedulerJobDto job = response.getSchedulerJobList().get(0);
        assertEquals("CryptoAssetPqcSweepTask", job.getJobName());
        assertEquals("0 30 * ? * *", job.getCronExpression());
        assertEquals(SchedulerTriggerState.NORMAL, job.getTriggerState());
        assertEquals(Instant.parse("2026-09-29T12:30:00Z"), job.getNextFireTime());
        assertEquals(Instant.parse("2026-09-29T11:30:00Z"), job.getPreviousFireTime());
    }

    @Test
    void listScheduledJobs_leavesTriggerFactsAbsentWhenTheSchedulerDoesNotSendThem() {
        mockServer.stubFor(get(urlPathEqualTo(LIST_PATH)).willReturn(okJson(LEGACY_LIST_JSON)));

        SchedulerJobDto job = client.listScheduledJobs().getSchedulerJobList().get(0);

        assertEquals("CryptoAssetPqcSweepTask", job.getJobName());
        assertNull(job.getTriggerState());
        assertNull(job.getNextFireTime());
        assertNull(job.getPreviousFireTime());
    }

    @Test
    void listScheduledJobs_failsUncheckedOnAServerError() {
        mockServer.stubFor(get(urlPathEqualTo(LIST_PATH)).willReturn(serverError().withBody("Internal server error.")));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> client.listScheduledJobs());

        ConnectionServiceException cause = assertInstanceOf(ConnectionServiceException.class,
                Exceptions.unwrap(thrown));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, cause.getHttpStatus());
    }

    @Test
    void listScheduledJobs_givesUpAfterItsTimeout() {
        mockServer.stubFor(get(urlPathEqualTo(LIST_PATH)).willReturn(aResponse().withFixedDelay(2_000)));
        long started = System.nanoTime();

        assertThrows(IllegalStateException.class, () -> client.listScheduledJobs(Duration.ofMillis(200)));

        long elapsedMillis = Duration.ofNanos(System.nanoTime() - started).toMillis();
        assertTrue(elapsedMillis < 1_500, "gave up after " + elapsedMillis + " ms, not at the timeout");
    }
}
