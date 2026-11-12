package com.otilm.api.model.common.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProblemDetailExtendedFromErrorCodeTest {

    @Test
    void commonCategoryEmits2SegmentUri() {
        ProblemDetailExtended pd = ProblemDetailExtended
                .fromErrorCode(ErrorCode.VALIDATION_FAILED, "detail", null, "corr-1");
        assertEquals("https://docs.otilm.com/problems/common/VALIDATION_FAILED", pd.getType().toString());
    }

    @Test
    void connectorGeneralEmits2SegmentUri() {
        ProblemDetailExtended pd = ProblemDetailExtended
                .fromErrorCode(ErrorCode.UPSTREAM_ERROR, "detail", null, "corr-2");
        assertEquals("https://docs.otilm.com/problems/connector/UPSTREAM_ERROR", pd.getType().toString());
    }

    @Test
    void documentHandlingCodesEmitOneStable2SegmentUri() {
        ProblemDetailExtended malformed = ProblemDetailExtended
                .fromErrorCode(ErrorCode.DOCUMENT_MALFORMED, "detail", null, "corr-4");
        assertEquals("https://docs.otilm.com/problems/connector/DOCUMENT_MALFORMED", malformed.getType().toString());

        ProblemDetailExtended notFound = ProblemDetailExtended
                .fromErrorCode(ErrorCode.SIGNATURE_NOT_FOUND, "detail", null, "corr-5");
        assertEquals("https://docs.otilm.com/problems/connector/SIGNATURE_NOT_FOUND", notFound.getType().toString());
    }

    @Test
    void connectorAuthorityEmits3SegmentUri() {
        ProblemDetailExtended pd = ProblemDetailExtended
                .fromErrorCode(ErrorCode.CSR_MALFORMED, "detail", null, "corr-3");
        assertEquals("https://docs.otilm.com/problems/connector/authority/CSR_MALFORMED", pd.getType().toString());
    }

    @Test
    void retryableTrueSetsRetryAfter30() {
        ProblemDetailExtended pd = ProblemDetailExtended
                .fromErrorCode(ErrorCode.SERVICE_UNAVAILABLE, "detail", null, "corr-4");
        assertEquals(30, pd.getRetryAfterSeconds());
    }
}
