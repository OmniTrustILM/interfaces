package com.otilm.api.model.core.notification;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.common.enums.IPlatformEnum;
import com.otilm.api.model.core.auth.Resource;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Arrays;

@Schema(enumAsRef = true)
public enum RecipientType implements IPlatformEnum {

    NONE("none", "None", "None recipient type describes that no specific recipient is required when used", null),
    DEFAULT("default", "Default", "Default recipients are defined by context, e.g. by event and/or resource that is connected with notification", null),
    USER("user", "User", "Recipient is registered user", Resource.USER),
    GROUP("group", "Group", "Recipient is group from inventory", Resource.GROUP),
    ROLE("role", "Role", "Recipient is registered role", Resource.ROLE),
    OWNER("owner", "Owner", "Recipient is user that is associated as owner of resource object that is connected with notification", Resource.USER);

    private static final RecipientType[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final String label;
    private final String description;
    private final Resource recipientResource;

    RecipientType(String code, String label, String description, Resource recipientResource) {
        this.code = code;
        this.label = label;
        this.description = description;
        this.recipientResource = recipientResource;
    }

    @Override
    @JsonValue
    public String getCode() {
        return this.code;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public Resource getRecipientResource() {
        return recipientResource;
    }

    @JsonCreator
    public static RecipientType findByCode(String code) {
        return Arrays.stream(VALUES)
                .filter(a -> a.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                        new ValidationException(ValidationError.create("Unknown recipient type code {}", code)));
    }
}

