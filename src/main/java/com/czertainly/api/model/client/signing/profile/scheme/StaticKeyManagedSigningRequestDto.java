package com.czertainly.api.model.client.signing.profile.scheme;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonDeserialize(using = JsonDeserializer.None.class)
@Schema(name = "StaticKeyManagedSigningRequestDto", description = "Request to configure managed signing with a static certificate and associated key(s)")
@ToString(callSuper = true)
public class StaticKeyManagedSigningRequestDto extends ManagedSigningRequestDto {

    @NotNull
    @Schema(description = "UUID of the Certificate and associated key(s) to use for signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID certificateUuid;

    @NotNull
    @Schema(description = "List of attributes required for signing operations (such as digest algorithm), provided by the Cryptography Provider Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> signingOperationAttributes = new ArrayList<>();

    public StaticKeyManagedSigningRequestDto() {
        super(ManagedSigningType.STATIC_KEY);
    }
}
