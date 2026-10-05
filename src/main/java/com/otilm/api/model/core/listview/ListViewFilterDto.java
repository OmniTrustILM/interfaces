package com.otilm.api.model.core.listview;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.otilm.api.model.client.certificate.SearchFilterRequestDto;
import com.otilm.api.model.core.search.FilterConditionOperator;
import com.otilm.api.model.core.search.FilterFieldSource;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * One stored filter of a view. It is a listing filter term, so a client applies it by sending its addressing fields and
 * value to the listing, and it carries the same attribute binding as a {@link ListViewColumnDto}.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListViewFilterDto extends SearchFilterRequestDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ArraySchema(schema = @Schema(format = "uuid", example = "6f1d2c1e-6c3a-4c5e-9f0a-2b7d8e9f0a1b"),
            arraySchema = @Schema(accessMode = Schema.AccessMode.READ_ONLY,
                    requiredMode = Schema.RequiredMode.NOT_REQUIRED,
                    description = """
                            Attribute definitions the filter is bound to, set by the server when the filter is \
                            added or rebound. Absent for a property filter. Only definitions of the \
                            view's resource count. Empty for an attribute filter whose definition was already gone when \
                            stored views were first bound. A value sent in a request is \
                            ignored."""))
    private List<UUID> attributeDefinitionUuids;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY, requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = """
            How the filter resolves for the caller when the view is read. A client applies only an \
            `available` filter to the listing; an `unavailable` or `replaced` one is dormant and offered for \
            removal, and a `replaced` one also for rebinding. Returned on every read and write response; a \
            value sent in a request is ignored.""")
    private ListViewFieldStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            defaultValue = "false", description = """
                    Set to true to bind an attribute filter the view already holds to the attribute definitions now \
                    behind its identifier, which is how a client accepts a `replaced` filter. Absent or false \
                    carries a filter the view already \
                    holds over: while it still resolves it follows the definitions currently behind its \
                    identifier, and once it no longer does it keeps the binding it had, so a replacement never \
                    takes it over. Any other filter is bound to the current definitions. Any filter sent with \
                    rebind is held to the catalogue as a newly added one, property filters included, but only an \
                    attribute filter has a binding to change. The flag is not stored.""")
    private Boolean rebind;

    public ListViewFilterDto(FilterFieldSource fieldSource, String fieldIdentifier, FilterConditionOperator condition,
            Serializable value) {
        super(fieldSource, fieldIdentifier, condition, value);
    }
}
