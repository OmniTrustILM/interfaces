package com.otilm.api.model.core.connector.v2;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Connect Info dependent on connector version",
        type = "object",
        discriminatorProperty = "version",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "v1", schema = ConnectInfoV1.class),
                @DiscriminatorMapping(value = "v2", schema = ConnectInfoV2.class)
        },
        oneOf = {
                ConnectInfoV1.class,
                ConnectInfoV2.class,
        })
public interface ConnectInfoDto {

    @Schema(description = "Version of the Connector.", requiredMode = Schema.RequiredMode.REQUIRED, example = "v2")
    String getVersion();

}
