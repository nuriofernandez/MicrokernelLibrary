package me.nurio.microkernel.modules;

import lombok.RequiredArgsConstructor;
import me.nurio.events.EventManager;
import me.nurio.microkernel.events.ModuleDisableEvent;
import me.nurio.microkernel.events.ModuleEnableEvent;

@RequiredArgsConstructor
public class ModuleManager {

    private final EventManager eventManager;

    public void loadModule(Module module) {
        System.err.printf("Enabling '%s'...%n", module.getAuthor() + "@" + module.getName());

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
        System.err.printf("'%s' module was enabled successfully.%n", module.getAuthor() + "@" + module.getName());
    }

    public void unloadModule(Module module) {
        System.err.printf("Disabling '%s'...%n", module.getAuthor() + "@" + module.getName());

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
        System.err.printf("'%s' module was disabled successfully.%n", module.getAuthor() + "@" + module.getName());
    }

}