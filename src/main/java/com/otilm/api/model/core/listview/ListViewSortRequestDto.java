package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.client.certificate.SearchSortRequestDto;
import com.otilm.api.model.core.search.FilterFieldSource;
import com.otilm.api.model.core.search.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * The ordering a create or update request gives a view. It is a listing ordering, so a client can send the one it
 * applies, plus the flag that tells a newly chosen ordering apart from the stored one sent back.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListViewSortRequestDto extends SearchSortRequestDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            defaultValue = "false", description = """
                    Set to true when the user chose this ordering now, to bind an attribute ordering to the \
                    attribute definitions currently behind its identifier. Absent or false carries an ordering \
                    sent exactly as the view stores it - same field and direction - over, provided the listing \
                    still offers its field and can order by it: while it still resolves it follows the definitions \
                    currently behind its identifier, and once it no longer does, because the attribute was \
                    replaced, it keeps the binding it had, so the replacement never takes it over and the view \
                    reads back without it. Any other ordering is bound to the current definitions. Only an \
                    attribute ordering has a binding to change. The flag is not stored.""")
    private Boolean rebind;

    public ListViewSortRequestDto(FilterFieldSource fieldSource, String fieldIdentifier, SortDirection direction) {
        super(fieldSource, fieldIdentifier, direction);
    }
}
