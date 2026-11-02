package com.otilm.api.model.core.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProfilePermissionsDto {

    @Schema(description = """
            Resources the user may list. A resource is listed when the user holds the list action on it, either on \
            the resource as a whole or on at least one of its objects, and some listings are granted to every user by \
            default.

            A resource without owners that can be assigned to a group is also listed for a user who holds the members \
            action on at least one group.

            A resource whose objects have an owner (Certificate, Cryptographic Key, Secret) does not follow that group \
            rule. Without a list grant it is listed only when the user holds the create action on it, owns at least \
            one of its objects, or holds the members action on a group one of its objects is assigned to, and is not \
            listed for a user who has none of these, so a client can hide that section entirely.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Resource> allowedListings;

    @Schema(description = """
            Actions the user is permitted to perform on a resource as a whole, per resource. Use this, not \
            allowedListings, to decide whether to offer a control for anything other than a listing.

            Reports resource-level grants only. Object-scoped grants are excluded, because an action held on one \
            object says nothing about the next; they are reported in objectActions. Resources on which the caller \
            holds no action are omitted rather than reported with an empty list, and actions the authorization \
            service will not grant at all are never reported.

            This is not derivable from allowedListings and does not supersede it. That field applies a broader rule \
            to the list action alone, unioning object-scoped grants and ownership and adding the listings every user \
            gets by default, so neither field can be computed from the other.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResourceActionsDto> allowedActions;

    @Schema(description = """
            Actions the user is permitted to perform on specific objects, one entry per object. An action is \
            available on an object when allowedActions holds it for the object's resource, when it is listed here for \
            that object, or when the user owns the object. List and detail are also available when the user holds the \
            members action on a group the object is assigned to; that grant appears here under the group, or in \
            allowedActions for Group as a whole.

            Reports explicit object-scoped grants only. Ownership is not listed, because the object itself carries \
            its owner. Denials are not reported either, so the authorization service may still refuse a resource-level \
            action on an object it was denied on. Objects on which the caller holds no action are omitted rather than \
            reported with an empty list, and actions the authorization service will not grant at all are never \
            reported.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ObjectActionsDto> objectActions;

    /**
     * The signature this class carried before {@code objectActions} was added, so a caller that constructs it
     * positionally still compiles and links. The field is required on the wire, so it is reported empty rather than
     * left null, which tells a client the user holds no object-scoped grants.
     */
    @Deprecated(since = "2.21.0", forRemoval = true)
    public UserProfilePermissionsDto(List<Resource> allowedListings, List<ResourceActionsDto> allowedActions) {
        this(allowedListings, allowedActions, List.of());
    }

}
