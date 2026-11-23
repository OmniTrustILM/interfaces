package com.otilm.api.model.connector.cryptography.key;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.enums.cryptography.KeyAlgorithm;
import com.otilm.api.model.common.enums.cryptography.KeyFormat;
import com.otilm.api.model.common.enums.cryptography.KeyType;
import com.otilm.api.model.connector.cryptography.key.value.CustomKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.EprkiKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.KeyValue;
import com.otilm.api.model.connector.cryptography.key.value.PrkiKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.RawKeyValue;
import com.otilm.api.model.connector.cryptography.key.value.SpkiKeyValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class KeyData {

    @Schema(description = "Type of the Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyType type;

    @Schema(description = "Cryptographic algorithm of the Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyAlgorithm algorithm;

    @Schema(description = "Format of the key, which selects the shape of `value`", implementation = KeyFormatV1.class,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private KeyFormat format;

    @Schema(description = "Value of the Key", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "format")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = RawKeyValue.class, name = "Raw"),
            @JsonSubTypes.Type(value = SpkiKeyValue.class, name = "SubjectPublicKeyInfo"),
            @JsonSubTypes.Type(value = PrkiKeyValue.class, name = "PrivateKeyInfo"),
            @JsonSubTypes.Type(value = EprkiKeyValue.class, name = "EncryptedPrivateKeyInfo"),
            @JsonSubTypes.Type(value = CustomKeyValue.class, name = "Custom")})
    private KeyValue value;

    @Schema(description = "Bit length of the Key", requiredMode = Schema.RequiredMode.REQUIRED)
    private int length;

    @Schema(description = "Metadata for the Key, specific data that can be technology specific")
    private List<MetadataAttribute> metadata;

    public void setFormat(KeyFormat format) {
        if (format != null && !KeyFormatV1.covers(format)) {
            throw new IllegalArgumentException("v1 key data has no value in format " + format.getCode());
        }
        this.format = format;
    }

    /** The formats v1 key data has values for, published as the v1 contract's {@code KeyFormat}. */
    @Schema(name = "KeyFormat", enumAsRef = true)
    enum KeyFormatV1 {
        RAW(KeyFormat.RAW),
        SPKI(KeyFormat.SPKI),
        PRKI(KeyFormat.PRKI),
        EPRKI(KeyFormat.EPRKI),
        CUSTOM(KeyFormat.CUSTOM);

        private final KeyFormat format;

        KeyFormatV1(KeyFormat format) {
            this.format = format;
        }

        @JsonValue
        String getCode() {
            return format.getCode();
        }

        static boolean covers(KeyFormat format) {
            return Arrays.stream(values()).anyMatch(v1Format -> v1Format.format == format);
        }
    }
}
