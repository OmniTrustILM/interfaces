package com.otilm.api.model.connector.signatures.formatting;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FormatDtbsResponseDto {

    @Schema(
            description = "Data-to-be-signed bytes (e.g. DER-encoded TSTInfo for TSA)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] dtbs;
}
