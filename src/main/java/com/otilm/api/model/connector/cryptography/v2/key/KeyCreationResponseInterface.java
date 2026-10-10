package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

/**
 * OpenAPI schema for the polymorphic {@link KeyCreationResponseV2Dto} hierarchy.
 */
@Schema(name = "KeyCreationResponse", description = "Key-creation response selected by key request type",
        type = "object", discriminatorProperty = "keyRequestType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = KeyRequestType.Codes.SECRET, schema = SecretKeyDataResponseV2Dto.class),
                @DiscriminatorMapping(value = KeyRequestType.Codes.KEY_PAIR, schema = KeyPairDataResponseV2Dto.class)},
        oneOf = {SecretKeyDataResponseV2Dto.class, KeyPairDataResponseV2Dto.class})
@JsonPropertyOrder({"operationMeta", "keyRequestType"})
public interface KeyCreationResponseInterface extends Serializable {

    List<MetadataAttribute> getOperationMeta();

    KeyRequestType getKeyRequestType();
}
