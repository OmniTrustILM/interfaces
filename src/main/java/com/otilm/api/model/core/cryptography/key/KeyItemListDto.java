package com.otilm.api.model.core.cryptography.key;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Key item as the key listing returns it. */
@Getter
@Setter
@ToString(callSuper = true)
public class KeyItemListDto extends KeyItemDto {

    @Schema(description = "Whether the key this item belongs to meets the conditions for removal from the inventory",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean removableFromInventory;

}
