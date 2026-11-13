package com.otilm.api.model.client.raprofile;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ActivateEstForRaProfileRequestDto {

    @Schema(description = "List of Attributes to issue Certificate")
    private List<RequestAttribute> issueCertificateAttributes = new ArrayList<>();
}
