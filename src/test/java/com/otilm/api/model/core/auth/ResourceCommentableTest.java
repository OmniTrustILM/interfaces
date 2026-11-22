package com.otilm.api.model.core.auth;

import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceCommentableTest {

    private static final Set<Resource> SUPPORTED = EnumSet
            .of(Resource.CERTIFICATE, Resource.CRYPTOGRAPHIC_KEY, Resource.TOKEN, Resource.DISCOVERY, Resource.SECRET,
                    Resource.VAULT, Resource.AUTHORITY, Resource.ENTITY, Resource.LOCATION, Resource.CONNECTOR,
                    Resource.APPROVAL, Resource.RA_PROFILE, Resource.VAULT_PROFILE, Resource.COMPLIANCE_PROFILE,
                    Resource.APPROVAL_PROFILE, Resource.NOTIFICATION_PROFILE, Resource.SIGNING_PROFILE,
                    Resource.TOKEN_PROFILE, Resource.ACME_PROFILE, Resource.SCEP_PROFILE, Resource.CMP_PROFILE,
                    Resource.TSP_PROFILE, Resource.EST_PROFILE);

    @Test
    void commentableResourcesContainExactlyTheSupportedSet() {
        assertEquals(SUPPORTED, Resource.getCommentableResources());
        assertTrue(Resource.CERTIFICATE.commentable());
        assertFalse(Resource.USER.commentable());
    }

    @Test
    void commentableResourcesSetIsImmutable() {
        assertThrows(UnsupportedOperationException.class, () -> Resource.getCommentableResources().add(Resource.USER));
    }
}
