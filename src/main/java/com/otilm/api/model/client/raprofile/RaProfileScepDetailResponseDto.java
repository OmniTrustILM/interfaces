package com.otilm.api.model.client.raprofile;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class RaProfileScepDetailResponseDto extends NameAndUuidDto {

    @Schema(
            description = "SCEP availability flag - true = yes; false = no",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean scepAvailable;

    @Schema(
            description = "SCEP URL"
    )
    private String url;

    @Schema(
            description = "List of Attributes to issue Certificate"
    )

    private List<ResponseAttribute> issueCertificateAttributes;
}
