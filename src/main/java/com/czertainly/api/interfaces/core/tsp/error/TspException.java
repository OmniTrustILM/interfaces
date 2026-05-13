package com.czertainly.api.interfaces.core.tsp.error;

import com.czertainly.api.exception.PlatformException;
import lombok.Getter;

/**
 * Thrown when a RFC 3161 timestamp request cannot be fulfilled.
 * Carries a {@link TspFailureInfo} so the Core implementation can produce
 * a correctly coded PKIFailureInfo in the rejection TimeStampResp.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc3161#section-2.4.2">RFC 3161 section 2.4.2</a>
 */
@Getter
public class TspException extends Exception implements PlatformException {

    private final TspFailureInfo failureInfo;
    private final String clientMessage;

    public TspException(TspFailureInfo failureInfo, String message, String clientMessage) {
        super(message);
        this.failureInfo = failureInfo;
        this.clientMessage = clientMessage;
    }

    public TspException(TspFailureInfo failureInfo, String message, Throwable cause, String clientMessage) {
        super(message, cause);
        this.failureInfo = failureInfo;
        this.clientMessage = clientMessage;
    }

}
