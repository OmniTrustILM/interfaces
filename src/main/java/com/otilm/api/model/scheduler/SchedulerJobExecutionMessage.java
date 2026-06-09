package com.otilm.api.model.scheduler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SchedulerJobExecutionMessage {

    @JsonProperty
    private String jobName;

    @JsonProperty
    private String classToBeExecuted;

    public SchedulerJobExecutionMessage(final String jobName, final String classToBeExecuted) {
        this.jobName = jobName;
        this.classToBeExecuted = classToBeExecuted;
    }

    @Override
    public String toString() {
        return super.getClass().getName()
                + "(jobName=" + jobName
                + ", classToBeExecuted=" + classToBeExecuted
                + ")";
    }
}
