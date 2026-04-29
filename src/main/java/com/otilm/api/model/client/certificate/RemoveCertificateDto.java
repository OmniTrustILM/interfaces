package com.otilm.api.model.client.certificate;

import com.otilm.api.model.core.logging.Loggable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RemoveCertificateDto implements Loggable {
	
	@Schema(
			description = "List of Certificate UUIDs"
    )
	private List<String> uuids;

	@Schema(
			description = "Certificate filter input"
	)
	private List<SearchFilterRequestDto> filters;


	@Override
	public Serializable toLogData() {
		return null;
	}

	@Override
	public List<String> toLogResourceObjectsNames() {
		return List.of();
	}

	@Override
	public List<UUID> toLogResourceObjectsUuids() {
		return uuids.stream().map(UUID::fromString).toList();
	}
}
