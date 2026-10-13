package com.otilm.api.interfaces.connector.discovery.v2;

import com.otilm.api.interfaces.connector.common.v2.AuthProtectedConnectorController;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.connector.discovery.v2.DiscoverySupportedResourceDto;
import com.otilm.api.model.core.auth.Resource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/v2/discoveryProvider")
@Tag(name = "Discovery Metadata v2",
        description = "Which resource types the connector discovers, and the attributes that configure a run.")
public interface DiscoveryMetadataController extends AuthProtectedConnectorController {

    @Operation(summary = "List supported resources",
            description = "Returns the resource types this connector can discover. Whether a run can be stopped is "
                    + "declared per run, in the initiate response.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Supported resources retrieved"))
    @GetMapping(path = "/resources", produces = MediaType.APPLICATION_JSON_VALUE)
    List<DiscoverySupportedResourceDto> listSupportedResources();

    @Operation(summary = "List run-level attributes",
            description = "Returns the attributes that configure a run as a whole, such as target addresses. Their "
                    + "values are replayed as the run-level attributes on every lifecycle call.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Run-level attribute schema retrieved"))
    @GetMapping(path = "/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listRunAttributes();

    /**
     * The {@code resource} path segment binds by {@link Resource} code, not enum name: register
     * {@code com.otilm.api.config.converter.IPlatformEnumConverterFactory}, e.g. via
     * {@code WebMvcConfigurer#addFormatters}.
     */
    @Operation(summary = "List per-resource attributes",
            description = "Returns the attributes that refine discovery of one resource type. Their values are "
                    + "replayed as the per-resource attributes on every lifecycle call.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Per-resource attribute schema retrieved"))
    @GetMapping(path = "/{resource}/attributes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<BaseAttribute> listResourceAttributes(@Parameter(
            description = "Resource code, e.g. \"certificates\" or \"keys\"") @PathVariable Resource resource);
}
