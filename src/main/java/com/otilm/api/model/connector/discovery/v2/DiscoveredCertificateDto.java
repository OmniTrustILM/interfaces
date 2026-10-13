package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Payload for a discovered {@code certificates} item.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoveredCertificateDto implements DiscoveredItemPayloadDto {

    /**
     * The discriminator, fixed to this subtype. No setter, so a wire value cannot contradict the shape, and no
     * {@code @NotNull}, since the field cannot be null; {@code requiredMode} publishes it as required.
     */
    @Setter(AccessLevel.NONE)
    // No description: swagger-core would hoist it onto the shared Resource component. Not static despite S1170: Lombok
    // would generate a static getter, which cannot implement DiscoveredItemPayloadInterface.getResource().
    @SuppressWarnings("java:S1170")
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private final Resource resource = Resource.CERTIFICATE;

    @Schema(description = "Base64-encoded certificate data (DER)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "certificateData is required")
    private String certificateData;
}
