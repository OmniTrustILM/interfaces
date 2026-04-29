package com.otilm.api.model.core.cryptography.key;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class KeyAssociationDto extends NameAndUuidDto {

    @Schema(
            description = "Type of the resource",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Resource resource;

}
