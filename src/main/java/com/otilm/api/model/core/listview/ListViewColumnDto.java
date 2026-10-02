package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * One column of a stored view: which field it shows, and optionally what to call it.
 *
 * <p>
 * This is deliberately not the same type as the column reference on a listing request. The backend needs the source and
 * identifier to resolve a field, but a heading is presentation and is never sent to the listing endpoint; keeping the
 * label out of the request type stops callers expecting it to have an effect there.
 *
 * <p>
 * An attribute identifier names an attribute and content type, which a later, unrelated definition can share. A column
 * is therefore bound to the attribute definitions it was added for, and the view reports it as replaced rather than
 * resolving it against a definition it was never bound to.
 */
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ArraySchema(schema = @Schema(format = "uuid", example = "6f1d2c1e-6c3a-4c5e-9f0a-2b7d8e9f0a1b"),
            arraySchema = @Schema(accessMode = Schema.AccessMode.READ_ONLY,
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                    description = """
                            Attribute definitions the column is bound to, set by the server when the column is \
                            added or rebound. Absent for a property column. Only definitions of the \
                            view's resource count. Empty for an attribute column whose definition was already gone when \
                            stored views were first bound. A value sent in a request is \
                            ignored."""))
    private List<UUID> attributeDefinitionUuids;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = """
            How the column resolves for the caller when the view is read. A client shows only an `available` \
            column as its field; an `unavailable` or `replaced` one is dormant and offered for removal, and a \
            `replaced` one also for rebinding. Returned on every read and write response; a value sent in a \
            request is ignored.""")
    private ListViewFieldStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            defaultValue = "false", description = """
                    Set to true to bind an attribute column the view already holds to the attribute definitions now \
                    behind its identifier, which is how a client accepts a `replaced` column. The column is then \
                    held to the catalogue as a newly added one. Absent or false carries a column the view already \
                    holds over: while it still resolves it follows the definitions currently behind its \
                    identifier, and once it no longer does it keeps the binding it had, so a replacement never \
                    takes it over. Any other column is bound to the current definitions. Has no effect on a \
                    property column, and is not stored.""")
    private Boolean rebind;

    public ListViewColumnDto(FilterFieldSource fieldSource, String fieldIdentifier, String label) {
        this.fieldSource = fieldSource;
        this.fieldIdentifier = fieldIdentifier;
        this.label = label;
    }
}
