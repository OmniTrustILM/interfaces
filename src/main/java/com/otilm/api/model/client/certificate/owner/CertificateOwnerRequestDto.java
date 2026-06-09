package com.otilm.api.model.client.certificate.owner;

import io.swagger.v3.oas.annotations.media.Schema;

public class CertificateOwnerRequestDto {
	
	@Schema(
            description = "Owner of the certificate",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
	private String owner;

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
}
