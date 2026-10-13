package com.otilm.api.testsupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

/**
 * Lists the classes of a package and its subpackages.
 */
public final class ClassScanTestSupport {

    private ClassScanTestSupport() {
    }

    /**
     * Loads every class under the package uninitialized. A class that fails to load fails the scan.
     */
    public static List<Class<?>> classesIn(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader loader = ClassScanTestSupport.class.getClassLoader();
        String pattern = "classpath*:" + packageName.replace('.', '/') + "/**/*.class";
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
        CachingMetadataReaderFactory metadata = new CachingMetadataReaderFactory(resolver);
        List<Class<?>> classes = new ArrayList<>();
        for (Resource resource : resolver.getResources(pattern)) {
            String name = metadata.getMetadataReader(resource).getClassMetadata().getClassName();
            classes.add(Class.forName(name, false, loader));
        }
        return classes.stream().distinct().sorted(Comparator.comparing(Class::getName)).toList();
    }
}
