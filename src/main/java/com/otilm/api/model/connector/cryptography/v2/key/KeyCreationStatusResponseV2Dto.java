package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Polymorphic status response for an asynchronous key-creation operation. */
@Getter
@Setter
@ToString(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "keyRequestType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SecretKeyOperationStatusResponseV2Dto.class, name = KeyRequestType.Codes.SECRET),
        @JsonSubTypes.Type(value = KeyPairOperationStatusResponseV2Dto.class, name = KeyRequestType.Codes.KEY_PAIR)})
@Schema(implementation = KeyCreationStatusResponseInterface.class)
// Keeps the JSON in Jackson's default order. The order KeyCreationStatusResponseInterface declares is for its schema.
@JsonPropertyOrder
public abstract sealed class KeyCreationStatusResponseV2Dto extends KeyOperationStatusResponseV2Dto
        implements
            KeyCreationStatusResponseInterface
        permits SecretKeyOperationStatusResponseV2Dto, KeyPairOperationStatusResponseV2Dto {
}
