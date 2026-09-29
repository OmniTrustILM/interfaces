package com.otilm.api.model.client.auth;

import com.otilm.api.model.core.logging.Sensitive;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
public class UserIdentificationRequestDto {
    @Schema(description = "Base64 Content of the certificate")
    private String certificateContent;

    @ToString.Exclude
    @Schema(description = "Authentication Token")
    @Sensitive
    private String authenticationToken;
}
