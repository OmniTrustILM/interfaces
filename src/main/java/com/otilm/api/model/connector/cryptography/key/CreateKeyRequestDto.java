package com.otilm.api.model.connector.cryptography.key;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateKeyRequestDto {

    @Schema(description = "List of Token Profile Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> tokenProfileAttributes;

    @Schema(description = "List of Attributes to create a Key",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> createKeyAttributes;

}
