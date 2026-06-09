package com.otilm.api.model.client.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserIdentificationRequestDto {
    @Schema(description = "Base64 Content of the certificate")
    private String certificateContent;

    @Schema(description = "Authentication Token")
    private String authenticationToken;
}
