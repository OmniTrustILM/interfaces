package com.otilm.api.model.connector.cryptography.key;

import com.otilm.api.model.common.NameAndUuidDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeyDataResponseDto extends NameAndUuidDto {

    @Schema(
            description = "Identification of association with related key data. " +
                    "This may be used for example to associate public and private key, " +
                    "or associated multiple key data of split key parts, etc."
    )
    private String association;

    @Schema(
            description = "Data of the Key",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private KeyData keyData;

}
