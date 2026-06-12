package com.otilm.api.model.client.signing.protocols.tsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Schema(name = "TspBasicCredentialRequestDto", description = "A single Basic (username/password) credential on a TSP Profile. "
        + "The password is write-only: provide it to create or rotate the credential; leave it blank on update to keep the existing secret.")
public class TspBasicCredentialRequestDto {

    @NotBlank
    @Schema(description = "Basic username; unique within the TSP profile.",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "svc-account")
    private String username;

    @ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "Basic password (write-only). On create a value is required — there is no existing secret to keep. "
            + "On update, blank keeps the existing secret and a value rotates it.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED, accessMode = Schema.AccessMode.WRITE_ONLY)
    private String password;

    @NotNull
    @Schema(description = "UUID of the real ILM user this credential authenticates as.",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "6b55de1c-844f-11ec-a8a3-0242ac120002")
    private UUID mappedUserUuid;
}
