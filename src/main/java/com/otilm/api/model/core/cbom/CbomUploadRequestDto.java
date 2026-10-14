package com.otilm.api.model.core.cbom;

import com.otilm.api.model.client.attribute.RequestAttribute;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import lombok.Data;

@Data
public class CbomUploadRequestDto {

    @Schema(description = "Raw JSON content of CBOM document", requiredMode = Schema.RequiredMode.REQUIRED)
    private LinkedHashMap<String, Object> content;

    @Schema(description = "Custom attribute content assigned to the uploaded CBOM")
    private List<RequestAttribute> customAttributes = new ArrayList<>();

}
