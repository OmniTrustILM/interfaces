package com.czertainly.api.model.connector.v3.authority;

import com.czertainly.api.model.connector.v3.V3AuthorityScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CrlRequestDto extends V3AuthorityScopedRequestDto {

    @Schema(description = "If true, the delta CRL is returned where supported; otherwise the full CRL.",
            defaultValue = "false",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private boolean delta;
}
