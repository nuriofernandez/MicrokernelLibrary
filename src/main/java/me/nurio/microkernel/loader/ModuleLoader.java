package me.nurio.microkernel.loader;

import lombok.RequiredArgsConstructor;
import me.nurio.events.EventManager;
import me.nurio.microkernel.events.ModuleDisableEvent;
import me.nurio.microkernel.events.ModuleEnableEvent;
import me.nurio.microkernel.modules.IModule;

@RequiredArgsConstructor
public class ModuleLoader {

    private final EventManager eventManager;

    public void loadModule(IModule module) {
        System.out.printf("Enabling '%s'...%n", module.getAuthor() + "@" + module.getName());

        ModuleEnableEvent moduleEnableEvent = new ModuleEnableEvent(
            module.getName(),
            module.getAuthor(),
            module.getClass().getCanonicalName()
        );
        eventManager.callEvent(moduleEnableEvent);

        if (moduleEnableEvent.isCancelled()) {
            System.err.printf("Unable to enable '%s'. Enable event was cancelled.%n", module.getAuthor() + "@" + module.getName());
            return;
        }

        module.onEnable();
        System.out.printf("Module '%s' was enabled successfully.%n", module.getAuthor() + "@" + module.getName());
    }

    public void unloadModule(IModule module) {
        System.out.printf("Disabling '%s'...%n", module.getAuthor() + "@" + module.getName());

        ModuleDisableEvent moduleDisableEvent = new ModuleDisableEvent(
            module.getName(),
            module.getAuthor(),
            module.getClass().getCanonicalName()
        );
        eventManager.callEvent(moduleDisableEvent);

        if (moduleDisableEvent.isCancelled()) {
            System.err.printf("Unable to disable '%s'. Disable event was cancelled.%n", module.getAuthor() + "@" + module.getName());
            return;
        }

        module.onDisable();
        System.out.printf("Module '%s' was disabled successfully.%n", module.getAuthor() + "@" + module.getName());
    }

}