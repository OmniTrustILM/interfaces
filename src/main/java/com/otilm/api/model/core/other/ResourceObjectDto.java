package com.otilm.api.model.core.other;

import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@ToString
public class ResourceObjectDto implements Serializable, Loggable {

    @Schema(description = "Resource of object",
            examples = {Resource.Codes.CERTIFICATE},
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected Resource resource;

    @Schema(description = "Object UUID",
            examples = {"2dcc528b-9e16-46c6-877e-74eae258173f"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected UUID objectUuid;

    @Schema(description = "Object Name",
            examples = {"Name"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected String name;

    public ResourceObjectDto() {
        super();
    }

    public ResourceObjectDto(Resource resource, UUID objectUuid, String name) {
        super();
        this.resource = resource;
        this.objectUuid = objectUuid;
        this.name = name;
    }

    @Override
    public Serializable toLogData() {
        return this;
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of(name);
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return List.of(objectUuid);
    }
}
