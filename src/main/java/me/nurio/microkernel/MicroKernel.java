package me.nurio.microkernel;

import lombok.Getter;
import lombok.SneakyThrows;
import me.nurio.events.EventManager;
import me.nurio.microkernel.loader.ModuleManager;

public class MicroKernel {

    @Getter private static EventManager eventManager = new EventManager();
    @Getter private static ModuleManager moduleManager = new ModuleManager(eventManager);

    @SneakyThrows
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        System.out.println("Starting modules...");
        moduleManager.loadAll();

        System.out.println("Registering shutdown hook...");
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        long startupTime = System.currentTimeMillis() - startTime;
        System.out.printf("Done. Started in %dms.%n", startupTime);
    }

}