package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateSearchRequestDto extends SearchRequestDto {

    @Schema(description = "Include archived certificates")
    private boolean includeArchived;
}
