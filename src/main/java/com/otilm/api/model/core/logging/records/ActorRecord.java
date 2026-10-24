package com.otilm.api.model.core.logging.records;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.otilm.api.model.core.logging.enums.ActorType;
import com.otilm.api.model.core.logging.enums.AuthMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;

@Builder
public record ActorRecord(@NotNull ActorType type, @NotNull AuthMethod authMethod, UUID uuid, String name,
        @JsonInclude(JsonInclude.Include.NON_NULL) @Schema(
                description = "The immediate system acting on behalf of the user. Absent for direct operations and existing records.",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED) DelegateRecord delegate)
        implements
            Serializable {

    public ActorRecord(ActorType type, AuthMethod authMethod, UUID uuid, String name) {
        this(type, authMethod, uuid, name, null);
    }
}
