package me.nurio.microkernel.loader;

import me.nurio.microkernel.exceptions.InvalidModuleLoadException;
import me.nurio.microkernel.modules.ModuleYaml;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModuleYamlParser {

    private Yaml yamlLoader = new Yaml(new Constructor(ModuleYaml.class), new Representer() {
        {
            getPropertyUtils().setSkipMissingProperties(true);
            getPropertyUtils().setBeanAccess(BeanAccess.FIELD);
        }
    });

    public ModuleYaml getModuleYaml(File moduleJar) throws InvalidModuleLoadException {
        try (JarFile jarFile = new JarFile(moduleJar)) {
            JarEntry entry = jarFile.getJarEntry("module.yml");

            // Control missing module.yml file.
            if (entry == null) {
                throw new InvalidModuleLoadException("Module doesn't have a module.yml");
            }

            InputStream inputStream = jarFile.getInputStream(entry);
            ModuleYaml moduleYaml = yamlLoader.load(inputStream);

            // Validate yml file as required params.
            if (StringUtils.isBlank(moduleYaml.getMain())) {
                throw new InvalidModuleLoadException("Module main class is not specified at the module.yml");
            }

            return moduleYaml;
        } catch (Exception exception) {
            throw new InvalidModuleLoadException(exception.getMessage());
        }
    }

}