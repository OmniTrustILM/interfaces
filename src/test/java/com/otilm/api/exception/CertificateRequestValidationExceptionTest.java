package com.otilm.api.exception;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CertificateRequestValidationExceptionTest {

    @Test
    void carriesShapedMessageAndDetails_whenConstructedWithDetails() {
        // given
        var message = "Uploaded CSR does not satisfy the request-attribute policy";
        var findings = List.of(
                "Missing required mapped field: Common Name",
                "SAN dNSName 'evil.example.com' is not allowed by the request-attribute set");

        // when
        var exception = new CertificateRequestValidationException(message, findings);

        // then
        assertEquals(message, exception.getMessage());
        assertEquals(findings, exception.getDetails());
    }

    @Test
    void returnsEmptyDetails_whenConstructedWithNullDetails() {
        // given — a reject with a message but no per-finding detail

        // when
        var exception = new CertificateRequestValidationException("bad", null);

        // then
        assertTrue(exception.getDetails().isEmpty());
    }

    @Test
    void carriesMessageAndEmptyDetails_whenConstructedWithMessageOnly() {
        // given
        var message = "Uploaded CSR does not satisfy the request-attribute policy";

        // when
        var exception = new CertificateRequestValidationException(message);

        // then
        assertEquals(message, exception.getMessage());
        assertTrue(exception.getDetails().isEmpty());
    }

    @Test
    void returnsUnmodifiableDetails() {
        // given
        var exception = new CertificateRequestValidationException("bad", List.of("finding"));

        // when / then
        assertThrows(UnsupportedOperationException.class, () -> exception.getDetails().add("x"));
    }

    @Test
    void isolatesFromCallerListMutationAfterConstruction() {
        // given — a mutable list handed to the constructor
        var findings = new ArrayList<String>();
        findings.add("Missing required mapped field: Common Name");

        // when
        var exception = new CertificateRequestValidationException("bad", findings);
        findings.add("mutated after construction");

        // then
        assertEquals(List.of("Missing required mapped field: Common Name"), exception.getDetails());
    }
}
