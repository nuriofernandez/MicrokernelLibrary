package me.nurio.microkernel.modules;

import lombok.Getter;
import me.nurio.events.EventManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ModuleManagerTest {

    private ModuleTest module;
    private EventManager eventManager;
    private ModuleManager moduleManager;

    @Before
    public void start() {
        module = new ModuleTest();
        eventManager = new EventManager();
        moduleManager = new ModuleManager(eventManager);
    }

    @Test
    public void testLoadModule() {
        moduleManager.loadModule(module);
        assertTrue(module.isEnabled());
    }

    @Test
    public void testUnloadModule() {
        moduleManager.unloadModule(module);
        assertTrue(module.isDisabled());
    }

}

@Getter
class ModuleTest extends KernelModule {

    private String name = "Test";
    private String author = "Nurio";

    private boolean enabled;
    private boolean disabled;

    @Override
    public void onEnable() {
        enabled = true;
    }

    @Override
    public void onDisable() {
        disabled = true;
    }

}