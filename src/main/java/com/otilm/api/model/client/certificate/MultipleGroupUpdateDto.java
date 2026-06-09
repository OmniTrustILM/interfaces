package com.otilm.api.model.client.certificate;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class MultipleGroupUpdateDto {
	
	@Schema(
            description = "UUID of the Group",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
	private String uuid;
	
	@Schema(
			description = "List of Certificate UUIDs"
    )
	private List<String> certificateUuids;

	@Schema(
			description = "Certificate filter input"
	)
	private List<SearchFilterRequestDto> filters;

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

	public List<SearchFilterRequestDto> getFilters() {
		return filters;
	}

	public void setFilters(List<SearchFilterRequestDto> filters) {
		this.filters = filters;
	}
}
