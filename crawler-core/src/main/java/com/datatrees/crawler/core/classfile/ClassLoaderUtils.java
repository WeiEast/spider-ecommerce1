package com.datatrees.crawler.core.classfile;

import java.io.File;
import java.util.Objects;

/**
 * @author Jerry
 * @since 10:38 14/08/2017
 */
public final class ClassLoaderUtils {
    private ClassLoaderUtils() {
    }

    public static <T> T loadAndInstantiate(File file, ClassLoader parent, boolean forceReload, String className,
                                           Class<T> resultType) throws ClassNotFoundException, IllegalAccessException,
                                                                InstantiationException {
        Objects.requireNonNull(className);
        Objects.requireNonNull(resultType);
        ClassLoader classLoader = ClassLoaderManager.findClassLoader(file, parent, forceReload);

        Class<?> clazz = classLoader.loadClass(className);

        if (!resultType.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                "Can not load the class[" + className + "] which extends " + resultType.getSimpleName());
        }

        return resultType.cast(clazz.newInstance());
    }
}
