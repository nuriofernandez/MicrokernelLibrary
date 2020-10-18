package me.nurio.microkernel.loader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.nurio.microkernel.modules.IModule;
import me.nurio.microkernel.modules.ModuleYaml;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ModuleReflectionManager {

    private final ModuleFileManager moduleFileManager;

    private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
    private final List<KernelClassLoader> loaders = new CopyOnWriteArrayList<>();

    public List<IModule> getAllModules() {
        return moduleFileManager.getModulesJarFiles()
            .stream()
            .map(this::getModule)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @SneakyThrows
    public IModule getModule(File moduleFile) {
        ModuleYaml moduleYaml = moduleFileManager.getYamler().getModuleYaml(moduleFile);
        String mainClassPath = moduleYaml.getMain();
        if (StringUtils.isBlank(mainClassPath)) return null;

        KernelClassLoader classLoader = new KernelClassLoader(
            this,
            moduleFile,
            moduleYaml,
            this.getClass().getClassLoader()
        );
        loaders.add(classLoader);

        return classLoader.getModule();
    }

    /**
     * Caches provided class by his name.
     * - It will only work with the first call, the rest will be ignored.
     *
     * @param name  Class path to set.
     * @param clazz Class instance to cache.
     */
    protected void setClass(String name, Class<?> clazz) {
        if (classes.containsKey(name)) {
            // Cause multiple modules can have classes in the same package
            // We are going to take care only to the first one.
            return;
        }
        classes.put(name, clazz);
    }

    /**
     * Search class by name across all modules.
     *
     * @param name Class path to find.
     * @return Class instance or null in case of not found.
     */
    protected Class<?> getClassByName(String name) {
        // Check classes cache and avoid extra work.
        if (classes.containsKey(name)) return classes.get(name);

        // Loop all class loaders
        for (KernelClassLoader loader : loaders) {
            try {
                return loader.findClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore exception
            }
        }
        return null;
    }

}