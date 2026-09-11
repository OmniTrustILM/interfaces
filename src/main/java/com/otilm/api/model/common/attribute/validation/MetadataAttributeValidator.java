package com.otilm.api.model.common.attribute.validation;

import com.otilm.api.model.client.attribute.BaseAttributeContentDtoV3;
import com.otilm.api.model.common.attribute.common.AttributeContent;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.AttributeVersion;
import com.otilm.api.model.common.attribute.common.MetadataAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.v3.MetadataAttributeV3;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.UUID;

public final class MetadataAttributeValidator
        implements
            ConstraintValidator<ValidMetadataAttribute, MetadataAttribute> {

    private static boolean isUsableContent(Object item) {
        if (!(item instanceof AttributeContent content)) {
            return false;
        }
        String reference = content.getReference();
        Object data = content.getData();
        boolean hasUsableReference = isNonBlank(reference);
        boolean hasUsableData = isUsableData(data);
        return hasUsableReference || hasUsableData;
    }

    private static boolean isUsableData(Object data) {
        if (data == null) {
            return false;
        }

        if (data instanceof CharSequence text) {
            String textRepresentation = text.toString();
            return !textRepresentation.isBlank();
        }

        return true;
    }

    private static boolean isUuid(String value) {
        if (!isNonBlank(value)) {
            return false;
        }
        try {
            return UUID.fromString(value).toString().equalsIgnoreCase(value);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private static boolean isNonBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static AttributeVersion toAttributeVersion(int version) {
        try {
            return AttributeVersion.fromIntVersion(version);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    private static void addPropertyViolation(ConstraintValidatorContext context, String property, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addPropertyNode(property).addConstraintViolation();
    }

    private static void addContentViolation(ConstraintValidatorContext context, int index, String message) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(message)
                .addPropertyNode("content")
                .addContainerElementNode("<list element>", List.class, 0)
                .inIterable()
                .atIndex(index)
                .addConstraintViolation();
    }

    private static boolean hasValidContent(MetadataAttribute value, Class<?> expectedContentClass,
            ConstraintValidatorContext context) {
        List<?> content = value.getContent();
        if (content == null || content.isEmpty()) {
            addPropertyViolation(context, "content", "content is required and must not be empty");
            return false;
        }

        boolean valid = true;
        for (int index = 0; index < content.size(); index++) {
            Object item = content.get(index);
            if (!isUsableContent(item)) {
                addContentViolation(context, index, "content must contain a non-blank reference or usable data");
                valid = false;
            } else if (item instanceof BaseAttributeContentDtoV3 && expectedContentClass != null
                    && !expectedContentClass.isInstance(item)) {
                addContentViolation(context, index, "content must match contentType and attribute version");
                valid = false;
            }
        }
        return valid;
    }

    private static boolean matchesAttributeVersion(MetadataAttribute value, AttributeVersion attributeVersion) {
        return switch (attributeVersion) {
            case V2 -> value instanceof MetadataAttributeV2;
            case V3 -> value instanceof MetadataAttributeV3;
        };
    }

    private static Class<?> getExpectedContentClass(AttributeContentType contentType,
            AttributeVersion attributeVersion) {
        return switch (attributeVersion) {
            case V2 -> contentType.getContentV2Class();
            case V3 -> contentType.getContentV3Class();
        };
    }

    @Override
    public boolean isValid(MetadataAttribute value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean valid = true;

        if (!isUuid(value.getUuid())) {
            addPropertyViolation(context, "uuid", "uuid is required and must be a valid UUID");
            valid = false;
        }

        if (value.getName() == null || value.getName().isBlank()) {
            addPropertyViolation(context, "name", "name must not be blank");
            valid = false;
        }

        if (value.getType() != AttributeType.META) {
            addPropertyViolation(context, "type", "type must be meta");
            valid = false;
        }

        AttributeVersion attributeVersion = toAttributeVersion(value.getVersion());
        if (attributeVersion == null) {
            addPropertyViolation(context, "version", "version is required and must be supported");
            valid = false;
        } else if (!matchesAttributeVersion(value, attributeVersion)) {
            addPropertyViolation(context, "version", "version must match metadata attribute DTO");
            valid = false;
        }

        // Ideally, the schemaVersion is declared on MetadataAttribute so we don't have to check for
        // MetadataAttributeV3.
        // However, the schemaVersion is intended to be used as serialization polymorphic discriminator, and must be
        // marked in OpenAPI spec as required.
        if (attributeVersion != null && value instanceof MetadataAttributeV3 metadataV3
                && metadataV3.getSchemaVersion() != null && metadataV3.getSchemaVersion() != attributeVersion) {
            addPropertyViolation(context, "schemaVersion", "schemaVersion must match version");
            valid = false;
        }

        AttributeContentType contentType = value.getContentType();
        Class<?> expectedContentClass = null;
        if (contentType == null) {
            addPropertyViolation(context, "contentType", "contentType is required");
            valid = false;
        } else if (attributeVersion != null) {
            expectedContentClass = getExpectedContentClass(contentType, attributeVersion);
            if (expectedContentClass == null) {
                addPropertyViolation(context, "contentType", "contentType is not supported for attribute version");
                valid = false;
            }
        }

        if (value.getProperties() == null) {
            addPropertyViolation(context, "properties", "properties is required");
            valid = false;
        }

        if (!hasValidContent(value, expectedContentClass, context)) {
            valid = false;
        }

        return valid;
    }
}
