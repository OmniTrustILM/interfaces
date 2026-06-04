package com.czertainly.api.model.connector.v3.authority;

import com.czertainly.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CrlRequestDtoV3 extends AuthorityV3ScopedRequestDto {

    @Schema(description = "If true, the delta CRL is returned where supported; otherwise the full CRL.",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private boolean delta;
}
