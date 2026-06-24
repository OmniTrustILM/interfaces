package com.otilm.api.model.client.connector.v2.attribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

import java.util.List;
import java.util.UUID;

/**
 * One link in a callback's route scope chain — the credential-expanded attributes of a parent scope
 * object (e.g. the authority an RA-profile form lives under). Core injects these from its own database;
 * the connector never influences which scopes arrive.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScopedAttributes {

    @Schema(
            description = "Kind of the scope object. Serialized as the plural resource code, e.g. \"authorities\", \"raProfiles\", \"vaults\".",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Resource scope;

    @Schema(
            description = "UUID of the scope object, for correlation/logging only — never a lookup key on the connector side.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private UUID objectUuid;

    @Schema(
            description = "Credential-expanded attributes of the scope object.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Exclude
    @NotNull
    @Valid
    private List<RequestAttribute> attributes;
}
