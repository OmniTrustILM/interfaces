package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "DelegatedSigningRequestDto", description = "Request to configure delegated signing with Connector and attributes")
public class DelegatedSigningRequestDto extends SigningSchemeRequestDto {

    @NotNull
    @Schema(description = "UUID of the Connector to use for delegated signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID connectorUuid;

    @Schema(description = "List of attributes for the Connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> attributes = new ArrayList<>();

    public DelegatedSigningRequestDto() {
        super(SigningScheme.DELEGATED);
    }
}
