package com.czertainly.api.model.core.proxy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProxyInstallInstructionsDto {

    @Schema(description = "Proxy identifier",
        examples = {"7b55be1c-844f-11dc-a8a3-0242ac120002"},
        requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Installation instructions for the Proxy",
        examples = {"helm install my-proxy ./czertainly-proxy --set proxy.code=my-proxy-123"},
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String installationInstructions;

}
