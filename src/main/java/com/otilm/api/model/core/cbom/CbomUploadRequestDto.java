package com.otilm.api.model.core.cbom;

import java.util.LinkedHashMap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CbomUploadRequestDto {

	@Schema(description = "Raw JSON content of CBOM document", requiredMode = Schema.RequiredMode.REQUIRED)
	private LinkedHashMap<String, Object> content;

}
