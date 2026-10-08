package com.otilm.api.config.jackson3;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.util.ClassUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Jackson 3 ignores a Jackson 2 handler that has no twin in {@link Jackson2AnnotationBridge}. */
class Jackson2AnnotationBridgeTest {

    @Test
    void everyJackson2HandlerOnADtoHasAJackson3Twin() throws ReflectiveOperationException {
        Set<String> untranslated = new TreeSet<>();
        for (Class<?> type : classesUnder("com.otilm")) {
            for (AnnotatedElement element : annotatedElementsOf(type)) {
                for (Annotation annotation : List
                        .of(element.getAnnotationsByType(JsonSerialize.class),
                                element.getAnnotationsByType(JsonDeserialize.class))
                        .stream()
                        .flatMap(Arrays::stream)
                        .toList()) {
                    if (!translated(annotation)) {
                        untranslated.add(element + ": " + annotation);
                    }
                }
            }
        }
        assertEquals(Set.of(), untranslated, "Add a Jackson 3 twin to Jackson2AnnotationBridge");
    }

    /** The bridge reads {@code using} only, so any other attribute must be left at its default. */
    private static boolean translated(Annotation annotation) throws ReflectiveOperationException {
        for (Method attribute : annotation.annotationType().getDeclaredMethods()) {
            Object value = attribute.invoke(annotation);
            boolean ok = attribute.getName().equals("using")
                    ? Jackson2AnnotationBridge.translates((Class<?>) value)
                    : Objects.deepEquals(value, attribute.getDefaultValue());
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    private static List<AnnotatedElement> annotatedElementsOf(Class<?> type) {
        List<AnnotatedElement> elements = new ArrayList<>();
        elements.add(type);
        elements.addAll(Arrays.asList(type.getDeclaredFields()));
        elements.addAll(Arrays.asList(type.getDeclaredMethods()));
        return elements;
    }

    private static List<Class<?>> classesUnder(String basePackage) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition definition) {
                return true;
            }
        };
        scanner.addIncludeFilter((reader, factory) -> true);
        List<Class<?>> classes = new ArrayList<>();
        for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
            classes.add(ClassUtils.resolveClassName(candidate.getBeanClassName(), null));
        }
        return classes;
    }
}
