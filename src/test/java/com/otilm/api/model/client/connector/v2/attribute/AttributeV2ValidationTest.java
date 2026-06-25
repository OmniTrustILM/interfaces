package com.otilm.api.model.client.connector.v2.attribute;

import com.otilm.api.model.common.attribute.v3.content.StringAttributeContentV3;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the JSR-380 constraints that ARE the #725 contract actually fire — annotations that are
 * declared but never validated would be a silent regression.
 */
class AttributeV2ValidationTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void requestDto_missingRequiredFields_producesViolations() {
        Set<ConstraintViolation<AttributeCallbackRequestDto>> violations =
                validator.validate(new AttributeCallbackRequestDto());
        // connectorInterface, interfaceVersion, attributeUuid, attributeName, contextAttributes, currentAttributes
        assertEquals(6, violations.size(), "all 6 required fields must be flagged");
    }

    @Test
    void responseDto_bothArmsSet_failsExactlyOneArmRule() {
        AttributeCallbackResponseDto both = new AttributeCallbackResponseDto();
        both.setContent(List.of(new StringAttributeContentV3("x")));
        both.setAttributes(List.of());

        Set<ConstraintViolation<AttributeCallbackResponseDto>> violations = validator.validate(both);

        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getPropertyPath().toString().contains("exactlyOneArmSet"),
                "the @AssertTrue one-arm rule must be the failing constraint");
    }

    @Test
    void responseDto_exactlyOneArm_passesValidation() {
        AttributeCallbackResponseDto oneArm = new AttributeCallbackResponseDto();
        oneArm.setContent(List.of(new StringAttributeContentV3("x")));
        assertTrue(validator.validate(oneArm).isEmpty());
    }
}
