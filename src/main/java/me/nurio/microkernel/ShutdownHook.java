package me.nurio.microkernel;

import me.nurio.microkernel.loader.ModuleManager;

public class ShutdownHook extends Thread {

    private static ModuleManager moduleManager = MicroKernel.getModuleManager();

    @Override
    public void run() {
        System.out.println("Shutting down microkernel.");

        System.out.println("Disabling loaded modules...");
        moduleManager.unloadAll();
    }

}