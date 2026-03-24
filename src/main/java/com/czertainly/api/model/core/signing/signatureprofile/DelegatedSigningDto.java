package com.czertainly.api.model.core.signing.signatureprofile;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "DelegatedSigningDto", description = "Delegated signing configuration with Connector and attributes")
public class DelegatedSigningDto extends SigningSchemeDto {

    @NotNull
    @Schema(description = "Reference to the Connector used for delegated signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto connector;

    @Schema(description = "List of attributes provided by the Connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    public DelegatedSigningDto() {
        super(SigningScheme.DELEGATED);
    }
}
