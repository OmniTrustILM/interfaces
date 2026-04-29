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
public class CaCertificatesRequestDto {

    @Schema(
        description = "List of RA Profiles attributes",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<RequestAttribute>raProfileAttributes;

}
