package com.otilm.api.clients;

import com.otilm.api.model.client.attribute.ResponseAttribute;
import com.otilm.api.model.core.connector.AuthType;
import com.otilm.api.model.core.connector.ConnectorStatus;
import com.otilm.api.model.core.proxy.ProxyDto;

import java.io.Serializable;
import java.util.List;

public interface ApiClientConnectorInfo extends Serializable {

    String getUuid();

    String getName();

    String getUrl();

    ConnectorStatus getStatus();

    AuthType getAuthType();

    List<ResponseAttribute> getAuthAttributes();

    ProxyDto getProxy();

}
