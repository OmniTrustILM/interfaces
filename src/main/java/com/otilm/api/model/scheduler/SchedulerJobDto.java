package com.otilm.api.model.scheduler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerJobDto {

    private UUID uuidJob;

    private String jobName;

    private String cronExpression;

    private String classNameToBeExecuted;

    public SchedulerJobDto(String jobName, String cronExpression, String classNameToBeExecuted) {
        this.jobName = jobName;
        this.cronExpression = cronExpression;
        this.classNameToBeExecuted = classNameToBeExecuted;
    }

}
