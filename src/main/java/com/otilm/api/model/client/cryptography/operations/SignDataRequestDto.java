package com.otilm.api.model.client.cryptography.operations;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SignDataRequestDto {

    @Schema(
            description = "List of signature Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>signatureAttributes;

    @Schema(
            description = "Data to be signed",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<SignatureRequestData> data;

}
