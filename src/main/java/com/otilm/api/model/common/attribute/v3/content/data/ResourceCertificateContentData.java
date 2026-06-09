package com.otilm.api.model.common.attribute.v3.content.data;

import com.otilm.api.model.core.auth.AttributeResource;
import com.otilm.api.model.core.certificate.CertificateType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(
        title = "ResourceCertificateContentData",
        description = "Content data for resource object attribute containing certificate content"
)
public class ResourceCertificateContentData extends ResourceObjectContentData {

    @Schema(description = "Certificate type", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CertificateType certificateType;

    @Schema(description = "Base64 encoded content of the certificate", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String content;

    public ResourceCertificateContentData() {
        super(AttributeResource.CERTIFICATE);
    }

    public ResourceCertificateContentData(String uuid, String name, CertificateType certificateType, String content) {
        this();
        this.uuid = uuid;
        this.name = name;
        this.certificateType = certificateType;
        this.content = content;
    }

}
