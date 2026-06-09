package com.otilm.api.model.core.secret;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class SyncVaultProfileDto extends NameAndUuidDto {

        @Schema(description = "Attributes for creating the secret in the vault")
    private List<ResponseAttribute> secretAttributes;
}
