package com.otilm.api.model.connector.secrets;

import com.otilm.api.model.connector.secrets.content.SecretContent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class SecretContentResponseDto {

    @Schema(description = "Secret version, if versioning is supported", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String version;

    @Schema(description = "Secret Content", requiredMode = Schema.RequiredMode.REQUIRED)
    private SecretContent content;
}
