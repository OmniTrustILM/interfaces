package com.otilm.core.util;

import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.v1.AttributeDefinition;
import com.otilm.api.model.common.attribute.v1.AttributeType;
import com.otilm.api.model.common.attribute.v1.content.BaseAttributeContent;
import com.otilm.api.model.common.attribute.v1.content.FileAttributeContent;
import com.otilm.api.model.common.attribute.v1.content.JsonAttributeContent;
import com.otilm.api.model.common.attribute.v2.BaseAttributeV2;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.MetadataAttributeV2;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallbackMapping;
import com.otilm.api.model.common.attribute.common.callback.AttributeValueTarget;
import com.otilm.api.model.common.attribute.common.constraint.BaseAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.RegexpAttributeConstraint;
import com.otilm.api.model.common.attribute.v2.content.*;
import com.otilm.api.model.common.attribute.common.content.data.CodeBlockAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.FileAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.SecretAttributeContentData;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.common.properties.MetadataAttributeProperties;
import com.otilm.core.deprecated.AttributeDefinitionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.InvalidMimeTypeException;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class V2AttributeMigrationUtils {
    private static final Logger logger = LoggerFactory.getLogger(V2AttributeMigrationUtils.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> getMigrationCommands(ResultSet rows, String tableName, String columnName, String rowIdentifier) throws SQLException, JsonProcessingException {
        logger.debug("Migrating Table: {}, Column: {}", tableName, columnName);
        List<String> migrationCommands = new ArrayList<>();
        while (rows.next()) {
            // the certificate_location table has composite key
            if (tableName.equals("certificate_location")) {
                logger.debug("Migrating record with Location id: {}, Certificate id: {}", rows.getString("location_uuid"), rows.getString("certificate_uuid"));
            } else {
                logger.debug("Migrating record with is: {}", rows.getString(rowIdentifier));
            }
            List<BaseAttribute> attributeDefinitions = new ArrayList<>();
            List<AttributeDefinition> oldAttributeValue = AttributeDefinitionUtils.deserialize(rows.getString(columnName));
            if (oldAttributeValue == null) {
                continue;
            }
            for (AttributeDefinition item : oldAttributeValue) {
                attributeDefinitions.add(getNewAttributes(item));
            }
            String updateCommand;
            String serializedAttributes = com.otilm.core.util.AttributeDefinitionUtils.serialize(attributeDefinitions);
            if (tableName.equals("certificate_location")) {
                updateCommand = "UPDATE " + tableName + " SET " + columnName + " = '" + serializedAttributes.replace("'", "") + "' WHERE location_uuid = '" + rows.getString("location_uuid") + "' AND certificate_uuid = '" + rows.getString("certificate_uuid") + "';";
            } else {
                updateCommand = "UPDATE " + tableName + " SET " + columnName + " = '" + serializedAttributes.replace("'", "") + "' WHERE " + rowIdentifier + " = '" + rows.getString(rowIdentifier) + "';";
            }
            migrationCommands.add(updateCommand);
        }
        return migrationCommands;
    }

    public static DataAttributeV2 getNewAttributes(AttributeDefinition oldAttribute) {

        //Old Attribute Value to new attribute properties

        DataAttributeV2 attribute = new DataAttributeV2();

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setList(oldAttribute.isList());
        properties.setMultiSelect(oldAttribute.isMultiSelect());
        properties.setReadOnly(oldAttribute.isReadOnly());
        properties.setRequired(oldAttribute.isRequired());
        properties.setLabel(oldAttribute.getLabel());
        properties.setVisible(oldAttribute.isVisible());
        properties.setGroup(oldAttribute.getGroup());

        attribute.setName(oldAttribute.getName());
        attribute.setUuid(oldAttribute.getUuid());
        attribute.setDescription(oldAttribute.getDescription());
        attribute.setType(com.otilm.api.model.common.attribute.common.AttributeType.DATA);
        attribute.setContentType(getAttributeContentType(oldAttribute.getType()));
        attribute.setContent(getAttributeContent(oldAttribute.getType() != null ? oldAttribute.getType() : AttributeType.STRING, oldAttribute.getContent()));
        attribute.setProperties(properties);
        attribute.setAttributeCallback(getAttributeCallback(oldAttribute.getAttributeCallback()));
        attribute.setConstraints(getAttributeConstraint(oldAttribute.getValidationRegex()));
        return attribute;
    }

    private static List<BaseAttributeConstraint<?>> getAttributeConstraint(String regex) {
        if (regex == null) {
            return null;
        }
        return List.of(new RegexpAttributeConstraint("", "", regex));
    }

    private static AttributeCallback getAttributeCallback(com.otilm.api.model.common.attribute.v1.AttributeCallback oldCallback) {
        if (oldCallback == null) {
            return null;
        }
        AttributeCallback callback = new AttributeCallback();
        callback.setCallbackContext(oldCallback.getCallbackContext());
        callback.setCallbackMethod(oldCallback.getCallbackMethod());
        Set<AttributeCallbackMapping> mappings = new HashSet<>();
        for (com.otilm.api.model.common.attribute.v1.AttributeCallbackMapping oldMapping : oldCallback.getMappings()) {
            AttributeCallbackMapping mapping = new AttributeCallbackMapping();
            mapping.setAttributeType(com.otilm.api.model.common.attribute.common.AttributeType.DATA);
            mapping.setAttributeContentType(getAttributeContentType(oldMapping.getAttributeType()));
            mapping.setFrom(oldMapping.getFrom());
            mapping.setTo(oldMapping.getTo());
            mapping.setTargets(oldMapping.getTargets().stream().map(e -> AttributeValueTarget.findByCode(e.getCode())).collect(Collectors.toSet()));
            mapping.setValue(oldMapping.getValue());
            mappings.add(mapping);
        }
        callback.setMappings(mappings);
        return callback;
    }

    private static AttributeContentType getAttributeContentType(AttributeType type) {
        if (type == null) {
            return null;
        }
        if (type.equals(AttributeType.JSON)) {
            return AttributeContentType.OBJECT;
        }
        return AttributeContentType.fromCode(type.getCode());
    }

    private static List<BaseAttributeContentV2<?>> getAttributeContent(AttributeType attributeType, Object oldContentData) {
        List<BaseAttributeContentV2<?>> attributeContents = new ArrayList<>();
        List<com.otilm.api.model.common.attribute.v1.content.BaseAttributeContent> oldContentListItems = new ArrayList<>();
        if (oldContentData == null) {
            return null;
        }
        if (!(oldContentData instanceof List)) {
            oldContentListItems.add(convertOldContents(attributeType, oldContentData));
        } else {
            ((List<?>) oldContentData).forEach(e -> oldContentListItems.add(convertOldContents(attributeType, e)));
        }
        for (com.otilm.api.model.common.attribute.v1.content.BaseAttributeContent oldContent : oldContentListItems) {
            switch (attributeType) {
                case STRING:
                    attributeContents.add(new StringAttributeContentV2(oldContent.getValue().toString(), oldContent.getValue().toString()));
                    break;
                case INTEGER:
                    attributeContents.add(new IntegerAttributeContentV2(oldContent.getValue().toString(), Integer.parseInt(oldContent.getValue().toString())));
                    break;
                case BOOLEAN:
                    getBooleanContent(oldContent, attributeContents);
                    break;
                case CREDENTIAL:
                    getCredentialContent((JsonAttributeContent) oldContent, attributeContents);
                    break;
                case DATE:
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    attributeContents.add(new DateAttributeContentV2(LocalDate.parse(oldContent.getValue().toString(), df)));
                    break;
                case DATETIME:
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                    ZonedDateTime z = ZonedDateTime.parse(oldContent.getValue().toString(), f);
                    attributeContents.add(new DateTimeAttributeContentV2(z));
                    break;
                case FLOAT:
                    attributeContents.add(new FloatAttributeContentV2(oldContent.getValue().toString(), (Float) oldContent.getValue()));
                    break;
                case TIME:
                    DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");
                    attributeContents.add(new TimeAttributeContentV2(LocalTime.parse(oldContent.getValue().toString(), tf)));
                    break;
                case TEXT:
                    attributeContents.add(new TextAttributeContentV2((String) oldContent.getValue()));
                    break;
                case SECRET:
                    attributeContents.add(new SecretAttributeContentV2("", new SecretAttributeContentData((String) oldContent.getValue())));
                    break;
                case JSON:
                    attributeContents.add(new ObjectAttributeContentV2(((JsonAttributeContent) oldContent).getValue(), (Serializable) ((JsonAttributeContent) oldContent).getData()));
                    break;
                case FILE:
                    getFileContent((FileAttributeContent) oldContent, attributeContents);
                    break;
                case CODEBLOCK:
                    CodeBlockAttributeContentData codeBlockAttributeContentData = (CodeBlockAttributeContentData) oldContent.getValue();
                    attributeContents.add(new CodeBlockAttributeContentV2("", new CodeBlockAttributeContentData(codeBlockAttributeContentData.getLanguage(), codeBlockAttributeContentData.getCode())));
                    break;
            }
        }
        return attributeContents;
    }

    private static void getFileContent(FileAttributeContent oldContent, List<BaseAttributeContentV2<?>> attributeContents) {
        FileAttributeContentData data = new FileAttributeContentData();
        FileAttributeContent oldContentFileData = oldContent;
        data.setContent(oldContentFileData.getValue());
        data.setFileName(oldContentFileData.getFileName());
        if (oldContentFileData.getContentType() != null && !oldContentFileData.getContentType().isEmpty())
            try {
                data.setMimeType(oldContentFileData.getContentType());
            } catch (InvalidMimeTypeException e) {
                //Do nothing
            }
        attributeContents.add(new FileAttributeContentV2(oldContentFileData.getFileName(), data));
    }

    private static void getCredentialContent(JsonAttributeContent oldContent, List<BaseAttributeContentV2<?>> attributeContents) {
        CredentialAttributeContentData credentialDto = new CredentialAttributeContentData();
        LinkedHashMap<String, Object> credentialData = (LinkedHashMap<String, Object>) oldContent.getData();
        credentialDto.setName((String) credentialData.get("name"));
        credentialDto.setUuid((String) credentialData.get("uuid"));
        credentialDto.setKind((String) credentialData.get("kind"));
        List<DataAttributeV2> credentialAttributes = new ArrayList<>();
        List<AttributeDefinition> oldCredentialAttributeValue = new ArrayList<>();
        try {
            oldCredentialAttributeValue = AttributeDefinitionUtils.deserialize(mapper.writeValueAsString(credentialData.get("attributes")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (oldCredentialAttributeValue == null) {
            oldCredentialAttributeValue = new ArrayList<>();
        }
        for (AttributeDefinition item : oldCredentialAttributeValue) {
            credentialAttributes.add(getNewAttributes(item));
        }
        credentialDto.setAttributes(credentialAttributes);
        attributeContents.add(new CredentialAttributeContentV2(oldContent.getValue(), credentialDto));
    }

    private static void getBooleanContent(BaseAttributeContent<?> oldContent, List<BaseAttributeContentV2<?>> attributeContents) {
        if (oldContent.getValue() instanceof Boolean) {
            attributeContents.add(new BooleanAttributeContentV2((Boolean) oldContent.getValue()));
        } else {
            String otherValue = oldContent.getValue().toString().toLowerCase();
            if (otherValue.equals("yes")) {
                attributeContents.add(new BooleanAttributeContentV2(true));
            } else {
                attributeContents.add(new BooleanAttributeContentV2(false));
            }
        }
    }

    private static com.otilm.api.model.common.attribute.v1.content.BaseAttributeContent convertOldContents(AttributeType attributeType, Object oldContentData) {
        if (oldContentData == null) {
            return null;
        }
        if (attributeType.equals(AttributeType.JSON)) {
            return mapper.convertValue(oldContentData, com.otilm.api.model.common.attribute.v1.content.JsonAttributeContent.class);
        } else if (attributeType.equals(AttributeType.CREDENTIAL)) {
            return mapper.convertValue(oldContentData, com.otilm.api.model.common.attribute.v1.content.JsonAttributeContent.class);
        } else if (attributeType.equals(AttributeType.FILE)) {
            return mapper.convertValue(oldContentData, com.otilm.api.model.common.attribute.v1.content.FileAttributeContent.class);
        } else {
            return mapper.convertValue(oldContentData, com.otilm.api.model.common.attribute.v1.content.BaseAttributeContent.class);
        }
    }

    // All the methods defined below will be used fot Metadata mirations

    public static List<String> getMetadataMigrationCommands(ResultSet rows, String tableName, String columnName, String rowIdentifier) throws SQLException, JsonProcessingException {
        logger.debug("Metadata - Migrating Table: {}, Column: {}", tableName, columnName);
        List<String> migrationCommands = new ArrayList<>();
        while (rows.next()) {
            // the certificate_location table has composite key
            if (tableName.equals("certificate_location")) {
                logger.debug("Metadata - Migrating record with Location id: {}, Certificate id: {}", rows.getString("location_uuid"), rows.getString("certificate_uuid"));
            } else {
                logger.debug("Metadata - Migrating record with is: {}", rows.getString(rowIdentifier));
            }

            List<MetadataAttributeV2> metadataDefinitions = new ArrayList<>();
            if (rows.getString(columnName) == null) {
                continue;
            }
            Map<String, Object> oldMetadata = mapper.readValue(rows.getString(columnName), new TypeReference<>() {
            });
            if (oldMetadata == null) {
                continue;
            }
            for (Map.Entry<String, Object> entry : oldMetadata.entrySet()) {
                metadataDefinitions.add(getMetadataAttribute(entry));
            }
            String updateCommand;
            String serializedMetadata = com.otilm.core.util.AttributeDefinitionUtils.serialize(metadataDefinitions);
            if (tableName.equals("certificate_location")) {
                updateCommand = "UPDATE " + tableName + " SET " + columnName + " = '" + serializedMetadata.replace("'", "") + "' WHERE location_uuid = '" + rows.getString("location_uuid") + "' AND certificate_uuid = '" + rows.getString("certificate_uuid") + "';";
            } else {
                updateCommand = "UPDATE " + tableName + " SET " + columnName + " = '" + serializedMetadata.replace("'", "") + "' WHERE " + rowIdentifier + " = '" + rows.getString(rowIdentifier) + "';";
            }
            migrationCommands.add(updateCommand);
        }
        return migrationCommands;
    }

    public static List<MetadataAttributeV2> getMetadataMigrationAttributes(String metadata) throws JsonProcessingException {
        List<MetadataAttributeV2> metadataDefinitions = new ArrayList<>();
        if (metadata == null) {
            return null;
        }
        Map<String, Object> oldMetadata = mapper.readValue(metadata, new TypeReference<>() {
        });
        if (oldMetadata == null) {
            return null;
        }
        for (Map.Entry<String, Object> entry : oldMetadata.entrySet()) {
            metadataDefinitions.add(getMetadataAttribute(entry));
        }
        return metadataDefinitions;
    }

    public static MetadataAttributeV2 getMetadataAttribute(Map.Entry<String, Object> oldMetadata) {
        MetadataAttributeV2 attribute = new MetadataAttributeV2();
        attribute.setUuid(null);
        attribute.setName(oldMetadata.getKey());

        //Attribute Properties
        MetadataAttributeProperties attributeProperties = new MetadataAttributeProperties();
        attributeProperties.setLabel(camelToHumanForm(oldMetadata.getKey()));
        attributeProperties.setVisible(true);
        attribute.setProperties(attributeProperties);
        attribute.setContentType(getMetadataAttributeType(oldMetadata.getValue()));

        attribute.setContent(getMetadataAttributeValue(oldMetadata.getValue()));
        attribute.setType(com.otilm.api.model.common.attribute.common.AttributeType.META);
        return attribute;
    }

    private static AttributeContentType getMetadataAttributeType(Object value) {
        if (value instanceof String) {
            return AttributeContentType.STRING;
        } else if (value instanceof Integer) {
            return AttributeContentType.INTEGER;
        } else if (value instanceof Float) {
            return AttributeContentType.FLOAT;
        } else if (value instanceof Boolean) {
            return AttributeContentType.BOOLEAN;
        } else {
            return AttributeContentType.OBJECT;
        }
    }

    private static List<BaseAttributeContentV2<?>> getMetadataAttributeValue(Object value) {
        if (value instanceof String) {
            String metadataValue = (String) value;
            return List.of(new StringAttributeContentV2(metadataValue, metadataValue));
        } else if (value instanceof Integer) {
            Integer metadataValue = (Integer) value;
            return List.of(new IntegerAttributeContentV2(metadataValue.toString(), metadataValue));
        } else if (value instanceof Float) {
            Float metadataValue = (Float) value;
            return List.of(new FloatAttributeContentV2(metadataValue.toString(), metadataValue));
        } else if (value instanceof Boolean) {
            Boolean metadataValue = (Boolean) value;
            return List.of(new BooleanAttributeContentV2(Boolean.TRUE.equals(metadataValue) ? "Yes" : "No", metadataValue));
        } else {
            return List.of(new ObjectAttributeContentV2((Serializable) value));
        }
    }


    public static String camelToHumanForm(String word) {
        return StringUtils.capitalize(
                StringUtils.join(
                        StringUtils.splitByCharacterTypeCamelCase(word),
                        ' '
                )
        );
    }

}
