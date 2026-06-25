package com.otilm.api.model.client.connector.v2.attribute;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Full definition-registry dump returned by {@code GET /v2/attributes}, or the found-only subset for
 * the batch lookup {@code GET /v2/attributes?uuids=…}. One attribute UUID maps to exactly one
 * definition across all attribute types; a GROUP definition is returned as the group itself, never its
 * children.
 *
 * <p><b>Version axes — read carefully (canonical note for this package).</b> Three independent "vN"
 * numbers appear in this feature; do not conflate them:
 * <ul>
 *   <li><b>Common-interface / NG generation</b> — the {@code v2} in this package
 *       ({@code model.client.connector.v2}) and in {@code interfaces.connector.common.v2}: it means
 *       "version 2 of the common <i>connector interface</i>" (the NG generation, alongside Info/Health/
 *       Metrics). This is what "Attributes <b>v2</b> API" refers to. It is NOT attribute schema v2.</li>
 *   <li><b>Attribute schema version</b> — the {@code v2}/{@code v3} on the payloads, e.g.
 *       {@code BaseAttribute} / {@code RequestAttribute} / {@code attribute.v3.content.BaseAttributeContentV3}:
 *       the <i>shape of the attribute content</i>. {@code definitions} here is polymorphic over schema
 *       v2/v3; the callback response {@code content} arm is pinned to schema v3 by design.</li>
 *   <li><b>Functional provider interface version</b> — e.g. authority provider "v3", carried as
 *       {@code AttributeCallbackRequestDto.interfaceVersion}. Yet another axis.</li>
 * </ul>
 * Consequence: an Attributes <b>v2</b> API message legitimately carries attribute schema <b>v3</b>
 * content. That is correct, not a bug.
 */
@Getter
@Setter
@ToString
public class AttributeDefinitionsDto {

    @Schema(
            description = "Connector build version, echoed so Core can detect staleness. A version string, not a content hash.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String connectorVersion;

    @Schema(
            description = "Attribute definitions keyed by connector-global UUID. Polymorphic across attribute schema v2/v3.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private List<BaseAttribute> definitions;
}
