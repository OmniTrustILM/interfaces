package com.otilm.api.model.core.cbom;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CbomDetailDto extends CbomDto {

    @Schema(description = "Raw JSON content of CBOM document", requiredMode = Schema.RequiredMode.REQUIRED)
    private Map<String, Object> content;

    @Schema(description = "Custom attribute content assigned to this CBOM", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResponseAttribute> customAttributes = new ArrayList<>();
}
