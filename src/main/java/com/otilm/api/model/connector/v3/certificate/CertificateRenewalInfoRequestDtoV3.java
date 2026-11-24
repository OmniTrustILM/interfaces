package com.otilm.api.model.connector.v3.certificate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.otilm.api.model.connector.v3.AuthorityV3ScopedRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Body for v3 /renewalInfo: a batch of 1 to 50 certificates issued through one RA profile.
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CertificateRenewalInfoRequestDtoV3 extends AuthorityV3ScopedRequestDto {

    @Schema(description = "Certificates to look up, 1 to 50 per request; references must be unique",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "certificates is required")
    @Size(min = 1, max = 50, message = "certificates must contain 1 to 50 items")
    private List<@NotNull @Valid CertificateRenewalInfoRequestItemDtoV3> certificates;

    @AssertTrue(message = "certificate references must be unique within the request")
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isReferencesUnique() {
        return certificates == null || certificates
                .stream()
                .filter(Objects::nonNull)
                .map(CertificateRenewalInfoRequestItemDtoV3::getReference)
                .distinct()
                .count() == certificates.stream().filter(Objects::nonNull).count();
    }
}
