package com.otilm.api.model.connector.v3.authority;

import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthorityConnectionResponseDto {

    @Schema(description = "Metadata the connector learned from the upstream CA while validating the authority, stored "
            + "by Core as the authority's metadata. Replaces the metadata of an earlier save. Never carries secrets: "
            + "metadata is shown to anyone who can read the authority.",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<MetadataAttribute> metadata;
}
