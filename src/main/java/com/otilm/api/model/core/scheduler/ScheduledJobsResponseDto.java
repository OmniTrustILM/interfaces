package com.otilm.api.model.core.scheduler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ScheduledJobsResponseDto extends PaginationResponseDto {

    @Schema(description = "Scheduled jobs", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ScheduledJobDto> scheduledJobs;

}
