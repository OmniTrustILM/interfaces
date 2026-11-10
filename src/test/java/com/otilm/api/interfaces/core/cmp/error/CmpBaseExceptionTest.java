package com.otilm.api.interfaces.core.cmp.error;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.otilm.api.exception.ConnectorException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Named.named;

class CmpBaseExceptionTest {

    private static final String ECHOED_SECRET = "passphrase-the-connector-echoed";

    @ParameterizedTest
    @MethodSource("cmpFailures")
    void logsNothing_whenBuilt(Supplier<CmpBaseException> cmpFailure) {
        // given — the root logger records every event while the failure is built

        // when
        List<String> logged = captureLogsWhile(cmpFailure::get);

        // then
        assertEquals(List.of(), logged);
    }

    @Test
    void returnsTheDetailsItWasRaisedWith_whenCausedByAConnectorsFailure() {
        // given
        var raisedWith = "cannot issue certificate";
        var cmpFailure = new CmpProcessingException(new DEROctetString(new byte[]{1, 2, 3, 4}),
                PKIFailureInfo.systemFailure, raisedWith, new ConnectorException(ECHOED_SECRET));

        // when
        String errorDetails = cmpFailure.getErrorDetails();

        // then
        assertEquals(raisedWith, errorDetails);
    }

    @Test
    void returnsAFixedDetail_whenBuiltFromANonCmpFailureAlone() {
        // given
        var connectorFailure = new ConnectorException(ECHOED_SECRET);

        // when
        String errorDetails = new CmpBaseException(connectorFailure).getErrorDetails();

        // then
        assertEquals("system failure", errorDetails);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Stream<Named<Supplier<CmpBaseException>>> cmpFailures() {
        var tid = new DEROctetString(new byte[]{1, 2, 3, 4});
        return Stream
                .of(named("caused by a connector's failure",
                        () -> new CmpProcessingException(tid, PKIFailureInfo.systemFailure, "cannot issue certificate",
                                new ConnectorException(ECHOED_SECRET))),
                        named("caused by a connector's failure, without a transaction",
                                () -> new CmpProcessingException(null, PKIFailureInfo.systemFailure,
                                        "cannot issue certificate", new ConnectorException(ECHOED_SECRET))),
                        named("caused by another CMP failure",
                                () -> new CmpProcessingException(tid, PKIFailureInfo.systemFailure,
                                        "cannot issue certificate",
                                        new CmpProcessingException(tid, PKIFailureInfo.badRequest, ECHOED_SECRET))),
                        named("without a cause",
                                () -> new CmpProcessingException(tid, PKIFailureInfo.badRequest, ECHOED_SECRET)),
                        named("from a failure alone",
                                () -> new CmpBaseException(new ConnectorException(ECHOED_SECRET))));
    }

    /** Every message logged while the action runs. */
    private static List<String> captureLogsWhile(Runnable action) {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        ListAppender<ILoggingEvent> captured = new ListAppender<>();
        captured.start();
        root.addAppender(captured);
        try {
            action.run();
        } finally {
            root.detachAppender(captured);
        }
        return captured.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }
}
