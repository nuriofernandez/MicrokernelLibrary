package me.nurio.microkernel.loader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.nurio.microkernel.exceptions.InvalidModuleLoadException;
import me.nurio.microkernel.modules.IModule;
import me.nurio.microkernel.modules.KernelModule;
import me.nurio.microkernel.modules.ModuleManager;
import me.nurio.microkernel.modules.ModuleYaml;
import org.apache.commons.lang3.StringUtils;

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

    public List<IModule> loadAll() {
        List<IModule> modules = getAllModules();
        modules.forEach(moduleManager::loadModule);
        return modules;
    }

    @SneakyThrows
    public IModule getModule(File moduleFile) {
        ModuleYaml moduleYaml = moduleFileManager.getModuleYML(moduleFile);
        String mainClassPath = moduleYaml.getMain();
        if (StringUtils.isBlank(mainClassPath)) return null;

        URLClassLoader child = new URLClassLoader(
            new URL[]{moduleFile.toURI().toURL()},
            this.getClass().getClassLoader()
        );

        Class<?> mainClass = Class.forName(mainClassPath, true, child);

        // Prevent loading invalid modules.
        if (!mainClass.getSuperclass().equals(KernelModule.class)) {
            throw new InvalidModuleLoadException("Module main class does not extends KernelModule");
        }

        return (IModule) mainClass.getConstructor().newInstance();
    }

    public List<IModule> getAllModules() {
        return moduleFileManager.getModulesFiles()
            .stream()
            .map(this::getModule)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

}