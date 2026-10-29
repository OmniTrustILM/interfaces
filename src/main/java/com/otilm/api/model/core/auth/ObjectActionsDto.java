package com.otilm.api.model.core.auth;

import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = """
        Actions granted on one specific object, so a client can offer a control on that object to a user who holds \
        the action there but not on the resource as a whole. The authorization service remains the only gate; this \
        is a hint that keeps a control the caller cannot use off the screen.
        """)
public class ObjectActionsDto {

    @Schema(description = "Resource the object belongs to", requiredMode = Schema.RequiredMode.REQUIRED)
    private Resource resource;

    @Schema(description = "UUID of the object the actions are granted on", requiredMode = Schema.RequiredMode.REQUIRED)
    private String objectUuid;

    @Schema(description = "Actions the user is permitted to perform on the object",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResourceAction> actions;

}
