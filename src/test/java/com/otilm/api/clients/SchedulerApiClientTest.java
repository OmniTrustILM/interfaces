package com.otilm.api.clients;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.otilm.api.exception.ConnectionServiceException;
import com.otilm.api.model.scheduler.SchedulerJobDto;
import com.otilm.api.model.scheduler.SchedulerResponseDto;
import com.otilm.api.model.scheduler.SchedulerStatus;
import com.otilm.api.model.scheduler.SchedulerTriggerState;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

/**
 * The scheduler list as core reads it: the whole wire shape, the old shape, an answer with no body, and each way the
 * read is known to fail -- every one of them unchecked, since the caller degrades on any failure.
 */
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
    void setUp() throws ReflectiveOperationException {
        mockServer.resetAll();
        client = clientFor(mockServer.baseUrl());
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

    /** A base URL that carries a path, such as a scheduler behind a reverse proxy, keeps that path. */
    @Test
    void listScheduledJobs_keepsThePathOfTheBaseUrl() throws ReflectiveOperationException {
        mockServer.stubFor(get(urlPathEqualTo("/ctx" + LIST_PATH)).willReturn(okJson(LIST_JSON)));

        SchedulerResponseDto response = clientFor(mockServer.baseUrl() + "/ctx").listScheduledJobs();

        assertEquals("CryptoAssetPqcSweepTask", response.getSchedulerJobList().get(0).getJobName());
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

    @ParameterizedTest
    @ValueSource(ints = {200, 204})
    void listScheduledJobs_answersNullWhenTheSchedulerSendsNoBody(int status) {
        mockServer.stubFor(get(urlPathEqualTo(LIST_PATH)).willReturn(aResponse().withStatus(status)));

        assertNull(client.listScheduledJobs());
    }

    @Test
    void listScheduledJobs_givesUpAfterItsTimeout() {
        Duration timeout = Duration.ofMillis(200);
        mockServer.stubFor(get(urlPathEqualTo(LIST_PATH)).willReturn(aResponse().withFixedDelay(10_000)));
        long started = System.nanoTime();

        IllegalStateException thrown = assertThrows(IllegalStateException.class,
                () -> client.listScheduledJobs(timeout));

        long elapsedMillis = Duration.ofNanos(System.nanoTime() - started).toMillis();
        assertTrue(thrown.getMessage().startsWith("Timeout on blocking read"), thrown.getMessage());
        assertTrue(elapsedMillis >= timeout.toMillis(), "gave up after " + elapsedMillis + " ms, before the timeout");
        assertTrue(elapsedMillis < 5_000, "gave up after " + elapsedMillis + " ms, not at the timeout");
    }

    /**
     * An error status without a body carries no status to the caller: the error handler the client shares turns only a
     * body into an exception, so the exchange ends empty. Distinct from the timeout only by its message.
     */
    @ParameterizedTest
    @ValueSource(ints = {500, 503})
    void listScheduledJobs_failsUncheckedOnAnErrorStatusWithoutABody(int status) {
        mockServer.stubFor(get(urlPathEqualTo(LIST_PATH)).willReturn(aResponse().withStatus(status)));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> client.listScheduledJobs());

        assertTrue(thrown.getMessage().contains("without emitting a response"), thrown.getMessage());
    }

    @Test
    void listScheduledJobs_failsUncheckedOnABodyThatIsNotJson() {
        mockServer
                .stubFor(get(urlPathEqualTo(LIST_PATH))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/html")
                                .withBody("<html>proxy error</html>")));

        assertThrows(WebClientResponseException.class, () -> client.listScheduledJobs());
    }

    @Test
    void listScheduledJobs_failsUncheckedOnATriggerStateItDoesNotKnow() {
        mockServer
                .stubFor(get(urlPathEqualTo(LIST_PATH))
                        .willReturn(okJson(LIST_JSON.replace("\"NORMAL\"", "\"WAITING\""))));

        assertThrows(DecodingException.class, () -> client.listScheduledJobs());
    }

    @Test
    void listScheduledJobs_failsUncheckedWhenTheSchedulerCannotBeReached()
            throws IOException, ReflectiveOperationException {
        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }
        SchedulerApiClient unreachable = clientFor("http://localhost:" + closedPort);

        assertThrows(WebClientRequestException.class, () -> unreachable.listScheduledJobs());
    }

    /**
     * The client as Spring builds it: {@code scheduler.base-url} injected into the field the requests are built from.
     */
    private static SchedulerApiClient clientFor(String schedulerBaseUrl) throws ReflectiveOperationException {
        SchedulerApiClient schedulerApiClient = new SchedulerApiClient();
        Field baseUrl = SchedulerApiClient.class.getDeclaredField("schedulerBaseUrl");
        baseUrl.setAccessible(true);
        baseUrl.set(schedulerApiClient, schedulerBaseUrl);
        return schedulerApiClient;
    }
}
