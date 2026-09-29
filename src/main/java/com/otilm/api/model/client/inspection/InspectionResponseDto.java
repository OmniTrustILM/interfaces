package com.otilm.api.model.client.inspection;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * What an uploaded file contains. Nothing here has to be carried into an import: an entry reference is derived from the
 * entry's own content.
 */
@Getter
@Setter
@ToString
@Schema(name = "InspectionResponseDto", description = "Entries found in an uploaded file")
public class InspectionResponseDto {

    @Schema(description = "The lowercase hex SHA-256 of the uploaded file, so a caller can tell two uploads apart and "
            + "confirm the file it is about to import is the one it read", requiredMode = Schema.RequiredMode.REQUIRED)
    private String containerDigest;

    @Schema(description = "Entries found in the file", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<InspectedEntryDto> entries;
}
