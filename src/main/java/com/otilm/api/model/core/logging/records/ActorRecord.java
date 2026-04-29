package com.otilm.api.model.core.logging.records;

import com.otilm.api.model.core.logging.enums.ActorType;
import com.otilm.api.model.core.logging.enums.AuthMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;

@Builder
public record ActorRecord(
        @NotNull ActorType type,
        @NotNull AuthMethod authMethod,
        UUID uuid,
        String name
) implements Serializable {
}