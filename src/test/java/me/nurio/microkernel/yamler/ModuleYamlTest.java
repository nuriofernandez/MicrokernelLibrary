package me.nurio.microkernel.yamler;

import me.nurio.microkernel.exceptions.InvalidModuleLoadException;
import me.nurio.microkernel.loader.ModuleFileManager;
import me.nurio.microkernel.modules.ModuleYaml;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ModuleYamlTest {

    private ModuleFileManager moduleFileManager;
    private ClassLoader classLoader;

    @Before
    public void start() {
        moduleFileManager = new ModuleFileManager();
        classLoader = getClass().getClassLoader();
    }

    @Test
    public void loadModule_shouldLoadSuccessfully_whenModuleIsWellMade() throws InvalidModuleLoadException {
        File file = new File(classLoader.getResource("valid-module-main.jar").getFile());
        ModuleYaml moduleYaml = moduleFileManager.getModuleYML(file);
        assertEquals("org.packagepath.to.mainclass.CounterModule", moduleYaml.getMain());
    }

    @Test
    public void loadModule_shouldLoadSuccessfully_whenModuleIsWellMadeButHasMoreFieldsThanExpected() throws InvalidModuleLoadException {
        File file = new File(classLoader.getResource("valid-module-main-extra-field-potato.jar").getFile());
        ModuleYaml moduleYaml = moduleFileManager.getModuleYML(file);
        assertEquals("org.packagepath.to.mainclass.CounterModule", moduleYaml.getMain());
    }

    @Test(expected = InvalidModuleLoadException.class)
    public void loadModule_shouldThrowAnException_whenModuleDoesNotSpecifyRequiredMainClassAtTheYmlFile() throws InvalidModuleLoadException {
        File file = new File(classLoader.getResource("invalid-module-no-main.jar").getFile());
        moduleFileManager.getModuleYML(file);
    }

}