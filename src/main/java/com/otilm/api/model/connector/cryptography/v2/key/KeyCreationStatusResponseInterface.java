package com.otilm.api.model.connector.cryptography.v2.key;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.common.v2.OperationStatus;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;

/** OpenAPI schema for the polymorphic {@link KeyCreationStatusResponseV2Dto} hierarchy. */
@Schema(name = "KeyCreationStatusResponse",
        description = "Status of an asynchronous key-creation operation selected by key request type", type = "object",
        discriminatorProperty = "keyRequestType",
        discriminatorMapping = {
                @DiscriminatorMapping(value = KeyRequestType.Codes.SECRET,
                        schema = SecretKeyOperationStatusResponseV2Dto.class),
                @DiscriminatorMapping(value = KeyRequestType.Codes.KEY_PAIR,
                        schema = KeyPairOperationStatusResponseV2Dto.class)},
        oneOf = {SecretKeyOperationStatusResponseV2Dto.class, KeyPairOperationStatusResponseV2Dto.class})
@JsonPropertyOrder({"status", "reason", "result", "keyRequestType"})
public interface KeyCreationStatusResponseInterface extends Serializable {

    OperationStatus getStatus();

    String getReason();

    KeyCreationResponseV2Dto getResult();

    KeyRequestType getKeyRequestType();
}
