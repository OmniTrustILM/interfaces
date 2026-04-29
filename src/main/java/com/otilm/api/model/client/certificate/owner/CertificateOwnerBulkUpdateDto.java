package com.otilm.api.model.client.certificate.owner;

import com.otilm.api.model.client.certificate.SearchFilterRequestDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class CertificateOwnerBulkUpdateDto {
	
	@Schema(
            description = "Owner of the Certificates",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
	private String owner;
	
	@Schema(
			description = "List of Certificate UUIDs"
    )
	private List<String> certificateUuids;

	@Schema(
			description = "Certificate filter input"
	)
	private List<SearchFilterRequestDto> filters;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
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
