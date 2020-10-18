package me.nurio.microkernel.loader;

import lombok.Getter;
import me.nurio.microkernel.modules.IModule;
import me.nurio.microkernel.modules.KernelModule;
import me.nurio.microkernel.modules.ModuleYaml;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * This class will manage class loading and will make class sharing across modules possible.
 *
 * @see ModuleReflectionManager to see this class in use.
 */
public class KernelClassLoader extends URLClassLoader {

    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();

    private final ModuleReflectionManager moduleReflectionManager;
    private final ModuleYaml moduleYaml;

    private final File moduleFile;
    private final JarFile moduleJar;
    private final URL url;

    @Getter private final IModule module;

    public KernelClassLoader(ModuleReflectionManager moduleReflectionManager, File moduleFile, ModuleYaml moduleYaml, ClassLoader parent) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        super(new URL[]{moduleFile.toURI().toURL()}, parent);

        this.moduleReflectionManager = moduleReflectionManager;
        this.moduleYaml = moduleYaml;
        this.moduleFile = moduleFile;

        moduleJar = new JarFile(moduleFile);
        url = moduleFile.toURI().toURL();

        Class<? extends KernelModule> mainClass = getModuleMainClass(moduleYaml);
        module = mainClass.getConstructor().newInstance();
    }

    private Class<? extends KernelModule> getModuleMainClass(ModuleYaml moduleYaml) {
        try {
            Class<?> jarClass = Class.forName(moduleYaml.getMain(), true, this);
            return jarClass.asSubclass(KernelModule.class);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Cannot find main class '" + moduleYaml.getMain() + "'", ex);
        } catch (ClassCastException ex) {
            throw new RuntimeException("Module main class '" + moduleYaml.getMain() + "' does not extend KernelModule", ex);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    protected Class<?> findClass(String name, boolean checkAllModules) throws ClassNotFoundException {
        // Check classes cache and avoid extra work.
        if (classes.containsKey(name)) return classes.get(name);

        // Try to find the class requested by the module.
        Class<?> clazz = findClassInJar(name);

        // In case the class is not found in the jar file, search for it
        // around all the modules. This will make modules share classes.
        if (clazz == null && checkAllModules) {
            clazz = moduleReflectionManager.getClassByName(name);
        }

        // Finally, if the class stills unknown, call the default findClass method.
        if (clazz == null) clazz = super.findClass(name);

        if (clazz != null) {
            // Cache in the parent manager this class instance.
            // Telling to the manager that the class is not found can
            // cause problems with other modules. Take care with that.
            moduleReflectionManager.setClass(name, clazz);
        }

        // Cache the class result (doesn't matter if null)
        classes.put(name, clazz);
        return clazz;
    }

    private Class<?> findClassInJar(String name) throws ClassNotFoundException {
        // Obtain module jar entry.
        String path = name.replace('.', '/').concat(".class");
        JarEntry entry = moduleJar.getJarEntry(path);
        if (entry == null) return null;

        // Read class bytes.
        byte[] classBytes;
        try (InputStream is = moduleJar.getInputStream(entry)) {
            classBytes = IOUtils.toByteArray(is);
        } catch (IOException ex) {
            throw new ClassNotFoundException(name, ex);
        }

        // Define class package in case it's not already defined.
        definePackage(name);

        // Finally define class itself.
        CodeSigner[] signers = entry.getCodeSigners();
        CodeSource source = new CodeSource(url, signers);
        return defineClass(name, classBytes, 0, classBytes.length, source);
    }

    private void definePackage(String name) {
        int lastDotPosition = name.lastIndexOf('.');

        boolean hasPackage = lastDotPosition != -1;
        if (!hasPackage) return;

        String pkgName = name.substring(0, lastDotPosition);
        if (getDefinedPackage(pkgName) != null) return;

        try {
            definePackage(pkgName, null, null, null, null, null, null, null);
        } catch (IllegalArgumentException ex) {
            if (getDefinedPackage(pkgName) != null) return; // Cause it has been created, don't mind about it.
            throw new IllegalStateException("Cannot find package " + pkgName);
        }
    }

}