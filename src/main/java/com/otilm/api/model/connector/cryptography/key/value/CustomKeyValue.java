package com.otilm.api.model.connector.cryptography.key.value;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.HashMap;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class CustomKeyValue extends KeyValue {

    @Schema(
            description = "Custom values associated with the Key. It can be anything specific to the implementation," +
                    "for example external ID, custom handlers, etc. Represented as a map of key-value pairs.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private HashMap<String, String> values;

}
