package com.otilm.api.model.core.settings.logging;

import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.logging.enums.Module;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class ResourceLoggingSettingsDto implements Serializable {

    @NotNull
    @Schema(description = "Collect logs for all modules. If true, logged modules list is not taken into account only ignored modules", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean logAllModules = false;

    @NotNull
    @Schema(description = "Collect logs for all resources. If true, logged resources list is not taken into account only ignored resources", requiredMode = Schema.RequiredMode.REQUIRED)
    private boolean logAllResources = false;

    @Schema(description = "List of modules for which logs are collected.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<Module> loggedModules = new HashSet<>();

    @Schema(description = "List of modules that will be ignored when logging.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<Module> ignoredModules = new HashSet<>();

    @Schema(description = "List of resources for which logs are collected.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<Resource> loggedResources = new HashSet<>();

    @Schema(description = "List of resources that will be ignored when logging.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Set<Resource> ignoredResources = new HashSet<>();
}
