package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * List of the parameters for the challenge object in the ACME protocol
 */
public class Challenge {
    /**
     * Type of the Challenge encoded in the object.
     */
    @Schema(description = "Type of Challenge",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"dns-01"})
    private ChallengeType type;

    /**
     * URL to which the response can be posted after the client completes the Challenge
     */
    @Schema(description = "URL to which the response can be posted after the client completes the Challenge",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"https://some-server.com/api/v1/protocols/acme/chall/JHjhrt&6hf"})
    private String url;

    /**
     * Status of the Challenge. The possible values are "pending", "processing", "valid", "invalid"
     * This is a mandatory field.
     */
    @Schema(description = "Challenge status",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private ChallengeStatus status;

    /**
     * Time at which the server validated this Challenge. This data is encoded in the format
     * specified in RFC3339.
     * This is a Non-Mandatory parameter bus is required if the status of the status field is set to valid
     */
    @Schema(description = "Timestamp at which the Challenge is validated", format = "date-time", type = "string")
    private String validated;

    /**
     * Error that occurred while the server was validating the Challenge, if any, structured as a problem document
     * [RFC7807].  Multiple errors can be indicated by using subproblems.
     * A challenge object with an error MUST have status equal to "invalid".
     */
    @Schema(description = "Errors in Challenge validation")
    private ProblemDocument error;

    /**
     * Random string generated using the SecureRandom class of JAVA to provide a cryptography random key
     */
    @Schema(description = "Token for the Challenge",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"JGuyIUgkRGFYTER658ykjfYFur76fkFitur7FGHRiytrkfIruFF"})
    private String token;

    public ChallengeType getType() {
        return type;
    }

    public void setType(ChallengeType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public String getValidated() {
        return validated;
    }

    public void setValidated(String validated) {
        this.validated = validated;
    }

    public ProblemDocument getError() {
        return error;
    }

    public void setError(ProblemDocument error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("url", url)
                .append("status", status)
                .append("validated", validated)
                .append("error", error)
                .append("token", token)
                .toString();
    }
}
