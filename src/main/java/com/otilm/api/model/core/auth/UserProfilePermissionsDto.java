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
            one of its objects, or holds the members action on a group one of its objects is assigned to while that \
            object is in no group on which the action is denied. It is not listed for a user who has none of these, \
            so a client can hide that section entirely.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Resource> allowedListings;

    @Schema(description = """
            Actions the user is permitted to perform on a resource as a whole, per resource. Use this, not \
            allowedListings, to decide whether to offer a control for anything other than a listing.

            Reports resource-level grants only. Object-scoped grants are excluded, including the implicit access an \
            owner has and the access that the members action on a group gives to the objects assigned to that group, \
            because an action held on one object says nothing about the next; \
            POST /v1/resources/{resource}/objects/allowed-actions reports the actions the user may perform on \
            specific objects. Resources on which the caller holds no action are omitted rather than reported with an \
            empty list, and actions the authorization service will not grant at all are never reported.

            This is not derivable from allowedListings and does not supersede it. That field applies a broader rule \
            to the list action alone, unioning object-scoped grants and ownership and adding the listings every user \
            gets by default, so neither field can be computed from the other.
            """, requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ResourceActionsDto> allowedActions;

}
