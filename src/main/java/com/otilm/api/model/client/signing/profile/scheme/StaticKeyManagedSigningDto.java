package com.otilm.api.model.client.signing.profile.scheme;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.certificate.CertificateSimpleDto;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
@JsonDeserialize(using = JsonDeserializer.None.class)
@Schema(name = "StaticKeyManagedSigningDto", description = "Managed signing using a pre-existing static certificate and associated key(s)")
@ToString(callSuper = true)
public class StaticKeyManagedSigningDto extends ManagedSigningDto {

    @Schema(description = "Certificate and the associated key(s) used for signing", requiredMode = Schema.RequiredMode.REQUIRED)
    private CertificateSimpleDto certificate;

    @Schema(description = "List of attributes required for signing operations (such as digest algorithm), provided by the Cryptography Provider Connector",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> signingOperationAttributes = new ArrayList<>();

    public StaticKeyManagedSigningDto() {
        super(ManagedSigningType.STATIC_KEY);
    }
}
