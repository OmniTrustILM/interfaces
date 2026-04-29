package com.otilm.api.model.connector.authority;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
public class AuthorityProviderInstanceDto extends NameAndUuidDto {

    @Schema(description = "List of Authority instance Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<BaseAttribute> attributes;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("attributes", attributes)
                .toString();
    }
}
