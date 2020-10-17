package me.nurio.microkernel.loader;

import me.nurio.events.EventManager;
import me.nurio.microkernel.modules.IModule;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 * This will test that modules are sharing classes across.
 */
public class ModuleLoaderAllowClassSharingAcrossModulesTest {

    private ClassLoader classLoader;

    private EventManager eventManager;
    private ModuleManager moduleManager;
    private ModuleReflectionManager moduleReflectionManager;

    @Before
    public void start() {
        classLoader = getClass().getClassLoader();

        eventManager = new EventManager();
        moduleManager = new ModuleManager(eventManager);
        moduleReflectionManager = moduleManager.getModuleReflectionManager();
    }

    @Test
    public void test() throws IllegalAccessException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        // Enable test module
        IModule dependencyModule = enableDependencyModule();

        // No exception here means it has been load properly.
        IModule dependentModule = enableDependentModule();
        Object anUnusedClassToTest = useUnusedClass(dependentModule);

        // Assert if class field value matches expected.
        String testingFieldValue = getWorkingValue(anUnusedClassToTest);
        assertEquals("It works!", testingFieldValue);
    }

    private IModule enableDependencyModule() {
        File moduleFile = new File(classLoader.getResource(
            "ModuleLoaderAllowClassSharingAcrossModulesTest/module-with-an-unused-class.jar").getFile()
        );

        IModule module = moduleReflectionManager.getModule(moduleFile);
        moduleManager.getModuleLoader().loadModule(module);

        return module;
    }

    private IModule enableDependentModule() {
        File moduleFile = new File(classLoader.getResource(
            "ModuleLoaderAllowClassSharingAcrossModulesTest/module-that-uses-the-unused-class.jar").getFile()
        );

        IModule module = moduleReflectionManager.getModule(moduleFile);
        moduleManager.getModuleLoader().loadModule(module);

        return module;
    }

    private Object useUnusedClass(IModule module) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method = module.getClass().getDeclaredMethod("useTheUnusedClass");
        return method.invoke(module);
    }

    private String getWorkingValue(Object anUnusedClassToTest) throws IllegalAccessException, NoSuchFieldException {
        Field filed = anUnusedClassToTest.getClass().getDeclaredField("text");
        filed.setAccessible(true);

        return (String) filed.get(anUnusedClassToTest);
    }

}