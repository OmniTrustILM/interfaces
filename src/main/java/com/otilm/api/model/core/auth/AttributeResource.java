package com.otilm.api.model.core.auth;

import com.otilm.api.model.common.attribute.v3.content.data.ResourceCertificateContentData;
import com.otilm.api.model.common.attribute.v3.content.data.ResourceSecretContentData;
import com.otilm.api.model.common.attribute.v3.content.data.ResourceSimpleContentData;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(enumAsRef = true)
public enum AttributeResource implements IPlatformEnum {

    CERTIFICATE(Resource.Codes.CERTIFICATE, "Certificate", ResourceCertificateContentData.class),
    CREDENTIAL(Resource.Codes.CREDENTIAL, "Credential", ResourceSimpleContentData.class),
    AUTHORITY(Resource.Codes.AUTHORITY, "Authority", ResourceSimpleContentData.class),
    ENTITY(Resource.Codes.ENTITY, "Entity Instance", ResourceSimpleContentData.class),
    LOCATION(Resource.Codes.LOCATION, "Location", ResourceSimpleContentData.class),
    SECRET(Resource.Codes.SECRET, "Secret", ResourceSecretContentData.class),
    ;

    private final String code;
    private final String label;
    @Getter
    private final Class<?> contentClass;

    AttributeResource(String code, String label, Class<?> contentClass) {
        this.code = code;
        this.label = label;
        this.contentClass = contentClass;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getDescription() {
        return null;
    }

}
