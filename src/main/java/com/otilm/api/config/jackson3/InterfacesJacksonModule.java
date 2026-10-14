package com.otilm.api.config.jackson3;

import tools.jackson.databind.module.SimpleModule;

/**
 * Lets Jackson 3 apply the handlers that this artifact's DTOs name in Jackson 2's {@code @JsonSerialize} and
 * {@code @JsonDeserialize}. Jackson finds it through {@code META-INF/services} on any mapper built with
 * {@code findAndAddModules()}, Spring Boot 4's auto-configured one included; a hand-built mapper needs
 * {@code addModule(new InterfacesJacksonModule())}.
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
