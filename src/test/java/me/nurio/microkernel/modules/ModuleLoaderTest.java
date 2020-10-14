package me.nurio.microkernel.modules;

import lombok.Getter;
import me.nurio.events.EventManager;
import me.nurio.microkernel.loader.ModuleLoader;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ModuleLoaderTest {

    private ModuleTest module;
    private EventManager eventManager;
    private ModuleLoader moduleLoader;

    @Before
    public void start() {
        module = new ModuleTest();
        eventManager = new EventManager();
        moduleLoader = new ModuleLoader(eventManager);
    }

    @Test
    public void testLoadModule() {
        moduleLoader.loadModule(module);
        assertTrue(module.isEnabled());
    }

    @Test
    public void testUnloadModule() {
        moduleLoader.unloadModule(module);
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