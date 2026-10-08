package com.otilm.api.clients;

import com.fasterxml.jackson.databind.module.SimpleModule;

/** Listed only in {@code META-INF/services}, so only ServiceLoader discovery registers it. */
public class ServiceLoaderProbeModule extends SimpleModule {

    static final String NAME = "service-loader-probe";

    public ServiceLoaderProbeModule() {
        super(NAME);
    }
}
