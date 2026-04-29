package com.otilm.api.model.connector.discovery;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.core.discovery.DiscoveryStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
public class DiscoveryProviderDto extends NameAndUuidDto {

	@Schema(description = "Status of Discovery",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private DiscoveryStatus status;
	
	@Schema(description = "Number of Certificates discovered",
			defaultValue = "0")
	private Integer totalCertificatesDiscovered;

	@Schema(description = "Certificate data",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private List<DiscoveryProviderCertificateDataDto> certificateData;
	
	@Schema(description = "Certificate Metadata",
			requiredMode = Schema.RequiredMode.REQUIRED)
	private List<MetadataAttribute> meta;


	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", uuid)
				.append("totalCertificatesDiscovered", totalCertificatesDiscovered).toString();
	}
}
