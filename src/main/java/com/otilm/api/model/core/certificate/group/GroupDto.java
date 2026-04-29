package com.otilm.api.model.core.certificate.group;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.common.NameAndUuidDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
public class GroupDto extends NameAndUuidDto {

    @Schema(description = "Description of the Group")
    private String description;

    @Schema(description = "Group contact email")
    private String email;

    @Schema(description = "List of Custom Attributes")
    private List<ResponseAttribute> customAttributes;


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("description", description)
                .append("email", email)
                .append("customAttributes", customAttributes)
                .toString();
    }
}
