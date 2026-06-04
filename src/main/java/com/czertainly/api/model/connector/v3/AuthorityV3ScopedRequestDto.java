package com.czertainly.api.model.connector.v3;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Abstract base for all v3 request bodies that target an authority + RA profile context.
 * Every v3 request body extends this so the stateless connector can reconstruct the
 * upstream CA session from the inherited attribute lists.
 */
@Getter
@Setter
public abstract class AuthorityV3ScopedRequestDto {

    @Schema(description = "Authority attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "authorityAttributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> authorityAttributes;

    @Schema(description = "RA profile attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "raProfileAttributes is required (may be empty list, but must be present)")
    private List<RequestAttribute> raProfileAttributes;
}
