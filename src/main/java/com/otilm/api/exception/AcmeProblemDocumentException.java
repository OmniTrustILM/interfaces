package com.otilm.api.exception;

import com.otilm.api.model.core.acme.Problem;
import com.otilm.api.model.core.acme.ProblemDocument;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class AcmeProblemDocumentException extends Exception {
    @Schema(description = "HTTP Status code corresponding to the error")
    private int httpStatusCode;
    @Schema(description = "ACME Problem document described in RFC8555")
    private ProblemDocument problemDocument;
    @Schema(description = "Additional headers for the response")
    private Map<String, String> additionalHeaders;

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public ProblemDocument getProblemDocument() {
        return problemDocument;
    }

    public Map<String, String> getAdditionalHeaders() {
        return additionalHeaders;
    }

    public AcmeProblemDocumentException(HttpStatus httpStatus, ProblemDocument problemDocument) {
        super(problemDocument.getDetail());
        this.httpStatusCode = httpStatus.value();
        this.problemDocument = problemDocument;
    }

    public AcmeProblemDocumentException(HttpStatus httpStatus, ProblemDocument problemDocument, Map additionalHeaders) {
        super(problemDocument.getDetail());
        this.httpStatusCode = httpStatus.value();
        this.problemDocument = problemDocument;
        this.additionalHeaders = additionalHeaders;
    }

    public AcmeProblemDocumentException(HttpStatus httpStatus, Problem problem) {
        this(httpStatus, new ProblemDocument(problem));
    }

    public AcmeProblemDocumentException(HttpStatus httpStatus, Problem problem, String detail) {
        this(httpStatus, new ProblemDocument(problem.getType(), problem.getTitle(), detail));

    }

    @Override
    public String toString() {
        return getLocalizedMessage();
    }
}
