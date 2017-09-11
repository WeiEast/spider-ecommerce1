package com.datatrees.crawler.core.classfile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.util.concurrent.UncheckedExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jerry
 * @since 09:55 11/08/2017
 */
public final class ClassLoaderManager {

    private static final Logger                  LOGGER       = LoggerFactory.getLogger(ClassLoaderManager.class);
    private static final Cache<Key, ClassLoader> LOADER_CACHE = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).softValues().removalListener((RemovalListener<Key, ClassLoader>) notification -> {
        ClassLoader classLoader = notification.getValue();
        if (classLoader instanceof URLClassLoader) {
            try {
                ((URLClassLoader) classLoader).close();
            } catch (IOException e) {
                String paths = Arrays.stream(((URLClassLoader) classLoader).getURLs()).map(URL::getPath).collect(Collectors.joining(","));
                LOGGER.error("Error closing classLoader:" + paths, e);
            }
        }
    }).build();

    public static ClassLoader findClassLoader(final File file, final ClassLoader classLoader, boolean expired) {
        Objects.requireNonNull(file);
        try {

            Key key = new Key(file, classLoader);
            if (expired) {
                LOADER_CACHE.invalidate(key);
            }

            return LOADER_CACHE.get(key, () -> ClassLoaderFactory.create(file, classLoader));
        } catch (ExecutionException e) {
            throw new UncheckedExecutionException(e);
        }
    }

    private static class Key {

        private final File        file;
        private final ClassLoader classLoader;

        Key(File file, ClassLoader classLoader) {
            this.file = file;
            this.classLoader = classLoader;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Key key = (Key) o;

            try {
                if (file != null && key.file != null && Files.isSameFile(file.toPath(), key.file.toPath()) || (file == null && key.file == null)) {
                    return classLoader != null ? classLoader.equals(key.classLoader) : key.classLoader == null;
                }
            } catch (IOException e) {
                throw new UncheckedExecutionException(e);
            }

            return false;
        }

        @Override
        public int hashCode() {
            int result = file != null ? file.hashCode() : 0;
            result = 31 * result + (classLoader != null ? classLoader.hashCode() : 0);
            return result;
        }
    }
}
