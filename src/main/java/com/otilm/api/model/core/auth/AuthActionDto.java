package com.otilm.api.model.core.auth;

import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Setter
@Getter
public class AuthActionDto extends NameAndUuidDto {

    @Schema(description = "Resource label", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayName;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("displayName", displayName)
                .toString();
    }
}
