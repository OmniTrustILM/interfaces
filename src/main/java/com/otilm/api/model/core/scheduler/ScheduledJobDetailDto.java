package com.otilm.api.model.core.scheduler;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class ScheduledJobDetailDto extends ScheduledJobDto {

    private UUID userUuid;

    private Object objectData;

}
