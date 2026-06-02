package com.czertainly.api.model.common.validation;

import com.czertainly.api.model.core.signing.TspAuthenticationMethod;

import java.util.Collection;

/**
 * Implemented by request DTOs that carry both an allowed-authentication-methods list and a
 * basic-credentials list, so {@link BasicCredentialsRequiredIfBasicPasswordValidator} can validate
 * across both fields without being coupled to a concrete DTO class.
 */
public interface BasicPasswordConstrained {

    Collection<TspAuthenticationMethod> getAllowedAuthenticationMethods();

    Collection<?> getBasicCredentials();
}
