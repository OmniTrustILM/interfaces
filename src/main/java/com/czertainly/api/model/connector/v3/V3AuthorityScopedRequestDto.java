package com.czertainly.api.model.connector.v3;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
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
public abstract class V3AuthorityScopedRequestDto {

    @Schema(description = "Authority attribute blob — full set of values Core stored when the authority was created",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> authorityAttributes;

    @Schema(description = "RA profile attribute blob — full set of values Core stored when the RA profile was created",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> raProfileAttributes;
}
