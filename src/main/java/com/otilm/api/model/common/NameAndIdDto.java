package com.otilm.api.model.common;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class NameAndIdDto implements Serializable {

    @Schema(description = "Object identifier",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private int id;

    @Schema(description = "Object name",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    public NameAndIdDto() {
    }

    public NameAndIdDto(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("name", name)
                .toString();
    }
}
