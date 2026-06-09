package com.otilm.api.model.connector.authority;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CertificateRevocationListRequestDto {

    @Schema(
        description = "If true, the delta CRL is returned, otherwise the full CRL is returned",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED,
        defaultValue = "false"
    )
    private boolean delta;

    @Schema(
        description = "List of RA Profiles attributes",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>raProfileAttributes;

}
