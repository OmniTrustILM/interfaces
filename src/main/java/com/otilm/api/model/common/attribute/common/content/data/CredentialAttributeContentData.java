package com.otilm.api.model.common.attribute.common.content.data;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class CredentialAttributeContentData extends NameAndUuidDto {

    @Schema(description = "Credential Kind",
            examples = {"SoftKeyStore, Basic, ApiKey, etc"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String kind;

    @Schema(description = "List of Credential Attributes",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<DataAttributeV2> attributes;

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("uuid", uuid)
                .append("name", name)
                .append("kind", kind)
                .append("attributes", attributes)
                .toString();
    }
}
