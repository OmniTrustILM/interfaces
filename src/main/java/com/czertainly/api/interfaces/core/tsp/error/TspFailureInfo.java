package com.czertainly.api.interfaces.core.tsp.error;

import lombok.Getter;

/**
 * RFC 3161 PKIFailureInfo bit positions.
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc3161#section-2.4.2">RFC 3161 section 2.4.2</a>
 */
@Getter
public enum TspFailureInfo {

    BAD_ALG(0),                // unrecognized or unsupported Algorithm Identifier
    BAD_REQUEST(2),            // transaction not permitted or supported
    BAD_DATA_FORMAT(5),        // the data submitted has the wrong format
    TIME_NOT_AVAILABLE(14),    // TSA's time source is not available
    UNACCEPTED_POLICY(15),     // the requested TSA policy is not supported
    UNACCEPTED_EXTENSION(16),  // the requested extension is not supported
    ADD_INFO_NOT_AVAILABLE(17),// the additional information requested is not available
    SYSTEM_FAILURE(25);        // the request cannot be handled due to system failure

    private final int bitPosition;

    TspFailureInfo(int bitPosition) {
        this.bitPosition = bitPosition;
    }

}
