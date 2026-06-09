package com.otilm.api.model.scheduler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SchedulerRequestDto {

    private SchedulerJobDto schedulerJob;

    public SchedulerRequestDto(SchedulerJobDto schedulerDetail) {
        this.schedulerJob = schedulerDetail;
    }
}
