package com.otilm.api.model.core.auth;

import com.otilm.core.model.auth.ResourceAction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Actions the caller may perform on one object")
public class ObjectAllowedActionsDto {

    @Schema(description = "UUID of the object", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID uuid;

    @Schema(description = """
            Actions the caller may perform on this object. Each action is answered the way the endpoint that \
            performs it is authorized: on the object itself, where ownership, grants on the object and, for list \
            and detail, the members action on a group the object is assigned to count; on the resource as a whole, \
            where only resource-level grants count; or on a parent object first, such as the authority of an RA \
            profile. A reported action is one its endpoint accepts for the caller, and an action that is left out \
            is one it refuses. Actions that do not act on an existing object, such as create and list, and actions \
            the authorization service will not grant at all are never reported. The authorization service remains \
            the only gate; this is a hint that keeps a control the caller cannot use off the screen.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResourceAction> allowedActions;

}
