package com.otilm.util;

import com.otilm.api.exception.ValidationError;
import com.otilm.api.exception.ValidationException;
import com.otilm.api.model.client.attribute.RequestAttribute;
import com.otilm.api.model.client.attribute.RequestAttributeV2;
import com.otilm.api.model.client.attribute.ResponseAttributeV2;
import com.otilm.api.model.common.NameAndIdDto;
import com.otilm.api.model.common.NameAndUuidDto;
import com.otilm.api.model.common.attribute.common.AttributeType;
import com.otilm.api.model.common.attribute.common.BaseAttribute;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallback;
import com.otilm.api.model.common.attribute.common.callback.AttributeCallbackMapping;
import com.otilm.api.model.common.attribute.common.callback.AttributeValueTarget;
import com.otilm.api.model.common.attribute.common.callback.RequestAttributeCallback;
import com.otilm.api.model.common.attribute.common.constraint.AttributeConstraintType;
import com.otilm.api.model.common.attribute.common.constraint.DateTimeAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.JsonSchemaAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.RangeAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.RegexpAttributeConstraint;
import com.otilm.api.model.common.attribute.common.constraint.data.DateTimeAttributeConstraintData;
import com.otilm.api.model.common.attribute.common.constraint.data.RangeAttributeConstraintData;
import com.otilm.api.model.common.attribute.common.content.AttributeContentType;
import com.otilm.api.model.common.attribute.common.content.data.CredentialAttributeContentData;
import com.otilm.api.model.common.attribute.common.content.data.SecretAttributeContentData;
import com.otilm.api.model.common.attribute.common.properties.DataAttributeProperties;
import com.otilm.api.model.common.attribute.v2.DataAttributeV2;
import com.otilm.api.model.common.attribute.v2.content.BaseAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.CredentialAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.DateAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.DateTimeAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.FileAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.FloatAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.IntegerAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.ObjectAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.SecretAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.StringAttributeContentV2;
import com.otilm.api.model.common.attribute.v2.content.TimeAttributeContentV2;
import com.otilm.core.util.AttributeDefinitionUtils;
import com.sun.net.httpserver.HttpServer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.otilm.core.util.AttributeDefinitionUtils.containsAttributeDefinition;
import static com.otilm.core.util.AttributeDefinitionUtils.containsRequestAttributes;
import static com.otilm.core.util.AttributeDefinitionUtils.deserialize;
import static com.otilm.core.util.AttributeDefinitionUtils.getAttributeContent;
import static com.otilm.core.util.AttributeDefinitionUtils.getAttributeContentValue;
import static com.otilm.core.util.AttributeDefinitionUtils.getAttributeContentValueList;
import static com.otilm.core.util.AttributeDefinitionUtils.getCredentialContent;
import static com.otilm.core.util.AttributeDefinitionUtils.getNameAndIdData;
import static com.otilm.core.util.AttributeDefinitionUtils.getNameAndUuidDataList;
import static com.otilm.core.util.AttributeDefinitionUtils.getObjectAttributeContentData;
import static com.otilm.core.util.AttributeDefinitionUtils.getObjectAttributeContentDataList;
import static com.otilm.core.util.AttributeDefinitionUtils.getRequestAttributes;
import static com.otilm.core.util.AttributeDefinitionUtils.serialize;
import static com.otilm.core.util.AttributeDefinitionUtils.validateAttributes;
import static com.otilm.core.util.AttributeDefinitionUtils.validateCallback;

class AttributeDefinitionUtilsTest {

    @Test
    void testGetAttribute() {
        String attributeName = "testAttribute";
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV2(UUID.randomUUID(), attributeName, AttributeContentType.INTEGER,
                        List.of(new IntegerAttributeContentV2(1234))));
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

        HashMap<String, Object> attribute1Value = new HashMap<>();
        attribute1Value.put("uuid", UUID.randomUUID().toString());
        attribute1Value.put("name", "testName");

        HashMap<String, Object> attribute2Value = new HashMap<>();
        attribute2Value.put("uuid", UUID.randomUUID().toString());
        attribute2Value.put("name", "testName2");
        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV2(UUID.randomUUID(), attribute1Name, AttributeContentType.OBJECT,
                        List
                                .of(new ObjectAttributeContentV2(attribute1Name, attribute1Value),
                                        new ObjectAttributeContentV2(attribute1Name, attribute2Value))));
        List<NameAndUuidDto> dto2 = getNameAndUuidDataList(attribute1Name, attributes);

        Assertions.assertNotNull(dto2);
        Assertions.assertEquals(2, dto2.size());
        Assertions.assertEquals(attribute1Value.get("uuid"), dto2.get(0).getUuid());
        Assertions.assertEquals(attribute1Value.get("name"), dto2.get(0).getName());
        Assertions.assertEquals(attribute2Value.get("uuid"), dto2.get(1).getUuid());
        Assertions.assertEquals(attribute2Value.get("name"), dto2.get(1).getName());
    }

    @Test
    void testGetAttributeCredentialContent() {
        String attribute1Name = "testAttribute1";
        List<RequestAttribute> credentialAttributes = List
                .of(new RequestAttributeV2(UUID.randomUUID(), "credAttr", AttributeContentType.INTEGER,
                        List.of(new IntegerAttributeContentV2(987))));

        CredentialAttributeContentData credentialDto = new CredentialAttributeContentData();
        credentialDto.setUuid(UUID.randomUUID().toString());
        credentialDto.setName("testName");
        DataAttributeV2 atr = new DataAttributeV2();
        atr.setContent(credentialAttributes.get(0).getContent());
        atr.setName(credentialAttributes.get(0).getName());
        atr.setUuid(String.valueOf(credentialAttributes.get(0).getUuid()));
        credentialDto.setAttributes(List.of(atr));

        List<RequestAttribute> attributes = List
                .of(new RequestAttributeV2(UUID.randomUUID(), attribute1Name, AttributeContentType.CREDENTIAL,
                        List.of(new CredentialAttributeContentV2("testName", credentialDto))));
        CredentialAttributeContentData dto = getCredentialContent(attribute1Name, attributes);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(credentialDto.getUuid(), dto.getUuid());
        Assertions.assertEquals(credentialDto.getName(), dto.getName());
        Assertions.assertEquals(credentialAttributes.get(0).getName(), dto.getAttributes().get(0).getName());
    }

    @Test
    // @Disabled
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

        NameAndIdDto endEntityProfile = getNameAndIdData("endEntityProfile",
                AttributeDefinitionUtils.getClientAttributes(attrs));
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

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class, () -> validateAttributes(List.of(definition), List.of()));

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
        validateAttributes(List.of(definition), List.of(attribute)));

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    @Test
    void testValidateAttributes_regex() {
        String attributeName = "testAttribute1";
        String attributeId = "9379ca2c-aa51-42c8-8afd-2a2d16c99c57";
        StringAttributeContentV2 attributeContent = new StringAttributeContentV2("1234");
        String validationRegex = "^\\d{4}$";

        // TODO Validation

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
        validateAttributes(List.of(definition), List.of(attribute)));

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
        validateAttributes(List.of(definition), List.of(attribute)));

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
        validateAttributes(List.of(definition), List.of(attribute)));

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
        validateAttributes(List.of(definition), List.of(attribute)));

        Assertions.assertEquals(1, exception.getErrors().size());
    }

    private static List<String> rangeErrors(AttributeContentType contentType, Integer from, Integer to,
            String errorMessage, BaseAttributeContentV2<?> content) {
        RangeAttributeConstraintData data = new RangeAttributeConstraintData();
        data.setFrom(from);
        data.setTo(to);
        RangeAttributeConstraint constraint = new RangeAttributeConstraint();
        constraint.setData(data);
        constraint.setErrorMessage(errorMessage);

        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setLabel("Parallel executions");
        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName("parallelExecutions");
        definition.setType(AttributeType.DATA);
        definition.setContentType(contentType);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        return AttributeDefinitionUtils
                .validateConstraints(definition, List.of(content))
                .stream()
                .map(ValidationError::getErrorDescription)
                .toList();
    }

    @Test
    void testValidateConstraints_RangeBoundsAreInclusive() {
        Assertions
                .assertEquals(List.of(),
                        rangeErrors(AttributeContentType.INTEGER, 1, 100, null, new IntegerAttributeContentV2(1)));
        Assertions
                .assertEquals(List.of(),
                        rangeErrors(AttributeContentType.INTEGER, 1, 100, null, new IntegerAttributeContentV2(100)));
        Assertions
                .assertEquals(List.of(),
                        rangeErrors(AttributeContentType.FLOAT, 1, 100, null, new FloatAttributeContentV2(100f)));
    }

    @Test
    void testValidateConstraints_RangeMessageStatesInclusiveBounds() {
        Assertions
                .assertEquals(List.of("Value 101 of attribute Parallel executions must be between 1 and 100"),
                        rangeErrors(AttributeContentType.INTEGER, 1, 100, null, new IntegerAttributeContentV2(101)));
        Assertions
                .assertEquals(List.of("Value 0 of attribute Parallel executions must be between 1 and 100"),
                        rangeErrors(AttributeContentType.INTEGER, 1, 100, null, new IntegerAttributeContentV2(0)));
        Assertions
                .assertEquals(List.of("Value 100.5 of attribute Parallel executions must be between 1 and 100"),
                        rangeErrors(AttributeContentType.FLOAT, 1, 100, null, new FloatAttributeContentV2(100.5f)));
    }

    @Test
    void testValidateConstraints_RangeMessageWithOneBound() {
        Assertions
                .assertEquals(List.of("Value 0 of attribute Parallel executions must be at least 1"),
                        rangeErrors(AttributeContentType.INTEGER, 1, null, null, new IntegerAttributeContentV2(0)));
        Assertions
                .assertEquals(List.of("Value 101 of attribute Parallel executions must be at most 100"),
                        rangeErrors(AttributeContentType.INTEGER, null, 100, null, new IntegerAttributeContentV2(101)));
    }

    @Test
    void testValidateConstraints_RangeMessageUsesConstraintErrorMessage() {
        Assertions
                .assertEquals(List
                        .of("Value 101 of attribute Parallel executions is out of range: it can be between 1 and 100"),
                        rangeErrors(AttributeContentType.INTEGER, 1, 100, "it can be between 1 and 100",
                                new IntegerAttributeContentV2(101)));
        Assertions
                .assertEquals(List.of("Value 101 of attribute Parallel executions must be between 1 and 100"),
                        rangeErrors(AttributeContentType.INTEGER, 1, 100, " ", new IntegerAttributeContentV2(101)));
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
        validateAttributes(List.of(definition), List.of(attribute)));

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

        CredentialAttributeContentV2 credentialContent = new CredentialAttributeContentV2("Test Credential",
                credential);

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

        CredentialAttributeContentV2 credentialContent = new CredentialAttributeContentV2(attributeName,
                new CredentialAttributeContentData());

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setContent(List.of(credentialContent));

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_credentialWithNestedProviderAttributes() {
        // Mirrors what core injects before forwarding to a connector: the credential content
        // carries the full credential, including its provider attributes as nested DataAttributeV2.
        String attributeName = "credential";

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName(attributeName);
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.CREDENTIAL);

        DataAttributeV2 keyStoreType = new DataAttributeV2();
        keyStoreType.setName("keyStoreType");
        keyStoreType.setType(AttributeType.DATA);
        keyStoreType.setContentType(AttributeContentType.STRING);
        keyStoreType.setContent(List.of(new StringAttributeContentV2("PKCS12")));

        DataAttributeV2 keyStorePassword = new DataAttributeV2();
        keyStorePassword.setName("keyStorePassword");
        keyStorePassword.setType(AttributeType.DATA);
        keyStorePassword.setContentType(AttributeContentType.SECRET);
        SecretAttributeContentData secret = new SecretAttributeContentData();
        secret.setSecret("s3cr3t");
        keyStorePassword.setContent(List.of(new SecretAttributeContentV2("pwd", secret)));

        CredentialAttributeContentData credential = new CredentialAttributeContentData();
        credential.setUuid("3ca21efc-75cc-42f0-b250-47fc722ec3f7");
        credential.setName("ewgwefe");
        credential.setKind("SoftKeyStore");
        credential.setAttributes(List.of(keyStoreType, keyStorePassword));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(attributeName);
        attribute.setContent(List.of(new CredentialAttributeContentV2("ewgwefe", credential)));

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
        validateAttributes(List.of(definition), List.of(attribute)));

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
        attribute.setName("unknown-attribute"); // cause or failure
        attribute.setContent(List.of(new StringAttributeContentV2("123")));

        Assertions.assertDoesNotThrow(() -> validateAttributes(List.of(definition), List.of(attribute)));
    }

    @Test
    void testValidateAttributeCallback_success() {
        Set<AttributeCallbackMapping> mappings = new HashSet<>();
        mappings
                .add(new AttributeCallbackMapping("credentialKind", AttributeValueTarget.PATH_VARIABLE,
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
        mappings
                .add(new AttributeCallbackMapping("credentialKind", AttributeValueTarget.PATH_VARIABLE,
                        "softKeyStore"));
        mappings
                .add(new AttributeCallbackMapping("fromAttribute", AttributeType.DATA, AttributeContentType.CREDENTIAL,
                        "toQueryParam", Collections.singleton(AttributeValueTarget.REQUEST_PARAMETER)));
        mappings.add(new AttributeCallbackMapping("fromAttribute", "toBodyKey", AttributeValueTarget.BODY));

        AttributeCallback callback = new AttributeCallback();
        callback.setCallbackContext("core/getCredentials");
        callback.setCallbackMethod("GET");
        callback.setMappings(mappings);

        RequestAttributeCallback callbackRequest = new RequestAttributeCallback();
        callbackRequest.setPathVariable(Map.ofEntries(Map.entry("credentialKind", "softKeyStore")));
        callbackRequest.setRequestParameter(Map.ofEntries(Map.entry("toQueryParam", 1234)));

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class, () -> validateCallback(callback, callbackRequest, false));

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

        ObjectAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, ObjectAttributeContentV2.class)
                .get(0);

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

        StringAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, StringAttributeContentV2.class)
                .get(0);

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

        IntegerAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs,
                IntegerAttributeContentV2.class).get(0);

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

        DateTimeAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs,
                DateTimeAttributeContentV2.class).get(0);

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

        DateAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, DateAttributeContentV2.class)
                .get(0);

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

        TimeAttributeContentV2 data = getAttributeContent("testJsonAttribute", attrs, TimeAttributeContentV2.class)
                .get(0);

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

        String value = getAttributeContentValue("testStringAttribute", attrs, StringAttributeContentV2.class)
                .get(0)
                .getData();

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

        String value = getAttributeContentValue("testFileAttribute", attrs, FileAttributeContentV2.class)
                .get(0)
                .getData()
                .getContent();

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

        NameAndUuidDto data = getObjectAttributeContentData("testCredentialAttribute", attrs, NameAndUuidDto.class)
                .get(0);

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

        List<String> listString = getAttributeContentValueList("testAttributeListString", attrs,
                BaseAttributeContentV2.class);

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

        List<String> listData = getObjectAttributeContentDataList("testCredentialAttribute", attrs,
                NameAndUuidDto.class);

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

    @Test
    void testGetClientAttributes_responseAttributeV2_returnsConvertedRequestAttributes() {
        UUID uuid = UUID.randomUUID();
        ResponseAttributeV2 attr = new ResponseAttributeV2();
        attr.setUuid(uuid);
        attr.setName("username");
        attr.setContentType(AttributeContentType.STRING);
        attr.setContent(List.of(new StringAttributeContentV2("admin")));

        List<RequestAttribute> result = AttributeDefinitionUtils.getClientAttributes(List.of(attr));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("username", result.get(0).getName());
        Assertions.assertEquals(uuid, result.get(0).getUuid());
    }

    @Test
    void testGetClientAttributes_multipleResponseAttributes_allConverted() {
        ResponseAttributeV2 attr1 = new ResponseAttributeV2();
        attr1.setUuid(UUID.randomUUID());
        attr1.setName("username");
        attr1.setContentType(AttributeContentType.STRING);
        attr1.setContent(List.of(new StringAttributeContentV2("admin")));

        ResponseAttributeV2 attr2 = new ResponseAttributeV2();
        attr2.setUuid(UUID.randomUUID());
        attr2.setName("password");
        attr2.setContentType(AttributeContentType.SECRET);
        attr2.setContent(List.of(new SecretAttributeContentV2(null, new SecretAttributeContentData("secret"))));

        List<RequestAttribute> result = AttributeDefinitionUtils.getClientAttributes(List.of(attr1, attr2));

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("username", result.get(0).getName());
        Assertions.assertEquals("password", result.get(1).getName());
    }

    @Test
    void testGetClientAttributes_emptyList_returnsEmpty() {
        List<RequestAttribute> result = AttributeDefinitionUtils.getClientAttributes(List.of());

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void testValidateAttributes_jsonSchemaPass() {
        DataAttributeV2 definition = jsonSchemaDefinition("{\"type\":\"object\",\"required\":[\"sequence\"]}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"sequence\":[{\"boolean\":true}]}");

        validateAttributes(List.of(definition), List.of(attribute));
    }

    @Test
    void testValidateAttributes_jsonSchemaFail_namesThePath() {
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"type\":\"object\",\"properties\":{\"sequence\":{\"type\":\"array\",\"minItems\":2}},\"required\":[\"sequence\"]}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"sequence\":[{\"boolean\":true}]}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions.assertEquals(1, exception.getErrors().size());
        String message = exception.getErrors().get(0).getErrorDescription();
        Assertions.assertTrue(message.contains("JSON Schema constraint"), message);
        Assertions.assertTrue(message.contains("$.sequence"), message);
    }

    @Test
    void testValidateAttributes_jsonSchemaFail_usesTheAuthorsErrorMessage() {
        DataAttributeV2 definition = jsonSchemaDefinition("{\"type\":\"array\"}");
        ((JsonSchemaAttributeConstraint) definition.getConstraints().get(0))
                .setErrorMessage("workers only on this profile");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(
                        exception.getErrors().get(0).getErrorDescription().contains("workers only on this profile"));
    }

    @Test
    void testValidateAttributes_jsonSchemaRejectsNonJsonContent() {
        DataAttributeV2 definition = jsonSchemaDefinition("{\"type\":\"object\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "MAYBAf8CAQA=");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions.assertTrue(exception.getErrors().get(0).getErrorDescription().contains("not well-formed JSON"));
    }

    @Test
    void testValidateAttributes_jsonSchemaRejectsMismatchedContentType() {
        DataAttributeV2 definition = jsonSchemaDefinition("{\"type\":\"object\"}");
        definition.setContentType(AttributeContentType.INTEGER);
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(definition.getName());
        attribute.setUuid(UUID.fromString(definition.getUuid()));
        attribute.setContent(List.of(new IntegerAttributeContentV2(4)));

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions.assertTrue(exception.getErrors().get(0).getErrorDescription().contains("STRING and TEXT"));
    }

    @Test
    void testValidateAttributes_jsonSchemaWithBrokenSchemaDocumentDegradesToError() {
        DataAttributeV2 definition = jsonSchemaDefinition("this is not json");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    @Test
    void testValidateAttributes_jsonSchemaWithARemoteRefIsRejectedWithoutFetchingIt() {
        // A $ref target is fetched on first use; validating against one would let a constraint author make the
        // server issue a request of their choosing.
        DataAttributeV2 definition = jsonSchemaDefinition("{\"$ref\":\"http://169.254.169.254/latest/meta-data/\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        List<DataAttributeV2> definitions = List.of(definition);
        List<RequestAttribute> attributes = List.of(attribute);
        ValidationException exception = Assertions
                .assertThrows(ValidationException.class, () -> validateAttributes(definitions, attributes));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    @Test
    void testValidateAttributes_jsonSchemaWithALocalRefIsStillAccepted() {
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"$defs\":{\"node\":{\"type\":\"object\"}},\"$ref\":\"#/$defs/node\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        Assertions.assertDoesNotThrow(() -> validateAttributes(List.of(definition), List.of(attribute)));
    }

    @Test
    void testValidateAttributes_jsonSchemaRejectsADeclaredOlderDialect() {
        // The factory default applies only when $schema is absent, so a declared draft-04 would be honoured — and
        // there prefixItems is an unknown keyword, so the author's constraint would silently enforce nothing.
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"$schema\":\"http://json-schema.org/draft-04/schema#\"," + "\"type\":\"object\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    @Test
    void testValidateAttributes_jsonSchemaAcceptsTheDeclaredSupportedDialect() {
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"$schema\":\"https://json-schema.org/draft/2020-12/schema\",\"type\":\"object\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        Assertions.assertDoesNotThrow(() -> validateAttributes(List.of(definition), List.of(attribute)));
    }

    @Test
    void testValidateAttributes_jsonSchemaRejectsTrailingContent() {
        // readTree stops at the first complete value, so trailing text would be silently discarded.
        DataAttributeV2 definition = jsonSchemaDefinition("{\"type\":\"object\"} and then some");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    @Test
    void testValidateAttributes_jsonSchemaRejectsTrailingContentInValue() {
        // Mirrors testValidateAttributes_jsonSchemaRejectsTrailingContent, but for the value being validated rather
        // than the schema document: readTree stops at the first complete value, so a schema-valid prefix followed by
        // trailing content would otherwise pass on the prefix alone while the full string reaches the connector.
        DataAttributeV2 definition = jsonSchemaDefinition("{\"type\":\"object\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1} and then some");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions.assertTrue(exception.getErrors().get(0).getErrorDescription().contains("not well-formed JSON"));
    }

    @Test
    void testValidateAttributes_jsonSchemaOnNonRequiredAttributeWithNoContentDoesNotThrowNpe() {
        // A non-required attribute with no content reaches the constraint as contents == null. Content validation
        // already rejects it with a clean ValidationException; the constraint must not additionally NPE on top of
        // that by iterating a null list.
        JsonSchemaAttributeConstraint constraint = new JsonSchemaAttributeConstraint();
        constraint.setData("{\"type\":\"object\"}");
        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName("jsonAttribute");
        definition.setUuid("2f6c1b10-0000-4000-8000-000000000042");
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(false);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));

        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(definition.getName());
        attribute.setUuid(UUID.fromString(definition.getUuid()));
        attribute.setContent(null);

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions.assertTrue(exception.getErrors().get(0).getErrorDescription().contains("wrong value"));
    }

    @Test
    void testValidateAttributes_jsonSchemaRejectsMalformedKeywords() {
        // getSchema compiles these without complaint, so the constraint would enforce nothing.
        DataAttributeV2 definition = jsonSchemaDefinition("{\"minItems\":\"x\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    @Test
    void testValidateAttributes_jsonSchemaAcceptsARefInsideInstanceData() {
        // const holds a literal value, so a member named $ref there is data and not a reference.
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"properties\":{\"a\":{\"const\":{\"$ref\":\"https://example.invalid/x\"}}}}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        // The schema itself is accepted; the value simply does not match the const.
        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("violates"),
                        exception.getErrors().get(0).getErrorDescription());
    }

    @Test
    void testValidateAttributes_jsonSchemaNeverFetchesARemoteRef() throws Exception {
        // Asserting on elapsed time only infers that no fetch happened. Stand up a loopback endpoint, point a
        // $ref at it, and assert it is never contacted.
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        AtomicInteger hits = new AtomicInteger();
        server.createContext("/schema", exchange -> {
            hits.incrementAndGet();
            byte[] body = "{\"type\":\"object\"}".getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            String ref = "http://%s:%d/schema"
                    .formatted(server.getAddress().getAddress().getHostAddress(), server.getAddress().getPort());
            DataAttributeV2 definition = jsonSchemaDefinition("{\"$ref\":\"" + ref + "\"}");
            RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

            ValidationException exception = Assertions
                    .assertThrows(ValidationException.class,
                            () -> validateAttributes(List.of(definition), List.of(attribute)));

            Assertions
                    .assertTrue(
                            exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
            Assertions.assertEquals(0, hits.get(), "the referenced endpoint must never be contacted");
        } finally {
            server.stop(0);
        }
    }

    @Test
    void testValidateAttributes_jsonSchemaAcceptsARefUnderAnUnknownKeyword() {
        // Draft 2020-12 permits unknown keywords as annotations, so a member named $ref inside one is a
        // literal. Only subschema-valued keywords are walked.
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"type\":\"object\",\"x-ui\":{\"$ref\":\"https://example.invalid/x\"}}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        Assertions.assertDoesNotThrow(() -> validateAttributes(List.of(definition), List.of(attribute)));
    }

    @Test
    void testValidateAttributes_jsonSchemaStillRejectsARemoteRefInsideASubschema() {
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"properties\":{\"a\":{\"$ref\":\"https://example.invalid/x\"}}}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    @Test
    void testValidateConstraints_jsonSchemaReportsUnsupportedTypeWhenContentTypeIsNull() {
        // contentType is nullable on the definition and validateConstraints is public, so equals() on it threw
        // where this should report an unsupported type. (Through validateAttributes a null content type is
        // already fatal further upstream, for reasons unrelated to this constraint.)
        DataAttributeV2 definition = jsonSchemaDefinition("{\"type\":\"object\"}");
        definition.setContentType(null);

        List<ValidationError> errors = AttributeDefinitionUtils
                .validateConstraints(definition, List.of(new StringAttributeContentV2("{\"a\":1}")));

        Assertions.assertEquals(1, errors.size());
        Assertions.assertTrue(errors.get(0).getErrorDescription().contains("STRING and TEXT"));
    }

    @Test
    void testValidateAttributes_jsonSchemaAcceptsARefUnderTheDefinitionsAnnotation() {
        // Draft 2020-12 replaced definitions with $defs and treats it as an annotation, so a $ref inside one
        // is data rather than a reference.
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"type\":\"object\",\"definitions\":{\"x\":{\"$ref\":\"https://example.invalid/x\"}}}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        Assertions.assertDoesNotThrow(() -> validateAttributes(List.of(definition), List.of(attribute)));
    }

    @Test
    void testValidateAttributes_jsonSchemaStillRejectsARemoteRefUnderDefs() {
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"$defs\":{\"x\":{\"$ref\":\"https://example.invalid/x\"}}}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    @Test
    void testValidateAttributes_jsonSchemaAcceptsAnEmptyRefAndAnAbsoluteSelfReference() {
        // Draft 2020-12: "" is the document root, and an absolute reference matching the document's own $id
        // resolves inside it. Neither needs an external load.
        DataAttributeV2 emptyRef = jsonSchemaDefinition("{\"$defs\":{\"n\":{\"$ref\":\"\"}},\"type\":\"object\"}");
        Assertions
                .assertDoesNotThrow(
                        () -> validateAttributes(List.of(emptyRef), List.of(stringAttribute(emptyRef, "{\"a\":1}"))));

        DataAttributeV2 selfRef = jsonSchemaDefinition("{\"$id\":\"https://example.test/s\","
                + "\"$defs\":{\"n\":{\"type\":\"object\"}},\"$ref\":\"https://example.test/s#/$defs/n\"}");
        Assertions
                .assertDoesNotThrow(
                        () -> validateAttributes(List.of(selfRef), List.of(stringAttribute(selfRef, "{\"a\":1}"))));
    }

    @Test
    void testValidateAttributes_jsonSchemaStillRejectsAnAbsoluteRefToAnotherDocument() {
        DataAttributeV2 definition = jsonSchemaDefinition(
                "{\"$id\":\"https://example.test/s\",\"$ref\":\"https://elsewhere.invalid/other#/x\"}");
        RequestAttributeV2 attribute = stringAttribute(definition, "{\"a\":1}");

        ValidationException exception = Assertions
                .assertThrows(ValidationException.class,
                        () -> validateAttributes(List.of(definition), List.of(attribute)));

        Assertions
                .assertTrue(exception.getErrors().get(0).getErrorDescription().contains("valid JSON Schema document"));
    }

    private static DataAttributeV2 jsonSchemaDefinition(String schemaDocument) {
        JsonSchemaAttributeConstraint constraint = new JsonSchemaAttributeConstraint();
        constraint.setData(schemaDocument);

        DataAttributeV2 definition = new DataAttributeV2();
        definition.setName("jsonAttribute");
        definition.setUuid("2f6c1b10-0000-4000-8000-000000000042");
        definition.setType(AttributeType.DATA);
        definition.setContentType(AttributeContentType.STRING);
        DataAttributeProperties properties = new DataAttributeProperties();
        properties.setRequired(true);
        definition.setProperties(properties);
        definition.setConstraints(List.of(constraint));
        return definition;
    }

    private static RequestAttributeV2 stringAttribute(DataAttributeV2 definition, String value) {
        RequestAttributeV2 attribute = new RequestAttributeV2();
        attribute.setName(definition.getName());
        attribute.setUuid(UUID.fromString(definition.getUuid()));
        attribute.setContent(List.of(new StringAttributeContentV2(value)));
        return attribute;
    }
}
