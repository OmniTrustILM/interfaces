package com.otilm.api.model.core.cbom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CbomDto {
	@Schema(description = "UUID of a CBOM record", requiredMode = Schema.RequiredMode.REQUIRED)
	private UUID uuid;

	@Schema(description = "Creation timestamp of CBOM database record", requiredMode = Schema.RequiredMode.REQUIRED)
	private OffsetDateTime createdAt;

	@Schema(description = "CBOM serial number (URN)", requiredMode = Schema.RequiredMode.REQUIRED)
	private String serialNumber;

	@Schema(description = "CBOM version", requiredMode = Schema.RequiredMode.REQUIRED)
	private int version;

	@Schema(description = "CBOM spec version", requiredMode = Schema.RequiredMode.REQUIRED)
	private String specVersion;

	@Schema(description = "A timestamp from CBOM metadata", requiredMode = Schema.RequiredMode.REQUIRED)
	private OffsetDateTime timestamp;

	@Schema(description = "CBOM source (e.g.: CBOM-Lens)", requiredMode = Schema.RequiredMode.REQUIRED)
	private String source;

	@Schema(description = "Number of algorithms", requiredMode = Schema.RequiredMode.REQUIRED)
	private int algorithms;

	@Schema(description = "Number of certificates", requiredMode = Schema.RequiredMode.REQUIRED)
	private int certificates;

	@Schema(description = "Number of protocols", requiredMode = Schema.RequiredMode.REQUIRED)
	private int protocols;

	@Schema(description = "Number of crypto material items", requiredMode = Schema.RequiredMode.REQUIRED)
	private int cryptoMaterial;

	@Schema(description = "Total number of assets", requiredMode = Schema.RequiredMode.REQUIRED)
	private int totalAssets;
}
