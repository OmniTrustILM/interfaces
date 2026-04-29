package com.otilm.api.exception;

import com.otilm.api.model.common.error.ProblemDetailExtended;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConnectorProblemException extends ConnectorException {

    private final ProblemDetailExtended problemDetail;

    public ConnectorProblemException(ProblemDetailExtended problemDetail) {
        super(problemDetail.getDetail() != null ? problemDetail.getDetail() : problemDetail.getTitle());
        this.problemDetail = problemDetail;
    }

    public HttpStatus getHttpStatus() {
        return HttpStatus.valueOf(problemDetail.getStatus());
    }

    public String getFullMessage(boolean withProblemDetail) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append(this.getMessage());

        if (this.connector != null) {
            messageBuilder
                    .append(" ")
                    .append("Error is related to connector '")
                    .append(this.connector.getName()).append("' (")
                    .append(this.connector.getUuid())
                    .append(").");
        }
        if (withProblemDetail) {
            messageBuilder
                    .append(" ")
                    .append(problemDetail.toString());
        }
        return messageBuilder.toString();
    }

}
