package com.otilm.api.model.core.cbom;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import lombok.Data;

/**
 * One line of the report the cryptographic asset ingest wrote about a CBOM, rolled up per distinct message: what the
 * ingest had to say, how many components said it, and one of them by name. The text names the document member a finding
 * is about and never carries the value of a key or of any other material.
 */
@Data
public class CbomIngestFindingDto {

    @Schema(description = "Whether the line reports something about the document or a component that yielded no "
            + "asset", requiredMode = Schema.RequiredMode.REQUIRED)
    private CbomIngestFindingKind kind;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "The name of one component that raised the line, the first in document order. Absent when "
            + "the line is about the document as a whole rather than any one component",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String componentName;

    @Schema(description = "What the ingest had to say. A line names the document member it is about and may quote a "
            + "component's name, its bom-ref, the type of its material, the names of members it dropped and a size or "
            + "parameter set the document declares, but never the value of a key or of any other material",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String detail;

    @Schema(description = "How many components of the document raised this line",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int occurrences;

    @Schema(description = "When the ingest attempt that wrote the line ran; the same for every line of one report",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private OffsetDateTime recordedAt;
}
