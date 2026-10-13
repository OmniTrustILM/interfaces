package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.ToString;

/**
 * Common contract for synchronous or asynchronous key-creation response envelopes.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "keyRequestType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SecretKeyDataResponseV2Dto.class, name = KeyRequestType.Codes.SECRET),
        @JsonSubTypes.Type(value = KeyPairDataResponseV2Dto.class, name = KeyRequestType.Codes.KEY_PAIR)})
@Schema(implementation = KeyCreationResponseInterface.class)
@ToString(callSuper = true)
// Keeps the JSON in Jackson's default order. The order KeyCreationResponseInterface declares is for its schema.
@JsonPropertyOrder
public abstract sealed class KeyCreationResponseV2Dto extends KeyOperationResponseV2Dto
        implements
            KeyCreationResponseInterface
        permits SecretKeyDataResponseV2Dto, KeyPairDataResponseV2Dto {
}
