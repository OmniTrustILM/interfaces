package com.otilm.api.model.connector.cryptography.key.value;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class PrkiKeyValue extends KeyValue {

    @Schema(description = "Base64 ASN.1 encoded PrivateKeyInfo", requiredMode = Schema.RequiredMode.REQUIRED)
    @ToString.Exclude
    private String value;

}
