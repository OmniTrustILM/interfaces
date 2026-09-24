package com.otilm.api.model.connector.cryptography.v2.key;

import com.otilm.api.model.client.cryptography.key.KeyRequestType;
import com.otilm.api.model.connector.cryptography.v2.TokenProfileScopedRequestV2Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** Request for the creation-attribute schema of a specific key request type. */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(name = "CreateKeyAttributesRequestV2Dto", additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
public class CreateKeyAttributesRequestV2Dto extends TokenProfileScopedRequestV2Dto {

    @Schema(description = "Type of key whose creation attributes are requested",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "keyRequestType is required")
    private KeyRequestType keyRequestType;

    public static CreateKeyAttributesRequestV2Dto fromTokenProfileScopedRequest(
            TokenProfileScopedRequestV2Dto requestDto, KeyRequestType keyRequestType) {
        CreateKeyAttributesRequestV2Dto dto = new CreateKeyAttributesRequestV2Dto();
        dto.setTokenAttributes(requestDto.getTokenAttributes());
        dto.setTokenProfileAttributes(requestDto.getTokenProfileAttributes());
        dto.setKeyRequestType(keyRequestType);

        return dto;
    }
}
