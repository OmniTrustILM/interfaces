package com.otilm.core.util;

import com.otilm.api.config.serializer.AttributeContentDeserializer;
import com.otilm.api.config.serializer.BaseAttributeDeserializer;
import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.*;
import com.otilm.api.model.common.NameAndIdDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.*;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.ZonedDateTimeDeserializer;
import com.otilm.api.model.common.attribute.common.content.data.FileAttributeContentData;
import com.otilm.api.model.common.attribute.v2.*;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallbackMapping;
import com.otilm.api.model.common.attribute.common.callback.AttributeValueTarget;
import com.otilm.api.model.common.attribute.common.callback.RequestAttributeCallback;
import com.otilm.api.model.common.attribute.common.constraint.BaseAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.data.DateTimeAttributeConstraintData;
import com.otilm.api.model.common.attribute.common.constraint.data.RangeAttributeConstraintData;
import com.otilm.api.model.common.attribute.v2.content.*;
import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import com.otilm.api.model.common.attribute.v3.DataAttributeV3;
import com.otilm.api.model.common.attribute.v3.content.*;
import com.otilm.api.model.core.credential.CredentialDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AttributeDefinitionUtils {

    private static final ObjectMapper ATTRIBUTES_OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new SimpleModule()
                    .addDeserializer(BaseAttribute.class, new BaseAttributeDeserializer())
                    .addDeserializer(AttributeContent.class, new AttributeContentDeserializer())
                    .addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer())
            )
            .findAndAddModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .build();

    private static final Logger logger = LoggerFactory.getLogger(AttributeDefinitionUtils.class);

    static <T extends BaseAttribute> T getAttributeDefinition(String name, List<T> attributes) {
        return attributes.stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
    }

    // in int
    public static <T extends BaseAttribute> boolean containsAttributeDefinition(String name, List<T> attributes) {
        BaseAttribute definition = getAttributeDefinition(name, attributes);
        return definition != null;
    }

    // in int
    public static <T> T getRequestAttributes(String name, List<?> attributes) {
        if (attributes.isEmpty()) {
            return null;
        }
        if (attributes.get(0) instanceof RequestAttribute) {
            List<RequestAttribute> reloadedAttributes = (List<RequestAttribute>) attributes;
            return (T) reloadedAttributes.stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
        } else if (attributes.get(0) instanceof BaseAttribute) {
            List<BaseAttribute> reloadedAttributes = (List<BaseAttribute>) attributes;
            return (T) reloadedAttributes.stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
        } else if (attributes.get(0) instanceof ResponseAttribute) {
            List<ResponseAttribute> reloadedAttributes = (List<ResponseAttribute>) attributes;
            return (T) reloadedAttributes.stream().filter(x -> x.getName().equals(name)).findFirst().orElse(null);
        } else {
            throw new IllegalArgumentException("Invalid Object to get Attribute value");
        }

    }

    public static <T extends Object> T getAttributeContent(String name, List<?> attributes, Boolean singleItem) {
        if (attributes.size() == 0) {
            return null;
        }
        if (attributes.get(0) instanceof RequestAttribute) {
            RequestAttribute definition = getRequestAttributes(name, attributes);
            if (definition == null || definition.getContent() == null) {
                return null;
            }
            if (!singleItem) {
                return definition.getContent();
            } else {
                return ((List<T>) definition.getContent()).get(0);
            }
        } else if (attributes.get(0) instanceof BaseAttribute) {
            BaseAttribute definition = getRequestAttributes(name, attributes);
            if (definition == null || definition.getContent() == null) {
                return null;
            }
            if (!singleItem) {
                return (T) definition.getContent();
            } else {
                return ((List<T>) definition.getContent()).get(0);
            }
        } else if (attributes.get(0) instanceof ResponseAttribute) {
            ResponseAttribute definition = getRequestAttributes(name, attributes);
            if (definition == null || definition.getContent() == null) {
                return null;
            }
            if (!singleItem) {
                return definition.getContent();
            } else {
                return ((List<T>) definition.getContent()).get(0);
            }
        } else {
            throw new IllegalArgumentException("Invalid Object to get Attribute value");
        }
    }

    public static <T> List<T> getAttributeContent(String name, List<?> attributes, Class<T> clazz) {
        if (attributes == null || attributes.size() == 0) {
            return null;
        }
        if (attributes.get(0) instanceof RequestAttribute) {
            RequestAttribute definition = getRequestAttributes(name, attributes);
            if (definition == null || definition.getContent() == null) {
                return null;
            }
            return ATTRIBUTES_OBJECT_MAPPER.convertValue(definition.getContent(), ATTRIBUTES_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } else if (attributes.get(0) instanceof BaseAttribute) {
            BaseAttribute definition = getRequestAttributes(name, attributes);
            if (definition == null || definition.getContent() == null) {
                return null;
            }
            return ATTRIBUTES_OBJECT_MAPPER.convertValue(definition.getContent(), ATTRIBUTES_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } else if (attributes.get(0) instanceof ResponseAttribute) {
            ResponseAttribute definition = getRequestAttributes(name, attributes);
            if (definition == null || definition.getContent() == null) {
                return null;
            }
            return ATTRIBUTES_OBJECT_MAPPER.convertValue(definition.getContent(), ATTRIBUTES_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } else {
            throw new IllegalArgumentException("Invalid Object to get Attribute value");
        }
    }

    public static NameAndIdDto getNameAndIdData(String name, List<RequestAttribute> attributes) {
        if (attributes.isEmpty()) {
            return null;
        }

        return getObjectAttributeContentData(name, attributes, NameAndIdDto.class).get(0);
    }

    public static NameAndUuidDto getNameAndUuidData(String name, List<RequestAttribute> attributes) {
        if (attributes.isEmpty()) {
            return null;
        }

        return getObjectAttributeContentData(name, attributes, NameAndUuidDto.class).get(0);
    }

    public static CredentialAttributeContentData getCredentialContent(String name, List<RequestAttribute> attributes) {
        List<CredentialAttributeContentV2> content = AttributeDefinitionUtils.getAttributeContent(name, attributes, CredentialAttributeContentV2.class);
        if (content != null && !content.isEmpty()) {
            return content.get(0).getData();
        }
        return null;
    }

    public static <T extends BaseAttribute> String serialize(List<T> attributes) {
        if (attributes == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.writeValueAsString(attributes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T extends DataAttribute> String serializeData(List<T> attributes) {
        if (attributes == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.writeValueAsString(attributes);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    public static <T extends BaseAttribute> String serialize(T attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T extends BaseAttributeContentV2<?>> String serializeAttributeContent(List<T> attributeContent) {
        if (attributeContent == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.writeValueAsString(attributeContent);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T extends BaseAttribute> List<T> deserialize(String attributesJson, Class<T> clazz) {
        if (attributesJson == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.readValue(attributesJson, ATTRIBUTES_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T extends BaseAttributeContentV2<?>> List<T> deserializeAttributeContent(String attributeContentJson, Class<T> clazz) {
        if (attributeContentJson == null) {
            return null;
        }
        try {
            return ATTRIBUTES_OBJECT_MAPPER.readValue(attributeContentJson, ATTRIBUTES_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    public static List<BaseAttribute> mergeAttributes(List<? extends BaseAttribute> definitions, List<RequestAttribute> attributes) throws ValidationException {
        if (definitions == null || attributes == null) {
            return List.of();
        }

        List<BaseAttribute> attributeDefinitions = clientAttributeConverter(attributes);
        return attributeDefinitions.stream()
                .map(a -> {
                    if (a.getVersion() == 2) {
                        DataAttributeV2 definition = (DataAttributeV2) getAttributeDefinition(a.getName(), definitions);
                        if (definition == null) {
                            return a;
                        }

                        DataAttributeV2 extended = new DataAttributeV2(definition);
                        extended.setContent(a.getContent());
                        return extended;
                    }
                    if (a.getVersion() == 3) {
                        DataAttributeV3 definition = (DataAttributeV3) getAttributeDefinition(a.getName(), definitions);
                        if (definition == null) {
                            return a;
                        }

                        DataAttributeV3 extended = new DataAttributeV3(definition);
                        extended.setContent(a.getContent());
                        return extended;
                    }
                    return null;
                })
                .collect(Collectors.toList());
    }

    //TODO - Rework
    public static void validateAttributes(List<? extends BaseAttribute> definitions, List<RequestAttribute> attributes) throws ValidationException {
        List<ValidationError> errors = new ArrayList<>();

        // When the Group Attribute contains a group of other attributes, we currently do not have the definition of them
        // without executing the same sequence of callbacks or storing the definition in the database. Therefore,
        // we will need to skip the validation of Attributes that are unknown and rely on proper validation by the
        // connector.
        // TODO: Validation of Attributes that has unknown definition
        for (RequestAttribute attribute : attributes) {
            if (!containsAttributeDefinition(attribute.getName(), definitions)) {
                // do not throw error in case the definition is not found, warn only
                logger.warn("Cannot validate Attribute '{}' as it has unknown definition", attribute.getName());
            }
        }

        for (BaseAttribute definition : definitions) {
            validateSingleAttribute(attributes, definition, errors);
        }


        if (!errors.isEmpty()) {
            throw new ValidationException("Attributes validation failed.", errors);
        }
    }

    private static void validateSingleAttribute(List<RequestAttribute> attributes, BaseAttribute definition, List<ValidationError> errors) {
        RequestAttribute attribute = getRequestAttributes(definition.getName(), attributes);

        boolean isRequired = false;
        boolean isReadOnly = false;
        String label = null;
        AttributeContentType contentType;
        int version = definition.getVersion();

        if (definition.getType().equals(AttributeType.DATA)) {
            DataAttribute dataAttribute = (DataAttribute) definition;
            contentType = dataAttribute.getContentType();
            if (dataAttribute.getProperties() != null) {
                isRequired = dataAttribute.getProperties().isRequired();
                isReadOnly = dataAttribute.getProperties().isReadOnly();
                label = dataAttribute.getProperties().getLabel();
            }
        } else if (definition.getType().equals(AttributeType.CUSTOM)) {
            CustomAttribute customAttribute = (CustomAttribute) definition;
            contentType = customAttribute.getContentType();
            if (customAttribute.getProperties() != null) {
                isRequired = customAttribute.getProperties().isRequired();
                isReadOnly = customAttribute.getProperties().isReadOnly();
                label = customAttribute.getProperties().getLabel();
            }
        } else {
            logger.warn("Cannot validate {} attributes", definition.getType());
            return;
        }

        if (attribute == null) {
            if (isRequired) {
                errors.add(ValidationError.create("Required attribute {} not found.", label));
            }
            return;
        }

        Object attributeContent = null;
        try {
            attributeContent = getAttributeContent(version, attributeContent, attribute, contentType);
        } catch (IllegalArgumentException e) {
            errors.add(ValidationError.create(
                    "Wrong type of value for attribute {}.",
                    label));
            return;
        }

        if (isRequired && attributeContent == null) {
            errors.add(ValidationError.create("Value of required attribute {} not set.", label));
            return;
        }

        if (isReadOnly) {
            validateReadOnly(definition, errors, attributeContent, label);
        }

        validateAttributeContent(definition, attribute, errors);
        errors.addAll(validateConstraints(definition, attribute.getContent()));
    }

    private static Object getAttributeContent(int version, Object attributeContent, RequestAttribute attribute, AttributeContentType contentType) {
        if (version == 2) {
            attributeContent = ATTRIBUTES_OBJECT_MAPPER.convertValue(attribute.getContent(), ATTRIBUTES_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, contentType.getContentV2Class()));
        }
        if (version == 3) {
            attributeContent = ATTRIBUTES_OBJECT_MAPPER.convertValue(attribute.getContent(), ATTRIBUTES_OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, contentType.getContentV3Class()));
        }
        return attributeContent;
    }

    private static void validateReadOnly(BaseAttribute definition, List<ValidationError> errors, Object attributeContent, String label) {
        Object definitionContent = definition.getContent();
        if (definitionContent == null || !definitionContent.equals(attributeContent)) {
            errors.add(ValidationError.create(
                    "Wrong value of read only attribute {}. Definition value = {} and attribute value = {}.",
                    label,
                    definitionContent,
                    attributeContent));
        }
    }

    public static boolean containsRequestAttributes(String name, List<RequestAttribute> attributes) {
        RequestAttributeDto definition = getRequestAttributes(name, attributes);
        return definition != null;
    }

    public static List<ValidationError> validateConstraints(BaseAttribute attribute, List<? extends AttributeContent> contents) {
        List<BaseAttributeConstraint<?>> constraints;
        AttributeContentType contentType;
        String label = null;

        if (attribute.getType().equals(AttributeType.DATA)) {
            DataAttribute dataAttribute = (DataAttribute) attribute;
            constraints = dataAttribute.getConstraints();
            contentType = dataAttribute.getContentType();
            if (dataAttribute.getProperties() != null) label = dataAttribute.getProperties().getLabel();
        } else {
            return new ArrayList<>();
        }
        if (constraints == null) return new ArrayList<>();

        List<ValidationError> errors = new ArrayList<>();
        for (BaseAttributeConstraint<?> constraint : constraints) {
            switch (constraint.getType()) {
                case REGEXP -> validateRegexpConstraint(contents, constraint, contentType, errors, label);
                case RANGE -> validateRangeConstraint(contents, constraint, contentType, errors, label);
                case DATETIME -> validateDateTimeConstraint(contents, constraint, contentType, errors, label);
            }
        }
        return errors;
    }

    private static void validateRangeConstraint(List<? extends AttributeContent> contents, BaseAttributeConstraint<?> constraint, AttributeContentType contentType, List<ValidationError> errors, String label) {
        RangeAttributeConstraintData constraintData = (RangeAttributeConstraintData) constraint.getData();
        if (contentType.equals(AttributeContentType.INTEGER)) {
            validateIntegerRangeConstraint(contents, errors, label, constraintData);
        } else if (contentType.equals(AttributeContentType.FLOAT)) {
            validateFloatRangeConstraint(contents, errors, label, constraintData);
        } else {
            errors.add(ValidationError.create("Invalid Attribute Constraint Type and Attribute Content Type. Range can be validated only for INTEGER and FLOAT"));
        }
    }

    private static void validateFloatRangeConstraint(List<? extends AttributeContent> contents, List<ValidationError> errors, String label, RangeAttributeConstraintData constraintData) {
        for (AttributeContent value : contents) {
            if (constraintData.getFrom() != null && (Float) value.getData() < constraintData.getFrom()) {
                    errors.add(ValidationError.create(
                            "Value {} of attribute {} should be higher than {}",
                            value.getData(),
                            label,
                            constraintData.getFrom()));
                }

            if (constraintData.getTo() != null && (Float) value.getData() > constraintData.getTo()) {
                    errors.add(ValidationError.create(
                            "Value {} of attribute {} should be lower than {}",
                            value.getData(),
                            label,
                            constraintData.getTo()));
                }

        }
    }

    private static void validateIntegerRangeConstraint(List<? extends AttributeContent> contents, List<ValidationError> errors, String label, RangeAttributeConstraintData constraintData) {
        for (AttributeContent value : contents) {
            if (constraintData.getFrom() != null && (Integer) value.getData() < constraintData.getFrom()) {
                    errors.add(ValidationError.create(
                            "Value {} of attribute {} should be higher than {}",
                            value.getData(),
                            label,
                            constraintData.getFrom()));
                }

            if (constraintData.getTo() != null && (Integer) value.getData() > constraintData.getTo()) {
                errors.add(ValidationError.create(
                        "Value {} of attribute {} should be lower than {}",
                        value.getData(),
                        label,
                        constraintData.getTo()));
            }
        }
    }

    private static void validateDateTimeConstraint(List<? extends AttributeContent> contents, BaseAttributeConstraint<?> constraint, AttributeContentType contentType, List<ValidationError> errors, String label) {
        if (!contentType.equals(AttributeContentType.DATETIME)) {
            errors.add(ValidationError.create("Invalid Attribute Constraint Type and Attribute Content Type. DateTime can be associated for DATETIME type only"));
        }
        try {
            DateTimeAttributeConstraintData constraintData = (DateTimeAttributeConstraintData) constraint.getData();
            for (AttributeContent value : contents) {
                ZonedDateTime data = value.getData();
                if (constraintData.getFrom() != null && data.isBefore(constraintData.getFrom().atZone(ZoneId.systemDefault()))) {
                        errors.add(ValidationError.create(
                                "Value {} of attribute {} should be after {}",
                                data,
                                label,
                                constraintData.getFrom()));
                    }

                if (constraintData.getTo() != null && data.isAfter(constraintData.getTo().atZone(ZoneId.systemDefault()))) {
                        errors.add(ValidationError.create(
                                "Value {} of attribute {} should be before {}",
                                data,
                                label,
                                constraintData.getTo()));
                    }

            }
        } catch (Exception e) {
            errors.add(ValidationError.create(
                    "Could not validate value of field {} due to error {}",
                    label,
                    ExceptionUtils.getRootCauseMessage(e)));
        }
    }

    private static void validateRegexpConstraint(List<? extends AttributeContent> contents, BaseAttributeConstraint<?> constraint, AttributeContentType contentType, List<ValidationError> errors, String label) {
        if (!contentType.equals(AttributeContentType.STRING)) {
            errors.add(ValidationError.create("Invalid Attribute Constraint Type and Attribute Content Type. Regexp can be associated for STRING type only"));
        }
        Pattern pattern;
        try {
            pattern = Pattern.compile((String) constraint.getData());
            for (AttributeContent value : contents) {
                Matcher matcher = pattern.matcher(value.getData());
                if (!matcher.matches()) {
                    errors.add(ValidationError.create(
                            "Value {} of attribute {} doesn't match regex {}",
                            value.getData(),
                            label,
                            constraint.getData()));
                }
            }

        } catch (Exception e) {
            errors.add(ValidationError.create(
                    "Could not validate value of field {} due to error {}",
                    label,
                    ExceptionUtils.getRootCauseMessage(e)));
        }
    }

    public static void validateAttributeContent(BaseAttribute definition, RequestAttribute attributeContent, List<ValidationError> errors) {

        if (definition.getType() == null) {
            errors.add(ValidationError.create("Type of attribute definition not set."));
        }

        // TODO: checking all items in the list for the type

        AttributeContentType contentType = definition.getType() == AttributeType.DATA ? ((DataAttribute) definition).getContentType() : ((CustomAttribute) definition).getContentType();
        String label = getLabel(definition);
        try {
            for (AttributeContent baseAttributeContent : (List<AttributeContent>) attributeContent.getContent()) {
                if (definition.getVersion() == 2) {
                    validateAttributeContentByContentType(baseAttributeContent, contentType.getContentV2Class(), label, definition.getType(), errors);
                }
                if (definition.getVersion() == 3) {
                    validateAttributeContentByContentType(baseAttributeContent, contentType.getContentV3Class(), label, definition.getType(), errors);
                }
            }
        } catch (Exception e) {
            errors.add(ValidationError.create("Attribute {} of type {} has wrong value.", label, definition.getType()));
        }
    }

    private static String getLabel(BaseAttribute definition) {
        String label = null;
        if (definition.getType().equals(AttributeType.DATA)) {
            DataAttribute dataAttribute = (DataAttribute) definition;
            if (dataAttribute.getProperties() != null) label = dataAttribute.getProperties().getLabel();
        } else {
            CustomAttribute customAttribute = (CustomAttribute) definition;
            if (customAttribute.getProperties() != null) label = customAttribute.getProperties().getLabel();
        }
        return label;
    }

    private static void validateAttributeContentByContentType(
            Object baseAttributeContent,
            Class<?> targetClass,
            String label,
            AttributeType type,
            List<ValidationError> errors
    ) {
        AttributeContent content = (AttributeContent) ATTRIBUTES_OBJECT_MAPPER.convertValue(baseAttributeContent, targetClass);

        ValidationError wrongValueError = ValidationError.create(
                "Wrong value of Attribute {} {}.", label, type
        );

        if (content.getData() == null) {
            errors.add(wrongValueError);
            return;
        }

        if (targetClass == CredentialAttributeContentV2.class) {
            CredentialDto credentialDto = ATTRIBUTES_OBJECT_MAPPER.convertValue(content.getData(), CredentialDto.class);
            if (credentialDto == null) {
                errors.add(wrongValueError);
            }
        }

        if (targetClass == FileAttributeContentV2.class || targetClass == FileAttributeContentV3.class) {
            try {
                Base64.getDecoder().decode(((FileAttributeContentData) (content.getData())).getContent());
            } catch (Exception e) {
                errors.add(wrongValueError);
            }
        }
    }

    public static void validateCallback(AttributeCallback callback, RequestAttributeCallback request, boolean isResourceObjectCallback) {
        List<ValidationError> errors = new ArrayList<>();

        validateContextAndMethod(callback, isResourceObjectCallback, errors);

        if (callback.getMappings() != null) {
            for (AttributeCallbackMapping mapping : callback.getMappings()) {
                validateCallbackMapping(request, mapping, errors);
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Attribute callback validation failed.", errors);
        }
    }

    private static void validateCallbackMapping(RequestAttributeCallback request, AttributeCallbackMapping mapping, List<ValidationError> errors) {
        if (StringUtils.isBlank(mapping.getFrom()) && mapping.getValue() == null) {
            errors.add(ValidationError.create(
                    "Callback mapping invalid - 'from' not set and value in null"));
        }

        if (StringUtils.isBlank(mapping.getTo())) {
            errors.add(ValidationError.create("Callback mapping invalid - 'to' not set"));
        }

        if (mapping.getTargets() == null || mapping.getTargets().isEmpty()) {
            errors.add(ValidationError.create("Callback mapping invalid - 'targets' not set"));
        }

        if (mapping.getTargets() != null) {
            for (AttributeValueTarget target : mapping.getTargets()) {
                validateAttributeTarget(request, mapping, target, errors);
            }
        }
    }

    private static void validateAttributeTarget(RequestAttributeCallback request, AttributeCallbackMapping mapping, AttributeValueTarget target, List<ValidationError> errors) {
        switch (target) {
            case PATH_VARIABLE -> validatePathVariableTarget(request, mapping, errors);
            case REQUEST_PARAMETER -> {
                if (request.getRequestParameter() == null || request.getRequestParameter().isEmpty()) {
                    errors.add(ValidationError.create(
                            "Callback query parameters not set, but mapping require it {}", mapping));
                    break;
                }

                if (request.getRequestParameter().get(mapping.getTo()) == null) {
                    errors.add(ValidationError.create(
                            "Callback query parameters {} not set, but mapping require it {}", mapping.getTo(), mapping));
                    break;
                }
                if (AttributeContentType.CREDENTIAL.equals(mapping.getAttributeContentType())) {
                    errors.add(ValidationError.create(
                            "Callback mapping {} invalid. Type {} not allowed for query parameter", mapping, mapping.getAttributeType()));
                }
            }
            case BODY -> {
                if (request.getBody() == null || request.getBody().isEmpty()) {
                    errors.add(ValidationError.create(
                            "Callback request body not set, but mapping require it {}", mapping));
                    break;
                }

                if (request.getBody().get(mapping.getTo()) == null) {
                    errors.add(ValidationError.create(
                            "Callback request body key {} not set, but mapping require it {}", mapping.getTo(), mapping));
                }
            }
            default -> {
                // no validation for other types
            }
        }
    }

    private static void validatePathVariableTarget(RequestAttributeCallback request, AttributeCallbackMapping mapping, List<ValidationError> errors) {
        if (request.getPathVariable() == null || request.getPathVariable().isEmpty()) {
            errors.add(ValidationError.create(
                    "Callback path variables not set, but mapping require it {}", mapping));
            return;
        }

        if (request.getPathVariable().get(mapping.getTo()) == null) {
            errors.add(ValidationError.create(
                    "Callback path variable {} not set, but mapping require it {}", mapping.getTo(), mapping));
            return;
        }
        if (AttributeContentType.CREDENTIAL.equals(mapping.getAttributeContentType())) {
            errors.add(ValidationError.create(
                    "Callback mapping {} invalid. Type {} not allowed for path variable", mapping, mapping.getAttributeType()));
        }
    }

    private static void validateContextAndMethod(AttributeCallback callback, boolean isResourceObjectCallback, List<ValidationError> errors) {
        if (!isResourceObjectCallback) {
            if (StringUtils.isBlank(callback.getCallbackContext())) {
                errors.add(ValidationError.create("Callback context not set"));
            }

            if (StringUtils.isBlank(callback.getCallbackMethod())) {
                errors.add(ValidationError.create("Callback method not set"));
                return;
            }

            try {
                RequestMethod.valueOf(callback.getCallbackMethod());
            } catch (IllegalArgumentException e) {
                errors.add(ValidationError.create("Callback method invalid, because of {}", e.getMessage()));
            }
        }
    }


    public static List<BaseAttribute> clientAttributeConverter(List<RequestAttribute> attributes) {
        if (attributes == null) {
            return new ArrayList<>();
        }
        List<BaseAttribute> convertedDefinition = new ArrayList<>();
        for (RequestAttribute clt : attributes) {
            if (clt.getVersion() == AttributeVersion.V2) {
                DataAttributeV2 atr = new DataAttributeV2();
                atr.setContent(clt.getContent());
                atr.setName(clt.getName());
                atr.setUuid(String.valueOf(clt.getUuid()));
                convertedDefinition.add(atr);
            }
            if (clt.getVersion() == AttributeVersion.V3) {
                DataAttributeV3 atr = new DataAttributeV3();
                atr.setContent(clt.getContent());
                atr.setName(clt.getName());
                atr.setUuid(String.valueOf(clt.getUuid()));
                convertedDefinition.add(atr);
            }
        }
        return convertedDefinition;
    }

    public static List<RequestAttribute> getClientAttributes(List<?> attributes) {
        if (attributes == null || attributes.isEmpty()) {
            return new ArrayList<>();
        }
        List<RequestAttribute> convertedDefinition = new ArrayList<>();
        if (attributes.get(0) instanceof DataAttribute) {
            List<DataAttribute> dataAttributes = attributes.stream()
                    .filter(DataAttribute.class::isInstance)
                    .map(DataAttribute.class::cast)
                    .toList();
            for (DataAttribute attribute : dataAttributes) {
                if (attribute.getVersion() == 2) {
                    convertBaseAttributesV2ToRequestAttributes((DataAttributeV2) attribute, convertedDefinition);
                }
                if (attribute.getVersion() == 3) {
                    convertBaseAttributesV3ToRequestAttributes((DataAttributeV3) attribute, convertedDefinition);
                }
            }
        } else if (attributes.get(0) instanceof ResponseAttribute) {
            List<ResponseAttribute> responseAttributes = attributes.stream()
                    .filter(ResponseAttribute.class::isInstance)
                    .map(ResponseAttribute.class::cast)
                    .toList();
            convertResponseToRequestAttribute(responseAttributes);
        } else {
            throw new IllegalArgumentException("Invalid argument provided to get Attributes");
        }
        return convertedDefinition;
    }

    private static void convertResponseToRequestAttribute(List<ResponseAttribute> attributes) {
        List<ResponseAttribute> itrAttributes = attributes;
        for (ResponseAttribute clt : itrAttributes) {
            if (clt.getVersion() == AttributeVersion.V2) {
                convertResponseAttributeV2ToResponseAttribute(clt);
            }
            if (clt.getVersion() == AttributeVersion.V3) {
                convertResponseAttributeV3ToResponseAttribute(clt);
            }
        }
    }

    private static void convertResponseAttributeV3ToResponseAttribute(ResponseAttribute clt) {
        RequestAttributeV3 atr = new RequestAttributeV3();
        atr.setName(clt.getName());
        atr.setUuid(clt.getUuid());
        atr.setContentType(clt.getContentType());
        atr.setContent(clt.getContent());
    }

    private static void convertResponseAttributeV2ToResponseAttribute(ResponseAttribute clt) {
        RequestAttributeV2 atr = new RequestAttributeV2();
        atr.setName(clt.getName());
        atr.setUuid(clt.getUuid());
        atr.setContentType(clt.getContentType());
        atr.setContent(clt.getContent());
    }

    private static void convertBaseAttributesV3ToRequestAttributes(DataAttributeV3 dataAttribute, List<RequestAttribute> convertedDefinition) {
            if (dataAttribute.getType() != AttributeType.DATA) {
                return;
            }
            RequestAttributeV3 atr = new RequestAttributeV3();
            atr.setName(dataAttribute.getName());
            if (dataAttribute.getUuid() != null) atr.setUuid(UUID.fromString(dataAttribute.getUuid()));
            atr.setContent(dataAttribute.getContent());
            atr.setContentType(dataAttribute.getContentType());
            convertedDefinition.add(atr);
    }

    private static void convertBaseAttributesV2ToRequestAttributes(DataAttributeV2 dataAttributeV2, List<RequestAttribute> convertedDefinition) {
            if (dataAttributeV2.getType() != AttributeType.DATA) {
                return;
            }
            RequestAttributeV2 atr = new RequestAttributeV2();
            atr.setName(dataAttributeV2.getName());
            if (dataAttributeV2.getUuid() != null) atr.setUuid(UUID.fromString(dataAttributeV2.getUuid()));
            atr.setContent(dataAttributeV2.getContent());
            atr.setContentType(dataAttributeV2.getContentType());
            convertedDefinition.add(atr);
    }

    public static AttributeContentType deriveAttributeContentTypeFromContent(List<? extends AttributeContent> content) {
        if (content == null || content.isEmpty() || content.get(0).getData() instanceof LinkedHashMap) {
            return AttributeContentType.OBJECT;
        }
        try {
            return AttributeContentType.fromClass(content.get(0).getClass());
        } catch (IllegalArgumentException e) {
            logger.warn("Unable to calculate the content type for the content");
            return AttributeContentType.OBJECT;
        }
    }

    public static List<BaseAttributeContentV3<?>> convertContentItemsFromObject(Object object) {
        return ATTRIBUTES_OBJECT_MAPPER.convertValue(object, new TypeReference<>() {
        });
    }

    public static List<BaseAttributeContentV3<?>> createAttributeContentFromString(AttributeContentType attributeContentType, List<String> values) {
        if (!attributeContentType.isFilterByData()) {
            return null;
        }

        try {
            List<BaseAttributeContentV3<?>> contentItems = new ArrayList<>();
            switch (attributeContentType) {
                case STRING -> values.forEach(v -> contentItems.add(new StringAttributeContentV3(v)));
                case TEXT -> values.forEach(v -> contentItems.add(new TextAttributeContentV3(v)));
                case INTEGER ->
                        values.forEach(v -> contentItems.add(new IntegerAttributeContentV3(Integer.valueOf(v))));
                case FLOAT -> values.forEach(v -> contentItems.add(new FloatAttributeContentV3(Float.parseFloat(v))));
                case BOOLEAN ->
                        values.forEach(v -> contentItems.add(new BooleanAttributeContentV3(Boolean.valueOf(v))));
                case DATE ->
                        values.forEach(v -> contentItems.add(new DateAttributeContentV3(LocalDate.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd")))));
                case TIME ->
                        values.forEach(v -> contentItems.add(new TimeAttributeContentV3(LocalTime.parse(v, DateTimeFormatter.ofPattern("HH:mm:ss")))));
                case DATETIME ->
                        values.forEach(v -> contentItems.add(new DateTimeAttributeContentV3(ZonedDateTime.parse(v, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")))));
                default ->
                        throw new IllegalStateException("Unexpected content type to parse from string: " + attributeContentType);
            }

            return contentItems;
        } catch (Exception e) {
            logger.debug("Cannot create content items of content type {} from string '{}'", attributeContentType, String.join(", ", values));
            return null;
        }
    }

    public static <T> List<T> getAttributeContentValue(String attributeName, List<?> attributes, Class<T> clazz) {
        List<T> content = AttributeDefinitionUtils.getAttributeContent(attributeName, attributes, clazz);
        return content;
    }

    public static <T> T getSingleItemAttributeContentValue(String attributeName, List<?> attributes, Class<T> clazz) {
        List<T> content = AttributeDefinitionUtils.getAttributeContent(attributeName, attributes, clazz);
        if (content != null && !content.isEmpty()) {
            return content.get(0);
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> getObjectAttributeContentData(String attributeName, List<?> attributes, Class<T> clazz) {
        List<ObjectAttributeContentV2> content = AttributeDefinitionUtils.getAttributeContent(attributeName, attributes, ObjectAttributeContentV2.class);
        List<T> response = new ArrayList<>();
        if (content != null) {
            for (ObjectAttributeContentV2 intContent : content) {
                response.add(ATTRIBUTES_OBJECT_MAPPER.convertValue(intContent.getData(), clazz));
            }
            return response;
        }
        return Collections.emptyList();
    }

    public static <T> List<T> getAttributeContentValueList(String attributeName, List<?> attributes, Class<?> clazz) {
        // TODO: validation that the attribute is multiSelect, if it make sense, because the request attribute can be without this flag
        List<?> list = getAttributeContent(attributeName, attributes, false);
        if (list != null) {
            List<T> listContent = new ArrayList<>();
            for (Object item : list) {
                BaseAttributeContentV2<?> ac = (BaseAttributeContentV2<?>) ATTRIBUTES_OBJECT_MAPPER.convertValue(item, clazz);
                listContent.add((T) ac.getData());
            }
            return listContent;
        }
        return null;
    }

    public static <T> List<T> getObjectAttributeContentDataList(String attributeName, List<?> attributes, Class<?> clazz) {
        // TODO: validation that the attribute is multiSelect, if it make sense, because the request attribute can be without this flag
        List<?> list = getAttributeContent(attributeName, attributes, false);
        if (list != null) {
            List<T> listContent = new ArrayList<>();
            for (Object item : list) {
                ObjectAttributeContentV2 ac = ATTRIBUTES_OBJECT_MAPPER.convertValue(item, ObjectAttributeContentV2.class);
                listContent.add((T) ATTRIBUTES_OBJECT_MAPPER.convertValue(ac.getData(), clazz));
            }
            return listContent;
        }
        return null;
    }

    /**
     * Function return true if the attributes are equal. And returns false if the attribute are not equal
     *
     * @param requestAttributes List of request attribute DTOs
     * @param attributes        List of attribute definitions
     * @return True if attribute is equal and false if attribute is not equal
     */
    public static boolean checkAttributeEquality(List<RequestAttribute> requestAttributes, List<DataAttribute> attributes) {
        for (RequestAttributeDto requestAttribute : requestAttributes) {
            DataAttribute attribute = attributes.stream().filter(x -> x.getName().equals(requestAttribute.getName())).findFirst().orElse(null);
            if (attribute == null) return false;
            if (requestAttribute.getVersion() == AttributeVersion.V2 && compareV2Equality(requestAttributes, attributes, requestAttribute, attribute)) return false;

            if (requestAttribute.getVersion() == AttributeVersion.V3 && compareV3Equality(requestAttributes, attributes, requestAttribute, attribute)) return false;


        }
        return true;
    }

    private static boolean compareV3Equality(List<RequestAttribute> requestAttributes, List<DataAttribute> attributes, RequestAttributeDto requestAttribute, DataAttribute attribute) {
        var attributeContent = getAttributeContent(requestAttribute.getName(), requestAttributes, attribute.getContentType().getContentV3Class());
        if (attributeContent == null) return true;
        return !attributeContent.equals(getAttributeContent(requestAttribute.getName(), attributes, attribute.getContentType().getContentV3Class()));
    }

    private static boolean compareV2Equality(List<RequestAttribute> requestAttributes, List<DataAttribute> attributes, RequestAttributeDto requestAttribute, DataAttribute attribute) {
        var attributeContent = getAttributeContent(requestAttribute.getName(), requestAttributes, attribute.getContentType().getContentV2Class());
        if (attributeContent == null) return true;
        return !attributeContent.equals(getAttributeContent(requestAttribute.getName(), attributes, attribute.getContentType().getContentV2Class()));
    }

}
