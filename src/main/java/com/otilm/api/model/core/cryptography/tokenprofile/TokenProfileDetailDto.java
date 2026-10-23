package com.otilm.api.model.core.cryptography.tokenprofile;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.connector.cryptography.enums.TokenInstanceStatus;
import com.otilm.api.model.core.cryptography.key.KeyTransferCapabilityDto;
import com.otilm.api.model.core.cryptography.key.KeyUsage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class TokenProfileDetailDto extends NameAndUuidDto {
    @Schema(description = "Description of Token Profile")
    private String description;

    @Schema(description = "UUID of Token Instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tokenInstanceUuid;

    @Schema(description = "Name of Token instance", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tokenInstanceName;

    @Schema(description = "List of Token Profile attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;

    @Schema(description = "Token Instance Status", requiredMode = Schema.RequiredMode.REQUIRED)
    private TokenInstanceStatus tokenInstanceStatus;

    @Schema(description = "Enabled flag - true = enabled; false = disabled",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean enabled;

    @Schema(description = "Usages for the Keys assoiated to the profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<KeyUsage> usages;

    @Schema(description = "Whether key material can be imported into or exported from this token profile, and for "
            + "which key types", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KeyTransferCapabilityDto keyTransfer;

    /**
     * The signature this class carried before {@code keyTransfer} was added, so a caller that constructs it
     * positionally still compiles. Key transfer is then unreported rather than reported as unavailable.
     */
    public TokenProfileDetailDto(String description, String tokenInstanceUuid, String tokenInstanceName,
            List<ResponseAttribute> attributes, List<ResponseAttribute> customAttributes,
            TokenInstanceStatus tokenInstanceStatus, Boolean enabled, List<KeyUsage> usages) {
        this(description, tokenInstanceUuid, tokenInstanceName, attributes, customAttributes, tokenInstanceStatus,
                enabled, usages, null);
    }
}
