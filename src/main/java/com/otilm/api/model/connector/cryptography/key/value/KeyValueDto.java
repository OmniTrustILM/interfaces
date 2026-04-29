package com.otilm.api.model.connector.cryptography.key.value;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Value of the Key",
        type = "object",
        subTypes = {
                RawKeyValue.class,
                SpkiKeyValue.class,
                PrkiKeyValue.class,
                EprkiKeyValue.class,
                CustomKeyValue.class
        }
)
public interface KeyValueDto {
}
