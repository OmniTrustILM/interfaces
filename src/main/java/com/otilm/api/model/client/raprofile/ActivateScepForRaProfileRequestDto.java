package com.otilm.api.model.client.raprofile;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ActivateScepForRaProfileRequestDto {
    @Schema(description = "List of Attributes to issue Certificate",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<RequestAttribute> issueCertificateAttributes;
}
