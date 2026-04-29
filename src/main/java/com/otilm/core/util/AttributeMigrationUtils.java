package com.otilm.core.util;

import com.otilm.api.model.common.attribute.v1.AttributeCallback;
import com.otilm.api.model.common.attribute.v1.AttributeDefinition;
import com.otilm.api.model.common.attribute.v1.AttributeType;
import com.otilm.api.model.common.attribute.v1.content.BaseAttributeContent;
import com.otilm.api.model.common.attribute.v1.content.FileAttributeContent;
import com.otilm.api.model.common.attribute.v1.content.JsonAttributeContent;
import com.otilm.core.deprecated.AttributeDefinitionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeMigrationUtils {
    private static final Logger logger = LoggerFactory.getLogger(AttributeMigrationUtils.class);

    public static List<String> getMigrationCommands(ResultSet rows, String tableName, String columnName) throws SQLException, JsonProcessingException {
        logger.debug("Migrating Table: {}, Column: {}", tableName, columnName);
        ObjectMapper mapper = new ObjectMapper();
        List<String> migrationCommands = new ArrayList<>();
        while (rows.next()) {
            // the certificate_location table has composite key
            if (tableName.equals("certificate_location")) {
                logger.debug("Migrating record with Location Id: {}, Certificate Id: {}", rows.getString("location_id"), rows.getString("certificate_id"));
            } else {
                logger.debug("Migrating record with Id: {}", rows.getString("id"));
            }
            List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
            List<Map<String, Object>> oldAttributeValue = mapper.readValue(rows.getString(columnName), new TypeReference<>() {
            });
            for (Map<String, Object> item : oldAttributeValue) {
                attributeDefinitions.add(getNewAttributes(item));
            }
            String updateCommand;
            String serializedAttributes = AttributeDefinitionUtils.serialize(attributeDefinitions);
            if (tableName.equals("certificate_location")) {
                updateCommand = "UPDATE " + tableName + " SET " + columnName + " = '" + serializedAttributes + "' WHERE location_id = " + rows.getString("location_id") + " AND certificate_id = " + rows.getString("certificate_id") + ";";
            } else {
                updateCommand = "UPDATE " + tableName + " SET " + columnName + " = '" + serializedAttributes + "' WHERE id = " + rows.getString("id") + ";";
            }
            migrationCommands.add(updateCommand);
        }
        return migrationCommands;
    }

    public static AttributeDefinition getNewAttributes(Map<String, Object> oldAttribute) {
        AttributeDefinition attributeDefinition = new AttributeDefinition();
        attributeDefinition.setLabel(((String) oldAttribute.get("label")).replaceAll("'", "''"));
        attributeDefinition.setName(((String) oldAttribute.get("name")).replaceAll("'", "''"));
        attributeDefinition.setUuid((String) oldAttribute.get("uuid"));
        if (oldAttribute.get("readOnly") != null) {
            attributeDefinition.setReadOnly((Boolean) oldAttribute.get("readOnly"));
        }
        if (oldAttribute.get("required") != null) {
            attributeDefinition.setRequired((Boolean) oldAttribute.get("required"));
        }
        if (oldAttribute.get("description") != null) {
            attributeDefinition.setDescription(((String) oldAttribute.get("description")).replaceAll("'", "''"));
        }
        if (oldAttribute.get("visible") != null) {
            attributeDefinition.setVisible((Boolean) oldAttribute.get("visible"));
        }
        if (oldAttribute.get("multiValue") != null) {
            attributeDefinition.setList((Boolean) oldAttribute.get("multiValue"));
        }
        if (oldAttribute.get("validationRegex") != null) {
            attributeDefinition.setValidationRegex(((String) oldAttribute.get("validationRegex")).replaceAll("'", "''"));
        }
        if (oldAttribute.get("value") != null) {
            if(oldAttribute.get("type").equals("CREDENTIAL")){
                List<AttributeDefinition> innerAttributeDefinitions = new ArrayList<>();
                List<Map<String, Object>> oldAttributeValue = (List<Map<String, Object>>) ((Map<String, Object>) oldAttribute.get("value")).get("attributes");
                for (Map<String, Object> item : oldAttributeValue) {
                    innerAttributeDefinitions.add(getNewAttributes(item));
                }
                attributeDefinition.setMultiSelect(false);
                ((Map<String, Object>) oldAttribute.get("value")).put("attributes", innerAttributeDefinitions);
            }else {
                attributeDefinition.setMultiSelect(isMultiselect(oldAttribute.get("value")));
            }
            attributeDefinition.setContent(getAttributeValue(oldAttribute.get("value"), (String) oldAttribute.get("type")));
        }
        attributeDefinition.setType(getAttributeType(oldAttribute.get("value"), (String) oldAttribute.get("type")));

        if (oldAttribute.get("attributeCallback") != null) {
            ObjectMapper mapper = new ObjectMapper();
            String callbackAsString;
            try {
                callbackAsString = mapper.writeValueAsString(oldAttribute.get("attributeCallback"));
                AttributeCallback attributeCallback = mapper.readValue(callbackAsString, AttributeCallback.class);
                attributeDefinition.setAttributeCallback(attributeCallback);
            } catch (JsonProcessingException e) {
                logger.error("Unable to process callback data for attribute {}", oldAttribute.get("name"));
                throw new RuntimeException("Unable to process callback data for attribute " + oldAttribute.get("name"));
            }
        }
        return attributeDefinition;
    }

    private static AttributeType getAttributeType(Object oldValue, String oldAttributeType) {
        oldAttributeType = oldAttributeType.toUpperCase();
        if (oldAttributeType.equals("LIST")) {
            if (oldValue instanceof List) {
                if (((List) oldValue).isEmpty()) {
                    // If the old attribute is empty list, it is not possible to derive the original type.
                    // So STRING is used as default Attribute Type
                    return AttributeType.STRING;
                }
                if (((List<?>) oldValue).get(0) instanceof String) {
                    return AttributeType.STRING;
                } else if (((List<?>) oldValue).get(0) instanceof Integer) {
                    return AttributeType.INTEGER;
                } else if (((List<?>) oldValue).get(0) instanceof Float) {
                    return AttributeType.FLOAT;
                } else if (((List<?>) oldValue).get(0) instanceof Boolean) {
                    return AttributeType.BOOLEAN;
                } else if (((List<?>) oldValue).get(0) instanceof Map) {
                    return AttributeType.JSON;
                } else {
                    throw new RuntimeException("Unsupported attribute type in LIST: " + oldAttributeType);
                }
            } else if (oldValue instanceof Map) {
                return AttributeType.JSON;
            } else if (oldValue instanceof String) {
                return AttributeType.STRING;
            } else if (oldValue instanceof Integer) {
                return AttributeType.INTEGER;
            } else if (oldValue instanceof Float) {
                return AttributeType.FLOAT;
            } else if (oldValue instanceof Boolean) {
                return AttributeType.BOOLEAN;
            }
        }
        if (oldValue instanceof Integer) {
            return AttributeType.INTEGER;
        }
        if (oldValue instanceof Float) {
            return AttributeType.FLOAT;
        }

        return AttributeType.fromCode((oldAttributeType).toLowerCase());
    }

    private static Boolean isMultiselect(Object oldValue) {
        return oldValue instanceof List;
    }

    private static Object getAttributeValue(Object oldValue, String oldType) {
        oldType = oldType.toUpperCase();
        if (oldType.equals("FILE")) {
            FileAttributeContent content = new FileAttributeContent();
            content.setValue((String) oldValue);
            return content;
        }
        if (oldValue instanceof String) {
            return new BaseAttributeContent<>((String) oldValue);
        } else if (oldValue instanceof Float) {
            return new BaseAttributeContent<>((Float) oldValue);
        } else if (oldValue instanceof Boolean) {
            return new BaseAttributeContent<>((Boolean) oldValue);
        } else if (oldValue instanceof Integer) {
            return new BaseAttributeContent<>((Integer) oldValue);
        } else if (oldValue instanceof List || oldType.equals("LIST") || oldType.equals("CREDENTIAL")) {
            if (oldValue instanceof List) {
                if (((List) oldValue).isEmpty()) {
                    return new ArrayList<>();
                }
                if (((List<?>) oldValue).get(0) instanceof String) {
                    List<BaseAttributeContent<String>> multiObject = new ArrayList<>();
                    for (String innerValue : ((List<String>) oldValue)) {
                        multiObject.add(new BaseAttributeContent<>(innerValue));
                    }
                    return multiObject;
                } else if (((List<?>) oldValue).get(0) instanceof Integer) {
                    List<BaseAttributeContent<Integer>> multiObject = new ArrayList<>();
                    for (Integer innerValue : ((List<Integer>) oldValue)) {
                        multiObject.add(new BaseAttributeContent<>(innerValue));
                    }
                    return multiObject;
                } else if (((List<?>) oldValue).get(0) instanceof Float) {
                    List<BaseAttributeContent<Float>> multiObject = new ArrayList<>();
                    for (Float innerValue : ((List<Float>) oldValue)) {
                        multiObject.add(new BaseAttributeContent<>(innerValue));
                    }
                    return multiObject;
                } else if (((List<?>) oldValue).get(0) instanceof Boolean) {
                    List<BaseAttributeContent<Boolean>> multiObject = new ArrayList<>();
                    for (Boolean innerValue : ((List<Boolean>) oldValue)) {
                        multiObject.add(new BaseAttributeContent<>(innerValue));
                    }
                    return multiObject;
                } else if (((List<?>) oldValue).get(0) instanceof Map) {
                    List<JsonAttributeContent> multiObject = new ArrayList<>();
                    for (Map<String, Object> insValue : (List<Map<String, Object>>) oldValue) {
                        multiObject.add(getJsonAttribute(insValue));
                    }
                    return multiObject;
                }
            } else {
                return getJsonAttribute((Map<String, Object>) oldValue);
            }
        }
        throw new RuntimeException("Unsupported attribute type: " + oldType);
    }

    private static JsonAttributeContent getJsonAttribute(Map<String, Object> oldJsonValue) {
        String primKey;
        if (oldJsonValue.containsKey("name")) {
            primKey = (String) oldJsonValue.get("name");
        } else {
            try {
                primKey = (String) oldJsonValue.get(oldJsonValue.keySet().stream().findFirst().orElse(""));
            } catch (Exception e) {
                primKey = "error";
            }
        }
        String finalPrimKey = primKey;

        return new JsonAttributeContent(finalPrimKey, oldJsonValue);
    }
}
