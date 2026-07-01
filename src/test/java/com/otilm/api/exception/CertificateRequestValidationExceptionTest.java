package com.otilm.api.exception;

import org.junit.jupiter.api.Test;

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
}
