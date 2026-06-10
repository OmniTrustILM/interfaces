package com.otilm.api.model.connector.cryptography.operations;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RandomDataRequestDto {

    @Schema(
            description = "Number of random bytes to generate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private int length;

    @Schema(
            description = "Random generator Attributes"
    )
    private List<RequestAttribute>attributes;

}
