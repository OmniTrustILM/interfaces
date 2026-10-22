package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A key the token holds, as a listing or a resolution returns it.
 */
@Getter
@Setter
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "keyRequestType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ListedSecretKeyV2Dto.class, name = KeyRequestType.Codes.SECRET),
        @JsonSubTypes.Type(value = ListedKeyPairV2Dto.class, name = KeyRequestType.Codes.KEY_PAIR)})
@Schema(implementation = ListedKeyV2Dto.OpenApiView.class)
public abstract sealed class ListedKeyV2Dto permits ListedSecretKeyV2Dto, ListedKeyPairV2Dto {

    @Schema(description = "Label the token keeps for the key", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String label;

    public abstract KeyRequestType getKeyRequestType();

    /**
     * The metadata the key resolution takes back as the key's handle.
     */
    public abstract List<MetadataAttribute> handle();

    @Schema(name = "ListedKeyV2", description = "A key the token holds, selected by key request type", type = "object",
            discriminatorProperty = "keyRequestType",
            discriminatorMapping = {
                    @DiscriminatorMapping(value = KeyRequestType.Codes.SECRET, schema = ListedSecretKeyV2Dto.class),
                    @DiscriminatorMapping(value = KeyRequestType.Codes.KEY_PAIR, schema = ListedKeyPairV2Dto.class)},
            oneOf = {ListedSecretKeyV2Dto.class, ListedKeyPairV2Dto.class})
    @JsonPropertyOrder({"keyRequestType", "label"})
    interface OpenApiView {

        KeyRequestType getKeyRequestType();

        String getLabel();
    }
}
