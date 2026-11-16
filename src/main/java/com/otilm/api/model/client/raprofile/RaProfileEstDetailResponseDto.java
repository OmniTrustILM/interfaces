package com.otilm.api.model.client.raprofile;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RaProfileEstDetailResponseDto extends NameAndUuidDto {

    @Schema(description = "Whether EST is active on the RA Profile", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean estAvailable;

    @Schema(description = "Label EST clients use to reach the RA Profile", examples = {"devices-ra"})
    private String label;

    @Schema(description = "EST URL of the RA Profile",
            examples = {"https://your-domain.com/.well-known/est/devices-ra"})
    private String estUrl;

    @Schema(description = "List of Attributes to issue Certificate")
    private List<ResponseAttribute> issueCertificateAttributes;
}
