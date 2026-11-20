package com.otilm.api.model.client.signing.nativesigning;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Schema(name = "SignDocumentResponse", description = "Signing outputs of every request item")
public class SignDocumentResponseDto {

    @ArraySchema(arraySchema = @Schema(description = "One result per request item, in request order",
            requiredMode = Schema.RequiredMode.REQUIRED), minItems = 1)
    private List<SignDocumentResultDto> results;
}
