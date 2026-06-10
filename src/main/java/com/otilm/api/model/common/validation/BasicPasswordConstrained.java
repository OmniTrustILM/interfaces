package com.otilm.api.model.common.validation;

import com.otilm.api.model.core.signing.TspAuthenticationMethod;

import java.util.Collection;
import java.util.UUID;

/**
 * Implemented by request DTOs that carry an allowed-authentication-methods list, a basic-credentials
 * list, and a backing vault-profile reference, so {@link BasicCredentialsRequiredIfBasicPasswordValidator}
 * can validate across those fields without being coupled to a concrete DTO class.
 */
public interface BasicPasswordConstrained {

    Collection<TspAuthenticationMethod> getAllowedAuthenticationMethods();

    Collection<?> getBasicCredentials();

    UUID getVaultProfileUuid();
}
