package com.otilm.api.model.core.cbom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class CbomDetailDto extends CbomDto {

	@Schema(description = "Raw JSON content of CBOM document", requiredMode = Schema.RequiredMode.REQUIRED)
	private Map<String, Object> content;
}
