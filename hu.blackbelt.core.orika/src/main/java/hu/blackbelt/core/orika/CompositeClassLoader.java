package hu.blackbelt.core.orika;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newCopyOnWriteArrayList;

public class CompositeClassLoader extends ClassLoader {
    Logger LOGGER = LoggerFactory.getLogger(CompositeClassLoader.class);

    private static final String MANDATORY_CLASS_LOADER_MESSAGE = "The ClassLoader argument must be non-null.";

    // Class is used instead of interface to access the putIfAbsent() method.
    private CopyOnWriteArrayList<ClassLoader> classLoaders;

    public CompositeClassLoader() {
        super(CompositeClassLoader.class.getClassLoader());
        classLoaders = newCopyOnWriteArrayList();
    }

    public void insert(ClassLoader classLoader) {
        checkNotNull(classLoader, MANDATORY_CLASS_LOADER_MESSAGE);
        throw new UnsupportedOperationException();
    }

    public void append(ClassLoader classLoader) {
        checkNotNull(classLoader, MANDATORY_CLASS_LOADER_MESSAGE);
        classLoaders.addIfAbsent(classLoader);
    }

    public void remove(ClassLoader classLoader) {
        checkNotNull(classLoader, MANDATORY_CLASS_LOADER_MESSAGE);
        classLoaders.remove(classLoader);
    }

    @Override
    @SuppressWarnings("checkstyle:emptyblock")
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        LOGGER.trace("Finding {} class.", name);
        for (ClassLoader classLoader : classLoaders) {
            try {
                Class<?> cl = classLoader.loadClass(name);
                LOGGER.trace("Class {} found using {} class loader.", name, classLoader);
                return cl;
            } catch (ClassNotFoundException cnfe) {
                // This block intentionally left blank.
            }
        }
        LOGGER.trace("Class {} not found.", name);
        throw new ClassNotFoundException(name);
    }

    @Override
    protected URL findResource(String name) {
        LOGGER.trace("Finding {} resource.", name);
        URL result = null;
        for (ClassLoader classLoader : classLoaders) {
            result = classLoader.getResource(name);
            if (result != null) {
                LOGGER.trace("Resource {} found using {} class loader.", name, classLoader);
                break;
            }
        }
        if (result == null) {
            LOGGER.trace("Resource {} not found.", name);
        }
        return result;
    }
}
