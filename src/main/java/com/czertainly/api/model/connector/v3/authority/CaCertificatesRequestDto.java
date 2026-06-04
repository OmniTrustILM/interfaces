package com.czertainly.api.model.connector.v3.authority;

import com.czertainly.api.model.connector.v3.V3AuthorityScopedRequestDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CaCertificatesRequestDto extends V3AuthorityScopedRequestDto {
}
