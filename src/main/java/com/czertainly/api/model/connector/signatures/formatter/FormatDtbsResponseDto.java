package com.czertainly.api.model.connector.signatures.formatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FormatDtbsResponseDto {

    @Schema(
            description = "Data-to-be-signed bytes (e.g. DER-encoded TSTInfo for TSA)",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private byte[] dtbs;

}
