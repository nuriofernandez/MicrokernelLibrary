package me.nurio.microkernel.loader;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.nurio.microkernel.exceptions.InvalidModuleLoadException;
import me.nurio.microkernel.modules.IModule;
import me.nurio.microkernel.modules.KernelModule;
import me.nurio.microkernel.modules.ModuleYaml;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ModuleReflectionManager {

    private final ModuleFileManager moduleFileManager;

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

}