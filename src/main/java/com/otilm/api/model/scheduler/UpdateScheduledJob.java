package com.otilm.api.model.scheduler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UpdateScheduledJob {

    @Schema(description = "Cron expression for job schedule")
    private String cronExpression;
}
