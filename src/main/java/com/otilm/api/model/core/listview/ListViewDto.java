package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.certificate.SearchFilterRequestDto;
import com.otilm.api.model.client.certificate.SearchSortRequestDto;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * A stored list view belonging to the authenticated user.
 *
 * <p>
 * Columns are stored as field identifiers and resolved against the live field catalogue when the view is read, so a
 * renamed or deleted attribute leaves a column the client marks unavailable, rather than requiring stored views to be
 * migrated.
 */
@Data
public class ListViewDto {

    @Schema(description = "UUID of the view", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Name of the view", examples = {"Expiry watch"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Resource whose listing the view applies to", examples = {"certificates"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource resource;

    @Schema(description = "Columns of the view, in display order. A column whose field the resource no longer defines, "
            + "such as a deleted attribute, is still returned, so a client can show it as unavailable and offer to "
            + "remove it.", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ListViewColumnDto> columns;

    @Schema(description = "Whether this view applies when the listing is opened", defaultValue = "false",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean defaultView;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Filters the view applies. Absent or empty means the view shows the whole inventory.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<SearchFilterRequestDto> filters;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Ordering the view applies. Absent means the endpoint's own default ordering.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private SearchSortRequestDto sort;
}
