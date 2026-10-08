package com.otilm.api.config.jackson3;

import tools.jackson.databind.module.SimpleModule;

/**
 * Makes Jackson 3 read and write this artifact's DTOs as Jackson 2 does. Spring Boot 4 registers it on its
 * auto-configured mapper through {@code META-INF/services}, so a connector needs no code of its own.
 */
public class InterfacesJacksonModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public InterfacesJacksonModule() {
        super(InterfacesJacksonModule.class.getName());
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.insertAnnotationIntrospector(new Jackson2AnnotationBridge());
    }
}
