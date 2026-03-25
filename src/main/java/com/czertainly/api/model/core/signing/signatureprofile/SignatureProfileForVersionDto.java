package com.czertainly.api.model.core.signing.signatureprofile;

import lombok.Data;
import org.springdoc.core.annotations.ParameterObject;

@Data
@ParameterObject
public class SignatureProfileForVersionDto {
    private Integer version;
}
