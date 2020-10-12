package me.nurio.microkernel;

import lombok.Getter;
import lombok.SneakyThrows;
import me.nurio.events.EventManager;
import me.nurio.microkernel.loader.ModuleFileManager;
import me.nurio.microkernel.loader.ModuleLoader;
import me.nurio.microkernel.modules.IModule;
import me.nurio.microkernel.modules.ModuleManager;

import java.util.ArrayList;
import java.util.List;

public class MicroKernel {

    @Getter private static EventManager eventManager = new EventManager();
    @Getter private static ModuleFileManager moduleFileManager = new ModuleFileManager();

    @Getter private static ModuleManager moduleManager = new ModuleManager(eventManager);
    @Getter private static ModuleLoader moduleLoader = new ModuleLoader(moduleFileManager, moduleManager);

    @Getter private static List<IModule> loadedModules = new ArrayList<>();

    @SneakyThrows
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        System.out.println("Starting modules...");
        loadedModules = moduleLoader.loadAll();

        System.out.println("Registering shutdown hook...");
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        long startupTime = System.currentTimeMillis() - startTime;
        System.out.printf("Done. Started in %dms.%n", startupTime);
    }

}