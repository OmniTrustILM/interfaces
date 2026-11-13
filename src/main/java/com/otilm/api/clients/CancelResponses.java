package com.otilm.api.clients;

import com.otilm.api.exception.ConnectionServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

/**
 * Checks a connector's answer to a cancel request. Both the REST and the MQ clients raise errors for 4xx and 5xx only,
 * so a bodiless 3xx returns normally and would otherwise read as a cancellation the connector performed.
 */
public final class CancelResponses {

    private CancelResponses() {
    }

    /**
     * Accepts only a 2xx as a confirmed cancel.
     *
     * @param response the connector's answer, possibly {@code null}
     * @throws ConnectionServiceException carrying the answer's status, or {@code BAD_GATEWAY} when there is no answer
     * or its status has no {@link HttpStatus} constant
     */
    public static void requireConfirmed(ResponseEntity<?> response) throws ConnectionServiceException {
        HttpStatusCode status = response == null ? null : response.getStatusCode();
        if (status == null || !status.is2xxSuccessful()) {
            String label = status == null ? "no response" : String.valueOf(status.value());
            HttpStatus httpStatus = status == null ? null : HttpStatus.resolve(status.value());
            throw new ConnectionServiceException("Connector answered cancel with unexpected status " + label,
                    httpStatus == null ? HttpStatus.BAD_GATEWAY : httpStatus);
        }
    }
}
