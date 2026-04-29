package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class IdAndCertificateIdDto {
	
	@Schema(
            description = "UUID of the Object",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
	private String uuid;
	
	@Schema(
            description = "List of UUIDs of the Certificates in Inventory",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
	private List<String> certificateUuids;

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<String> getCertificateUuids() {
		return certificateUuids;
	}

	public void setCertificateUuids(List<String> certificateUuids) {
		this.certificateUuids = certificateUuids;
	}
}
