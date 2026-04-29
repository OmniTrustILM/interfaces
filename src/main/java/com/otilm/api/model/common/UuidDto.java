package com.otilm.api.model.common;

import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class UuidDto implements Serializable, Loggable {

    @Schema(description = "Object identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    public UuidDto() {
    }

    public UuidDto(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public Serializable toLogData() {
        return new UuidDto(uuid);
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of();
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of(UUID.fromString(uuid));
    }


}
