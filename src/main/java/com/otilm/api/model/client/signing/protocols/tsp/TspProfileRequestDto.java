package com.otilm.api.model.client.signing.protocols.tsp;

import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.common.validation.BasicCredentialsRequiredIfBasicPassword;
import com.otilm.api.model.common.validation.BasicPasswordConstrained;
import com.otilm.api.model.common.validation.ValidName;
import com.otilm.api.model.core.signing.TspAuthenticationMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@BasicCredentialsRequiredIfBasicPassword
@Schema(name = "TspProfileRequestDto", description = "Request to create or update a TSP (Timestamping Protocol) Profile")
public class TspProfileRequestDto implements BasicPasswordConstrained {

    @NotBlank
    @ValidName
    @Schema(description = "Name of the TSP Profile", requiredMode = Schema.RequiredMode.REQUIRED, example = "TSP-Profile-1")
    private String name;

    @Schema(description = "Description of the TSP Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "TSP profile for production timestamping")
    private String description;

    @Schema(description = "UUID of the default Signing Profile", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "6b55de1c-844f-11ec-a8a3-0242ac120002")
    private UUID defaultSigningProfileUuid;

    @Schema(description = "Vault profile that stores this profile's Basic credentials; required when Basic credentials are configured",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "6b55de1c-844f-11ec-a8a3-0242ac120002")
    private UUID vaultProfileUuid;

    @NotEmpty
    @Schema(description = "Authentication methods this TSP Profile accepts on the TSP protocol endpoints. Must be non-empty.",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<TspAuthenticationMethod> allowedAuthenticationMethods = new ArrayList<>();

    @Valid
    @Schema(description = "Basic (username/password) credentials for this profile. Required (>=1) when BASIC_PASSWORD is allowed. "
            + "Reconciled per row by uuid; password is write-only (blank on update keeps the existing secret).",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<TspBasicCredentialRequestDto> basicCredentials = new ArrayList<>();

    @Schema(description = "List of Custom Attributes", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<RequestAttribute> customAttributes = new ArrayList<>();
}
