package com.otilm.api.interfaces.core.tsp.error;

import com.otilm.api.exception.PlatformException;
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

    /**
     * @param failureInfo   RFC 3161 PKIFailureInfo bit position
     * @param message       internal message for logging — may include runtime detail
     * @param clientMessage safe text for PKIFreeText in the rejection TimeStampResp —
     *                      must be a static, hardcoded string; never pass {@code e.getMessage()}
     *                      or any runtime-derived value here
     */
    public TspException(TspFailureInfo failureInfo, String message, String clientMessage) {
        super(message);
        this.failureInfo = failureInfo;
        this.clientMessage = clientMessage;
    }

    /**
     * @param failureInfo   RFC 3161 PKIFailureInfo bit position
     * @param message       internal message for logging — may include runtime detail
     * @param cause         underlying exception
     * @param clientMessage safe text for PKIFreeText in the rejection TimeStampResp —
     *                      must be a static, hardcoded string; never pass {@code e.getMessage()}
     *                      or any runtime-derived value here
     */
    public TspException(TspFailureInfo failureInfo, String message, Throwable cause, String clientMessage) {
        super(message, cause);
        this.failureInfo = failureInfo;
        this.clientMessage = clientMessage;
    }

}
