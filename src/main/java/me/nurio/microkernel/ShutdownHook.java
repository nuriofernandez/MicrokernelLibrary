package me.nurio.microkernel;

import me.nurio.microkernel.modules.IModule;
import me.nurio.microkernel.modules.ModuleManager;

import java.util.List;

public class ShutdownHook extends Thread {

    private static List<IModule> loadedModules = MicroKernel.getLoadedModules();
    private static ModuleManager moduleManager = MicroKernel.getModuleManager();

    @Override
    public void run() {
        System.out.println("Shutting down microkernel.");
        System.out.println("Disabling loaded modules...");
        loadedModules.forEach(moduleManager::unloadModule);
    }

}