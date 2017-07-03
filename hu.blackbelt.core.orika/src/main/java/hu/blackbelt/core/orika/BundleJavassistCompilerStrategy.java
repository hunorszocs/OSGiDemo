package hu.blackbelt.core.orika;

import javassist.*;
import ma.glasnost.orika.impl.generator.CompilerStrategy;
import ma.glasnost.orika.impl.generator.JavassistCompilerStrategy;
import ma.glasnost.orika.impl.generator.SourceCodeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OSGi Bundle based Javassist extension for the {@link CompilerStrategy}.
 * The implementation allows Javassist to use bundle relative class loader to load its classes.
 */
public class BundleJavassistCompilerStrategy extends CompilerStrategy {
    private static final String WRITE_SOURCE_FILES_BY_DEFAULT = "false";
    private static final String WRITE_CLASS_FILES_BY_DEFAULT = "false";

    private final static Logger LOG = LoggerFactory.getLogger(JavassistCompilerStrategy.class);
    private final static Map<Class<?>, Boolean> superClasses = new ConcurrentHashMap<Class<?>, Boolean>(3);

    private BundleClassPool classPool;

    /**
     * Keep a set of class-loaders that have already been added to the javassist
     * class-pool Use a WeakHashMap to avoid retaining references to child
     * class-loaders
     */
    private WeakHashMap<ClassLoader, Boolean> referencedLoaders = new WeakHashMap<ClassLoader, Boolean>(8);

    /**
     */
    public BundleJavassistCompilerStrategy() {
        super(WRITE_SOURCE_FILES_BY_DEFAULT, WRITE_CLASS_FILES_BY_DEFAULT);

        this.classPool = new BundleClassPool();
        this.classPool.appendSystemPath();
    }

    /**
     * Produces the requested class files for debugging purposes.
     *
     * @throws javassist.CannotCompileException
     * @throws IOException
     */
    protected void writeClassFile(SourceCodeContext sourceCode, CtClass byteCodeClass) throws IOException {
        if (writeClassFiles) {
            try {
                File parentDir = preparePackageOutputPath(this.pathToWriteClassFiles, "");
                byteCodeClass.writeFile(parentDir.getAbsolutePath());
            } catch (CannotCompileException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    /**
     * Produces the requested source file for debugging purposes.
     *
     * @throws IOException
     */
    protected void writeSourceFile(SourceCodeContext sourceCode) throws IOException {
        if (writeSourceFiles) {
            File parentDir = preparePackageOutputPath(this.pathToWriteSourceFiles, sourceCode.getPackageName());
            File sourceFile = new File(parentDir, sourceCode.getClassSimpleName() + ".java");
            if (!sourceFile.exists() && !sourceFile.createNewFile()) {
                throw new IOException("Could not write source file for " + sourceCode.getClassName());
            }

            FileWriter fw = null;
            try {
                fw = new FileWriter(sourceFile);
                fw.append(sourceCode.toSourceFile());
            } finally {
                if (fw != null)
                    fw.close();
            }
        }
    }

    /**
     * Attempts to register a class-loader in the maintained list of referenced
     * class-loaders. Returns true if the class-loader was registered as a
     * result of the call; false is returned if the class-loader was already
     * registered.
     *
     * @param cl
     * @return true if the class-loader was registered as a result of this call;
     *         false if the class-loader was already registered
     */
    public boolean registerClassLoader(ClassLoader cl) {
        Boolean found = referencedLoaders.get(cl);
        if (found == null) {
            synchronized (cl) {
                found = referencedLoaders.get(cl);
                if (found == null) {
                    classPool.registerClassLoader(cl);
                    referencedLoaders.put(cl, Boolean.TRUE);
                    classPool.insertClassPath(new LoaderClassPath(cl));
                }
            }
        }
        return found == null || !found;
    }

    /*
     * (non-Javadoc)
     *
     * @see ma.glasnost.orika.impl.GeneratedSourceCodeCompilerStrategy#
     * assertClassLoaderAccessible(java.lang.Class)
     */
    public void assureTypeIsAccessible(Class<?> type) throws SourceCodeGenerationException {

        if (!type.isPrimitive()) {

            if (!Modifier.isPublic(type.getModifiers())) {
                throw new SourceCodeGenerationException(type + " is not accessible");
            } else if (type.isMemberClass()) {
                /*
                 * The type needs to be publicly accessible (including it's
                 * enclosing classes if any)
                 */
                Class<?> currentType = type;
                while (currentType != null) {
                    if (!Modifier.isPublic(type.getModifiers())) {
                        throw new SourceCodeGenerationException(type + " is not accessible");
                    }
                    currentType = currentType.getEnclosingClass();
                }
            }

            String className = type.getName();
            if (type.isArray()) {
                // Strip off the "[L" prefix from the internal name
                className = type.getComponentType().getName();
            }
            if (type.getClassLoader() != null) {
                try {
                    classPool.get(className);
                } catch (NotFoundException e) {

                    if (registerClassLoader(type.getClassLoader())) {
                        try {
                            classPool.get(className);
                        } catch (NotFoundException e2) {
                            throw new SourceCodeGenerationException(type + " is not accessible", e2);
                        }
                    } else {
                        throw new SourceCodeGenerationException(type + " is not accessible", e);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * ma.glasnost.orika.impl.GeneratedSourceCodeCompilerStrategy#compileClass
     * (ma.glasnost.orika.impl.GeneratedSourceCode)
     */
    public Class<?> compileClass(SourceCodeContext sourceCode) throws SourceCodeGenerationException {

        StringBuilder className = new StringBuilder(sourceCode.getClassName());
        CtClass byteCodeClass = null;
        int attempts = 0;
        Random rand = new Random();
        while (byteCodeClass == null) {
            try {
                byteCodeClass = classPool.makeClass(className.toString());
            } catch (RuntimeException e) {
                if (attempts < 5) {
                    className.append(Integer.toHexString(rand.nextInt()));
                } else {
                    // No longer likely to be accidental name collision;
                    // propagate the error
                    throw e;
                }
            }
        }

        CtClass abstractMapperClass;
        Class<?> compiledClass;

        try {
            writeSourceFile(sourceCode);

            assureTypeIsAccessible(this.getClass());

            Boolean existing = superClasses.put(sourceCode.getSuperClass(), true);
            if (existing == null || !existing) {
                classPool.insertClassPath(new ClassClassPath(sourceCode.getSuperClass()));
            }

            abstractMapperClass = classPool.get(sourceCode.getSuperClass().getCanonicalName());
            byteCodeClass.setSuperclass(abstractMapperClass);

            // Ugly Hack to acces field list which is a private method - WHY HAVE TO BE PRIVATE??
            // TO BE UNABLE USE SOURCE CONTEXT AS ANOTHER COMPILER STRATEGY USES?????
            List<String> fields = null;
            List<String> methods = null;

            try {
                Method getFields = sourceCode.getClass().getDeclaredMethod("getFields");
                getFields.setAccessible(true);
                fields = (List<String>) getFields.invoke(sourceCode);

                Method getMethods = sourceCode.getClass().getDeclaredMethod("getMethods");
                getMethods.setAccessible(true);
                methods = (List<String>) getMethods.invoke(sourceCode);
            } catch (Throwable th) {
                throw new CannotCompileException("Method invocation problem", th);
            }

            for (String fieldDef : fields) {
                try {
                    byteCodeClass.addField(CtField.make(fieldDef, byteCodeClass));
                } catch (CannotCompileException e) {
                    LOG.error("An exception occured while compiling: " + fieldDef + " for " + sourceCode.getClassName(), e);
                    throw e;
                }
            }

            for (String methodDef : methods) {
                try {
                    byteCodeClass.addMethod(CtNewMethod.make(methodDef, byteCodeClass));
                } catch (CannotCompileException e) {
                    LOG.error("An exception occured while compiling the following method:\n\n " + methodDef
                            + "\n\n for " + sourceCode.getClassName() + "\n", e);
                    throw e;
                }
            }
            compiledClass = byteCodeClass.toClass();
            writeClassFile(sourceCode, byteCodeClass);
        } catch (NotFoundException e) {
            throw new SourceCodeGenerationException(e);
        } catch (CannotCompileException e) {
            throw new SourceCodeGenerationException("Error compiling " + sourceCode.getClassName(), e);
        } catch (IOException e) {
            throw new SourceCodeGenerationException("Could not write files for " + sourceCode.getClassName(), e);
        }

        return compiledClass;
    }

}
