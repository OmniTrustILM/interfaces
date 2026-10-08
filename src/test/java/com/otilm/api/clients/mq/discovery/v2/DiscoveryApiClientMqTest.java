package com.otilm.api.clients.mq.discovery.v2;

import com.otilm.api.clients.ApiClientConnectorInfo;
import com.otilm.api.clients.discovery.v2.DiscoveryPaths;
import com.otilm.api.clients.mq.ProxyClient;
import com.otilm.api.exception.ConnectorClientException;
import com.otilm.api.exception.ConnectorEntityNotFoundException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorProblemException;
import com.otilm.api.interfaces.client.v2.DiscoverySyncApiClient;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.v3.InfoAttributeV3;
import com.otilm.api.model.common.error.ErrorCode;
import com.otilm.api.model.common.error.ProblemDetailExtended;
import com.otilm.api.model.connector.discovery.v2.DiscoveryDrainRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryInitiateResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryResultsResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryRunRequestDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStatusResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoveryStopResponseDto;
import com.otilm.api.model.connector.discovery.v2.DiscoverySupportedResourceDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.connector.ConnectorDto;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Delegation tests for the MQ Discovery v2 client: each method must reach {@link ProxyClient} with the right connector,
 * path, method, body and response type, with the right per-operation {@code Duration}, and must return what the
 * contract promises the caller.
 *
 * <p>
 * The proxy is a hand-written recording fake because no mocking framework is on this project's test classpath, matching
 * {@code com.otilm.api.clients.mq.v2.AttributesApiClientMqTest}. It throws {@link AssertionError} from every
 * {@code ProxyClient} overload lacking a {@code Duration}, which makes the explicit-timeout rule structural — a future
 * call to one fails at the call site, unasserted.
 *
 * <p>
 * The three timeouts (11s/22s/33s) differ from each other and from {@link DiscoveryMqTimeouts#defaults()}, so a
 * mis-mapped component fails rather than passing by coincidence.
 *
 * <p>
 * This class asserts against {@link DiscoveryPaths} constants while the REST suite hardcodes the same routes as
 * literals. Keep it that way: the literals are the independent pin on the constants, and converting them would leave
 * nothing checking the constants at all.
 */
class DiscoveryApiClientMqTest {

    private static final Duration STATUS_TIMEOUT = Duration.ofSeconds(11);
    private static final Duration DRAIN_TIMEOUT = Duration.ofSeconds(22);
    private static final Duration CONTROL_TIMEOUT = Duration.ofSeconds(33);

    private RecordingProxyClient proxyClient;
    private ConnectorDto connector;
    private DiscoveryApiClient client;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        connector = new ConnectorDto();
        connector.setUrl("http://localhost");
        client = new DiscoveryApiClient(proxyClient, timeouts());
    }

    private static DiscoveryMqTimeouts timeouts() {
        return new DiscoveryMqTimeouts(STATUS_TIMEOUT, DRAIN_TIMEOUT, CONTROL_TIMEOUT);
    }

    // ---- Metadata reads (GET, control timeout) ----

    @Test
    void listSupportedResources_delegatesGetWithControlTimeout() throws ConnectorException {
        DiscoverySupportedResourceDto item = new DiscoverySupportedResourceDto();
        proxyClient.syncResponse = new DiscoverySupportedResourceDto[]{item};

        List<DiscoverySupportedResourceDto> result = client.listSupportedResources(connector);

        Assertions.assertEquals(List.of(item), result);
        proxyClient
                .assertCalled(connector, DiscoveryPaths.RESOURCES, "GET", null, DiscoverySupportedResourceDto[].class,
                        CONTROL_TIMEOUT);
    }

    @Test
    void listRunAttributes_delegatesGetWithControlTimeout() throws ConnectorException {
        BaseAttribute attribute = new InfoAttributeV3();
        proxyClient.syncResponse = new BaseAttribute[]{attribute};

        List<BaseAttribute> result = client.listRunAttributes(connector);

        Assertions.assertEquals(List.of(attribute), result);
        proxyClient
                .assertCalled(connector, DiscoveryPaths.ATTRIBUTES, "GET", null, BaseAttribute[].class,
                        CONTROL_TIMEOUT);
    }

    @Test
    void listResourceAttributes_bindsResourceWireCodeIntoPath() throws ConnectorException {
        BaseAttribute attribute = new InfoAttributeV3();
        proxyClient.syncResponse = new BaseAttribute[]{attribute};

        List<BaseAttribute> result = client.listResourceAttributes(connector, Resource.CERTIFICATE);

        Assertions.assertEquals(List.of(attribute), result);
        // The wire code, never the Java enum name: "certificates", not "CERTIFICATE".
        proxyClient
                .assertCalled(connector, DiscoveryPaths.BASE + "/certificates/attributes", "GET", null,
                        BaseAttribute[].class, CONTROL_TIMEOUT);
    }

    @Test
    void listResourceAttributes_bindsKeyResourceWireCode() throws ConnectorException {
        proxyClient.syncResponse = new BaseAttribute[0];

        client.listResourceAttributes(connector, Resource.CRYPTOGRAPHIC_KEY);

        proxyClient
                .assertCalled(connector, DiscoveryPaths.BASE + "/keys/attributes", "GET", null, BaseAttribute[].class,
                        CONTROL_TIMEOUT);
    }

    // ---- Lifecycle (POST) ----

    @Test
    void initiate_delegatesPostWithControlTimeout() throws ConnectorException {
        DiscoveryInitiateRequestDto request = new DiscoveryInitiateRequestDto();
        DiscoveryInitiateResponseDto expected = new DiscoveryInitiateResponseDto();
        proxyClient.syncResponse = expected;

        DiscoveryInitiateResponseDto result = client.initiate(connector, request);

        Assertions.assertSame(expected, result);
        proxyClient
                .assertCalled(connector, DiscoveryPaths.INITIATE, "POST", request, DiscoveryInitiateResponseDto.class,
                        CONTROL_TIMEOUT);
    }

    @Test
    void status_delegatesPostWithStatusTimeout() throws ConnectorException {
        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        DiscoveryStatusResponseDto expected = new DiscoveryStatusResponseDto();
        proxyClient.syncResponse = expected;

        DiscoveryStatusResponseDto result = client.status(connector, request);

        Assertions.assertSame(expected, result);
        proxyClient
                .assertCalled(connector, DiscoveryPaths.STATUS, "POST", request, DiscoveryStatusResponseDto.class,
                        STATUS_TIMEOUT);
    }

    @Test
    void results_delegatesPostWithDrainTimeout() throws ConnectorException {
        DiscoveryDrainRequestDto request = new DiscoveryDrainRequestDto();
        DiscoveryResultsResponseDto expected = new DiscoveryResultsResponseDto();
        proxyClient.syncResponse = expected;

        DiscoveryResultsResponseDto result = client.results(connector, request);

        Assertions.assertSame(expected, result);
        // The drain is the one operation allowed a longer budget than the rest.
        proxyClient
                .assertCalled(connector, DiscoveryPaths.RESULTS, "POST", request, DiscoveryResultsResponseDto.class,
                        DRAIN_TIMEOUT);
    }

    @Test
    void stop_delegatesPostWithControlTimeout() throws ConnectorException {
        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        DiscoveryStopResponseDto expected = new DiscoveryStopResponseDto();
        proxyClient.syncResponse = expected;

        DiscoveryStopResponseDto result = client.stop(connector, request);

        Assertions.assertSame(expected, result);
        proxyClient
                .assertCalled(connector, DiscoveryPaths.STOP, "POST", request, DiscoveryStopResponseDto.class,
                        CONTROL_TIMEOUT);
    }

    @Test
    void resume_delegatesPostWithControlTimeout() throws ConnectorException {
        DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
        DiscoveryInitiateResponseDto expected = new DiscoveryInitiateResponseDto();
        proxyClient.syncResponse = expected;

        DiscoveryInitiateResponseDto result = client.resume(connector, request);

        Assertions.assertSame(expected, result);
        proxyClient
                .assertCalled(connector, DiscoveryPaths.RESUME, "POST", request, DiscoveryInitiateResponseDto.class,
                        CONTROL_TIMEOUT);
    }

    // ---- cancel: the one operation whose status is part of its contract ----

    /**
     * 204 is cancel's only contract success status, so whatever successful status the proxy reports — including the
     * {@code 200} the interface default produces — must reach the caller as 204.
     */
    @Test
    void cancel_normalizesEverySuccessfulProxyStatusToNoContent() throws ConnectorException {
        for (HttpStatus proxyStatus : List.of(HttpStatus.NO_CONTENT, HttpStatus.OK, HttpStatus.ACCEPTED)) {
            DiscoveryRunRequestDto request = new DiscoveryRunRequestDto();
            proxyClient.syncResponse = ResponseEntity.status(proxyStatus).<Void>build();

            ResponseEntity<Void> result = client.cancel(connector, request);

            Assertions
                    .assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode(),
                            "cancel must answer 204 regardless of the successful status the proxy reported ("
                                    + proxyStatus + ")");
            Assertions.assertNull(result.getBody());
            proxyClient.assertCalled(connector, DiscoveryPaths.CANCEL, "POST", request, Void.class, CONTROL_TIMEOUT);
            Assertions
                    .assertTrue(proxyClient.viaSendRequestForEntity,
                            "cancel must use sendRequestForEntity - its ResponseEntity<Void> return exists to carry the upstream status");
        }
    }

    /**
     * The composed case, with no override in the way: a proxy that leaves the {@code Duration} overload of
     * {@code sendRequestForEntity} to the interface default still yields 204, because the client normalizes what the
     * default wrapped in {@code ResponseEntity.ok}.
     */
    @Test
    void cancel_yieldsNoContentEvenWhenTheProxyLeavesTheOverloadToTheInterfaceDefault() throws ConnectorException {
        TimeoutCapturingProxyClient proxy = new TimeoutCapturingProxyClient(null);

        ResponseEntity<Void> result = new DiscoveryApiClient(proxy, timeouts())
                .cancel(connector, new DiscoveryRunRequestDto());

        Assertions.assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        Assertions.assertEquals(CONTROL_TIMEOUT, proxy.seenTimeout);
    }

    /**
     * The 404 the proxy classifies into {@link ConnectorEntityNotFoundException} is the run being untracked — already
     * the terminal state cancel asked for. REST reports it as a 404 response, so MQ must too, or the identical call
     * succeeds on one transport and hard-fails on the other.
     */
    @Test
    void cancel_reportsANotTrackedRunAsNotFoundInsteadOfThrowing() throws ConnectorException {
        proxyClient.failure = new ConnectorEntityNotFoundException("run 33333333 is not tracked");

        ResponseEntity<Void> result = client.cancel(connector, new DiscoveryRunRequestDto());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    /**
     * The same case in its problem+json shape, for when the proxy learns to relay a connector's
     * {@code application/problem+json} body: classified by error code, not by transport status.
     */
    @Test
    void cancel_reportsANotTrackedProblemAsNotFoundInsteadOfThrowing() throws ConnectorException {
        proxyClient.failure = problem(ErrorCode.OPERATION_NOT_TRACKED);

        ResponseEntity<Void> result = client.cancel(connector, new DiscoveryRunRequestDto());

        Assertions.assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    /**
     * The not-tracked catch must stay narrow: the 422 past-the-point-of-no-return refusal is a real failure and may not
     * be laundered into a 404 response.
     */
    @Test
    void cancel_rethrowsAProblemThatIsNotAboutAnUntrackedRun() {
        ConnectorProblemException refusal = problem(ErrorCode.OPERATION_PAST_POINT_OF_NO_RETURN);
        proxyClient.failure = refusal;

        ConnectorProblemException thrown = Assertions
                .assertThrows(ConnectorProblemException.class,
                        () -> client.cancel(connector, new DiscoveryRunRequestDto()));

        Assertions.assertSame(refusal, thrown, "a refusal must reach the caller unchanged");
    }

    private static ConnectorProblemException problem(ErrorCode code) {
        return new ConnectorProblemException(
                ProblemDetailExtended.fromErrorCode(code, "simulated " + code, null, "test-corr-mq-cancel"));
    }

    /**
     * A not-tracked error code is only ever legitimate on a 404. {@code REGISTRATION_NOT_FOUND} is authority's flavour
     * of not-tracked and is declared 422, and cancel's own 422 means the run is past the point of no return — so
     * honouring the code without checking the status would answer a refused cancel, or a non-conformant authority code
     * on a discovery route, as a successful cancellation.
     */
    @Test
    void cancel_notTrackedCodeOnANon404_isNotSwallowedAsSuccess() {
        ProblemDetailExtended problem = new ProblemDetailExtended();
        problem.setStatus(HttpStatus.UNPROCESSABLE_CONTENT.value());
        problem.setErrorCode(ErrorCode.REGISTRATION_NOT_FOUND);
        proxyClient.failure = new ConnectorProblemException(problem);

        ConnectorException thrown = Assertions
                .assertThrows(ConnectorException.class, () -> client.cancel(connector, new DiscoveryRunRequestDto()));

        Assertions
                .assertSame(proxyClient.failure, thrown, "a 422 must reach the caller, whatever error code it carries");
    }

    /**
     * {@link DiscoverySyncApiClient} allows exactly two returned outcomes for cancel, 204 and 404, and requires every
     * other failure to be thrown. A {@code ProxyClient} that preserves the upstream status hands back an entity rather
     * than throwing, so returning whatever arrived would break that rule from inside the client — cancel's own 422 most
     * of all, since it means the run is past the point of no return and must never read as a completed cancellation.
     */
    @Test
    void cancel_relayedNon404IsThrownRatherThanReturned() {
        proxyClient.syncResponse = ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).build();

        ConnectorClientException ex = Assertions
                .assertThrows(ConnectorClientException.class,
                        () -> client.cancel(connector, new DiscoveryRunRequestDto()));

        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_CONTENT, ex.getHttpStatus());
        Assertions.assertEquals(connector, ex.getConnector());
    }

    @Test
    void cancel_relayed404IsStillReturnedAsTheContractsTerminalOutcome() {
        proxyClient.syncResponse = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        ResponseEntity<Void> response = Assertions
                .assertDoesNotThrow(() -> client.cancel(connector, new DiscoveryRunRequestDto()));

        Assertions
                .assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                        "a relayed 404 is the one non-2xx the contract lets the caller read");
    }

    /**
     * {@code ConnectorProblemException#getHttpStatus()} runs {@code HttpStatus.valueOf} on the problem document's
     * status, which throws for a valid code with no enum constant such as 499. Asking it while deciding whether a
     * failure is not-tracked would therefore replace the connector's own problem exception with an
     * {@code IllegalArgumentException}, so the status is compared as an int.
     */
    @Test
    void cancel_problemOnANonEnumStatusPropagatesUnchanged() {
        ProblemDetailExtended problem = new ProblemDetailExtended();
        problem.setStatus(499);
        problem.setErrorCode(ErrorCode.UPSTREAM_ERROR);
        proxyClient.failure = new ConnectorProblemException(problem);

        ConnectorException thrown = Assertions
                .assertThrows(ConnectorException.class, () -> client.cancel(connector, new DiscoveryRunRequestDto()));

        Assertions
                .assertSame(proxyClient.failure, thrown,
                        "the connector's problem must survive a status outside Spring's HttpStatus enum");
    }

    // ---- Error propagation: this client adds no mapping of its own ----

    @Test
    void connectorExceptionPropagatesUnchangedFromEveryOperation() {
        ConnectorException failure = new ConnectorException("simulated connector failure");
        proxyClient.failure = failure;

        assertPropagates(failure, () -> client.listSupportedResources(connector));
        assertPropagates(failure, () -> client.listRunAttributes(connector));
        assertPropagates(failure, () -> client.listResourceAttributes(connector, Resource.CERTIFICATE));
        assertPropagates(failure, () -> client.initiate(connector, new DiscoveryInitiateRequestDto()));
        assertPropagates(failure, () -> client.status(connector, new DiscoveryRunRequestDto()));
        assertPropagates(failure, () -> client.results(connector, new DiscoveryDrainRequestDto()));
        assertPropagates(failure, () -> client.stop(connector, new DiscoveryRunRequestDto()));
        assertPropagates(failure, () -> client.resume(connector, new DiscoveryRunRequestDto()));
        assertPropagates(failure, () -> client.cancel(connector, new DiscoveryRunRequestDto()));
    }

    private static void assertPropagates(ConnectorException expected, Executable call) {
        ConnectorException thrown = Assertions.assertThrows(ConnectorException.class, call);
        Assertions.assertSame(expected, thrown, "the MQ client must not wrap or re-map connector failures");
    }

    // ---- Bodiless responses: the proxy returns null when the relayed response had no body ----

    /**
     * A list route answering with no body at all is non-conformant, not empty — it must return a JSON array. Reading it
     * as empty would make a broken connector indistinguishable from one that genuinely reports nothing, which for
     * {@code listSupportedResources} is a distinction Core acts on. REST rejects it identically through
     * {@code BaseApiClient.requireBody}.
     */
    @Test
    void listOperationsFailNamingTheOperationOnABodilessResponse() {
        proxyClient.syncResponse = null;

        assertFailsNaming("listSupportedResources", () -> client.listSupportedResources(connector));
        assertFailsNaming("listRunAttributes", () -> client.listRunAttributes(connector));
        assertFailsNaming("listResourceAttributes",
                () -> client.listResourceAttributes(connector, Resource.CERTIFICATE));
    }

    @Test
    void listOperationsReturnAMutableListWhenTheProxyHasABody() throws ConnectorException {
        proxyClient.syncResponse = new BaseAttribute[]{new InfoAttributeV3()};

        List<BaseAttribute> result = client.listRunAttributes(connector);

        Assertions.assertEquals(1, result.size());
        Assertions
                .assertDoesNotThrow(() -> result.add(new InfoAttributeV3()),
                        "the returned list must be mutable on both transports, not an Arrays.asList view");
    }

    /**
     * The operations whose 2xx response must carry a payload fail loudly instead of handing {@code null} to Core, where
     * it would resurface as an NPE attributed to no connector.
     */
    @Test
    void singleValueOperationsFailNamingTheOperationOnABodilessResponse() {
        proxyClient.syncResponse = null;

        assertFailsNaming("initiate", () -> client.initiate(connector, new DiscoveryInitiateRequestDto()));
        assertFailsNaming("status", () -> client.status(connector, new DiscoveryRunRequestDto()));
        assertFailsNaming("results", () -> client.results(connector, new DiscoveryDrainRequestDto()));
        assertFailsNaming("stop", () -> client.stop(connector, new DiscoveryRunRequestDto()));
        assertFailsNaming("resume", () -> client.resume(connector, new DiscoveryRunRequestDto()));
        // cancel has no body by contract, but it does need an entity to read the status from.
        assertFailsNaming("cancel", () -> client.cancel(connector, new DiscoveryRunRequestDto()));
    }

    private void assertFailsNaming(String operation, Executable call) {
        ConnectorException thrown = Assertions.assertThrows(ConnectorException.class, call);
        Assertions
                .assertTrue(thrown.getMessage().contains(operation),
                        "the failure must name the operation, was: " + thrown.getMessage());
        Assertions
                .assertSame(connector, thrown.getConnector(),
                        "the failure must be attributed to the connector that produced it");
    }

    // ---- Eager validation ----

    @Test
    void constructorsRejectNullCollaborators() {
        DiscoveryMqTimeouts timeouts = timeouts();
        ProxyClient noProxy = null;
        Assertions.assertThrows(NullPointerException.class, () -> new DiscoveryApiClient(null, timeouts));
        Assertions.assertThrows(NullPointerException.class, () -> new DiscoveryApiClient(proxyClient, null));
        Assertions.assertThrows(NullPointerException.class, () -> new DiscoveryApiClient(noProxy));
    }

    // ---- DiscoveryMqTimeouts ----

    @Test
    void timeoutDefaultsAreThirtySecondsForEveryComponent() {
        DiscoveryMqTimeouts defaults = DiscoveryMqTimeouts.defaults();

        Assertions.assertEquals(Duration.ofSeconds(30), defaults.status());
        Assertions.assertEquals(Duration.ofSeconds(30), defaults.drain());
        Assertions.assertEquals(Duration.ofSeconds(30), defaults.control());
    }

    @Test
    void timeoutsRejectNullComponents() {
        Assertions
                .assertThrows(NullPointerException.class,
                        () -> new DiscoveryMqTimeouts(null, DRAIN_TIMEOUT, CONTROL_TIMEOUT));
        Assertions
                .assertThrows(NullPointerException.class,
                        () -> new DiscoveryMqTimeouts(STATUS_TIMEOUT, null, CONTROL_TIMEOUT));
        Assertions
                .assertThrows(NullPointerException.class,
                        () -> new DiscoveryMqTimeouts(STATUS_TIMEOUT, DRAIN_TIMEOUT, null));
    }

    /**
     * A 0ms or negative timeout must fail at construction, not as an immediate, puzzling timeout on the first connector
     * call.
     */
    @Test
    void timeoutsRejectNonPositiveComponents() {
        Duration negative = Duration.ofSeconds(-1);

        assertNotPositive("status", () -> new DiscoveryMqTimeouts(Duration.ZERO, DRAIN_TIMEOUT, CONTROL_TIMEOUT));
        assertNotPositive("status", () -> new DiscoveryMqTimeouts(negative, DRAIN_TIMEOUT, CONTROL_TIMEOUT));
        assertNotPositive("drain", () -> new DiscoveryMqTimeouts(STATUS_TIMEOUT, Duration.ZERO, CONTROL_TIMEOUT));
        assertNotPositive("drain", () -> new DiscoveryMqTimeouts(STATUS_TIMEOUT, negative, CONTROL_TIMEOUT));
        assertNotPositive("control", () -> new DiscoveryMqTimeouts(STATUS_TIMEOUT, DRAIN_TIMEOUT, Duration.ZERO));
        assertNotPositive("control", () -> new DiscoveryMqTimeouts(STATUS_TIMEOUT, DRAIN_TIMEOUT, negative));
    }

    private static void assertNotPositive(String component, Executable construction) {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, construction);
        Assertions
                .assertTrue(thrown.getMessage().startsWith(component + " must be a positive duration"),
                        "the message must name the component and match the ClientTuning shape, was: "
                                + thrown.getMessage());
    }

    @Test
    void singleArgConstructorUsesTheDefaults() throws ConnectorException {
        RecordingProxyClient fake = new RecordingProxyClient();
        fake.syncResponse = new DiscoveryStatusResponseDto();

        new DiscoveryApiClient(fake).status(connector, new DiscoveryRunRequestDto());

        Assertions.assertEquals(Duration.ofSeconds(30), fake.timeout);
    }

    // ---- ProxyClient default overload ----

    /**
     * {@code sendRequestForEntity(…, Duration)} is a {@code default} method that {@link RecordingProxyClient}
     * overrides, so this covers the default body itself: it must forward the caller's timeout to {@code sendRequest}
     * rather than drop it. The status it produces is not asserted — it is not the contract status, and {@code cancel}
     * normalizes it; see {@link #cancel_yieldsNoContentEvenWhenTheProxyLeavesTheOverloadToTheInterfaceDefault()}.
     */
    @Test
    void sendRequestForEntityDefaultForwardsTheCallersTimeout() throws ConnectorException {
        DiscoveryStopResponseDto body = new DiscoveryStopResponseDto();
        TimeoutCapturingProxyClient proxy = new TimeoutCapturingProxyClient(body);

        ResponseEntity<DiscoveryStopResponseDto> response = proxy
                .sendRequestForEntity(connector, DiscoveryPaths.STOP, "POST", null, DiscoveryStopResponseDto.class,
                        DRAIN_TIMEOUT);

        Assertions.assertEquals(DRAIN_TIMEOUT, proxy.seenTimeout);
        Assertions.assertSame(body, response.getBody());
    }

    /**
     * Implements only the abstract {@link ProxyClient} methods, so the interface's {@code default}
     * {@code sendRequestForEntity} runs for real.
     */
    private static final class TimeoutCapturingProxyClient extends UnsupportedProxyClient {
        private final Object response;
        private Duration seenTimeout;

        private TimeoutCapturingProxyClient(Object response) {
            this.response = response;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body,
                Class<T> responseType, Duration timeout) {
            this.seenTimeout = timeout;
            return (T) response;
        }
    }

    /**
     * Records the one call the client under test makes.
     *
     * <p>
     * The recorded connector must stay named {@code seenConnector}, not {@code connector}: sharing the enclosing test's
     * field name makes {@code assertCalled} compare the field with itself, so it could never fail and nothing would
     * check that the client forwards the caller's connector.
     */
    private static final class RecordingProxyClient extends UnsupportedProxyClient {
        private ApiClientConnectorInfo seenConnector;
        private String path;
        private String method;
        private Object body;
        private Class<?> responseType;
        private Duration timeout;
        private boolean viaSendRequestForEntity;

        private Object syncResponse;
        private ConnectorException failure;

        @Override
        @SuppressWarnings("unchecked")
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body,
                Class<T> responseType, Duration timeout) throws ConnectorException {
            capture(connector, path, method, body, responseType, timeout, false);
            return (T) syncResponse;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> ResponseEntity<T> sendRequestForEntity(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType, Duration timeout) throws ConnectorException {
            capture(connector, path, method, body, responseType, timeout, true);
            return (ResponseEntity<T>) syncResponse;
        }

        private void capture(ApiClientConnectorInfo connector, String path, String method, Object body,
                Class<?> responseType, Duration timeout, boolean forEntity) throws ConnectorException {
            this.seenConnector = connector;
            this.path = path;
            this.method = method;
            this.body = body;
            this.responseType = responseType;
            this.timeout = timeout;
            this.viaSendRequestForEntity = forEntity;
            if (failure != null) {
                throw failure;
            }
        }

        private void assertCalled(ApiClientConnectorInfo expectedConnector, String expectedPath, String expectedMethod,
                Object expectedBody, Class<?> expectedResponseType, Duration expectedTimeout) {
            Assertions
                    .assertSame(expectedConnector, seenConnector, "the caller's connector must be forwarded unchanged");
            Assertions.assertEquals(expectedPath, path);
            Assertions.assertEquals(expectedMethod, method);
            Assertions.assertSame(expectedBody, body);
            Assertions.assertEquals(expectedResponseType, responseType);
            Assertions.assertEquals(expectedTimeout, timeout, "wrong DiscoveryMqTimeouts component for this operation");
        }
    }

    /**
     * Base fake: every {@link ProxyClient} member the discovery client must not touch. Reaching a timeout-less overload
     * means an operation silently readopted the proxy's shared default timeout.
     */
    private abstract static class UnsupportedProxyClient implements ProxyClient {

        private static final String NO_TIMEOUT = "the discovery v2 MQ client must pass an explicit Duration on every call";

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method, Object body,
                Class<T> responseType) {
            throw new AssertionError(NO_TIMEOUT + " - timeout-less sendRequest was called for " + path);
        }

        @Override
        public <T> ResponseEntity<T> sendRequestForEntity(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType) {
            throw new AssertionError(NO_TIMEOUT + " - timeout-less sendRequestForEntity was called for " + path);
        }

        // sendRequest(…, Duration) is left abstract: it is the one overload the discovery client may
        // call, so each concrete fake supplies it.

        @Override
        public <T> T sendRequest(ApiClientConnectorInfo connector, String path, String method,
                Map<String, String> pathVariables, Object body, Class<T> responseType) {
            throw new AssertionError(NO_TIMEOUT + " - the path-variable sendRequest overload takes no timeout");
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
                Object body, Class<T> responseType, Duration timeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public <T> CompletableFuture<T> sendRequestAsync(ApiClientConnectorInfo connector, String path, String method,
                Map<String, String> pathVariables, Object body, Class<T> responseType, Duration timeout) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendFireAndForget(ApiClientConnectorInfo connector, String path, String method, Object body,
                String messageType) {
            throw new UnsupportedOperationException();
        }
    }
}
