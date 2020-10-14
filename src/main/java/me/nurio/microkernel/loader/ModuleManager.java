package me.nurio.microkernel.loader;

import lombok.Getter;
import me.nurio.events.EventManager;
import me.nurio.microkernel.modules.IModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private final EventManager eventManager;

    @Getter private ModuleLoader moduleLoader;
    @Getter private ModuleFileManager moduleFileManager;
    @Getter private ModuleReflectionManager moduleReflectionManager;

    @Getter private List<IModule> loadedModules = new ArrayList<>();

    public ModuleManager(EventManager eventManager) {
        this.eventManager = eventManager;
        this.moduleLoader = new ModuleLoader(eventManager);
        this.moduleFileManager = new ModuleFileManager();
        this.moduleReflectionManager = new ModuleReflectionManager(moduleFileManager);
    }

    public List<IModule> loadAll() {
        List<IModule> modules = moduleReflectionManager.getAllModules();
        for (IModule module : modules) {
            moduleLoader.loadModule(module);
            loadedModules.add(module);
        }
        return modules;
    }

    public void unloadAll() {
        for (IModule module : loadedModules) {
            moduleLoader.unloadModule(module);
            loadedModules.remove(module);
        }
    }

}