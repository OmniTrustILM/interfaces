package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class ObjectPermissionsDto {

    @Schema(description = "UUID of the Object", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuid;

    @Schema(description = "Name of the Object", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Allowed Action list", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> allow;

    @Schema(description = "Denied Action list", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> deny;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public List<String> getAllow() {
        return allow;
    }

    public void setAllow(List<String> allow) {
        this.allow = allow;
    }

    public List<String> getDeny() {
        return deny;
    }

    public void setDeny(List<String> deny) {
        this.deny = deny;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("allow", allow)
                .append("deny", deny)
                .toString();
    }
}
