package com.czertainly.api.model.client.signing.timequality.validation;

import java.util.List;

public interface NtpConfiguration {
    List<String> getNtpServers();
    int getNtpServersMinReachable();
}
