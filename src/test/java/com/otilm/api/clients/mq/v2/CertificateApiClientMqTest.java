package com.otilm.api.clients.mq.v2;

import com.otilm.api.exception.ConnectionServiceException;
import com.otilm.api.exception.ConnectorEntityNotFoundException;
import com.otilm.api.exception.ConnectorException;
import com.otilm.api.exception.ConnectorServerException;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.connector.v2.CertificateOperationCancelRequestDto;
import com.otilm.api.model.core.connector.ConnectorDto;
import com.otilm.api.testsupport.RecordingProxyClient;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Named.named;

/**
 * The MQ v2 cancel calls read the connector's status and accept only a 2xx as a confirmed cancel, while the proxy's
 * mapping of 404, 422 and 5xx answers reaches the caller unchanged.
 */
class CertificateApiClientMqTest {

    private static final String AUTHORITY_UUID = "6b1c2f4e-0d5a-4c39-9f7e-3a2b1c0d9e8f";

    private RecordingProxyClient proxyClient;
    private CertificateApiClient client;
    private ConnectorDto connector;
    private CertificateOperationCancelRequestDto request;

    @BeforeEach
    void setUp() {
        proxyClient = new RecordingProxyClient();
        client = new CertificateApiClient(proxyClient);
        connector = new ConnectorDto();
        connector.setUrl("http://localhost");
        request = new CertificateOperationCancelRequestDto();
    }

    @FunctionalInterface
    private interface CancelCall {
        void cancel(CertificateApiClient client, ConnectorDto connector, CertificateOperationCancelRequestDto request)
                throws ConnectorException;
    }

    private static Stream<Arguments> cancelCalls() {
        return Stream
                .of(Arguments
                        .of(named("cancel issue",
                                (CancelCall) (c, conn, req) -> c.cancelIssueCertificate(conn, AUTHORITY_UUID, req)),
                                "/certificates/issue/cancel"),
                        Arguments
                                .of(named("cancel revoke",
                                        (CancelCall) (c, conn, req) -> c
                                                .cancelRevokeCertificate(conn, AUTHORITY_UUID, req)),
                                        "/certificates/revoke/cancel"));
    }

    private static Stream<Arguments> cancelCallsWithStatus(HttpStatus... statuses) {
        return cancelCalls()
                .flatMap(call -> Stream.of(statuses).map(status -> Arguments.of(call.get()[0], call.get()[1], status)));
    }

    private static Stream<Arguments> confirmingStatuses() {
        return cancelCallsWithStatus(HttpStatus.NO_CONTENT, HttpStatus.OK);
    }

    private static Stream<Arguments> redirectStatuses() {
        return cancelCallsWithStatus(HttpStatus.FOUND, HttpStatus.TEMPORARY_REDIRECT);
    }

    private static Stream<Arguments> proxyFailures() {
        return cancelCalls()
                .flatMap(call -> Stream
                        .of(Arguments
                                .of(call.get()[0], named("404", new ConnectorEntityNotFoundException("not tracked")),
                                        ConnectorEntityNotFoundException.class),
                                Arguments
                                        .of(call.get()[0], named("422", new ValidationException("refused")),
                                                ValidationException.class),
                                Arguments
                                        .of(call.get()[0],
                                                named("500",
                                                        new ConnectorServerException("server error",
                                                                HttpStatus.INTERNAL_SERVER_ERROR)),
                                                ConnectorServerException.class)));
    }

    @ParameterizedTest
    @MethodSource("confirmingStatuses")
    void returnsNormallyAndReadsTheStatus_whenTheConnectorAnswers2xx(CancelCall call, String pathSuffix,
            HttpStatus status) throws ConnectorException {
        // given
        proxyClient.respondWithEntity(ResponseEntity.status(status).build());

        // when
        call.cancel(client, connector, request);

        // then
        RecordingProxyClient.Invocation invocation = proxyClient.invocation();
        assertTrue(invocation.entityResponse());
        assertEquals("/v2/authorityProvider/authorities/" + AUTHORITY_UUID + pathSuffix, invocation.path());
        assertEquals("POST", invocation.method());
        assertSame(request, invocation.body());
        assertEquals(Void.class, invocation.responseType());
    }

    @ParameterizedTest
    @MethodSource("redirectStatuses")
    void throwsConnectionServiceException_whenTheConnectorAnswersARedirect(CancelCall call, String pathSuffix,
            HttpStatus status) {
        // given
        proxyClient.respondWithEntity(ResponseEntity.status(status).build());

        // when / then
        ConnectionServiceException thrown = assertThrows(ConnectionServiceException.class,
                () -> call.cancel(client, connector, request));
        assertEquals(status, thrown.getHttpStatus());
    }

    @ParameterizedTest
    @MethodSource("cancelCalls")
    void throwsConnectionServiceException_whenThereIsNoResponse(CancelCall call, String pathSuffix) {
        // given
        proxyClient.respondWithEntity(null);

        // when / then
        ConnectionServiceException thrown = assertThrows(ConnectionServiceException.class,
                () -> call.cancel(client, connector, request));
        assertEquals(HttpStatus.BAD_GATEWAY, thrown.getHttpStatus());
    }

    @ParameterizedTest
    @MethodSource("proxyFailures")
    void propagatesTheProxyMapping_whenTheConnectorAnswersAnError(CancelCall call, Exception failure,
            Class<? extends Exception> expected) {
        // given
        if (failure instanceof ConnectorException connectorFailure) {
            proxyClient.failWith(connectorFailure);
        } else {
            proxyClient.failWith((RuntimeException) failure);
        }

        // when / then
        Exception thrown = assertThrows(expected, () -> call.cancel(client, connector, request));
        assertSame(failure, thrown);
    }
}
