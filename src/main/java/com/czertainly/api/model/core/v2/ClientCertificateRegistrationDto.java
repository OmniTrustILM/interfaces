package com.czertainly.api.model.core.v2;

import com.czertainly.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Request body for the {@code POST /certificates/register} operator endpoint.
 *
 * <p>Reserves a slot or identity at the CA before any CSR exists. The CA returns a
 * tracking handle (carried in the response metadata) that is later supplied to the
 * standard issue flow to complete certificate issuance.</p>
 *
 * <p>Only valid against v3 authorities that advertise the
 * {@code CERTIFICATE_REGISTRATION} feature flag.</p>
 */
@Data
@Schema(
        name = "ClientCertificateRegistrationRequest",
        description = "Pre-registration request for a v3 authority. Reserves a slot/identity at the CA "
                + "before any CSR exists; the returned tracking handle is later used to complete issuance."
)
public class ClientCertificateRegistrationDto {

    @Schema(
            description = "Authority-defined registration attributes (e.g., subject DN template, validity hints)."
    )
    private List<RequestAttribute> attributes;

    @Schema(
            description = "RA-profile-scoped override attributes for the registration."
    )
    private List<RequestAttribute> raProfileAttributes;

    @Schema(
            description = "List of Custom Attributes"
    )
    private List<RequestAttribute> customAttributes;

}
