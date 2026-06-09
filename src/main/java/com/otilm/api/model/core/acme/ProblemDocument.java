package com.otilm.api.model.core.acme;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

/**
 * Parameter for the problem documents or errors during the ACME
 * process. This is based on the RFC7807
 */
public class ProblemDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Type of the problem.
     */
    @Schema(description = "Type of the ACME problem",
            requiredMode = Schema.RequiredMode.REQUIRED,
            examples = {"notFound"})
    private String type;

    /**
     * Title of the problem. In short words.
     */
    @Schema(description = "ACME problem title", examples = {"Not Found"})
    private String title;

    /**
     * Details of the problem. These statements should be understandable by the user.
     */
    @Schema(description = "ACME problem details",
            requiredMode = Schema.RequiredMode.REQUIRED, examples = {"Requested object is not found"})
    private String detail;

    /**
     * URL of the changes if something needs to be approved. Used in ACME to notify the new instance of
     * Terms of Service to be agreed by the client to continue with the ACME operations
     */
    @Schema(description = "URL of the changes if something needs to be approved",
            requiredMode = Schema.RequiredMode.REQUIRED, examples = {"https://some-company.com/instances/changes"})
    private String instance;

    /**
     * Subproblems if more than one error is occurred
     */
    @Schema(description = "List of subproblems related to error")
    private List<ProblemDocument> subproblems;

    /**
     * List of supported algorithms supported by the server.
     * This field is mandatory if the client signs the JWS by some unsupported algorithm
     */
    @Schema(description = "List of supported algorithms", example = "[\"ES256\"]")
    private List<String> algorithms;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<ProblemDocument> getSubproblems() {
        return subproblems;
    }

    public void setSubproblems(List<ProblemDocument> subproblems) {
        this.subproblems = subproblems;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public List<String> getAlgorithms() {
        return algorithms;
    }

    public void setAlgorithms(List<String> algorithms) {
        this.algorithms = algorithms;
    }

    public ProblemDocument() {
    }

    public ProblemDocument(String type, String title, String detail) {
        setType(type);
        setTitle(title);
        setDetail(detail);
    }

    public ProblemDocument(Problem problem) {
        setType(problem.getType());
        setTitle(problem.getTitle());
        setDetail(problem.getDetail());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("type", type)
                .append("title", title)
                .append("detail", detail)
                .append("instance", instance)
                .append("algorithms", algorithms)
                .toString();
    }

}
