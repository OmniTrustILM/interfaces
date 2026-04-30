package com.czertainly.api.model.client.signing.timequality.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HostnameListValidatorTest {

    @Test
    void nullOrEmptyStringIsInvalid() {
        assertFalse(HostnameListValidator.isValidHostname(null));
        assertFalse(HostnameListValidator.isValidHostname(""));
    }

    @Test
    void hostnameLongerThan253CharsIsInvalid() {
        assertFalse(HostnameListValidator.isValidHostname("a".repeat(254)));
    }

    @Test
    void validIpv4IsValid() {
        assertTrue(HostnameListValidator.isValidHostname("192.168.1.1"));
        assertTrue(HostnameListValidator.isValidHostname("10.0.0.1"));
    }

    @Test
    void validIpv6IsValid() {
        assertTrue(HostnameListValidator.isValidHostname("::1"));
        assertTrue(HostnameListValidator.isValidHostname("2001:db8::1"));
    }

    @Test
    void singleLabelHostnameIsValid() {
        assertTrue(HostnameListValidator.isValidHostname("localhost"));
        assertTrue(HostnameListValidator.isValidHostname("ntp1"));
    }

    @Test
    void fullyQualifiedDomainNameIsValid() {
        assertTrue(HostnameListValidator.isValidHostname("pool.ntp.org"));
        assertTrue(HostnameListValidator.isValidHostname("ntp-server-1.example.com"));
    }

    @Test
    void trailingDotIsInvalid() {
        assertFalse(HostnameListValidator.isValidHostname("pool.ntp.org."));
    }

    @Test
    void hyphenAtStartOrEndOfLabelIsInvalid() {
        assertFalse(HostnameListValidator.isValidHostname("-invalid.com"));
        assertFalse(HostnameListValidator.isValidHostname("invalid-.com"));
    }

    @Test
    void labelExceeding63CharsIsInvalid() {
        assertFalse(HostnameListValidator.isValidHostname("a".repeat(64) + ".com"));
    }

    @Test
    void nullListIsValid() {
        HostnameListValidator validator = new HostnameListValidator();
        assertTrue(validator.isValid(null, null));
    }
}
