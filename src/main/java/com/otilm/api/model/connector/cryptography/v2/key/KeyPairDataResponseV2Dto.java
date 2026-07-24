package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Response envelope for {@code POST /v2/cryptographyProvider/keys/pair}.
 *
 * <p>On a sync 200 both key entries are populated, each carrying its own {@code keyMeta} handle, and
 * {@code keyPairMeta} describes their association. On an async 202 both key entries are null and
 * {@code operationMeta} is the tracking handle.</p>
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(name = "KeyPairDataResponseV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public final class KeyPairDataResponseV2Dto {

    @Schema(description = "Data of the public key. Populated on sync 200; null on async 202.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private PublicKeyDataResponseV2Dto publicKeyData;

    @Schema(description = "Data of the private key. Populated on sync 200; null on async 202.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @Valid
    private PrivateKeyDataResponseV2Dto privateKeyData;

    @Schema(description = "Connector-defined metadata for the pair as a whole. On a sync 200 it describes the "
            + "association between the two keys.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MetadataAttributeV2> keyPairMeta;

    @Schema(description = "Connector-defined operation tracking handle. Present on an asynchronous 202 response.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttributeV2> operationMeta;

    @JsonAnySetter
    @Schema(hidden = true)
    public void rejectUnknownProperty(String property, Object ignoredValue) {
        throw new IllegalArgumentException("Unsupported key-pair response property: " + property);
    }
}
