package com.otilm.api.model.client.certificate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.inspection.InspectedEntryKind;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What became of one selected entry.
 *
 * <p>
 * Entries succeed or fail on their own, so a caller learns the outcome of each rather than one verdict for the whole
 * file.
 * </p>
 */
@Getter
@Setter
@ToString
@Schema(name = "CertificateImportResultDto", description = "Outcome of importing one entry")
public class CertificateImportResultDto {

    @Schema(description = "Reference of the entry this outcome belongs to", requiredMode = Schema.RequiredMode.REQUIRED)
    private String entryReference;

    @Schema(description = "What the entry turned out to be", requiredMode = Schema.RequiredMode.REQUIRED)
    private InspectedEntryKind kind;

    @Schema(description = "Whether the entry's objects are in the inventory after the call",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean imported;

    @Schema(description = "What became of the certificate, when the entry carried one",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImportOutcome certificateOutcome;

    @Schema(description = "UUID of the entry's certificate, withheld when the caller may not see it in detail",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String certificateUuid;

    @Schema(description = "What became of the key, when the entry carried key material",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ImportOutcome keyOutcome;

    @Schema(description = "UUID of the entry's key, withheld for a key already in the inventory that the caller "
            + "may not see in detail", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String keyUuid;

    @Schema(description = "Why this entry was not imported, when it was not",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String message;
}
