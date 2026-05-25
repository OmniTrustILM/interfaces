package com.czertainly.api.model.connector.signatures.formatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FormattedResponseDto {

    @Schema(
            description = "Final formatted output bytes (e.g. DER-encoded TimeStampResp for TSA)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] response;

}
