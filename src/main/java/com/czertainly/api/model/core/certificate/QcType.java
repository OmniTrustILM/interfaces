package com.czertainly.api.model.core.certificate;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Closed set of qualified certificate types from ETSI EN 319 412-5 §4.3.4.
 */
public enum QcType {
    @JsonProperty("esign") ESIGN,   // id-etsi-qct-esign  0.4.0.1862.1.6.1  – natural person signature
    @JsonProperty("eseal") ESEAL,   // id-etsi-qct-eseal  0.4.0.1862.1.6.2  – legal entity seal
    @JsonProperty("web") WEB        // id-etsi-qct-web    0.4.0.1862.1.6.3  – website authentication
}
