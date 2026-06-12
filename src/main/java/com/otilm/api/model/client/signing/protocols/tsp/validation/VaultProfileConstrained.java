package com.otilm.api.model.client.signing.protocols.tsp.validation;

import com.otilm.api.model.core.signing.TspAuthenticationMethod;

import java.util.Collection;
import java.util.UUID;

/**
 * Implemented by request DTOs that carry an allowed-authentication-methods list and a backing
 * vault-profile reference, so {@link VaultProfileRequiredForBasicPasswordValidator} can validate
 * across those fields without being coupled to a concrete DTO class.
 */
public interface VaultProfileConstrained {

    Collection<TspAuthenticationMethod> getAllowedAuthenticationMethods();

    UUID getVaultProfileUuid();
}
