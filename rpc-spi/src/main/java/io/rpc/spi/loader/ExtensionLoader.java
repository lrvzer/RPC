package io.rpc.spi.loader;

import io.rpc.spi.annotation.SPI;
import io.rpc.spi.annotation.SPIClass;
import io.rpc.spi.factory.ExtensionFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ExtensionLoader<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String SERVICES_DIRECTORY = "META-INF/services/";
    private static final String RPC_DIRECTORY = "META-INF/rpc/";
    private static final String RPC_DIRECTORY_EXTERNAL = "META-INF/rpc/external/";
    private static final String RPC_DIRECTORY_INTERNAL = "META-INF/rpc/internal/";

    private static final String[] SPI_DIRECTORIES = new String[]{
            SERVICES_DIRECTORY,
            RPC_DIRECTORY,
            RPC_DIRECTORY_EXTERNAL,
            RPC_DIRECTORY_INTERNAL
    };

    private static final Map<Class<?>, ExtensionLoader<?>> LOADERS = new ConcurrentHashMap<>();
    private final Class<T> clazz;
    private final ClassLoader classLoader;
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private final Map<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Map<Class<?>, Object> spiClassInstances = new ConcurrentHashMap<>();
    private String cachedDefaultName;

    /**
     * Instantiates a new Extension loader.
     *
     * @param clazz       the clazz.
     * @param classLoader
     */
    public ExtensionLoader(final Class<T> clazz, final ClassLoader classLoader) {
        this.clazz = clazz;
        this.classLoader = classLoader;
        if (!Objects.equals(clazz, ExtensionFactory.class)) {
            ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getExtensionClasses();
        }
    }

    /**
     * 直接获取想要的类实例
     *
     * @param clazz 接口的Class实例
     * @param name  SPI名称
     * @param <T>   泛型类型
     * @return 泛型实例
     */
    public static <T> T getExtension(final Class<T> clazz, String name) {
        return StringUtils.isEmpty(name) ? getExtensionLoader(clazz).getDefaultSpiClassInstance() : getExtensionLoader(clazz).getSpiClassInstance(name);
    }

    /**
     * Gets spi class.
     *
     * @param name the name
     * @return the spi class instance.
     */
    private T getSpiClassInstance(String name) {
        if (StringUtils.isBlank(name)) {
            throw new NullPointerException("get spi class name is null");
        }
        Holder<Object> objectHolder = cachedInstances.get(name);
        if (Objects.isNull(objectHolder)) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            objectHolder = cachedInstances.get(name);
        }

        Object value = objectHolder.getValue();
        if (Objects.isNull(value)) {
            synchronized (cachedInstances) {
                value = objectHolder.getValue();
                if (Objects.isNull(value)) {
                    value = createExtension(name);
                    objectHolder.setValue(value);
                }
            }
        }
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    private T createExtension(final String name) {
        Class<?> aClass = getExtensionClasses().get(name);
        if (Objects.isNull(aClass)) {
            throw new IllegalArgumentException("name is error");
        }
        Object o = spiClassInstances.get(aClass);
        if (Objects.isNull(o)) {
            try {
                spiClassInstances.putIfAbsent(aClass, aClass.newInstance());
                o = spiClassInstances.get(aClass);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Extension instance(name: " + name + ", class: " + aClass + ") could not be instantiated: " + e.getMessage(), e);
            }
        }
        return (T) o;
    }

    /**
     * Gets default spi class instance.
     *
     * @return the default spi class instance.
     */
    public T getDefaultSpiClassInstance() {
        getExtensionClasses();
        if (StringUtils.isBlank(cachedDefaultName)) {
            return null;
        }
        return getSpiClassInstance(cachedDefaultName);
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.getValue();
        if (Objects.isNull(classes)) {
            synchronized (cachedClasses) {
                classes = cachedClasses.getValue();
                if (Objects.isNull(classes)) {
                    classes = loadExtensionClass();
                    cachedClasses.setValue(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClass() {
        SPI annotation = clazz.getAnnotation(SPI.class);
        if (Objects.nonNull(annotation)) {
            String value = annotation.value();
            if (StringUtils.isNotBlank(value)) {
                cachedDefaultName = value;
            }
        }
        Map<String, Class<?>> classes = new HashMap<>(10);
        loadDirectory(classes);
        return classes;
    }

    private void loadDirectory(Map<String, Class<?>> classes) {
        for (String directory : SPI_DIRECTORIES) {
            String filename = directory + clazz.getName();
            try {
                Enumeration<URL> urls = Objects.nonNull(this.classLoader) ? classLoader.getResources(filename) : ClassLoader.getSystemResources(filename);
                if (Objects.nonNull(urls)) {
                    while (urls.hasMoreElements()) {
                        URL url = urls.nextElement();
                        loadResources(classes, url);
                    }
                }
            } catch (IOException e) {
                logger.error("load extension class error {}", filename, e);
            }
        }
    }

    private void loadResources(Map<String, Class<?>> classes, URL url) {
        try (InputStream inputStream = url.openStream()) {
            Properties properties = new Properties();
            properties.load(inputStream);
            properties.forEach((k, v) -> {
                String name = (String) k;
                String classPath = (String) v;
                if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(classPath)) {
                    try {
                        loadClass(classes, name, classPath);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("load extension resources error", e);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadClass(Map<String, Class<?>> classes, String name, String classPath) throws ClassNotFoundException {
        Class<?> subClass = Objects.nonNull(this.classLoader) ? Class.forName(classPath, true, this.classLoader) : Class.forName(classPath);
        if (!clazz.isAssignableFrom(subClass)) {
            throw new IllegalStateException("load extension resources error," + subClass + " subtype is not of " + clazz);
        }
        if (!subClass.isAnnotationPresent(SPIClass.class)) {
            throw new IllegalStateException("load extension resources error," + subClass + " without @" + SPIClass.class + " annotation");
        }
        Class<?> oldClass = classes.get(name);
        if (Objects.isNull(oldClass)) {
            classes.put(name, subClass);
        } else if (!Objects.equals(oldClass, subClass)) {
            throw new IllegalStateException("load extension resources error,Duplicate class " + clazz.getName() + " name " + name + " on " + oldClass.getName() + " or " + subClass.getName());
        }
    }

    /**
     * Gets extension loader.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the extension loader
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz) {
        return getExtensionLoader(clazz, ExtensionLoader.class.getClassLoader());
    }

    /**
     * Gets extension loader.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @param cl    the cl
     * @return the extension loader.
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> clazz, final ClassLoader cl) {
        Objects.requireNonNull(clazz, "extension clazz is null");
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") is not interface!");
        }

        if (!clazz.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("extension clazz (" + clazz + ") without @" + SPI.class + " Annotation");
        }

        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) LOADERS.get(clazz);
        if (Objects.nonNull(extensionLoader)) {
            return extensionLoader;
        }
        LOADERS.putIfAbsent(clazz, new ExtensionLoader<T>(clazz, cl));
        return (ExtensionLoader<T>) LOADERS.get(clazz);
    }

    public static class Holder<T> {
        private volatile T value;

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }
    }
}
