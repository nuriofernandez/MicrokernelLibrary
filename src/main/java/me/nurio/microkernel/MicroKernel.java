package me.nurio.microkernel;

import lombok.Getter;
import me.nurio.events.EventManager;
import me.nurio.microkernel.modules.ModuleManager;

public class MicroKernel {

    @Getter private static EventManager eventManager = new EventManager();
    @Getter private static ModuleManager moduleManager = new ModuleManager(eventManager);

    public static void main(String[] args) {
        System.out.println("That's it.");
    }

}