package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One column of a stored view: which field it shows, and optionally what to call it.
 *
 * <p>
 * This is deliberately not the same type as the column reference on a listing request. The backend needs the source and
 * identifier to resolve a field, but a heading is presentation and is never sent to the listing endpoint; keeping the
 * label out of the request type stops callers expecting it to have an effect there.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ListViewColumnDto {

    @NotNull
    @Schema(description = "Field source of the column", requiredMode = Schema.RequiredMode.REQUIRED)
    private FilterFieldSource fieldSource;

    @NotBlank
    @Schema(description = "Field identifier of the column, resolved against the resource's field catalogue when the "
            + "view is read. " + "Available fields with their identifiers can be retrieved from the resource's "
            + "searchable-fields operation, for example `GET /v1/certificates/search` or "
            + "`GET /v2/connectors/search`.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fieldIdentifier;

    @Size(max = 255)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Heading to show instead of the field's catalogue label, for this view only. Absent or "
            + "null means the column uses the catalogue label, so a field that is later relabelled follows along. An "
            + "empty or whitespace-only string is preserved as given and shows no visible heading.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String label;
}
