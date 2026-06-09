package com.otilm.api.model.client.signing.profile.scheme;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "DelegatedSigningDto", description = "Delegated signing configuration with Connector and attributes")
@ToString(callSuper = true)
public class DelegatedSigningDto extends SigningSchemeDto {

    @Schema(description = "Reference to the Connector used for delegated signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto connector;

    @Schema(description = "List of attributes provided by the Connector", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    public DelegatedSigningDto() {
        super(SigningScheme.DELEGATED);
    }
}
