package me.nurio.microkernel;

import lombok.Getter;
import lombok.SneakyThrows;
import me.nurio.events.EventManager;
import me.nurio.microkernel.loader.ModuleFileManager;
import me.nurio.microkernel.loader.ModuleLoader;
import me.nurio.microkernel.modules.Module;
import me.nurio.microkernel.modules.ModuleManager;

import java.util.List;

public class MicroKernel {

    @Getter private static EventManager eventManager = new EventManager();
    @Getter private static ModuleFileManager moduleFileManager = new ModuleFileManager();

    @Getter private static ModuleManager moduleManager = new ModuleManager(eventManager);
    @Getter private static ModuleLoader moduleLoader = new ModuleLoader(moduleFileManager, moduleManager);

    @SneakyThrows
    public static void main(String[] args) {
        System.out.println("Starting modules...");

        List<Module> loadedModules = moduleLoader.loadAll();

        System.out.println("Waiting 12 seconds to stop...");
        Thread.sleep(12000);

        System.out.println("Stopping...");
        Thread.sleep(500);
        loadedModules.forEach(moduleManager::unloadModule);
    }

}