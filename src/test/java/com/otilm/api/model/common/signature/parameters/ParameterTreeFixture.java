package com.otilm.api.model.common.signature.parameters;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Builds a signature-parameters tree with every field carrying a distinguishing value. */
final class ParameterTreeFixture {

    static final String DISCRIMINATOR = "family";

    private ParameterTreeFixture() {
    }

    static <T extends SignatureParameterContainer> T populated(Class<T> type) {
        try {
            T instance = type.getDeclaredConstructor().newInstance();
            for (Field field : instanceFields(type)) {
                if (DISCRIMINATOR.equals(field.getName())) {
                    continue;
                }
                field.setAccessible(true);
                field.set(instance, value(field));
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("could not populate " + type.getSimpleName(), e);
        }
    }

    private static Object value(Field field) {
        Class<?> type = field.getType();
        String name = field.getName();
        if (SignatureParameterContainer.class.isAssignableFrom(type)) {
            return populated(type.asSubclass(SignatureParameterContainer.class));
        }
        if (type.isEnum()) {
            Object[] constants = type.getEnumConstants();
            return constants[constants.length - 1];
        }
        if (type == String.class) {
            return name + "-value";
        }
        if (type == Boolean.class) {
            return Boolean.TRUE;
        }
        if (type == Integer.class) {
            return distinct(name);
        }
        if (type == Float.class) {
            return (float) distinct(name);
        }
        if (type == byte[].class) {
            return name.getBytes(StandardCharsets.UTF_8);
        }
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>(List.of(name + "-0", name + "-1"));
        }
        throw new IllegalStateException("the fixture has no value for " + name + " of type " + type.getSimpleName()
                + "; teach ParameterTreeFixture about the type rather than letting the field go unswept");
    }

    private static int distinct(String name) {
        return Math.abs(name.hashCode() % 97) + 1;
    }

    /** @return every instance field of {@code type} and its supertypes, in declaration order, subtype first */
    static List<Field> instanceFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isSynthetic() && !Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    static Object read(Field field, Object node) {
        try {
            return field.get(node);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("could not read " + field.getName(), e);
        }
    }
}
