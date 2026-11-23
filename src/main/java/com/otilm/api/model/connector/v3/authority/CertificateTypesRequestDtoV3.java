package com.otilm.api.model.connector.v3.authority;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Asks which certificate types an authority issues, or which it issues through one RA profile when the attributes of
 * that RA profile are given.
 */
@Getter
@Setter
@ToString
public class CertificateTypesRequestDtoV3 {

    @Schema(description = "Authority attributes", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "authorityAttributes is required (may be empty list, but must be present)")
    @ToString.Exclude
    private List<RequestAttribute> authorityAttributes;

    @Schema(description = "RA profile attributes. Given when the types of one RA profile are asked for; absent when "
            + "the types of the authority as a whole are asked for", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ToString.Exclude
    private List<RequestAttribute> raProfileAttributes;
}
