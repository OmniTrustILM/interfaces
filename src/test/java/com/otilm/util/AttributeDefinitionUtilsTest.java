package com.otilm.util;

import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.common.NameAndIdDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallbackMapping;
import com.otilm.api.model.common.attribute.common.callback.AttributeValueTarget;
import com.otilm.api.model.common.attribute.common.callback.RequestAttributeCallback;
import com.otilm.api.model.common.attribute.common.constraint.DateTimeAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.RangeAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.RegexpAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.data.DateTimeAttributeConstraintData;
import com.otilm.api.model.common.attribute.common.constraint.data.RangeAttributeConstraintData;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.common.constraint.AttributeConstraintType;
import com.otilm.api.model.common.attribute.v2.content.*;
import com.otilm.core.util.AttributeDefinitionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.otilm.core.util.AttributeDefinitionUtils.*;

class AttributeDefinitionUtilsTest {

    @Test
    void testGetAttribute() {
        String attributeName = "testAttribute";
        List<RequestAttribute> attributes = List.of(new RequestAttributeV2(UUID.randomUUID(), attributeName, AttributeContentType.INTEGER, List.of(new IntegerAttributeContentV2(1234))));
        RequestAttributeV2 attribute = getRequestAttributes(attributeName, attributes);
        Assertions.assertNotNull(attribute);
        Assertions.assertTrue(containsRequestAttributes(attributeName, attributes));
        Assertions.assertEquals(attributes.get(0), attribute);
    }


    @Test
    void testGetAttributeContent() {
        String attribute1Name = "testAttribute1";
        String attribute2Name = "testAttribute2";

        DataAttributeV2 attribute1 = new DataAttributeV2();
        attribute1.setName(attribute1Name);
        attribute1.setContent(List.of(new IntegerAttributeContentV2(1234)));

        DataAttributeV2 attribute2 = new DataAttributeV2();
        attribute2.setName(attribute2Name);
        attribute2.setContent(List.of(new StringAttributeContentV2("value")));

        List<DataAttributeV2> attributes = List.of(attribute1, attribute2);

        Object value1 = AttributeDefinitionUtils.getAttributeContent(attribute1Name, attributes, false);
        Assertions.assertNotNull(value1);
        Assertions.assertTrue(containsAttributeDefinition(attribute1Name, attributes));
        Assertions.assertEquals(attribute1.getContent(), value1);

        Object value2 = AttributeDefinitionUtils.getAttributeContent(attribute2Name, attributes, false);
        Assertions.assertNotNull(value2);
        Assertions.assertTrue(containsAttributeDefinition(attribute2Name, attributes));
        Assertions.assertEquals(attribute2.getContent(), value2);

        Object value3 = AttributeDefinitionUtils.getAttributeContent("wrongName", attributes, false);
        Assertions.assertNull(value3);
        Assertions.assertFalse(containsAttributeDefinition("wrongName", attributes));
    }

    @Test
    void testGetAttributeNameAndUuidContent() {
        String attribute1Name = "testAttribute1";

        HashMap<String, Object> attribute2Value = new HashMap<>();
        attribute2Value.put("uuid", UUID.randomUUID().toString());
        attribute2Value.put("name", "testName");

        List<RequestAttribute> attributes = List.of(new RequestAttributeV2(UUID.randomUUID(), attribute1Name, AttributeContentType.OBJECT, List.of(new ObjectAttributeContentV2(attribute1Name, attribute2Value))));

        NameAndUuidDto dto = getNameAndUuidData(attribute1Name, attributes);

        Assertions.assertNotNull(dto);
        Assertions.assertEquals(attribute2Value.get("uuid"), dto.getUuid());
        Assertions.assertEquals(attribute2Value.get("name"), dto.getName());
    }

    @Test
    void testGetAttributeCredentialContent() {
        String attribute1Name = "testAttribute1";
        List<RequestAttribute> credentialAttributes = List.of(new RequestAttributeV2(UUID.randomUUID(), "credAttr", AttributeContentType.INTEGER, List.of(new IntegerAttributeContentV2(987))));


        CredentialAttributeContentData credentialDto = new CredentialAttributeContentData();
        credentialDto.setUuid(UUID.randomUUID().toString());
        credentialDto.setName("testName");
        DataAttributeV2 atr = new DataAttributeV2();
        atr.setContent(credentialAttributes.get(0).getContent());
        atr.setName(credentialAttributes.get(0).getName());
        atr.setUuid(String.valueOf(credentialAttributes.get(0).getUuid()));
        credentialDto.setAttributes(List.of(atr));

        List<RequestAttribute> attributes = List.of(new RequestAttributeV2(UUID.randomUUID(), attribute1Name, AttributeContentType.CREDENTIAL, List.of(new CredentialAttributeContentV2("testName", credentialDto))));
        CredentialAttributeContentData dto = getCredentialContent(attribute1Name, attributes);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(credentialDto.getUuid(), dto.getUuid());
        Assertions.assertEquals(credentialDto.getName(), dto.getName());
        Assertions.assertEquals(credentialAttributes.get(0).getName(), dto.getAttributes().get(0).getName());
    }

    @Test
//    @Disabled
    void testAttributeSerialization() {
        String attrData = """
                [
                   {
                     "name": "tokenType",
                     "content": [
                       {
                         "reference": "PEM",
                         "data": "PEM",
                         "contentType": "string"
                       }
                     ],
                     "type": "data",
                     "version": 3,
                     "contentType": "string"
                   },
                   {
                     "name": "description",
                     "content": [
                       {
                         "reference": "DEMO RA Profile",
                         "data": "DEMO RA Profile",
                         "contentType": "string"
                       }
                     ],
                     "contentType": "string",
                     "type": "data",
                     "version": 3
                   },
                   {
                     "name": "endEntityProfile",
                     "content": [
                       {
                         "reference": "DemoTLSServerEndEntityProfile",
                         "data": {
                           "id": 0,
                           "name": "DemoTLSServerEndEntityProfile"
                         },
                         "contentType": "object"
                       }
                     ],
                     "contentType": "object",
                     "type": "data",
                     "version": 3
                   },
                   {
                     "name": "certificateProfile",
                     "content": [
                       {
                         "reference": "DemoTLSServerEECertificateProfile",
                         "data": {
                           "id": 0,
                           "name": "DemoTLSServerEECertificateProfile"
                         },
                         "contentType": "object"
                       }
                     ],
                     "contentType": "object",
                     "type": "data",
                     "version": 3
                   },
                   {
                     "name": "certificationAuthority",
                     "content": [
                       {
                         "reference": "DemoServerSubCA",
                         "data": {
                           "id": 0,
                           "name": "DemoServerSubCA"
                         }
                       }
                     ],
                     "contentType": "object",
                     "type": "data",
                     "version": 2
                   },
                   {
                     "name": "sendNotifications",
                     "content": [
                       {
                         "reference": "",
                         "data": false,
                         "contentType": "boolean"
                       }
                     ],
                     "type": "data",
                     "version": 3,
                     "contentType": "boolean"
                   },
                   {
                     "name": "keyRecoverable",
                     "content": [
                       {
                         "reference": "",
                         "data": true,
                         "contentType": "boolean"
                       }
                     ],
                     "type": "data",
                     "version": 3,
                     "contentType": "boolean"
                   }
                 ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);
        Assertions.assertNotNull(attrs);
        Assertions.assertEquals(7, attrs.size());

        List<RequestAttribute> clientAttributes = AttributeDefinitionUtils.getClientAttributes(attrs);
        Assertions.assertEquals(7, clientAttributes.size());
        Assertions.assertTrue(clientAttributes.stream().allMatch(a -> a.getContentType() != null));

        NameAndIdDto endEntityProfile = getNameAndIdData("endEntityProfile", AttributeDefinitionUtils.getClientAttributes(attrs));
        Assertions.assertNotNull(endEntityProfile);
        Assertions.assertEquals(0, endEntityProfile.getId());
        Assertions.assertEquals("DemoTLSServerEndEntityProfile", endEntityProfile.getName());

        String serialized = serialize(attrs);
        Assertions.assertTrue(serialized.matches("^.*\"name\":\"tokenType\".*\"content\":.*\"data\":\"PEM\".*$"));
        Assertions.assertTrue(serialized.matches("^.*\"name\":\"keyRecoverable\".*\"data\":true.*$"));
    }

    @Test
    void testValidateAttributes_success() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        StringAttributeContentV2 attributeContent = new StringAttributeContentV2("1234");

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_failNoAttribute() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                validateAttributes(List.of(definition), List.of())
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_failNoValue() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(null); // cause or failure

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_regex() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        StringAttributeContentV2 attributeContent = new StringAttributeContentV2("1234");
        String validationRegex = "^\\d{4}$";

        //TODO Validation

        Assertions.assertTrue(attributeContent.getData().matches(validationRegex));

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_regexFail() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        StringAttributeContentV2 attributeContent = new StringAttributeContentV2("12345");
        String validationRegex = "^\\d{4}$";

        RegexpAttributeConstraint constraint = new RegexpAttributeConstraint();
        constraint.setData(validationRegex);

        Assertions.assertFalse(attributeContent.getData().matches(validationRegex));

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));

        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_regexPass() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        StringAttributeContentV2 attributeContent = new StringAttributeContentV2("1234");
        String validationRegex = "^\\d{4}$";

        RegexpAttributeConstraint constraint = new RegexpAttributeConstraint();
        constraint.setData(validationRegex);

        Assertions.assertTrue(attributeContent.getData().matches(validationRegex));

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_IntegerRange() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        IntegerAttributeContentV2 attributeContent = new IntegerAttributeContentV2(1234);

        RangeAttributeConstraint constraint = new RangeAttributeConstraint();

        RangeAttributeConstraintData data = new RangeAttributeConstraintData();
        data.setFrom(100);
        data.setTo(2000);
        constraint.setData(data);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.INTEGER);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_IntegerRangeFail() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        IntegerAttributeContentV2 attributeContent = new IntegerAttributeContentV2(2001);

        RangeAttributeConstraint constraint = new RangeAttributeConstraint();

        RangeAttributeConstraintData data = new RangeAttributeConstraintData();
        data.setFrom(100);
        data.setTo(2000);
        constraint.setData(data);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.INTEGER);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_IntegerRangeTypeFail() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        StringAttributeContentV2 attributeContent = new StringAttributeContentV2("2001");

        RangeAttributeConstraint constraint = new RangeAttributeConstraint();

        RangeAttributeConstraintData data = new RangeAttributeConstraintData();
        data.setFrom(100);
        data.setTo(2000);
        constraint.setData(data);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_FloatRange() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        FloatAttributeContentV2 attributeContent = new FloatAttributeContentV2(121.34f);

        RangeAttributeConstraint constraint = new RangeAttributeConstraint();

        RangeAttributeConstraintData data = new RangeAttributeConstraintData();
        data.setFrom(100);
        data.setTo(2000);
        constraint.setData(data);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.FLOAT);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_FloatRangeFail() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        FloatAttributeContentV2 attributeContent = new FloatAttributeContentV2(20.01f);

        RangeAttributeConstraint constraint = new RangeAttributeConstraint();

        RangeAttributeConstraintData data = new RangeAttributeConstraintData();
        data.setFrom(100);
        data.setTo(2000);
        constraint.setData(data);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.FLOAT);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_DateTime() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        DateTimeAttributeContentV2 attributeContent = new DateTimeAttributeContentV2(ZonedDateTime.now());

        DateTimeAttributeConstraint constraint = new DateTimeAttributeConstraint();

        DateTimeAttributeConstraintData data = new DateTimeAttributeConstraintData();
        data.setFrom(LocalDateTime.now().minusMinutes(10));
        data.setTo(LocalDateTime.now().plusMinutes(10));
        constraint.setData(data);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.DATETIME);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_DateTimeFailure() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        DateTimeAttributeContentV2 attributeContent = new DateTimeAttributeContentV2(ZonedDateTime.now());

        DateTimeAttributeConstraint constraint = new DateTimeAttributeConstraint();

        DateTimeAttributeConstraintData data = new DateTimeAttributeConstraintData();
        data.setFrom(LocalDateTime.now().plusMinutes(5));
        data.setTo(LocalDateTime.now().plusMinutes(10));
        constraint.setData(data);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setUuid(attributeId);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.DATETIME);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setUuid(UUID.fromString(attributeId));
        attribute.setContent(List.of(attributeContent));
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }


    @Test
    void testValidateAttributes_credentialMap() {
        String attributeName = "testAttribute1";

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.CREDENTIAL);

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);

        CredentialAttributeContentData credential = new CredentialAttributeContentData();
        credential.setName("testName");
        credential.setUuid("testUuid");

        CredentialAttributeContentV2 credentialContent = new CredentialAttributeContentV2("Test Credential", credential);

        attribute.setContent(List.of(credentialContent));

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_credentialDto() {
        String attributeName = "testAttribute1";

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.CREDENTIAL);

        CredentialAttributeContentV2 credentialContent = new CredentialAttributeContentV2(attributeName, new CredentialAttributeContentData());

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setContent(List.of(credentialContent));

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_credentialFail() {
        String attributeName = "testAttribute1";

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.CREDENTIAL);

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setContent(List.of(new IntegerAttributeContentV2(123))); // cause or failure
        ValidationException exception = Assertions.assertThrows(ValidationException.class, () ->
                // tested method
                validateAttributes(List.of(definition), List.of(attribute))
        );

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_unknownAttribute() {
        String attributeName = "testAttribute1";

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName("unknown-attribute");  // cause or failure
        attribute.setContent(List.of(new StringAttributeContentV2("123")));

        Assertions.assertDoesNotThrow(() -> validateAttributes(List.of(definition), List.of(attribute)));
    }

    @Test
    void testValidateAttributeCallback_success() {
        Set<AttributeCallbackMapping> mappings = new HashSet<>();
        mappings.add(new AttributeCallbackMapping(
                "credentialKind",
                AttributeValueTarget.PATH_VARIABLE,
                "softKeyStore"));

        AttributeCallback callback = new AttributeCallback();
        callback.setCallbackContext("v1/test");
        callback.setCallbackMethod("GET");
        callback.setMappings(mappings);

        RequestAttributeCallback callbackRequest = new RequestAttributeCallback();
        callbackRequest.setPathVariable(Map.ofEntries(Map.entry("credentialKind", "softKeyStore")));

        validateCallback(callback, callbackRequest, false); // should not throw exception

        callback.setCallbackContext(null);
        callback.setCallbackMethod(null);

        Assertions.assertThrows(ValidationException.class, () -> validateCallback(callback, callbackRequest, false));
        validateCallback(callback, callbackRequest, true);
    }

    @Test
    void testValidateAttributeCallback_fail() {
        Set<AttributeCallbackMapping> mappings = new HashSet<>();
        mappings.add(new AttributeCallbackMapping(
                "credentialKind",
                AttributeValueTarget.PATH_VARIABLE,
                "softKeyStore"));
        mappings.add(new AttributeCallbackMapping(
                "fromAttribute",
                AttributeType.DATA,
                AttributeContentType.CREDENTIAL,
                "toQueryParam",
                Collections.singleton(AttributeValueTarget.REQUEST_PARAMETER)));
        mappings.add(new AttributeCallbackMapping(
                "fromAttribute",
                "toBodyKey",
                AttributeValueTarget.BODY));

        AttributeCallback callback = new AttributeCallback();
        callback.setCallbackContext("core/getCredentials");
        callback.setCallbackMethod("GET");
        callback.setMappings(mappings);

        RequestAttributeCallback callbackRequest = new RequestAttributeCallback();
        callbackRequest.setPathVariable(Map.ofEntries(Map.entry("credentialKind", "softKeyStore")));
        callbackRequest.setRequestParameter(Map.ofEntries(Map.entry("toQueryParam", 1234)));


        ValidationException exception = Assertions.assertThrows(ValidationException.class, () -> validateCallback(callback, callbackRequest, false));

        Assertions.assertNotNull(exception.getErrors());
        Assertions.assertFalse(exception.getErrors().isEmpty());
        Assertions.assertEquals(2, exception.getErrors().size());
    }

    @Test
    void testGetAttributeJsonContent_success() {
        String attrData = """
                [
                  {
                    "name": "testJsonAttribute",
                    "content": [
                      {
                        "reference": "Item",
                        "data": {
                          "customField": 1234,
                          "exists": true,
                          "name": "testingName"
                        },
                        "contentType": "object"
                      }
                    ],
                    "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        ObjectAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, ObjectAttributeContentV2.class).get(0);

        Assertions.assertEquals("Item", data.getReference());
    }

    @Test
    void testGetAttributeStringContent_success() {
        String attrData = """
                [
                  {
                    "name": "testJsonAttribute",
                    "content": [
                      {
                        "data": "Item",
                        "contentType": "string"
                      }
                    ],
                    "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        StringAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, StringAttributeContentV2.class).get(0);

        Assertions.assertEquals("Item", data.getData());
    }

    @Test
    void testGetAttributeIntegerContent_success() {
        String attrData = """
                [
                  {
                    "name": "testJsonAttribute",
                    "content": [{
                      "data": 1234,
                      "contentType": "integer"
                    }],
                     "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        IntegerAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, IntegerAttributeContentV2.class).get(0);

        Assertions.assertEquals(1234, data.getData());
    }

    @Test
    void testGetAttributeDateTimeContent_success() {
        String attrData = """
                [
                  {
                    "name": "testJsonAttribute",
                    "content": [{
                      "data": "2011-12-03T10:15:30+01:00",
                      "contentType": "datetime"
                    }],
                     "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);


        DateTimeAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, DateTimeAttributeContentV2.class).get(0);

        String dateInString = "2011-12-03T10:15:30+01:00";
        ZonedDateTime date = ZonedDateTime.parse(dateInString, DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        Assertions.assertEquals(date, data.getData());
    }

    @Test
    void testGetAttributeDateContent_success() {
        String attrData = """
                [
                  {
                    "name": "testJsonAttribute",
                    "content": [{
                      "data": "2001-07-04",
                      "contentType": "date"
                    }],
                     "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        DateAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, DateAttributeContentV2.class).get(0);

        String dateInString = "2001-07-04";
        LocalDate localDate = LocalDate.parse(dateInString);

        Assertions.assertEquals(localDate, data.getData());
    }

    @Test
    void testGetAttributeTimeContent_success() {
        String attrData = """
                [
                  {
                    "name": "testJsonAttribute",
                    "content": [{
                      "data": "12:14:25",
                      "contentType": "time"
                    }],
                    "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        TimeAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, TimeAttributeContentV2.class).get(0);

        String dateInString = "12:14:25";
        LocalTime localTime = LocalTime.parse(dateInString);

        Assertions.assertEquals(localTime, data.getData());
    }

    @Test
    void testGetStringAttributeContentValue_success() {
        String attrData = """
                [
                  {
                    "name": "testStringAttribute",
                    "content": [{
                      "data": "Test String Value",
                      "contentType": "string"
                    }],
                     "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        String value = getAttributeContentValue("testStringAttribute", attrs, StringAttributeContentV2.class).get(0).getData();

        Assertions.assertNotNull(value);
        Assertions.assertEquals("Test String Value", value);
    }

    @Test
    void testGetFileAttributeContentValue_success() {
        String attrData = """
                [
                  {
                    "name": "testFileAttribute",
                    "content": [{
                      "data": {"content":"Test File Value"},
                      "contentType": "file"
                    }],
                     "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        String value = getAttributeContentValue("testFileAttribute", attrs, FileAttributeContentV2.class).get(0).getData().getContent();

        Assertions.assertNotNull(value);
        Assertions.assertEquals("Test File Value", value);
    }

    @Test
    void testGetCredentialAttributeContent_success() {
        String attrData = """
                [
                  {
                    "name": "testCredentialAttribute",
                    "content": [
                      {
                        "reference": "Test Credential Value",
                        "data": {
                          "uuid": "9379ca2c-aa51-42c8-8afd-2a2d16c99c57",
                          "name": "Test Credential"
                        },
                        "contentType": "object"
                      }
                    ],
                    "type": "data",
                    "version": 2
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        NameAndUuidDto data = getObjectAttributeContentData("testCredentialAttribute", attrs, NameAndUuidDto.class).get(0);

        Assertions.assertNotNull(data);
        Assertions.assertEquals(data.getClass(), NameAndUuidDto.class);
    }

    @Test
    void testGetAttributeContentAsListOfString_success() {
        String attrData = """
                [
                  {
                    "name": "testAttributeListString",
                    "type": "data",
                    "version": 2,
                    "content": [
                      {
                        "data": "string1",
                        "contentType": "string"
                      },
                      {
                        "data": "string2",
                        "contentType": "string"
                      },
                      {
                        "data": "string3",
                        "contentType": "string"
                      }
                    ]
                  }
                ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        List<String> listString = getAttributeContentValueList("testAttributeListString", attrs, BaseAttributeContentV2.class);

        Assertions.assertNotNull(listString);
        Assertions.assertEquals(3, listString.size());
    }

    @Test
    void testGetJsonAttributeContentAsListOfUuidAndName_success() {
        String attrData = """
                 [
                   {
                     "name": "testCredentialAttribute",
                     "content": [
                       {
                         "reference": "Test Credential Value 1",
                         "data": {
                           "uuid": "9379ca2c-aa51-42c8-8afd-2a2d16c99c57",
                           "name": "Test Credential 1"
                         },
                         "contentType": "object"
                       },
                       {
                         "reference": "Test Credential Value 2",
                         "data": {
                           "uuid": "696a354f-55d2-4507-b454-a5a7475a7932",
                           "name": "Test Credential 2"
                         },
                        "contentType": "object"
                       }
                     ],
                      "type": "data",
                    "version": 2
                   }
                 ]
                """;

        List<BaseAttribute> attrs = deserialize(attrData, BaseAttribute.class);

        List<String> listData = getObjectAttributeContentDataList("testCredentialAttribute", attrs, NameAndUuidDto.class);

        Assertions.assertNotNull(listData);
        Assertions.assertEquals(2, listData.size());
    }

    @Test
    void testGetJsonAttributeConstraints_success() {
        String regExp = "^((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])|(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\\\-]*[a-zA-Z0-9])\\\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\\\-]*[A-Za-z0-9]))$";

        String attrData = """
                [
                  {
                    "uuid": "93ca0ba2-3863-4ffa-a469-fd14ab3992bf",
                    "name": "address",
                    "type": "data",
                    "contentType": "string",
                    "properties": {
                       "label": "Test Credential Value 1",
                       "visible": true,
                       "required": true
                    },
                    "constraints": [
                      {
                       "type": "regExp",
                       "data": "%s"
                      }
                    ],
                    "content": [
                      {
                        "reference": "Test Credential Value 1",
                        "data": {
                          "uuid": "9379ca2c-aa51-42c8-8afd-2a2d16c99c57",
                          "name": "Test Credential 1"
                        },
                        "contentType": "object"
                      },
                      {
                        "reference": "Test Credential Value 2",
                        "data": {
                          "uuid": "696a354f-55d2-4507-b454-a5a7475a7932",
                          "name": "Test Credential 2"
                        },
                        "contentType": "object"
                      }
                    ]
                  }
                ]
                """.formatted(regExp);

        List<DataAttributeV2> attrs = deserialize(attrData, DataAttributeV2.class);
        Assertions.assertEquals(1, attrs.size());
        Assertions.assertEquals(1, attrs.get(0).getConstraints().size());
        Assertions.assertEquals(AttributeConstraintType.REGEXP, attrs.get(0).getConstraints().get(0).getType());
    }
}
