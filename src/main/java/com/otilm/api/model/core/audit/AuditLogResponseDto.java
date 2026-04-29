package com.otilm.api.model.core.audit;

import com.otilm.api.model.core.scheduler.PaginationResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuditLogResponseDto extends PaginationResponseDto {

    @Schema(
            description = "Audit log items",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<AuditLogDto> items;
}
