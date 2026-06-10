package com.otilm.api.model.client.signing.timequality.validation;

import java.util.List;

public interface NtpConfiguration {
    List<String> getNtpServers();
    int getNtpServersMinReachable();
}
