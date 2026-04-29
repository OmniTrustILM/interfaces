package com.otilm.api.model.common.events.data;

import com.otilm.api.model.core.compliance.v2.ComplianceCheckResultDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateNotCompliantEventData extends CertificateEventData {

    @Schema(description = "Result of compliance check", requiredMode = Schema.RequiredMode.REQUIRED)
    private ComplianceCheckResultDto complianceCheckResultDto;

}
