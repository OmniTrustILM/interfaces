package com.czertainly.api.model.connector.secrets;

import com.czertainly.api.model.client.attribute.RequestAttribute;

import java.util.List;

public interface SecretOperationRequest {

    List<RequestAttribute> getVaultAttributes();
    void setVaultAttributes(List<RequestAttribute> vaultAttributes);

    List<RequestAttribute> getVaultProfileAttributes();
    void setVaultProfileAttributes(List<RequestAttribute> vaultProfileAttributes);

    List<RequestAttribute> getSecretAttributes();
    void setSecretAttributes(List<RequestAttribute> secretAttributes);
}
