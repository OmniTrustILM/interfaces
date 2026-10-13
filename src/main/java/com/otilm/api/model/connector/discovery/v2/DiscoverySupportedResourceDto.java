package com.otilm.api.model.connector.discovery.v2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * One resource type a discovery connector supports. {@code ignoreUnknown} is required: connectors built against an
 * earlier draft still send the retired {@code capabilities} field, and every mapper must tolerate it.
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiscoverySupportedResourceDto {

    // No description: it would be hoisted onto the shared Resource component
    // (pinned by discoveryDoesNotRewriteThePlatformWideResourceComponent).
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "resource is required")
    private Resource resource;
}
