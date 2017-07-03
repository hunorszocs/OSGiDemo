package hu.blackbelt.core.orika;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import ma.glasnost.orika.impl.GeneratedMapperBase;

/**
 * OSGi bundle extension for the standard Javassist {@link javassist.ClassPool}.
 * The implementation overrides the {@link javassist.ClassPool#getClassLoader()} to
 * return the bundle relative class loader which was provided in the
 *
 * @see javassist.ClassPool
 */
public class BundleClassPool extends ClassPool {

    private CompositeClassLoader classLoader = new CompositeClassLoader();

    /**
     * Creates a root class pool which will use a bundle relative class loader.
     */
    public BundleClassPool() {
        super(null);
        this.insertClassPath(new ClassClassPath(GeneratedMapperBase.class));
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public void registerClassLoader(ClassLoader classLoader) {
        this.classLoader.append(classLoader);
        this.insertClassPath(new LoaderClassPath(classLoader));
    }
}
