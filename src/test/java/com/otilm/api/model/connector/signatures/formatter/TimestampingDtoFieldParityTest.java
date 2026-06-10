package com.otilm.api.model.connector.signatures.formatter;

import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimestampingDtoFieldParityTest {

    private static final Set<String> SHARED_FIELDS = Set.of(
            "serialNumber", "signingTime", "accuracy", "signatureAlgorithm", "hashAlgorithm",
            "policy", "nonce", "includeSignerCertificate", "qualifiedTimestamp", "requestExtensions");

    @Test
    void sharedFieldsHaveIdenticalFieldTypes() {
        for (String fieldName : SHARED_FIELDS) {
            Class<?> dtbsType = fieldType(TimestampingFormatDtbsRequestDto.class, fieldName);
            Class<?> responseType = fieldType(TimestampingFormatResponseRequestDto.class, fieldName);
            assertEquals(dtbsType, responseType,
                    "Field type mismatch on shared field '" + fieldName + "'");
        }
    }

    @Test
    void sharedFieldsHaveIdenticalAnnotationTypes() {
        for (String fieldName : SHARED_FIELDS) {
            Set<Class<? extends Annotation>> dtbsAnnotations = annotationTypes(TimestampingFormatDtbsRequestDto.class, fieldName);
            Set<Class<? extends Annotation>> responseAnnotations = annotationTypes(TimestampingFormatResponseRequestDto.class, fieldName);
            assertEquals(dtbsAnnotations, responseAnnotations,
                    "Annotation type mismatch on shared field '" + fieldName + "'");
        }
    }

    @Test
    void dtbsAndResponseDeclareIdenticalSharedFields() {
        Set<String> dtbsFields = declaredFieldNames(TimestampingFormatDtbsRequestDto.class);
        Set<String> responseFields = declaredFieldNames(TimestampingFormatResponseRequestDto.class);

        Set<String> dtbsShared = intersection(dtbsFields, SHARED_FIELDS);
        Set<String> responseShared = intersection(responseFields, SHARED_FIELDS);

        assertEquals(SHARED_FIELDS, dtbsShared,
                "TimestampingFormatDtbsRequestDto is missing shared fields: " + difference(SHARED_FIELDS, dtbsShared));
        assertEquals(SHARED_FIELDS, responseShared,
                "TimestampingFormatResponseRequestDto is missing shared fields: " + difference(SHARED_FIELDS, responseShared));
    }

    private static Class<?> fieldType(Class<?> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName).getType();
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Field '" + fieldName + "' not found in " + clazz.getSimpleName(), e);
        }
    }

    private static Set<Class<? extends Annotation>> annotationTypes(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return Arrays.stream(field.getAnnotations())
                    .map(Annotation::annotationType)
                    .collect(Collectors.toSet());
        } catch (NoSuchFieldException e) {
            throw new AssertionError("Field '" + fieldName + "' not found in " + clazz.getSimpleName(), e);
        }
    }

    private static Set<String> declaredFieldNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());
    }

    private static Set<String> intersection(Set<String> a, Set<String> b) {
        return a.stream().filter(b::contains).collect(Collectors.toSet());
    }

    private static Set<String> difference(Set<String> a, Set<String> b) {
        return a.stream().filter(f -> !b.contains(f)).collect(Collectors.toSet());
    }
}
