package com.otilm.api.model.core.workflows;

import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.core.auth.Resource;
import com.otilm.api.model.core.other.ResourceEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class TriggerDto extends NameAndUuidDto {

    @Schema(
            description = "Description of the trigger"
    )
    private String description;

    @Schema(
            description = "Type of the trigger",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private TriggerType type;

    @Schema(
            description = "Resource associated with the trigger",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Resource resource;

    @Schema(
            description = "Flag if to ignore object when trigger rules are matched and do not perform any actions and stop evaluating other triggers. Based on context could have other implications to object processing. If ignore is set, trigger does not have any actions.",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private boolean ignoreTrigger;

    @Schema(
            description = "Event that trigger can be assigned to"
    )
    private ResourceEvent event;

}
