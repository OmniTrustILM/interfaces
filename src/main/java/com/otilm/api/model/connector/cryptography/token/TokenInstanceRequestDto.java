package com.otilm.api.model.connector.cryptography.token;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TokenInstanceRequestDto {

    @Schema(description = "Token instance name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Kind of Token instance",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "List of Token instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> attributes;

}
