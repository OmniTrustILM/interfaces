package com.otilm.api.model.connector.cryptography.v2;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.otilm.api.testsupport.ClassScanTestSupport.classesIn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guards platform attribute identities across cryptography v2 definitions, including new definitions with no test
 * registration.
 */
class PlatformReservedAttributeIdentityTest {

    private static List<Class<?>> packageClasses;

    @BeforeAll
    static void discoverPackageClasses() throws IOException, ClassNotFoundException {
        packageClasses = classesIn(PlatformReservedAttribute.class.getPackageName());
    }

    @Test
    void attributeDefinitions_implementPlatformAttribute() {
        // given
        List<Class<?>> definitions = packageClasses
                .stream()
                .filter(PlatformReservedAttributeIdentityTest::isAttributeDefinition)
                .toList();

        // when
        List<String> unmarked = definitions
                .stream()
                .filter(type -> !PlatformReservedAttribute.class.isAssignableFrom(type))
                .map(Class::getName)
                .toList();

        // then
        assertFalse(definitions.isEmpty(), "No cryptography v2 attribute definitions discovered");
        assertTrue(unmarked.isEmpty(), "Attribute definitions must implement PlatformAttribute: " + unmarked);
    }

    @Test
    void platformAttributes_haveDistinctUuids() throws ReflectiveOperationException {
        // given
        List<Class<?>> definitions = platformAttributeClasses();

        // when
        Map<Class<?>, UUID> identities = readConstants(definitions, "ATTRIBUTE_UUID", UUID.class);

        // then
        assertDistinct(identities, "ATTRIBUTE_UUID");
    }

    @Test
    void platformAttributes_haveDistinctNames() throws ReflectiveOperationException {
        // given
        List<Class<?>> definitions = platformAttributeClasses();

        // when
        Map<Class<?>, String> identities = readConstants(definitions, "NAME", String.class);

        // then
        assertDistinct(identities, "NAME");
        identities.forEach((type, name) -> assertFalse(name.isBlank(), type.getName() + ".NAME must not be blank"));
    }

    /**
     * Detects forgotten markers by the definition naming convention or identity declaration, independently of the
     * marker.
     */
    private static boolean isAttributeDefinition(Class<?> type) {
        return !type.isInterface() && (type.getSimpleName().endsWith("Attribute")
                || Arrays.stream(type.getDeclaredFields()).anyMatch(field -> field.getName().equals("ATTRIBUTE_UUID")));
    }

    private static List<Class<?>> platformAttributeClasses() {
        List<Class<?>> attributes = packageClasses
                .stream()
                .filter(type -> !type.isInterface() && PlatformReservedAttribute.class.isAssignableFrom(type))
                .toList();
        assertFalse(attributes.isEmpty(), "No PlatformAttribute implementations discovered");
        return attributes;
    }

    /**
     * Requires each definition to declare its own correctly typed constants rather than inherit another identity.
     */
    private static <T> Map<Class<?>, T> readConstants(List<Class<?>> attributes, String name, Class<T> valueType)
            throws ReflectiveOperationException {
        Map<Class<?>, T> identities = new LinkedHashMap<>();
        for (Class<?> type : attributes) {
            Field field = type.getDeclaredField(name);
            String label = type.getName() + "." + name;
            int modifiers = field.getModifiers();
            assertTrue(Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers),
                    label + " must be public static final");
            assertEquals(valueType, field.getType(), label + " has wrong type");
            T value = valueType.cast(field.get(null));
            assertNotNull(value, label + " must not be null");
            identities.put(type, value);
        }
        return identities;
    }

    /**
     * Reports both declaring classes for a collision so a failed contract check identifies the conflicting helpers.
     */
    private static <T> void assertDistinct(Map<Class<?>, T> identities, String name) {
        Map<T, Class<?>> owners = new LinkedHashMap<>();
        identities.forEach((type, value) -> {
            assertNotNull(value, type.getName() + "." + name + " must not be null");
            Class<?> previous = owners.putIfAbsent(value, type);
            assertNull(previous,
                    () -> "Duplicate " + name + " '" + value + "': " + previous.getName() + " and " + type.getName());
        });
    }
}
