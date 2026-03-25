package com.czertainly.api.model.core.signing.signatureprofile.scheme;

import com.czertainly.api.model.client.attribute.ResponseAttribute;
import com.czertainly.api.model.common.NameAndUuidDto;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonDeserialize(using = JsonDeserializer.None.class)
@Schema(name = "OneTimeKeyManagedSigningDto",
        description = "Managed signing using a freshly issued one-time certificate and key pair")
public class OneTimeKeyManagedSigningDto extends ManagedSigningDto {

    @NotNull
    @Schema(description = "RA Profile used to issue the one-time signing certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto raProfile;

    @NotNull
    @Schema(description = "CSR Template used for the certificate issuance request",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto csrTemplate;

    @NotNull
    @Schema(description = "Token Profile used to store and manage the issued certificate and key pair",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private NameAndUuidDto tokenProfile;

    @NotNull
    @Schema(description = "List of attributes required for signing operations (such as digest algorithm), provided by the Cryptography Provider Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> attributes = new ArrayList<>();

    public OneTimeKeyManagedSigningDto() {
        super(ManagedSigningType.ONE_TIME_KEY);
    }
}
