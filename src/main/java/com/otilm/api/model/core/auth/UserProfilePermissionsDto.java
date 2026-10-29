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
            the resource as a whole or on at least one of its objects. A resource that can be assigned to a group is \
            also listed for a user who may manage group members, and some listings are granted to every user by \
            default.

            A resource whose objects have an owner (Certificate, Cryptographic Key, Secret) is also listed when the \
            user owns at least one of its objects, or belongs to a group that owns one. It is not listed for a user \
            who has none of these, so a client can hide that section entirely.
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
            that object, or when the user owns the object or belongs to its owning group.

            Reports explicit object-scoped grants only. Ownership is not listed, because the object itself carries \
            its owner. Denials are not reported either, so the authorization service may still refuse a resource-level \
            action on an object it was denied on. Objects on which the caller holds no action are omitted rather than \
            reported with an empty list, and actions the authorization service will not grant at all are never \
            reported.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ObjectActionsDto> objectActions;

}
