package com.otilm.api.model.common;

import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class NameAndUuidDto implements Serializable, Loggable {

    @Schema(description = "Object identifier",
            examples = {"7b55ge1c-844f-11dc-a8a3-0242ac120002"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected String uuid;

    @Schema(description = "Object Name",
            examples = {"Name"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    protected String name;

    public NameAndUuidDto() {
        super();
    }

    public NameAndUuidDto(String uuid, String name) {
        super();
        this.uuid = uuid;
        this.name = name;
    }

    public NameAndUuidDto(UUID uuid, String name) {
        this(uuid.toString(), name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .toString();
    }

    @Override
    public Serializable toLogData() {
        return new NameAndUuidDto(uuid, name);
    }

    @Override
    public List<String> toLogResourceObjectsNames() {
        return List.of(this.name);
    }

    @Override
    public List<UUID> toLogResourceObjectsUuids() {
        return this.uuid == null ? List.of() : List.of(UUID.fromString(this.uuid));
    }
}
