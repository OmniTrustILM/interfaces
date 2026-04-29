package com.otilm.util;

import com.otilm.api.model.common.attribute.v1.AttributeDefinition;
import com.otilm.core.deprecated.AttributeDefinitionUtils;
import com.otilm.core.util.AttributeMigrationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttributeMigrationUtilsTest {

    @Test
    public void testMultiSelectAttribute() throws JsonProcessingException {
        String attribute = "[{\"uuid\":\"87a94421-c5d8-4a23-bb2c-bbee76cb4eaa\",\"name\":\"caInstance\",\"label\":\"CA Instance\",\"type\":\"LIST\",\"required\":true,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":false,\"description\":null,\"validationRegex\":null,\"dependsOn\":null,\"attributeCallback\":null,\"value\":\"lab02-ADCS\"},{\"uuid\":\"25b35cf2-45cb-4e4c-b5b3-a99ecb8aa8e6\",\"name\":\"caAdcs\",\"label\":\"Certificate Authority\",\"type\":\"LIST\",\"required\":false,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":true,\"description\":null,\"validationRegex\":null,\"dependsOn\":null,\"attributeCallback\":{\"callbackContext\":\"/v1/discoveryProvider/listCertificateAuthority/{caInstance}\",\"callbackMethod\":\"GET\",\"mappings\":[{\"from\":\"caInstance\",\"attributeType\":null,\"to\":\"caInstance\",\"targets\":[\"pathVariable\"],\"value\":null}]},\"value\":[\"vmi307469.3key.local\\\\Demo MS Sub CA\"]},{\"uuid\":\"83c0f20b-4789-44f2-abd2-a84c131d5e97\",\"name\":\"template\",\"label\":\"Template\",\"type\":\"LIST\",\"required\":false,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":true,\"description\":null,\"validationRegex\":null,\"dependsOn\":null,\"attributeCallback\":{\"callbackContext\":\"/v1/discoveryProvider/listTemplate/{caInstance}\",\"callbackMethod\":\"GET\",\"mappings\":[{\"from\":\"caInstance\",\"attributeType\":null,\"to\":\"caInstance\",\"targets\":[\"pathVariable\"],\"value\":null}]},\"value\":[\"Administrator\",\"WebServer\",\"CodeSigning\"]}]";

        ObjectMapper mapper = new ObjectMapper();

        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        List<Map<String, Object>> oldAttributeValue = mapper.readValue(attribute, new TypeReference<>() {
        });
        for (Map<String, Object> item : oldAttributeValue) {
            attributeDefinitions.add(AttributeMigrationUtils.getNewAttributes(item));
        }
        String serializedAttributes = AttributeDefinitionUtils.serialize(attributeDefinitions);

        Assertions.assertNotNull(serializedAttributes);
        Assertions.assertTrue(attributeDefinitions.get(1).isMultiSelect());
    }

    @Test
    public void testMultiSelectAttributeJSON() throws JsonProcessingException {
        String attribute = "[{\"uuid\":\"83c0f20b-4789-44f2-abd2-a84c131d5e97\",\"name\":\"template\",\"label\":\"Template\",\"type\":\"LIST\",\"required\":false,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":true,\"description\":null,\"validationRegex\":null,\"dependsOn\":null,\"attributeCallback\":{\"callbackContext\":\"/v1/discoveryProvider/listTemplate/{caInstance}\",\"callbackMethod\":\"GET\",\"mappings\":[{\"from\":\"caInstance\",\"attributeType\":null,\"to\":\"caInstance\",\"targets\":[\"pathVariable\"],\"value\":null}]},\"value\":[{\"name\":\"testName\",\"uuid\":\"83c0f20b-4789-44f2-abd2-a84c131d5e97\"},{\"name\":\"testName\",\"uuid\":\"83c0f20b-4789-44f2-abd2-a84c131d5e97\"},{\"name\":\"testName\",\"uuid\":\"83c0f20b-4789-44f2-abd2-a84c131d5e97\"}]}]";
        ObjectMapper mapper = new ObjectMapper();

        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        List<Map<String, Object>> oldAttributeValue = mapper.readValue(attribute, new TypeReference<>() {
        });
        for (Map<String, Object> item : oldAttributeValue) {
            attributeDefinitions.add(AttributeMigrationUtils.getNewAttributes(item));
        }
        String serializedAttributes = AttributeDefinitionUtils.serialize(attributeDefinitions);
        Assertions.assertNotNull(serializedAttributes);
        Assertions.assertTrue(attributeDefinitions.get(0).isMultiSelect());
        Assertions.assertTrue(attributeDefinitions.get(0).getContent() instanceof List);
    }


    @Test
    public void testMultiSelectAttributeCredential() throws JsonProcessingException {
        String attribute = "[{\"uuid\":\"93ca0ba2-3863-4ffa-a469-fd14ab3992bf\",\"name\":\"address\",\"label\":\"MS-ADCS Address\",\"type\":\"STRING\",\"required\":true,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":false,\"description\":\"Address of ADCS server.\",\"validationRegex\":\"^((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])|(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\\\-]*[a-zA-Z0-9])\\\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\\\-]*[A-Za-z0-9]))$\",\"dependsOn\":null,\"attributeCallback\":null,\"value\":\"demo\"},{\"uuid\":\"d9f79ba6-47e5-437b-a7bc-82dbafa9cf01\",\"name\":\"https\",\"label\":\"HTTPS Enabled\",\"type\":\"BOOLEAN\",\"required\":true,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":false,\"description\":\"Use https for connection with ADCS server.\",\"validationRegex\":null,\"dependsOn\":null,\"attributeCallback\":null,\"value\":false},{\"uuid\":\"9587a320-a487-4084-9645-0b6c24636fa6\",\"name\":\"port\",\"label\":\"Port\",\"type\":\"NUMBER\",\"required\":true,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":false,\"description\":\"Define WinRM port, default port for http is 5985 and for https 5986.\",\"validationRegex\":null,\"dependsOn\":null,\"attributeCallback\":null,\"value\":5985},{\"uuid\":\"d9f79ba6-47e5-437b-a7bc-82dbafa9cf03\",\"name\":\"credential\",\"label\":\"Credential\",\"type\":\"CREDENTIAL\",\"required\":true,\"readOnly\":false,\"editable\":true,\"visible\":true,\"multiValue\":false,\"description\":\"Credential for the communication\",\"validationRegex\":null,\"dependsOn\":null,\"attributeCallback\":{\"callbackContext\":\"core/getCredentials\",\"callbackMethod\":\"GET\",\"mappings\":[{\"from\":null,\"attributeType\":null,\"to\":\"credentialKind\",\"targets\":[\"pathVariable\"],\"value\":\"Basic\"}]},\"value\":{\"uuid\":\"224c5e7e-b733-4441-922a-0158b4897886\",\"name\":\"lab02-ADCS\",\"kind\":\"Basic\",\"attributes\":[{\"uuid\":\"fe2d6d35-fb3d-4ea0-9f0b-7e39be93beeb\",\"name\":\"username\",\"label\":\"Username\",\"type\":\"STRING\",\"value\":\"userx\"},{\"uuid\":\"04506d45-c865-4ddc-b6fc-117ee5d5c8e7\",\"name\":\"password\",\"label\":\"Password\",\"type\":\"SECRET\",\"value\":\"password\"}],\"enabled\":true,\"connectorUuid\":\"2793f559-65b6-4f1a-8877-dc93bc596b41\",\"connectorName\":\"Common-Credential-Provider\"}}]";
        ObjectMapper mapper = new ObjectMapper();

        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();

        List<Map<String, Object>> oldAttributeValue = mapper.readValue(attribute, new TypeReference<>() {
        });
        for (Map<String, Object> item : oldAttributeValue) {
            attributeDefinitions.add(AttributeMigrationUtils.getNewAttributes(item));
        }
        String serializedAttributes = AttributeDefinitionUtils.serialize(attributeDefinitions);
        Assertions.assertNotNull(serializedAttributes);
    }
}
