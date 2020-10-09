package me.nurio.microkernel.loader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.nurio.microkernel.modules.Module;
import me.nurio.microkernel.modules.ModuleManager;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ModuleLoader {

    private final ModuleFileManager moduleFileManager;
    private final ModuleManager moduleManager;

    public List<Module> loadAll() {
        List<Module> modules = getAllModules();
        modules.forEach(moduleManager::loadModule);
        return modules;
    }

    @SneakyThrows
    public Module getModule(File moduleFile) {
        String mainClassPath = moduleFileManager.getMainClassPath(moduleFile);
        if (mainClassPath == null) return null;

        URLClassLoader child = new URLClassLoader(
            new URL[]{moduleFile.toURI().toURL()},
            this.getClass().getClassLoader()
        );

        Class<?> mainClass = Class.forName(mainClassPath, true, child);
        return (Module) mainClass.getConstructor().newInstance();
    }

    public List<Module> getAllModules() {
        return moduleFileManager.getModulesFiles()
            .stream()
            .map(this::getModule)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}