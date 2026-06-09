package com.otilm.api.model.core.oid;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(
        description = "Additional properties of custom OID entry",
        type = "object",
        subTypes = {
                RdnAttributeTypeOidPropertiesDto.class
        }
)
public interface AdditionalOidPropertiesDto extends Serializable {
}
