package com.czertainly.api.exception;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PlatformExceptionTest {

    @Test
    void allConcreteThrowables_implementPlatformException() {
        List<Class<?>> found = new ArrayList<>();
        found.addAll(concreteThrowables("com.czertainly.api"));
        found.addAll(concreteThrowables("com.czertainly.core"));
        assertFalse(found.isEmpty(), "Scan found no concrete Throwable classes — check package name");
        assertAll(found.stream().map(clazz -> () ->
                assertTrue(PlatformException.class.isAssignableFrom(clazz),
                        clazz.getName() + " must implement PlatformException")));
    }

    private static List<Class<?>> concreteThrowables(String pkgName) {
        String pkgPath = pkgName.replace('.', '/') + '/';
        int pkgDepth = pkgName.split("\\.").length;
        List<Class<?>> classes = new ArrayList<>();

        try {
            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(pkgPath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (!"file".equals(url.getProtocol())) continue;
                Path pkgDir = Path.of(url.toURI());
                Path classpathRoot = pkgDir;
                for (int i = 0; i < pkgDepth; i++) {
                    classpathRoot = classpathRoot.getParent();
                }
                extractClassesInto(pkgDir, classpathRoot, classes);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to scan package: " + pkgName, e);
        }
        return classes;
    }

    private static void extractClassesInto(Path pkgDir, Path root, List<Class<?>> into) {
        try (Stream<Path> walk = Files.walk(pkgDir)) {
            walk.filter(p -> p.toString().endsWith(".class"))
                    .filter(p -> !p.getFileName().toString().contains("$"))
                    .map(p -> loadClass(root.relativize(p).toString()
                            .replace(File.separatorChar, '.')
                            .replaceAll("\\.class$", "")))
                    .filter(Throwable.class::isAssignableFrom)
                    .filter(cls -> !cls.isInterface())
                    .filter(cls -> !Modifier.isAbstract(cls.getModifiers()))
                    .forEach(into::add);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to walk: " + pkgDir, e);
        }
    }

    private static Class<?> loadClass(String fqName) {
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            return Class.forName(fqName, false, cl);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot load class: " + fqName, e);
        }
    }
}
