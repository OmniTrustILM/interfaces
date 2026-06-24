package com.otilm.api.model.client.connector.v2.attribute;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.connector.v2.ConnectorInterface;
import com.otilm.api.model.core.scheduler.PaginationRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

import java.util.List;
import java.util.UUID;

/**
 * The callback envelope Core dispatches to {@code POST /v2/attributes/callback}. The connector
 * dispatches internally on {@code attributeUuid}. {@code connectorInterface}/{@code interfaceVersion}
 * are Core-stamped context derived from the form, never routing.
 *
 * <p>{@code contextAttributes} and {@code currentAttributes} carry the polymorphic
 * {@code RequestAttribute} (attribute schema v2 OR v3 — independent of this being the Attributes
 * <i>v2</i> API; see the package javadoc note in {@link AttributeDefinitionsDto}). {@code interfaceVersion}
 * is the functional provider interface version (e.g. authority "v3"), again a separate axis.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttributeCallbackRequestDto {

    @Schema(
            description = "Provider interface the triggering schema belongs to. Core-stamped from /v2/info.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private ConnectorInterface connectorInterface;

    @Schema(
            description = "Version of the provider interface. Core-stamped from /v2/info.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String interfaceVersion;

    @Schema(
            description = "Connector-global UUID of the attribute whose callback fired — the dispatch key.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID attributeUuid;

    @Schema(
            description = "Name of the attribute whose callback fired. Informative / logging.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String attributeName;

    @Schema(
            description = "Route scope chain, credentials expanded inline by Core. Empty when the form has no parent scope.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Exclude
    @NotNull
    @Valid
    private List<ScopedAttributes> contextAttributes;

    @Schema(
            description = "The dependsOn-named form values only, reference-typed values expanded inline by Core.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @Exclude
    @NotNull
    @Valid
    private List<RequestAttribute> currentAttributes;

    @Schema(
            description = "Optional pagination for content responses.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private PaginationRequestDto pagination;
}
