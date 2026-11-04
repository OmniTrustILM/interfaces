package com.otilm.api.model.core.auth;

import com.otilm.core.model.auth.ResourceAction;
import java.util.List;

public interface ObjectActionsAware {

    String ALLOWED_ACTIONS_DESCRIPTION = """
            Actions the user is permitted to perform on this object, decided by the authorization check on the \
            object itself: grants on the resource, grants on this object, ownership, and for list and detail the \
            members action on a group the object is assigned to. A permission an operation also requires on a parent \
            object, such as the authority of an RA profile, is not considered, so a reported action can still be \
            refused. Reported on the object's detail response only: \
            absent where the same model is returned in a list, by a create or update request, or nested inside \
            another object. Actions the authorization service will not grant at all are never reported. The \
            authorization service remains the only gate; this is a hint that keeps a control the caller cannot use off \
            the screen.
            """;

    List<ResourceAction> getAllowedActions();

    void setAllowedActions(List<ResourceAction> allowedActions);

}
